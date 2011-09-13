/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartitengineering.sturtstest.engineserviceimpl;

import com.google.inject.Inject;
import com.smartitengineering.common.dao.search.CommonFreeTextSearchDao;
import com.smartitengineering.dao.common.CommonReadDao;
import com.smartitengineering.dao.common.CommonWriteDao;
import com.smartitengineering.engine.domain.PersistentReportConfig;
import com.smartitengineering.engine.service.ReportConfigService;

/**
 *
 * @author saumitra
 */
public abstract class AbstractReportConfigService implements ReportConfigService{

  @Inject
  protected CommonReadDao<PersistentReportConfig , Long> commonReadDao;
  @Inject
  protected CommonWriteDao<PersistentReportConfig> commonWriteDao;
  @Inject
  protected CommonFreeTextSearchDao<PersistentReportConfig> freeTextSearchDao;

  @Override
  public PersistentReportConfig getById(long id) {
    if (id == 0) {
      return null;
    }
    return commonReadDao.getById(id);
  }

}
