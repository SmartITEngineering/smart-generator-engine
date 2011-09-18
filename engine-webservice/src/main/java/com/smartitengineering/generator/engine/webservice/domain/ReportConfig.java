/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.webservice.domain;

import java.util.Collections;
import java.util.List;
import java.util.Date;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
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

  @JsonIgnore
  public String getCronExpression() {
    return cronExpression;
  }

  @JsonProperty
  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<EmailConfig> getEmailConfig() {
    if (emailConfig == null) {
      return Collections.emptyList();
    }
    return emailConfig;
  }

  public void setEmailConfig(List<EmailConfig> emailConfig) {
    this.emailConfig = emailConfig;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty
  public List<Date> getSchedules() {
    if (schedules == null) {
      return Collections.emptyList();
    }
    return schedules;
  }

  @JsonIgnore
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
