/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.sturtstest.engineserviceimpl;

import com.google.inject.Inject;
import com.smartitengineering.engine.domain.PersistentReportConfig;
import java.util.Collection;
/**
 *
 * @author saumitra
 */
public class ReportConfigServiceImpl extends AbstractReportConfigService {

  @Inject

  
  @Override
  public void save(PersistentReportConfig reportConfig) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void delete(PersistentReportConfig reportConfig) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void update(PersistentReportConfig reportConfig) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Collection<PersistentReportConfig> getAll() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
