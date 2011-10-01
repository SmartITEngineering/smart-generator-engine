/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generetor.engine.webservice.client.api.Impl;

import com.smartitengineering.generator.engine.webservice.providers.JacksonJsonProvider;
import com.smartitengineering.generetor.engine.webservice.client.api.ConfigsResource;
import com.smartitengineering.generetor.engine.webservice.client.api.RootResource;
import com.smartitengineering.util.bean.PropertiesLocator;
import com.smartitengineering.util.rest.atom.AbstractFeedClientResource;
import com.smartitengineering.util.rest.atom.AtomClientUtil;
import com.smartitengineering.util.rest.client.ApplicationWideClientFactoryImpl;
import com.smartitengineering.util.rest.client.ConfigProcessor;
import com.smartitengineering.util.rest.client.ConnectionConfig;
import com.smartitengineering.util.rest.client.Resource;
import com.smartitengineering.util.rest.client.ResourceLink;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.atom.abdera.impl.provider.entity.FeedProvider;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import javax.ws.rs.core.UriBuilder;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author imyousuf
 */
public class RootResourceImpl extends AbstractFeedClientResource<Resource<? extends Feed>> implements RootResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(RootResourceImpl.class);
  private static final int PORT = 10080;
  public static final String REL_CONFIGS = "reportconfigs";
  public static final String REL_REPORTS = "reports";
  private static final ConnectionConfig SMART_CMS_CONNECTION_CONFIG;
  private static final boolean CONNECTION_CONFIGURED;
  private static final URI SMART_CMS_BASE_URI;
  private static final ConfigProcessor CONFIG_PROCESSOR = new EngineClientConfigProcessor();

  static {
    if (LOGGER.isInfoEnabled()) {
      System.setProperty("com.smartitengineering.util.rest.client.ApplicationWideClientFactoryImpl.trace", "true");
    }
    SMART_CMS_CONNECTION_CONFIG = new ConnectionConfig();
    String propFileName = "smart-generator-engine-client-config.properties";
    PropertiesLocator locator = new PropertiesLocator();
    locator.setSmartLocations(propFileName);
    final Properties properties = new Properties();
    try {
      locator.loadProperties(properties);
    }
    catch (IOException ex) {
      LOGGER.warn("Exception!", ex);
    }
    if (!properties.isEmpty()) {
      CONNECTION_CONFIGURED = true;
      SMART_CMS_CONNECTION_CONFIG.setBasicUri(properties.getProperty("baseUri", ""));
      SMART_CMS_CONNECTION_CONFIG.setContextPath(properties.getProperty("contextPath", "/"));
      SMART_CMS_CONNECTION_CONFIG.setHost(properties.getProperty("host", "localhost"));
      SMART_CMS_CONNECTION_CONFIG.setPort(NumberUtils.toInt(properties.getProperty("port", ""), PORT));
      SMART_CMS_BASE_URI = UriBuilder.fromUri(SMART_CMS_CONNECTION_CONFIG.getContextPath()).path(SMART_CMS_CONNECTION_CONFIG.
          getBasicUri()).host(SMART_CMS_CONNECTION_CONFIG.getHost()).port(SMART_CMS_CONNECTION_CONFIG.getPort()).
          scheme("http").build();
    }
    else {
      CONNECTION_CONFIGURED = false;
      SMART_CMS_BASE_URI = null;
    }
  }

  private RootResourceImpl(URI uri) throws IllegalArgumentException,
                                           UniformInterfaceException {
    super(null, CONNECTION_CONFIGURED && uri == null ? SMART_CMS_BASE_URI : uri, false,
          CONNECTION_CONFIGURED ? ApplicationWideClientFactoryImpl.getClientFactory(SMART_CMS_CONNECTION_CONFIG,
                                                                                    CONFIG_PROCESSOR) : null);
    if (logger.isDebugEnabled()) {
      logger.debug("Root resource URI for Smart CMS " + uri);
    }
  }

  public static RootResource getRoot(URI uri) {
    try {
      RootResource resource = new RootResourceImpl(uri);
      return resource;
    }
    catch (RuntimeException ex) {
      LOGGER.error(ex.getMessage(), ex);
      throw ex;
    }
  }

  private static class EngineClientConfigProcessor implements ConfigProcessor {

    public EngineClientConfigProcessor() {
    }

    @Override
    public void process(ClientConfig clientConfig) {
      clientConfig.getClasses().add(JacksonJsonProvider.class);
      clientConfig.getClasses().add(FeedProvider.class);
    }
  }

  @Override
  protected void processClientConfig(ClientConfig clientConfig) {
    CONFIG_PROCESSOR.process(clientConfig);
  }

  @Override
  protected Resource<? extends Feed> instantiatePageableResource(ResourceLink link) {
    return null;
  }

  public ConfigsResource getConfigsResource() {
    Link link = getLastReadStateOfEntity().getLink(REL_CONFIGS);
    ConfigsResource resource = new ConfigsResourceImpl(this, AtomClientUtil.convertFromAtomLinkToResourceLink(link));
    return resource;
  }
}
