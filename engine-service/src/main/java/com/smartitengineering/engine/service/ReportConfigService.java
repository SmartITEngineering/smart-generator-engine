/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.engine.service;

import com.smartitengineering.engine.domain.ReportConfig;

/**
 *
 * @author saumitra
 */
public interface ReportConfigService {

  public void save(ReportConfig reportConfig);

  public void delete(ReportConfig reportConfig);

  public void update(ReportConfig reportConfig);
}
