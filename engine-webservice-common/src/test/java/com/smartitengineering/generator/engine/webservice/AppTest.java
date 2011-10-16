package com.smartitengineering.generator.engine.webservice;

import com.smartitengineering.generator.engine.webservice.domain.ReportConfig;
import com.smartitengineering.generator.engine.webservice.domain.SourceCode;
import com.smartitengineering.generator.engine.webservice.domain.SourceCodeType;
import java.io.StringWriter;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.codehaus.jackson.map.ObjectMapper;

public class AppTest extends TestCase {

  public void testApp() throws Exception {
    ReportConfig config = new ReportConfig();
    config.setCronExpression("test");
    config.setId("id");
    config.setName("testName");
    SourceCode code = new SourceCode();
    code.setCodeType(SourceCodeType.GROOVY);
    code.setCode("SomeCode");
    config.setCode(code);
    ObjectMapper mapper = new ObjectMapper();
    StringWriter writer = new StringWriter();
    mapper.writeValue(writer, config);
    final String expectedOut =
                 "{\"name\":\"testName\",\"id\":\"id\",\"cronExpression\":\"test\",\"emailConfig\":[],\"code\":{\"code\":\"SomeCode\",\"codeType\":\"GROOVY\"}}";
    Assert.assertEquals(mapper.readTree(expectedOut), mapper.readTree(writer.toString()));
    final String in =
                 "{\"name\":\"testName\",\"id\":\"id\",\"emailConfig\":[],\"cronExpression\":\"inputExpression\",\"code\":{\"code\":\"SomeCode\",\"codeType\":\"GROOVY\"}}";
    ReportConfig inConfig = mapper.readValue(in, ReportConfig.class);
    Assert.assertEquals("inputExpression", inConfig.getCronExpression());
    Assert.assertEquals("SomeCode", inConfig.getCode().getCode());
    Assert.assertEquals(SourceCodeType.GROOVY, inConfig.getCode().getCodeType());
  }
}
