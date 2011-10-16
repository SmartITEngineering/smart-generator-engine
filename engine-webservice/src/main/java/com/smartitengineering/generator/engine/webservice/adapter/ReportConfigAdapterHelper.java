/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.webservice.adapter;

import com.smartitengineering.generator.engine.domain.Map;
import com.smartitengineering.generator.engine.domain.Map.Entries;
import com.smartitengineering.generator.engine.webservice.domain.ReportConfig;
import com.smartitengineering.generator.engine.domain.ReportConfig.EmailConfig;
import com.smartitengineering.generator.engine.domain.SourceCode;
import com.smartitengineering.generator.engine.domain.SourceCode.Code;
import com.smartitengineering.generator.engine.webservice.domain.SourceCodeType;
import com.smartitengineering.util.bean.adapter.AbstractAdapterHelper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author saumitra
 */
public class ReportConfigAdapterHelper extends AbstractAdapterHelper<ReportConfig, com.smartitengineering.generator.engine.domain.ReportConfig> {

  @Override
  protected com.smartitengineering.generator.engine.domain.ReportConfig newTInstance() {
    return new com.smartitengineering.generator.engine.domain.ReportConfig();
  }

  @Override
  protected void mergeFromF2T(ReportConfig f, com.smartitengineering.generator.engine.domain.ReportConfig t) {
    t.setName(f.getName());
    t.setId(f.getId());
    t.setTrigger(f.getCronExpression());
    List<EmailConfig> emailConfigs = new ArrayList<EmailConfig>();
    for (com.smartitengineering.generator.engine.webservice.domain.EmailConfig config : f.getEmailConfig()) {
      EmailConfig emailConfig = new EmailConfig();
      emailConfig.setBcc(config.getBcc());
      emailConfig.setCc(config.getCc());
      emailConfig.setTo(config.getTo());
      emailConfig.setRepresentationName(config.getRepresentationName());
      emailConfig.setSubject(config.getSubject());
      emailConfigs.add(emailConfig);
    }
    t.setEmailConfig(emailConfigs);
    if (f.getCode() != null && f.getCode().getCodeType() != null && StringUtils.isNotBlank(f.getCode().getCode())) {
      SourceCode sourceCode = new SourceCode();
      t.setEmbeddedSourceCode(sourceCode);
      Code code = new Code();
      sourceCode.setCode(code);
      code.setCodeType(Code.CodeType.valueOf(f.getCode().getCodeType().name()));
      code.setEmbeddedCode(f.getCode().getCode());
    }
    if (f.getParams() != null && !f.getParams().isEmpty()) {
      Map map = new Map();
      List<Entries> entries = new ArrayList<Entries>();
      map.setEntries(entries);
      for (java.util.Map.Entry<String, String> entry : f.getParams().entrySet()) {
        Entries tEntries = new Entries();
        tEntries.setKey(entry.getKey());
        tEntries.setValue(entry.getValue());
        entries.add(tEntries);
      }
    }
  }

  @Override
  protected ReportConfig convertFromT2F(com.smartitengineering.generator.engine.domain.ReportConfig t) {
    ReportConfig reportConfig = new ReportConfig();
    reportConfig.setId(t.getId());
    reportConfig.setName(t.getName());
    reportConfig.setCronExpression(t.getTrigger());
    List<com.smartitengineering.generator.engine.webservice.domain.EmailConfig> emailConfigs =
                                                                                new ArrayList<com.smartitengineering.generator.engine.webservice.domain.EmailConfig>();
    for (EmailConfig emailConfig : t.getEmailConfig()) {
      com.smartitengineering.generator.engine.webservice.domain.EmailConfig config =
                                                                            new com.smartitengineering.generator.engine.webservice.domain.EmailConfig();
      config.setBcc(new ArrayList<String>(emailConfig.getBcc()));
      config.setCc(new ArrayList<String>(emailConfig.getCc()));
      config.setTo(new ArrayList<String>(emailConfig.getTo()));
      config.setSubject(emailConfig.getSubject());
      config.setRepresentationName(emailConfig.getRepresentationName());
      emailConfigs.add(config);
    }
    if (t.getParams() != null && t.getParams().getEntries() != null && !t.getParams().getEntries().isEmpty()) {
      java.util.Map<String, String> map = new LinkedHashMap<String, String>();
      for (Entries entry : t.getParams().getEntries()) {
        map.put(entry.getKey(), entry.getValue());
      }
      reportConfig.setParams(map);
    }
    if (t.getEmbeddedSourceCode() != null) {
      com.smartitengineering.generator.engine.webservice.domain.SourceCode code = getSourceCode(
          t.getEmbeddedSourceCode());
      reportConfig.setCode(code);
    }
    else if (t.getCodeOnDemand() != null) {
      com.smartitengineering.generator.engine.webservice.domain.SourceCode code = getSourceCode(t.getCodeOnDemand().
          getCode());
      reportConfig.setCode(code);
    }
    reportConfig.setEmailConfig(emailConfigs);
    return reportConfig;
  }

  private com.smartitengineering.generator.engine.webservice.domain.SourceCode getSourceCode(
      SourceCode embeddedSourceCode) {
    if (embeddedSourceCode == null || embeddedSourceCode.getCode().getCodeType() == null || embeddedSourceCode.getCode().
        getEmbeddedCode() == null) {
      return null;
    }
    com.smartitengineering.generator.engine.webservice.domain.SourceCode sourceCode =
                                                                         new com.smartitengineering.generator.engine.webservice.domain.SourceCode();
    sourceCode.setCode(embeddedSourceCode.getCode().getEmbeddedCode());
    sourceCode.setCodeType(SourceCodeType.valueOf(embeddedSourceCode.getCode().getCodeType().name()));
    return sourceCode;
  }
}
