/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.engine.service.factory;

import com.smartitengineering.engine.service.ReportConfigService;
import com.smartitengineering.util.bean.BeanFactoryRegistrar;
import com.smartitengineering.util.bean.annotations.Aggregator;
import com.smartitengineering.util.bean.annotations.InjectableField;

/**
 *
 * @author saumitra
 */
@Aggregator(contextName = Services.CONTEXT_NAME)
public class Services {

  public static final String CONTEXT_NAME = "";

  @InjectableField(beanName = "apiReportConfigService")
  private ReportConfigService reportConfigService;

  private Services() {
  }

  public ReportConfigService getReportConfigService() {
    return reportConfigService;
  }

  public void setReportConfigService(ReportConfigService reportConfigService) {
    this.reportConfigService = reportConfigService;
  }
  private static Services services;

  public static Services getInstance() {
    if (services == null) {
      initServices();
    }
    return services;
  }

  private synchronized static void initServices() {
    if (services == null) {
      services = new Services();
      BeanFactoryRegistrar.aggregate(services);
    }
  }
}
