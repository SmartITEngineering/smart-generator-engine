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
package com.smartitengineering.generator.engine.guice.binder;

import com.smartitengineering.generator.engine.service.factory.Services;
import junit.framework.Assert;
import junit.framework.TestCase;

public class InjectionTest extends TestCase {

  @Override
  protected void setUp() throws Exception {
    Initializer.init();
  }

  public void testApi() {
    Assert.assertNotNull(Services.getInstance());
    Assert.assertNotNull(Services.getInstance().getReportConfigService());
  }
}
