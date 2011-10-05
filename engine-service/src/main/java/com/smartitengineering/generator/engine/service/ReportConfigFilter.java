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

  public String getNameLike() {
    return nameLike;
  }

  public void setNameLike(String nameLike) {
    this.nameLike = nameLike;
  }
}
