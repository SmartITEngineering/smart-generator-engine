/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartitengineering.generetor.generatorguicebinder;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.smartitengineering.engine.service.ReportConfigService;
import com.smartitengineering.sturtstest.engineserviceimpl.ReportConfigServiceImpl;

/**
 *
 * @author saumitra
 */
public class APIModule extends AbstractModule{

  @Override
  protected void configure() {
    bind(ReportConfigService.class).annotatedWith(Names.named("apiReportConfigService")).to(ReportConfigServiceImpl.class);
  }

}
