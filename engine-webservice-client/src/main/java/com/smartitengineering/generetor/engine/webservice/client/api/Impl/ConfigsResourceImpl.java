/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generetor.engine.webservice.client.api.Impl;

import com.smartitengineering.generator.engine.webservice.domain.ReportConfig;
import com.smartitengineering.generetor.engine.webservice.client.api.ConfigResource;
import com.smartitengineering.generetor.engine.webservice.client.api.ConfigsResource;
import com.smartitengineering.util.rest.atom.AbstractFeedClientResource;
import com.smartitengineering.util.rest.client.ClientUtil;
import com.smartitengineering.util.rest.client.Resource;
import com.smartitengineering.util.rest.client.ResourceLink;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.config.ClientConfig;
import java.util.Collection;
import javax.ws.rs.core.MediaType;
import org.apache.abdera.model.Feed;

/**
 *
 * @author saumitra
 */
public class ConfigsResourceImpl extends AbstractFeedClientResource<Resource<? extends Feed>> implements ConfigsResource {

  public ConfigsResourceImpl(Resource referrer, ResourceLink resouceLink) throws IllegalArgumentException,
                                                                                 UniformInterfaceException {
    super(referrer, resouceLink);
  }

  @Override
  protected void processClientConfig(ClientConfig cc) {
  }

  @Override
  protected Resource<? extends Feed> instantiatePageableResource(ResourceLink rl) {
    return new ConfigsResourceImpl(this, rl);
  }

  @Override
  public ConfigsResource createConfig(ReportConfig reportConfig) {
    ClientResponse response = post(MediaType.APPLICATION_JSON, reportConfig, ClientResponse.Status.CREATED);
    if (response.getLocation() == null) {
      logger.info("response.getLocation is null for reportconfig resource");
    }
    final ResourceLink configLink = ClientUtil.createResourceLink("config", response.getLocation(),
                                                                  MediaType.APPLICATION_ATOM_XML);
    return new ConfigsResourceImpl(this, configLink);
  }

  @Override
  public Collection<ConfigResource> searchConfig() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
