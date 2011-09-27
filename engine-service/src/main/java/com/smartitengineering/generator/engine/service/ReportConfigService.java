/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.service;

import com.smartitengineering.cms.api.content.Content;
import com.smartitengineering.generator.engine.domain.ReportConfig;
import java.util.Collection;

/**
 *
 * @author saumitra
 */
public interface ReportConfigService {

  public void save(ReportConfig reportConfig);

  public void delete(ReportConfig reportConfig);

  public void update(ReportConfig reportConfig);

  public ReportConfig getById(String id);

  public Collection<ReportConfig> searchConfigs(ReportConfigFilter filter);

  public Collection<Content> getReport (String configId);
}
