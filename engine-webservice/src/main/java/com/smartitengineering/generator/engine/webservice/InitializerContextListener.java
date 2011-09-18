/*
 *
 * This is a simple Content Management System (CMS)
 * Copyright (C) 2010  Imran M Yousuf (imyousuf@smartitengineering.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.smartitengineering.generator.engine.webservice;

import com.smartitengineering.generator.engine.guice.binder.Initializer;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author imyousuf
 */
public class InitializerContextListener implements ServletContextListener {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    // Create workspace and upload content types if necessary to CMS using its API
    logger.info("DI Smart Generator Engine and Smart CMS");
    Initializer.init();
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
  }
}
