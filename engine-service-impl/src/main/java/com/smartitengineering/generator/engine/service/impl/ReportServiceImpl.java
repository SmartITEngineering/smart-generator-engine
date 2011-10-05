/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.service.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.smartitengineering.cms.api.content.Content;
import com.smartitengineering.cms.api.content.ContentId;
import com.smartitengineering.cms.api.content.Filter;
import com.smartitengineering.cms.api.factory.SmartContentAPI;
import com.smartitengineering.cms.api.factory.content.ContentLoader;
import com.smartitengineering.cms.api.type.ContentTypeId;
import com.smartitengineering.cms.api.type.FieldDef;
import com.smartitengineering.cms.api.workspace.WorkspaceId;
import com.smartitengineering.dao.common.queryparam.MatchMode;
import com.smartitengineering.dao.common.queryparam.QueryParameterFactory;
import com.smartitengineering.generator.engine.domain.Report;
import com.smartitengineering.generator.engine.service.ReportFilter;
import com.smartitengineering.generator.engine.service.ReportService;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author imyousuf
 */
public class ReportServiceImpl implements ReportService {
  
  public static final String INJECT_NAME_WORKSPACE_ID = "workspaceId";
  public static final String INJECT_NAME_REPORT_CONTENT_TYPE_ID = "reportContentTypeId";

  @Inject
  @Named(INJECT_NAME_WORKSPACE_ID)
  private WorkspaceId workspaceId;
  @Inject
  @Named(INJECT_NAME_REPORT_CONTENT_TYPE_ID)
  private ContentTypeId reportTypeId;

  public Collection<Content> search(ReportFilter reportFilter) {
    Filter filter = SmartContentAPI.getInstance().getContentLoader().craeteFilter();
    filter.setWorkspaceId(workspaceId);
    filter.addContentTypeToFilter(reportTypeId);
    if (reportFilter.getCount() > 0) {
      filter.setMaxContents(reportFilter.getCount());
      if (reportFilter.getPageIndex() > -1) {
        filter.setStartFrom(reportFilter.getCount() * reportFilter.getPageIndex());
      }
    }
    if (StringUtils.isNotBlank(reportFilter.getConfigId())) {
      String contentId = getContentId(workspaceId, reportFilter.getConfigId()).toString();
      FieldDef reportConfigFieldDef = reportTypeId.getContentType().getFieldDefs().get(Report.PROPERTY_REPORTCONFIG);
      String searchFieldName = SmartContentAPI.getInstance().getContentTypeLoader().getSearchFieldName(
          reportConfigFieldDef);
      if (StringUtils.isNotBlank(searchFieldName) && StringUtils.isNotBlank(contentId)) {
        filter.addFieldFilter(QueryParameterFactory.getStringLikePropertyParam(searchFieldName, contentId,
                                                                               MatchMode.EXACT));
      }
    }
    Collection<Content> contents = SmartContentAPI.getInstance().getContentLoader().search(filter).getResult();
    return contents;
  }

  public static ContentId getContentId(WorkspaceId workspaceId, String stringId) {
    return SmartContentAPI.getInstance().getContentLoader().createContentId(workspaceId,
                                                                            org.apache.commons.codec.binary.StringUtils.
        getBytesUtf8(stringId));
  }

  public Content getReportContent(String id) {
    final ContentLoader contentLoader = SmartContentAPI.getInstance().getContentLoader();
    ContentId contentId = contentLoader.createContentId(workspaceId, org.apache.commons.codec.binary.StringUtils.
        getBytesUtf8(id));
    final Content content = contentLoader.loadContent(contentId);
    return content;
  }
}
