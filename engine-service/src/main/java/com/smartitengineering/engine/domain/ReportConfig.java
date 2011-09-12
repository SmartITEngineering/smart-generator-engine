/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.engine.domain;

import java.util.Collection;
import java.util.Date;

/**
 *
 * @author saumitra
 */
public class ReportConfig {

  String name;
  Collection<Date> dates;

  public ReportConfig() {
  }

  public ReportConfig(String name,
                      Collection<Date> dates) {
    this.name = name;
    this.dates = dates;
  }

  public Collection<Date> getDates() {
    return dates;
  }

  public void setDates(Collection<Date> dates) {
    this.dates = dates;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
