/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.service;

import com.smartitengineering.cms.api.content.Content;
import java.util.Collection;

/**
 *
 * @author imyousuf
 */
public interface ReportService {

  Collection<Content> search(ReportFilter filter);

  Content getReportContent(String contentId);
}
