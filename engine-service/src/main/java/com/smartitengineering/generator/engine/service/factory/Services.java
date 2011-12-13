/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.service.factory;

import com.smartitengineering.generator.engine.service.ReportConfigService;
import com.smartitengineering.generator.engine.service.ReportService;
import com.smartitengineering.util.bean.BeanFactoryRegistrar;
import com.smartitengineering.util.bean.annotations.Aggregator;
import com.smartitengineering.util.bean.annotations.InjectableField;
import java.util.concurrent.Semaphore;

/**
 *
 * @author saumitra
 */
@Aggregator(contextName = Services.CONTEXT_NAME)
public class Services {

  public static final String CONTEXT_NAME = "com.smartitengineering.generator.engine.service";
  private static final Semaphore semaphore = new Semaphore(1);
  @InjectableField
  private ReportConfigService reportConfigService;
  @InjectableField
  private ReportService reportService;

  private Services() {
  }

  public ReportConfigService getReportConfigService() {
    return reportConfigService;
  }

  public void setReportConfigService(ReportConfigService reportConfigService) {
    this.reportConfigService = reportConfigService;
  }

  public ReportService getReportService() {
    return reportService;
  }

  public void setReportService(ReportService reportService) {
    this.reportService = reportService;
  }
  private static Services services;

  public static Services getInstance() {
    if (services == null) {
      try {
        semaphore.acquire();
      }
      catch (Exception ex) {
        throw new IllegalStateException(ex);
      }
      try {
        initServices();
      }
      finally {
        semaphore.release();
      }
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
