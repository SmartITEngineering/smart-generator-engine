/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartitengineering.engine.domain;

import com.smartitengineering.domain.AbstractGenericPersistentDTO;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author saumitra
 */
public class PersistentReportConfig extends AbstractGenericPersistentDTO<PersistentReportConfig, String, Long>{

  private String id;
  private String name;
  private Collection<Date> dates;

  public PersistentReportConfig() {
  }

  public Collection<Date> getDates() {
    return dates;
  }

  public void setDates(Collection<Date> dates) {
    this.dates = dates;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  
  @Override
  public boolean isValid() {
    return true;
  }

}
