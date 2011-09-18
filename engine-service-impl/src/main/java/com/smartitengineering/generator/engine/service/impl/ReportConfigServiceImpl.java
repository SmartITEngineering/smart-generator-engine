/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.service.impl;

import com.google.inject.Inject;
import com.smartitengineering.dao.common.CommonReadDao;
import com.smartitengineering.dao.common.CommonWriteDao;
import com.smartitengineering.generator.engine.domain.ReportConfig;
import com.smartitengineering.generator.engine.service.ReportConfigService;
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
}
