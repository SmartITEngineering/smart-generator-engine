/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.service;

import com.smartitengineering.cms.api.factory.content.WriteableContent;
import com.smartitengineering.cms.api.workspace.WorkspaceId;
import java.util.Date;
import java.util.Map;

/**
 * The API to be used by external users to generate Report which are basically Content of CMS.
 * @author imyousuf
 */
public interface ReportExecutor {

  /**
   * A API to be implemented in scripting languages by report config sources. Its soul purpose is to execute processes
   * required for generating a report and creating a content that can hold the output.
   * @param params The parameters configured in report config
   * @param dateReportScheduledFor The date this report execution was scheduled for.
   * @return A CMS Content which is of a type that is instance of Report configured by this engine
   */
  public WriteableContent createReport(WorkspaceId workspaceId, Date dateReportScheduledFor, Map<String, String> params);
}
