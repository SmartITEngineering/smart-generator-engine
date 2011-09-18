/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartitengineering.generator.engine.webservice.adapter;

import com.smartitengineering.engine.webservice.domain.ReportConfig;
import com.smartitengineering.generator.engine.domain.ReportConfig.EmailConfig;
import com.smartitengineering.util.bean.adapter.AbstractAdapterHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author saumitra
 */
public class ReportConfigAdapterHelper extends AbstractAdapterHelper<ReportConfig, com.smartitengineering.generator.engine.domain.ReportConfig>{

  @Override
  protected com.smartitengineering.generator.engine.domain.ReportConfig newTInstance() {
    return new com.smartitengineering.generator.engine.domain.ReportConfig();
  }

  @Override
  protected void mergeFromF2T(ReportConfig f, com.smartitengineering.generator.engine.domain.ReportConfig t) {
    t.setName(f.getName());
    t.setId(f.getId());
    t.setValidTill(f.getValidTill());
    List<Date> schedulers = new ArrayList<Date>();
    for (Date date : f.getSchedules()){
      schedulers.add(date);
    }
    t.setSchedules(schedulers);
    List<EmailConfig> emailConfigs = new ArrayList<EmailConfig>();
    for (EmailConfig emailConfig : t.getEmailConfig()){
      emailConfigs.add(emailConfig);
    }
    t.setEmailConfig(emailConfigs);

  }

  @Override
  protected ReportConfig convertFromT2F(com.smartitengineering.generator.engine.domain.ReportConfig t) {
    ReportConfig reportConfig = new ReportConfig();
    reportConfig.setId(t.getId());
    reportConfig.setName(t.getName());
    reportConfig.setValidTill(t.getValidTill());
    List<Date> schedulers = new ArrayList<Date>();
    for (Date date : t.getSchedules()){
      schedulers.add(date);
    }
    reportConfig.setSchedules(schedulers);
    List<EmailConfig> emailConfigs = new ArrayList<EmailConfig>();
    for (EmailConfig emailConfig : t.getEmailConfig()){
      emailConfigs.add(emailConfig);
    }
    reportConfig.setEmailConfig(emailConfigs);
    return reportConfig;
  }

}
