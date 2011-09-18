package com.smartitengineering.generator.engine.webservice;

import com.smartitengineering.generator.engine.webservice.domain.ReportConfig;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.codehaus.jackson.map.ObjectMapper;

public class AppTest extends TestCase {

  public void testApp() throws Exception {
    final long fixedDate = 1316337350925l;
    ReportConfig config = new ReportConfig();
    config.setCronExpression("test");
    config.setId("id");
    config.setName("testName");
    config.setSchedules(Arrays.asList(new Date(fixedDate), new Date(fixedDate + 1)));
    config.setValidTill(new Date(fixedDate + 2));
    ObjectMapper mapper = new ObjectMapper();
    StringWriter writer = new StringWriter();
    mapper.writeValue(writer, config);
    final String expectedOut =
                 "{\"name\":\"testName\",\"id\":\"id\",\"emailConfig\":[],\"schedules\":[1316337350925,1316337350926],\"validTill\":1316337350927}";
    Assert.assertEquals(mapper.readTree(expectedOut), mapper.readTree(writer.toString()));
    final String in =
                 "{\"name\":\"testName\",\"id\":\"id\",\"emailConfig\":[],\"validTill\":1316337350927,\"cronExpression\":\"inputExpression\"}";
    ReportConfig inConfig = mapper.readValue(in, ReportConfig.class);
    Assert.assertEquals("inputExpression", inConfig.getCronExpression());
  }
}
