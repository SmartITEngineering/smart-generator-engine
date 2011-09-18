/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartitengineering.engine.webservice.domain;

import java.util.Collection;
import java.util.Date;

/**
 *
 * @author saumitra
 */
public class ReportConfig {
  private String name ;
  private Date validTill;
  private Collection<Date> schedules;
}
