/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generetor.engine.webservice.client.api.Impl;

import com.smartitengineering.generator.engine.webservice.domain.ReportConfig;
import com.smartitengineering.generetor.engine.webservice.client.api.ConfigResource;
import com.smartitengineering.util.rest.atom.AbstractFeedClientResource;
import com.smartitengineering.util.rest.client.Resource;
import com.smartitengineering.util.rest.client.ResourceLink;
import com.smartitengineering.util.rest.client.SimpleResourceImpl;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.config.ClientConfig;
import javax.ws.rs.core.MediaType;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;

/**
 *
 * @author saumitra
 */
public class ConfigResourceImpl extends AbstractFeedClientResource<Resource<? extends Feed>> implements ConfigResource {

  public ConfigResourceImpl(Resource referrer, ResourceLink resouceLink) throws IllegalArgumentException,
                                                                                UniformInterfaceException {
    super(referrer, resouceLink);
    final ResourceLink altLink = getRelatedResourceUris().getFirst(Link.REL_ALTERNATE);
    addNestedResource("config",
                      new SimpleResourceImpl<ReportConfig>(this, altLink.getUri(), altLink.getMimeType(),
                                                               ReportConfig.class, null,
                                                               false,
                                                               null,
                                                               null));
  }

  @Override
  protected void processClientConfig(ClientConfig cc) {
  }

  @Override
  protected Resource<? extends Feed> instantiatePageableResource(ResourceLink rl) {
    return null;
  }

  @Override
  public void updateConfig() {
    put(MediaType.APPLICATION_JSON, getConfig(), ClientResponse.Status.OK, ClientResponse.Status.SEE_OTHER,
        ClientResponse.Status.FOUND);
  }

  @Override
  public void deleteConfig() {
    delete(ClientResponse.Status.OK);
  }

  @Override
  public ReportConfig getConfig() {
    return getConfig(false);

  }

  public ReportConfig getConfig(boolean reload) {
    Resource<ReportConfig> config = super.<ReportConfig>getNestedResource("config");
    if (reload) {
      return config.get();
    }
    else {
      return config.getLastReadStateOfEntity();
    }
  }
}
