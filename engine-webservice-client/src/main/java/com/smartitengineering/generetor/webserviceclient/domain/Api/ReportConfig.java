/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartitengineering.generetor.webserviceclient.domain.Api;

import java.util.Date;
import java.util.List;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author saumitra
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ReportConfig {

  private String id;
  private String name;
  private Date validTill;
  private String cronExpression;
  private List<Date> schedules;
  private List<EmailConfig> emailConfig;

  public String getCronExpression() {
    return cronExpression;
  }

  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  public List<EmailConfig> getEmailConfig() {
    return emailConfig;
  }

  public void setEmailConfig(List<EmailConfig> emailConfig) {
    this.emailConfig = emailConfig;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Date> getSchedules() {
    return schedules;
  }

  public void setSchedules(List<Date> schedules) {
    this.schedules = schedules;
  }

  public Date getValidTill() {
    return validTill;
  }

  public void setValidTill(Date validTill) {
    this.validTill = validTill;
  }

}
