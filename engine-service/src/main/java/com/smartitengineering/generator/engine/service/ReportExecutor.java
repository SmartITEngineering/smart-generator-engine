/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.service;

import com.smartitengineering.cms.api.content.Content;
import java.util.Map;

/**
 *
 * @author imyousuf
 */
public interface ReportExecutor {

  public Content createReport(Map<String, String> params);
}
