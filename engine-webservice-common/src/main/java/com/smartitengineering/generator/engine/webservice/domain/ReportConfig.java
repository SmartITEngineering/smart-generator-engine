/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.webservice.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author saumitra
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ReportConfig {

  private String id;
  private String name;
  private String cronExpression;
  private List<EmailConfig> emailConfig;
  private Map<String, String> params;
  private SourceCode code;

  public String getCronExpression() {
    return cronExpression;
  }

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

  public Map<String, String> getParams() {
    return params;
  }

  public void setParams(Map<String, String> params) {
    this.params = params;
  }

  public SourceCode getCode() {
    return code;
  }

  public void setCode(SourceCode code) {
    this.code = code;
  }
}
