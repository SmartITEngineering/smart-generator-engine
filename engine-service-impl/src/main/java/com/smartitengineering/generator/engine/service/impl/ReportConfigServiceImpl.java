/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.service.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.smartitengineering.cms.api.workspace.WorkspaceId;
import com.smartitengineering.dao.common.CommonReadDao;
import com.smartitengineering.dao.common.CommonWriteDao;
import com.smartitengineering.dao.common.queryparam.MatchMode;
import com.smartitengineering.dao.common.queryparam.QueryParameter;
import com.smartitengineering.dao.common.queryparam.QueryParameterFactory;
import com.smartitengineering.generator.engine.domain.ReportConfig;
import com.smartitengineering.generator.engine.domain.ReportEvent;
import com.smartitengineering.generator.engine.service.ReportConfigFilter;
import com.smartitengineering.generator.engine.service.ReportConfigService;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.quartz.DateIntervalTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author saumitra
 */
@Singleton
public class ReportConfigServiceImpl implements ReportConfigService {

  private static final int DEFAULT_SCHEDULE_SIZE = 100;
  public static final String INJECT_NAME_NUM_OF_SCHEDULES = "numberOfSchedulesToReside";
  public static final String INJECT_NAME_EXECUTION_SERVICE = "reportGenerationExec";
  @Inject
  protected CommonReadDao<ReportConfig, String> commonReadDao;
  @Inject
  protected CommonWriteDao<ReportConfig> commonWriteDao;
  @Inject
  protected CommonReadDao<ReportEvent, String> commonEventReadDao;
  @Inject
  protected CommonWriteDao<ReportEvent> commonEventWriteDao;
  @Inject(optional = true)
  @Named(INJECT_NAME_NUM_OF_SCHEDULES)
  protected int defaultScheduleSize;
  @Inject
  @Named(ReportServiceImpl.INJECT_NAME_WORKSPACE_ID)
  private WorkspaceId workspaceId;
  @Inject
  @Named(INJECT_NAME_EXECUTION_SERVICE)
  private ExecutorService executor;
  protected final transient Logger logger = LoggerFactory.getLogger(getClass());
  private final Semaphore syncMutex = new Semaphore(1);
  private final Semaphore reportMutex = new Semaphore(1);
  private final Semaphore resyncMutex = new Semaphore(1);
  private Scheduler scheduler;

  @Override
  public ReportConfig getById(String id) {
    if (StringUtils.isBlank(id)) {
      return null;
    }
    return commonReadDao.getById(id);
  }

  @Override
  public void save(ReportConfig reportConfig) {
    resetScheduleGeneration(reportConfig);
    commonWriteDao.save(reportConfig);
  }

  @Override
  public void delete(ReportConfig reportConfig) {
    commonWriteDao.delete(reportConfig);
    deleteConfigEvents(reportConfig);
  }

  @Override
  public void update(ReportConfig reportConfig) {

    commonWriteDao.update(reportConfig);
  }

  @Inject
  public void initCrons() {
    logger.info("Initialize cron jobs");
    try {
      scheduler = StdSchedulerFactory.getDefaultScheduler();
      JobDetail detail = new JobDetail("reportSyncJob", "reportSyncPoll", EventSyncJob.class);
      Trigger trigger = new DateIntervalTrigger("reportSyncTrigger", "reportSyncPoll",
                                                DateIntervalTrigger.IntervalUnit.MINUTE,
                                                5);
      JobDetail redetail = new JobDetail("reportJob", "reportPoll", EventReSyncJob.class);
      Trigger retrigger = new DateIntervalTrigger("reportTrigger", "reportPoll",
                                                  DateIntervalTrigger.IntervalUnit.DAY, 1);
      JobDetail reportDetail = new JobDetail("reportJob", "reportPoll", ReportJob.class);
      Trigger reportTrigger = new DateIntervalTrigger("reportTrigger", "reportPoll",
                                                      DateIntervalTrigger.IntervalUnit.MINUTE, 1);
      scheduler.setJobFactory(new JobFactory() {

        public Job newJob(TriggerFiredBundle bundle) throws SchedulerException {
          try {
            Class<? extends Job> jobClass = bundle.getJobDetail().getJobClass();
            if (ReportConfigServiceImpl.class.equals(jobClass.getEnclosingClass())) {
              Constructor<? extends Job> constructor =
                                         (Constructor<? extends Job>) jobClass.getDeclaredConstructors()[0];
              constructor.setAccessible(true);
              Job job = constructor.newInstance(ReportConfigServiceImpl.this);
              return job;
            }
            else {
              return jobClass.newInstance();
            }
          }
          catch (Exception ex) {
            throw new SchedulerException(ex);
          }
        }
      });
      scheduler.start();
      scheduler.scheduleJob(detail, trigger);
      scheduler.scheduleJob(redetail, retrigger);
      scheduler.scheduleJob(reportDetail, reportTrigger);
    }
    catch (Exception ex) {
      logger.warn("Could initialize cron job!", ex);
    }
  }

  private class ReportJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
      try {
        reportMutex.acquire();
      }
      catch (Exception ex) {
        logger.warn("Could not acquire lock!", ex);
        throw new JobExecutionException(ex);
      }
      generateReport();
      reportMutex.release();
    }
  }

  private class EventSyncJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
      try {
        syncMutex.acquire();
      }
      catch (Exception ex) {
        logger.warn("Could not acquire lock!", ex);
        throw new JobExecutionException(ex);
      }
      publishReportEventsForConfig();
      syncMutex.release();
    }
  }

  private class EventReSyncJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
      try {
        resyncMutex.acquire();
      }
      catch (Exception ex) {
        logger.warn("Could not acquire lock!", ex);
        throw new JobExecutionException(ex);
      }
      reSyncReportEventsForConfig();
      resyncMutex.release();
    }
  }

  private class GenerateReport implements Runnable {

    private final ReportEvent reportEvent;

    public GenerateReport(ReportEvent reportEvent) {
      if (reportEvent == null) {
        throw new IllegalArgumentException("ReportEvent can not be null!");
      }
      this.reportEvent = reportEvent;
    }

    public void run() {
      //Implement script execution of report event
    }
  }

  protected void generateReport() {
    try {
      List<ReportEvent> events = commonEventReadDao.getList(QueryParameterFactory.getEqualPropertyParam(
          ReportEvent.PROPERTY_EVENTSTATUS, ReportEvent.EventStatus.Pending.name()), QueryParameterFactory.
          getLesserThanEqualToPropertyParam(ReportEvent.PROPERTY_DATEREPORTSCHEDULEDFOR, new Date()));
      if (events != null && !events.isEmpty()) {
        for (ReportEvent event : events) {
          executor.submit(new GenerateReport(event));
        }
      }
    }
    catch (Exception ex) {
      logger.error("Cpuld not generate report!", ex);
    }
  }

  protected void publishReportEventsForConfig() {
    if (logger.isInfoEnabled()) {
      logger.info("Tracking all reports to be for which a schedule is required");
    }
    List<ReportConfig> configs;
    try {
      configs = commonReadDao.getList(QueryParameterFactory.getEqualPropertyParam(
          ReportConfig.PROPERTY_EVENTSCREATED, false));
    }
    catch (Exception ex) {
      logger.warn("Could not get to be processed reports configs!", ex);
      configs = null;
    }
    createFreshSchedules(configs, true);
  }

  protected void reSyncReportEventsForConfig() {
    if (logger.isInfoEnabled()) {
      logger.info("Tracking all reports to be for which a schedule is required");
    }
    List<ReportConfig> configs;
    Calendar date = Calendar.getInstance();
    date.setTime(new Date());
    date.add(Calendar.DATE, 3);
    try {
      configs = commonReadDao.getList(QueryParameterFactory.getEqualPropertyParam(ReportConfig.PROPERTY_EVENTSCREATED,
                                                                                  true), QueryParameterFactory.
          getLesserThanEqualToPropertyParam(ReportConfig.PROPERTY_VALIDTILL, date.getTime()));
    }
    catch (Exception ex) {
      logger.warn("Could not get to be processed reports configs!", ex);
      configs = null;
    }
    createFreshSchedules(configs, false);
  }

  protected void createFreshSchedules(List<ReportConfig> configs, boolean deleteOlds) {
    if (configs != null && !configs.isEmpty()) {
      for (ReportConfig config : configs) {
        //Delete the old events
        if (deleteOlds) {
          if (!deleteConfigEvents(config)) {
            continue;
          }
        }
        try {
          final List<Date> schedules = getSchedules(config, config.getValidTill());
          if (schedules != null && !schedules.isEmpty()) {
            //Create the new events
            for (Date schedule : schedules) {
              ReportEvent event = new ReportEvent();
              event.setDateReportScheduledFor(schedule);
              event.setEventStatus(ReportEvent.EventStatus.Pending);
              event.setReportConfig(config);
              commonEventWriteDao.save(event);
            }
            config.setEventsCreated(true);
            config.setValidTill(schedules.get(schedules.size() - 1));
            commonWriteDao.update(config);
          }

        }
        catch (Exception ex) {
          logger.error("Could not create all events for config " + ReportServiceImpl.getContentId(workspaceId, config.
              getId()), ex);
        }
      }
    }
  }

  protected boolean deleteConfigEvents(ReportConfig config) {
    try {
      List<ReportEvent> events = commonEventReadDao.getList(QueryParameterFactory.getEqualPropertyParam(
          ReportEvent.PROPERTY_EVENTSTATUS, ReportEvent.EventStatus.Pending.name()), QueryParameterFactory.
          getStringLikePropertyParam(ReportEvent.PROPERTY_REPORTCONFIG, ReportServiceImpl.getContentId(workspaceId,
                                                                                                       config.getId()).
          toString()));
      if (events != null && !events.isEmpty()) {
        commonEventWriteDao.delete(events.toArray(new ReportEvent[events.size()]));
      }
      return true;
    }
    catch (Exception ex) {
      logger.warn("Could not delete old events!", ex);
      logger.error("As error in deleting old events skipping the config for this iteration!");
      return false;
    }
  }

  protected int getScheduleSize() {
    return defaultScheduleSize > 0 ? defaultScheduleSize : DEFAULT_SCHEDULE_SIZE;
  }

  protected List<Date> getSchedules(ReportConfig config, final Date startDate) {
    try {
      CronExpression expression = new CronExpression(config.getTrigger());
      List<Date> schedules = new ArrayList<Date>(getScheduleSize());
      Date baseDate = startDate == null ? new Date() : startDate;
      for (int i = 0; i < getScheduleSize(); ++i) {
        final Date nextDate = expression.getNextValidTimeAfter(baseDate);
        schedules.add(nextDate);
        baseDate = nextDate;
      }
      return schedules;
    }
    catch (ParseException ex) {
      logger.warn("Could not parse expression!", ex);
      throw new IllegalStateException(ex);
    }
    catch (Exception ex) {
      logger.warn("Could not create schedules!", ex);
      throw new IllegalStateException(ex);
    }
  }

  @Override
  public Collection<ReportConfig> searchConfigs(ReportConfigFilter filter) {
    List<QueryParameter> queries = new ArrayList<QueryParameter>();
    if (filter.getCount() > 0) {
      queries.add(QueryParameterFactory.getMaxResultsParam(filter.getCount()));
    }
    if (filter.getPageIndex() > -1 && filter.getCount() > 0) {
      queries.add(QueryParameterFactory.getFirstResultParam(filter.getCount() * filter.getPageIndex()));
    }
    if (filter.getCreated() != null) {
      queries.add(QueryParameterFactory.getGreaterThanEqualToPropertyParam("creationDate", filter.getCreated()));
    }
    if (StringUtils.isNotBlank(filter.getNameLike())) {
      queries.add(QueryParameterFactory.getStringLikePropertyParam(ReportConfig.PROPERTY_NAME, filter.getNameLike(),
                                                                   MatchMode.ANYWHERE));
    }
    return commonReadDao.getList(queries);
  }

  private void resetScheduleGeneration(ReportConfig reportConfig) {
    reportConfig.setEventsCreated(Boolean.FALSE);
    reportConfig.setValidTill(new Date());
    final List<Date> schedules = getSchedules(reportConfig, null);
    if (schedules == null || schedules.isEmpty()) {
      throw new IllegalArgumentException("Config does not have any schedules!");
    }
  }
}
