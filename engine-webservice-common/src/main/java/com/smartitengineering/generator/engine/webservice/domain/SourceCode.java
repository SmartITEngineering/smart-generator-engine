/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.webservice.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author imyousuf
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SourceCode {

  private SourceCodeType codeType;
  private String code;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public SourceCodeType getCodeType() {
    return codeType;
  }

  public void setCodeType(SourceCodeType codeType) {
    this.codeType = codeType;
  }
}
