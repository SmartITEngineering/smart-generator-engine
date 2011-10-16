/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generetor.engine.webservice.client.api;

import com.smartitengineering.generator.engine.webservice.domain.ReportConfig;
import com.smartitengineering.util.rest.client.WritableResource;
import org.apache.abdera.model.Feed;
import java.util.Collection;

/**
 *
 * @author saumitra
 */
public interface ConfigsResource extends WritableResource<Feed> {

  public ConfigResource createConfig(ReportConfig reportConfig);

  public Collection<ConfigResource> searchConfig();
}
