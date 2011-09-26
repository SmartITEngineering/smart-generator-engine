/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generetor.webserviceclient.domain.Impl;

import com.smartitengineering.generetor.webserviceclient.domain.Api.ReportConfig;
import java.util.Date;
import java.util.List;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author saumitra
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ReportConfigImpl implements ReportConfig {

  private String id;
  private String name;
  private Date validTill;
  private String cronExpression;
  private List<Date> schedules;
  private List<EmailConfig> emailConfig;

  @Override
  public String getCronExpression() {
    return cronExpression;
  }

  @Override
  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  @Override
  public List<EmailConfig> getEmailConfig() {
    return emailConfig;
  }

  @Override
  public void setEmailConfig(List<EmailConfig> emailConfig) {
    this.emailConfig = emailConfig;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public List<Date> getSchedules() {
    return schedules;
  }

  @Override
  public void setSchedules(List<Date> schedules) {
    this.schedules = schedules;
  }

  @Override
  public Date getValidTill() {
    return validTill;
  }

  @Override
  public void setValidTill(Date validTill) {
    this.validTill = validTill;
  }
}
