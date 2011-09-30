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

/**
 *
 * @author saumitra
 */
public class APIModule extends AbstractModule {

  private final String workspaceIdNamespace;
  private final String workspaceIdName;
  private final String reportNamespace;
  private final String reportName;

  public APIModule(Properties properties) {
    if (properties == null) {
      workspaceIdNamespace = "";
      workspaceIdName = "";
      reportNamespace = "";
      reportName = "";
    }
    else {
      PropertiesLocator propertiesLocator = new PropertiesLocator();
      propertiesLocator.setSmartLocations(properties.getProperty("domainProps"));
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
    bind(WorkspaceId.class).annotatedWith(Names.named("workspaceId")).toInstance(workspaceId);
    bind(ContentTypeId.class).annotatedWith(Names.named("reportContentTypeId")).toInstance(reportTypeId);
  }
}
