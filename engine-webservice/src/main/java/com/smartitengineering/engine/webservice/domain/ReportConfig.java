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

  String id;
  private String name;
  private Date validTill;
  private Collection<Date> schedules;
  private Collection<com.smartitengineering.generator.engine.domain.ReportConfig.EmailConfig> emailConfig;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Collection<EmailConfig> getEmailConfig() {
    return emailConfig;
  }

  public void setEmailConfig(Collection<EmailConfig> emailConfig) {
    this.emailConfig = emailConfig;
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
