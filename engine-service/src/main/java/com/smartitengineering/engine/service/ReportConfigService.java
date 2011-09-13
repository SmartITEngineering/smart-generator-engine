/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.engine.service;

import com.smartitengineering.engine.domain.PersistentReportConfig;
import java.util.Collection;

/**
 *
 * @author saumitra
 */
public interface ReportConfigService {

  public void save(PersistentReportConfig reportConfig);

  public void delete(PersistentReportConfig reportConfig);

  public void update(PersistentReportConfig reportConfig);

  public PersistentReportConfig getById(long id);

  public Collection<PersistentReportConfig> getAll();
}
