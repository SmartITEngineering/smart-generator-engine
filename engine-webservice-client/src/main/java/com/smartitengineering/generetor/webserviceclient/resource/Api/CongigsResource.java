/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generetor.webserviceclient.resource.Api;

import com.smartitengineering.util.rest.client.WritableResource;
import org.apache.abdera.model.Feed;
import java.net.URI;
import java.util.Collection;

/**
 *
 * @author saumitra
 */
public interface CongigsResource extends WritableResource<Feed> {

  public void createConfig(URI configUri);

  public Collection<ConfigResource> searchConfig();
}
