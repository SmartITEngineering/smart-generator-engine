/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.service;

import java.util.Date;

/**
 *
 * @author saumitra
 */
public class ReportFilter extends AbstractFilter {

  private String nameLike;
  private String configId;
  private Date executionStartDate;
  private Date executionEndDate;

  public String getConfigId() {
    return configId;
  }

  public void setConfigId(String configId) {
    this.configId = configId;
  }

  public Date getExecutionEndDate() {
    return executionEndDate;
  }

  public void setExecutionEndDate(Date executionEndDate) {
    this.executionEndDate = executionEndDate;
  }

  public Date getExecutionStartDate() {
    return executionStartDate;
  }

  public void setExecutionStartDate(Date executionStartDate) {
    this.executionStartDate = executionStartDate;
  }

  public String getNameLike() {
    return nameLike;
  }

  public void setNameLike(String nameLike) {
    this.nameLike = nameLike;
  }
}
