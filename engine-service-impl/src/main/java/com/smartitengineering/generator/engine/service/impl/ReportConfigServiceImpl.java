/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.service.impl;

import com.google.inject.Inject;
import com.smartitengineering.cms.api.content.Content;
import com.smartitengineering.cms.api.content.Filter;
import com.smartitengineering.cms.api.factory.SmartContentAPI;
import com.smartitengineering.cms.api.workspace.WorkspaceId;
import com.smartitengineering.dao.common.CommonReadDao;
import com.smartitengineering.dao.common.CommonWriteDao;
import com.smartitengineering.dao.common.queryparam.MatchMode;
import com.smartitengineering.dao.common.queryparam.QueryParameter;
import com.smartitengineering.dao.common.queryparam.QueryParameterFactory;
import com.smartitengineering.generator.engine.domain.ReportConfig;
import com.smartitengineering.generator.engine.service.ReportConfigFilter;
import com.smartitengineering.generator.engine.service.ReportConfigService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author saumitra
 */
public class ReportConfigServiceImpl implements ReportConfigService {

  @Inject
  protected CommonReadDao<ReportConfig, String> commonReadDao;
  @Inject
  protected CommonWriteDao<ReportConfig> commonWriteDao;

  @Override
  public ReportConfig getById(String id) {
    if (StringUtils.isBlank(id)) {
      return null;
    }
    return commonReadDao.getById(id);
  }

  @Override
  public void save(ReportConfig reportConfig) {
    commonWriteDao.save(reportConfig);
  }

  @Override
  public void delete(ReportConfig reportConfig) {
    commonWriteDao.delete(reportConfig);
  }

  @Override
  public void update(ReportConfig reportConfig) {
    commonWriteDao.update(reportConfig);
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
    if (filter.getScheduleRangeStart() != null && filter.getScheduleRangeEnd() == null) {
      queries.add(QueryParameterFactory.getGreaterThanEqualToPropertyParam(ReportConfig.PROPERTY_SCHEDULES, filter.
          getScheduleRangeStart()));
    }
    if (filter.getScheduleRangeStart() == null && filter.getScheduleRangeEnd() != null) {
      queries.add(QueryParameterFactory.getLesserThanEqualToPropertyParam(ReportConfig.PROPERTY_SCHEDULES, filter.
          getScheduleRangeEnd()));
    }
    if (filter.getScheduleRangeStart() != null && filter.getScheduleRangeEnd() != null) {
      queries.add(QueryParameterFactory.getBetweenPropertyParam(ReportConfig.PROPERTY_SCHEDULES, filter.
          getScheduleRangeStart(), filter.getScheduleRangeEnd()));
    }
    return commonReadDao.getList(queries);
  }
}
