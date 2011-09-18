/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.engine.webservice.domain;

import com.smartitengineering.generator.engine.domain.ReportConfig.EmailConfig;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author saumitra
 */
public class ReportConfig {

  private String name;
  private Date validTill;
  private Collection<Date> schedules;
  private Collection<com.smartitengineering.generator.engine.domain.ReportConfig.EmailConfig> emailConfigs;

  public Collection<EmailConfig> getEmailConfigs() {
    return emailConfigs;
  }

  public void setEmailConfigs(Collection<EmailConfig> emailConfigs) {
    this.emailConfigs = emailConfigs;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Collection<Date> getSchedules() {
    return schedules;
  }

  public void setSchedules(Collection<Date> schedules) {
    this.schedules = schedules;
  }

  public Date getValidTill() {
    return validTill;
  }

  public void setValidTill(Date validTill) {
    this.validTill = validTill;
  }
}
