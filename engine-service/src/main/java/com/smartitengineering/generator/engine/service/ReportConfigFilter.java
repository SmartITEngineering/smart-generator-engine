/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.service;

import java.util.Date;

/**
 *
 * @author imyousuf
 */
public class ReportConfigFilter extends AbstractFilter {

  private String nameLike;
  private Date scheduleRangeStart;
  private Date scheduleRangeEnd;

  public String getNameLike() {
    return nameLike;
  }

  public void setNameLike(String nameLike) {
    this.nameLike = nameLike;
  }

  public Date getScheduleRangeEnd() {
    return scheduleRangeEnd;
  }

  public void setScheduleRangeEnd(Date scheduleRangeEnd) {
    this.scheduleRangeEnd = scheduleRangeEnd;
  }

  public Date getScheduleRangeStart() {
    return scheduleRangeStart;
  }

  public void setScheduleRangeStart(Date scheduleRangeStart) {
    this.scheduleRangeStart = scheduleRangeStart;
  }
}
