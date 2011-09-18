/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartitengineering.engine.webservice.adapter;

import com.smartitengineering.engine.domain.PersistentReportConfig;
import com.smartitengineering.engine.domain.ReportConfig;
import com.smartitengineering.util.bean.adapter.AbstractAdapterHelper;

/**
 *
 * @author saumitra
 */
public class ReportConfigAdapterHelper extends AbstractAdapterHelper<com.smartitengineering.engine.webservice.domain.ReportConfig, ReportConfig>{

  @Override
  protected ReportConfig newTInstance() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected void mergeFromF2T(com.smartitengineering.engine.webservice.domain.ReportConfig f, ReportConfig t) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected com.smartitengineering.engine.webservice.domain.ReportConfig convertFromT2F(ReportConfig t) {
    throw new UnsupportedOperationException("Not supported yet.");
  }


}
