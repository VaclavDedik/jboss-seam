/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */ 
package org.jboss.seam.example.common.test.selenium;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * This class slightly enhaces a Selenium API for controlling a browser.
 * @author Jozef Hartinger
 *
 */
public class SeamSelenium extends DefaultSelenium
{
   
   protected String timeout = "30000";

   public SeamSelenium(String serverHost, int serverPort, String browserStartCommand, String browserURL)
   {
      super(serverHost, serverPort, browserStartCommand, browserURL);
   }
   
   @Override
   public void setTimeout(String timeout) {
      super.setTimeout(timeout);
      this.timeout = timeout;
   }

   /**
    * Same as click method but waits for page to load after clicking. Default timeout can be changed by setTimeout() method.
    * @param locator
    */
   public void clickAndWait(String locator) {
      super.click(locator);
      super.waitForPageToLoad(timeout);
   }
   
   /**
    * Simulates a user pressing "back" button and waits for page to load. Default timeout can be changed by setTimeout() method.
    */
   public void goBackAndWait() {
      super.goBack();
      super.waitForPageToLoad(timeout);
   }
   
   /**
    * Simulates a user pressing "refresh" button and waits for page to load. Default timeout can be changed by setTimeout() method.
    */
   public void refreshAndWait() {
      super.refresh();
      super.waitForPageToLoad(timeout);
   }
   
}
