/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generetor.webserviceclient.domain.Api;

import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author imyousuf
 */
public class EmailConfig {

  protected List<String> to;
  protected List<String> cc;
  protected List<String> bcc;
  protected String subject;
  protected String representationName;

  public List<String> getTo() {
    return to;
  }

  public void setTo(List<String> to) {
    this.to = to;
  }

  public List<String> getCc() {
    return cc;
  }

  public void setCc(List<String> cc) {
    this.cc = cc;
  }

  public List<String> getBcc() {
    return bcc;
  }

  public void setBcc(List<String> bcc) {
    this.bcc = bcc;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  @JsonIgnore
  public String getRepresentationName() {
    return representationName;
  }

  @JsonProperty
  public void setRepresentationName(String representationName) {
    this.representationName = representationName;
  }
}
