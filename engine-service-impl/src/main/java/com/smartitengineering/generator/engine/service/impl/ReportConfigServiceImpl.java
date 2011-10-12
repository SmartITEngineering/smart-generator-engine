/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.service.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.smartitengineering.cms.api.content.ContentId;
import com.smartitengineering.cms.api.content.FieldValue;
import com.smartitengineering.cms.api.content.MutableCollectionFieldValue;
import com.smartitengineering.cms.api.content.MutableCompositeFieldValue;
import com.smartitengineering.cms.api.content.MutableContentFieldValue;
import com.smartitengineering.cms.api.content.MutableDateTimeFieldValue;
import com.smartitengineering.cms.api.content.MutableField;
import com.smartitengineering.cms.api.content.MutableStringFieldValue;
import com.smartitengineering.cms.api.factory.SmartContentAPI;
import com.smartitengineering.cms.api.factory.content.ContentLoader;
import com.smartitengineering.cms.api.factory.content.WriteableContent;
import com.smartitengineering.cms.api.type.CollectionDataType;
import com.smartitengineering.cms.api.type.CompositeDataType;
import com.smartitengineering.cms.api.type.ContentType;
import com.smartitengineering.cms.api.type.ContentTypeId;
import com.smartitengineering.cms.api.type.FieldDef;
import com.smartitengineering.cms.api.workspace.WorkspaceId;
import com.smartitengineering.dao.common.CommonReadDao;
import com.smartitengineering.dao.common.CommonWriteDao;
import com.smartitengineering.dao.common.queryparam.MatchMode;
import com.smartitengineering.dao.common.queryparam.QueryParameter;
import com.smartitengineering.dao.common.queryparam.QueryParameterFactory;
import com.smartitengineering.generator.engine.domain.CodeOnDemand;
import com.smartitengineering.generator.engine.domain.Map;
import com.smartitengineering.generator.engine.domain.Map.Entries;
import com.smartitengineering.generator.engine.domain.Report;
import com.smartitengineering.generator.engine.domain.ReportConfig;
import com.smartitengineering.generator.engine.domain.ReportEvent;
import com.smartitengineering.generator.engine.domain.SourceCode;
import com.smartitengineering.generator.engine.domain.SourceCode.Code;
import com.smartitengineering.generator.engine.service.ReportConfigFilter;
import com.smartitengineering.generator.engine.service.ReportConfigService;
import com.smartitengineering.generator.engine.service.ReportExecutor;
import com.smartitengineering.generator.engine.service.impl.scripting.GroovyObjectFactory;
import com.smartitengineering.generator.engine.service.impl.scripting.JRubyObjectFactory;
import com.smartitengineering.generator.engine.service.impl.scripting.JavascriptObjectFactory;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
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
  @Named(ReportServiceImpl.INJECT_NAME_REPORT_CONTENT_TYPE_ID)
  private ContentTypeId reportTypeId;
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

  @Override
  public void scheduleReport(ReportConfig config, Date schedule) {
    if (config == null) {
      return;
    }
    if (schedule == null) {
      schedule = new Date();
    }
    createReportEvent(schedule, config);
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
      if (reportEvent.getReportConfig() == null) {
        throw new IllegalStateException("Config for report event is missing!");
      }
      this.reportEvent = reportEvent;
    }

    public void run() {
      reportEvent.setEventStatus(ReportEvent.EventStatus.In_Progress);
      commonEventWriteDao.update(reportEvent);
      try {
        final ReportConfig reportConfig = reportEvent.getReportConfig();
        ReportExecutor executor = getExecutor(reportConfig);
        final Map params = reportConfig.getParams();
        if (params != null && params.getEntries() != null) {
          final java.util.Map<String, String> paramMap = new LinkedHashMap<String, String>(params.getEntries().size());
          for (Map.Entries entries : params.getEntries()) {
            paramMap.put(entries.getKey(), entries.getValue());
          }
          long startDate = System.currentTimeMillis();
          WriteableContent content = executor.createReport(reportEvent.getDateReportScheduledFor(), paramMap);
          long endDate = System.currentTimeMillis();
          if (isInstanceOf(content.getContentDefinition(), reportTypeId)) {
            ContentLoader loader = SmartContentAPI.getInstance().getContentLoader();
            final java.util.Map<String, FieldDef> fieldDefs = reportTypeId.getContentType().getFieldDefs();
            //Exec start datetime
            MutableDateTimeFieldValue val = loader.createDateTimeFieldValue();
            val.setValue(new Date(startDate));
            FieldDef def = fieldDefs.get(Report.PROPERTY_EXECUTIONSTARTDATE);
            MutableField field = loader.createMutableField(null, def);
            field.setValue(val);
            content.setField(field);
            //Exec end datetime
            val = loader.createDateTimeFieldValue();
            val.setValue(new Date(endDate));
            def = fieldDefs.get(Report.PROPERTY_EXECUTIONENDDATE);
            field = loader.createMutableField(null, def);
            field.setValue(val);
            content.setField(field);
            //Trigger datetime
            val = loader.createDateTimeFieldValue();
            val.setValue(reportEvent.getDateReportScheduledFor());
            def = fieldDefs.get(Report.PROPERTY_TRIGGERDATE);
            field = loader.createMutableField(null, def);
            field.setValue(val);
            content.setField(field);
            //Params
            Map map = reportConfig.getParams();
            if (map != null && content.getField(Report.PROPERTY_PARAMS) == null && map.getEntries() != null && !map.
                getEntries().isEmpty()) {
              def = fieldDefs.get(Report.PROPERTY_PARAMS);
              CompositeDataType compositeDataType = ((CompositeDataType) def.getValueDef());
              FieldDef mapDef = compositeDataType.getComposedFieldDefs().get(Map.PROPERTY_ENTRIES);
              CompositeDataType entryType = ((CompositeDataType) ((CollectionDataType) mapDef.getValueDef()).
                                             getItemDataType());
              final Collection<Entries> entriess = map.getEntries();
              Collection<FieldValue> vals = new ArrayList<FieldValue>(entriess.size());
              for (Entries entries : entriess) {
                MutableStringFieldValue keyVal = loader.createStringFieldValue();
                keyVal.setValue(entries.getKey());
                MutableField keyField = loader.createMutableField(null, entryType.getComposedFieldDefs().get(
                    Map.Entries.PROPERTY_KEY));
                keyField.setValue(keyVal);
                MutableStringFieldValue valVal = loader.createStringFieldValue();
                valVal.setValue(entries.getValue());
                MutableField valField = loader.createMutableField(null, entryType.getComposedFieldDefs().get(
                    Map.Entries.PROPERTY_VALUE));
                valField.setValue(valVal);
                MutableCompositeFieldValue entryVal = loader.createCompositeFieldValue();
                entryVal.setField(keyField);
                entryVal.setField(valField);
                vals.add(entryVal);
              }
              MutableCollectionFieldValue cVal = loader.createCollectionFieldValue();
              cVal.setValue(vals);
              MutableCompositeFieldValue compVal = loader.createCompositeFieldValue();
              MutableField compField = loader.createMutableField(null, mapDef);
              compField.setValue(cVal);
              compVal.setField(compField);
              field = loader.createMutableField(null, def);
              field.setValue(compVal);
              content.setField(field);
            }
            //Config
            ContentId eventContentId = ReportServiceImpl.getContentId(workspaceId, reportEvent.getId());
            MutableContentFieldValue cVal = loader.createContentFieldValue();
            cVal.setValue(eventContentId);
            def = fieldDefs.get(Report.PROPERTY_REPORTEVENT);
            field = loader.createMutableField(null, def);
            field.setValue(cVal);
            content.setField(field);
            //Save content
            content.put();
          }
          else {
            throw new IllegalStateException("Content created as REPORT ain't instance of " + reportTypeId);
          }
        }
        reportEvent.setEventStatus(ReportEvent.EventStatus.Finished);
        commonEventWriteDao.update(reportEvent);
      }
      catch (Exception ex) {
        logger.error("Error while executing report!", ex);
        reportEvent.setEventStatus(ReportEvent.EventStatus.Error);
        StringWriter writer = new StringWriter();
        ex.printStackTrace(new PrintWriter(writer));
        reportEvent.setAdditionalStatusInfo(writer.toString());
        commonEventWriteDao.update(reportEvent);
      }
    }
  }

  protected ReportEvent createReportEvent(Date schedule, ReportConfig config) {
    ReportEvent event = new ReportEvent();
    event.setDateReportScheduledFor(schedule);
    event.setEventStatus(ReportEvent.EventStatus.Pending);
    event.setReportConfig(config);
    commonEventWriteDao.save(event);
    return event;
  }

  protected ReportExecutor getExecutor(ReportConfig config) {
    CodeOnDemand demand = config.getCodeOnDemand();
    if (demand != null) {
      SourceCode code = demand.getCode();
      return processSourceCodeToExecutor(code);
    }
    else {
      SourceCode embeddedCode = config.getEmbeddedSourceCode();
      return processSourceCodeToExecutor(embeddedCode);
    }
  }

  protected ReportExecutor processSourceCodeToExecutor(SourceCode sourceCode) throws IllegalArgumentException,
                                                                                     IllegalStateException {
    if (sourceCode != null) {
      try {
        Code code = sourceCode.getCode();
        final ReportExecutor reportExecutor;
        byte[] codeData = org.apache.commons.codec.binary.StringUtils.getBytesUtf8(code.getEmbeddedCode());
        switch (code.getCodeType()) {
          case GROOVY:
            reportExecutor = GroovyObjectFactory.getInstance().getObjectFromScript(codeData, ReportExecutor.class);
            break;
          case RUBY:
            reportExecutor = JRubyObjectFactory.getInstance().getObjectFromScript(codeData, ReportExecutor.class);
            break;
          case JAVASCRIPT:
            reportExecutor = JavascriptObjectFactory.getInstance().getObjectFromScript(codeData, ReportExecutor.class);
            break;
          default:
            reportExecutor = null;
        }
        return reportExecutor;
      }
      catch (Exception ex) {
        throw new IllegalStateException("Could not convert script!", ex);
      }
    }
    else {
      throw new IllegalArgumentException("Source code can not be null!");
    }
  }

  protected boolean isInstanceOf(ContentType type, final ContentTypeId typeDef) {
    boolean isInstanceOf = false;
    if (type.getContentTypeID().equals(typeDef)) {
      isInstanceOf = true;
    }
    if (!isInstanceOf) {
      ContentTypeId parentId = type.getParent();
      final ContentType parentType = parentId.getContentType();
      if (!isInstanceOf && parentId != null && parentType != null) {
        isInstanceOf = isInstanceOf(parentType, typeDef);
      }
    }
    return isInstanceOf;
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
              createReportEvent(schedule, config);
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
