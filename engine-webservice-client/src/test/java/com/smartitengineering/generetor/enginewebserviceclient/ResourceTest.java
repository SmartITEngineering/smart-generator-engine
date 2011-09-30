/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generetor.enginewebserviceclient;

import com.google.inject.AbstractModule;
import com.smartitengineering.dao.hbase.ddl.HBaseTableGenerator;
import com.smartitengineering.dao.hbase.ddl.config.json.ConfigurationJsonParser;
import com.smartitengineering.generator.engine.guice.binder.Initializer;
import com.smartitengineering.generator.engine.webservice.domain.ReportConfig;
import com.smartitengineering.generetor.engine.webservice.client.api.ConfigResource;
import com.smartitengineering.generetor.engine.webservice.client.api.ConfigsResource;
import com.smartitengineering.util.bean.guice.GuiceUtil;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.eclipse.jetty.server.Server;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author saumitra
 */
public class ResourceTest {

  private static Server jettyServer;
  private static final int PORT = 8080;
  private static ReportConfig reportConfig;
  private static ConfigsResource configsResource;
  private static ConfigResource configResource;
  private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

  @BeforeClass
  public static void setUp() throws Exception {
    System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                       "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
    TEST_UTIL.startMiniCluster();
    new HBaseTableGenerator(ConfigurationJsonParser.getConfigurations(ResourceTest.class.getClassLoader().
        getResourceAsStream(
        "com/smartitengineering/engine/impl/schema.json")), TEST_UTIL.getConfiguration(), true).generateTables();
    Properties properties = new Properties();
    properties.setProperty(GuiceUtil.CONTEXT_NAME_PROP,
                           "com.smartitengineering.dao.impl.hbase");
    properties.setProperty(GuiceUtil.IGNORE_MISSING_DEP_PROP, Boolean.TRUE.toString());
    properties.setProperty(GuiceUtil.MODULES_LIST_PROP, ConfigurationModule.class.getName());
    GuiceUtil.getInstance(properties).register();
    Initializer.init();
  }

//  @AfterClass
//  public static void tearDown() throws Exception {
//    TEST_UTIL.shutdownMiniCluster();
//    jettyServer.stop();
//  }
  @Test
  public void testReportConfig() {
    ReportConfig reportConfig1 = new ReportConfig();
    reportConfig1.setId("test1");
    reportConfig1.setName("TestConfig");
    try {
      Thread.sleep(3000);
    }
    catch (Exception ex) {
    }
  }

  public static class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
      bind(Configuration.class).toInstance(TEST_UTIL.getConfiguration());
    }
  }
}
