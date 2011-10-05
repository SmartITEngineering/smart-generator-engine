/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.guice.binder;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.smartitengineering.cms.api.factory.SmartContentAPI;
import com.smartitengineering.cms.api.type.ContentTypeId;
import com.smartitengineering.cms.api.workspace.WorkspaceId;
import com.smartitengineering.generator.engine.service.ReportConfigService;
import com.smartitengineering.generator.engine.service.ReportService;
import com.smartitengineering.generator.engine.service.impl.ReportConfigServiceImpl;
import com.smartitengineering.generator.engine.service.impl.ReportServiceImpl;
import com.smartitengineering.util.bean.PropertiesLocator;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author saumitra
 */
public class APIModule extends AbstractModule {

  public static final String WORKSPACE_PROPS = "domainProps";
  public static final String CONCURRENT_REPORT_THREADS_PROPS = "concurrentReportThreads";
  public static final String REPORT_EVENT_BATCH_SIZE_PROPS = "reportEventBatchSize";
  private final String workspaceIdNamespace;
  private final String workspaceIdName;
  private final String reportNamespace;
  private final String reportName;
  private final int concurrentReportThreads;
  private final int reportEventScheudleBatchSize;

  public APIModule(Properties properties) {
    if (properties == null) {
      workspaceIdNamespace = "";
      workspaceIdName = "";
      reportNamespace = "";
      reportName = "";
      concurrentReportThreads = 4;
      reportEventScheudleBatchSize = 100;
    }
    else {
      PropertiesLocator propertiesLocator = new PropertiesLocator();
      propertiesLocator.setSmartLocations(properties.getProperty(WORKSPACE_PROPS));
      Properties mainProps = new Properties();
      try {
        propertiesLocator.loadProperties(mainProps);
      }
      catch (Exception ex) {
        throw new IllegalStateException(ex);
      }
      workspaceIdNamespace = mainProps.getProperty(
          "com.smartitengineering.generator.engine.domains.workspaceId.namespace", "");
      workspaceIdName = mainProps.getProperty("com.smartitengineering.generator.engine.domains.workspaceId.name", "");
      reportNamespace = mainProps.getProperty(
          "com.smartitengineering.generator.engine.domains.report.namespace", "");
      reportName = mainProps.getProperty("com.smartitengineering.generator.engine.domains.report.name", "");
      concurrentReportThreads = NumberUtils.toInt(properties.getProperty(CONCURRENT_REPORT_THREADS_PROPS, "4"), 4);
      reportEventScheudleBatchSize = NumberUtils.toInt(properties.getProperty(REPORT_EVENT_BATCH_SIZE_PROPS, "10"), 100);
    }
  }

  @Override
  protected void configure() {
    bind(ReportConfigService.class).to(ReportConfigServiceImpl.class);
    bind(ReportService.class).to(ReportServiceImpl.class);
    final WorkspaceId workspaceId = SmartContentAPI.getInstance().getWorkspaceApi().createWorkspaceId(
        workspaceIdNamespace, workspaceIdName);
    final ContentTypeId reportTypeId = SmartContentAPI.getInstance().getContentTypeLoader().createContentTypeId(
        workspaceId, reportNamespace, reportName);
    bind(WorkspaceId.class).annotatedWith(Names.named(ReportServiceImpl.INJECT_NAME_WORKSPACE_ID)).toInstance(
        workspaceId);
    bind(ContentTypeId.class).annotatedWith(Names.named(ReportServiceImpl.INJECT_NAME_REPORT_CONTENT_TYPE_ID)).
        toInstance(reportTypeId);
    bind(int.class).annotatedWith(Names.named(ReportConfigServiceImpl.INJECT_NAME_NUM_OF_SCHEDULES)).toInstance(
        reportEventScheudleBatchSize);
    try {
      ExecutorService service = Executors.newFixedThreadPool(concurrentReportThreads);
      bind(ExecutorService.class).annotatedWith(Names.named(ReportConfigServiceImpl.INJECT_NAME_EXECUTION_SERVICE)).
          toInstance(service);
    }
    catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }
}
