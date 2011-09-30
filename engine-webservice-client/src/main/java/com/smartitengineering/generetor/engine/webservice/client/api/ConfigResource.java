/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generetor.engine.webservice.client.api;

import com.smartitengineering.generator.engine.webservice.domain.ReportConfig;
import com.smartitengineering.util.rest.client.WritableResource;
import org.apache.abdera.model.Feed;

/**
 *
 * @author saumitra
 */
public interface ConfigResource extends WritableResource<Feed> {

  public void updateConfig();

  public void deleteConfig();

  public ReportConfig getConfig();
}
