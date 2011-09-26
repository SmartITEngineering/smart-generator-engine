/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generetor.webserviceclient.domain.Api;

import com.smartitengineering.generetor.webserviceclient.domain.Impl.EmailConfig;
import java.util.Date;
import java.util.List;

/**
 *
 * @author saumitra
 */
public interface ReportConfig {

  public String getCronExpression();

  public void setCronExpression(String cronExpression);

  public List<EmailConfig> getEmailConfig();

  public void setEmailConfig(List<EmailConfig> emailConfig);

  public String getId();

  public void setId(String id);

  public String getName();

  public void setName(String name);

  public List<Date> getSchedules();

  public void setSchedules(List<Date> schedules);

  public Date getValidTill();

  public void setValidTill(Date validTill);
}
