/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.sturtstest.engineserviceimpl;

import com.smartitengineering.engine.domain.PersistentReportConfig;
import java.util.Collection;
/**
 *
 * @author saumitra
 */
public class ReportConfigServiceImpl extends AbstractReportConfigService {
  
  @Override
  public void save(PersistentReportConfig reportConfig) {
    commonWriteDao.save(reportConfig);
  }

  @Override
  public void delete(PersistentReportConfig reportConfig) {
    commonWriteDao.delete(reportConfig);
  }

  @Override
  public void update(PersistentReportConfig reportConfig) {
    commonWriteDao.update(reportConfig);
  }

  @Override
  public Collection<PersistentReportConfig> getAll() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
