/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartitengineering.generator.engine.guice.binder;

import com.google.inject.AbstractModule;
import com.smartitengineering.generator.engine.service.ReportConfigService;
import com.smartitengineering.generator.engine.service.impl.ReportConfigServiceImpl;

/**
 *
 * @author saumitra
 */
public class APIModule extends AbstractModule{

  @Override
  protected void configure() {
    bind(ReportConfigService.class).to(ReportConfigServiceImpl.class);
  }

}
