/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartitengineering.generator.engine.webservice.adapter;

import com.smartitengineering.engine.domain.PersistentReportConfig;
import com.smartitengineering.engine.domain.ReportConfig;
import com.smartitengineering.util.bean.adapter.AbstractAdapterHelper;

/**
 *
 * @author saumitra
 */
public class ReportConfigAdapterHelper extends AbstractAdapterHelper<ReportConfig, PersistentReportConfig>{

  @Override
  protected PersistentReportConfig newTInstance() {
    return new PersistentReportConfig();
  }

  @Override
  protected void mergeFromF2T(ReportConfig f, PersistentReportConfig t) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected ReportConfig convertFromT2F(PersistentReportConfig t) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
