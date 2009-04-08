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
package org.jboss.seam.test.functional.seamgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.jboss.seam.test.functional.seamgen.utils.SeamGenAdapter;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Base class for seam-gen functional tests.
 * @author Jozef Hartinger
 */
public class SeamGenTest
{

   protected static SeamGenAdapter seamGen;
   protected static Properties seamGenProperties;
   protected static Properties ftestProperties = new Properties();

   protected static String SEAM_DIR;
   protected static String SEAM_FTEST_PROPERTIES_FILE;
   protected static String SEAMGEN_BUILDFILE;
   protected static String SEAMGEN_PROPERTIES_FILE;
   protected static String WORKSPACE;

   // container specific properties
   protected static String CONTAINER;
   protected static String CONTAINER_LOCATION;
   protected static int DEPLOY_TIMEOUT;

   protected static boolean ICEFACES;
   protected static boolean WAR;

   protected static boolean DELETE_PROJECT;

   protected static String TEST_SEAMGEN_PROPERTIES_FILE;

   // Selenium related constants
   protected static String SELENIUM_HOST;
   protected static String SELENIUM_BROWSER;
   protected static String SELENIUM_BROWSER_URL;
   protected static int SELENIUM_SERVER_PORT;
   protected static String SELENIUM_SPEED;
   protected static String SELENIUM_TIMEOUT;
   protected static long SELENIUM_ICEFACES_WAIT_TIME;

   protected static String OUTPUT_DIR;

   // Test application specific constants
   protected static String APP_NAME;
   protected static String HOME_PAGE;

   // Selenium server instance
   protected static SeleniumServer seleniumServer;

   @BeforeSuite
   @Parameters("seam.dir")
   public void beforeSuite(@Optional(".") String seamDir) throws Exception {
      // Seam location
      SEAM_DIR = seamDir;
      SEAM_FTEST_PROPERTIES_FILE = SEAM_DIR + "/src/test/ftest/ftest.properties";
      SEAMGEN_BUILDFILE = SEAM_DIR + "/seam-gen/build.xml";
      SEAMGEN_PROPERTIES_FILE = SEAM_DIR + "/seam-gen/build.properties";
      OUTPUT_DIR =  SEAM_DIR + "/test-output/functional-framework/";
      
      loadFtestProperties();
      createOutputDir();
      startSeleniumServer();
   }
   
   @AfterSuite
   public void afterSuite() {
      seleniumServer.stop();
   }
   
   @BeforeTest
   @Parameters( { "icefaces", "type", "suffix", "explode" })
   public void setUp(@Optional("false") boolean icefaces, @Optional("ear") String type, @Optional("") String suffix, @Optional("true") boolean explode) throws Exception
   {
      ICEFACES = icefaces;
      WAR = type.equalsIgnoreCase("war");
      APP_NAME = "seamGenTestApp" + (ICEFACES ? "Ice" : "Rich") + (WAR ? "War" : "Ear") + (explode? "E" : "D") + suffix;
      HOME_PAGE = "/" + APP_NAME + "/home.seam";

      setSeamGenProperties();

      seamGen = new SeamGenAdapter(SEAMGEN_BUILDFILE);
      seamGen.setExplode(explode);

   }

   @AfterTest
   public void tearDown()
   {
      if (DELETE_PROJECT)
      {
         seamGen.deleteProject();
      }
      else
      {
         seamGen.undeploy();
      }
   }

   private void loadFtestProperties() throws FileNotFoundException, IOException
   {
      // load general properties
      ftestProperties.load(new FileInputStream(SEAM_FTEST_PROPERTIES_FILE));

      WORKSPACE = ftestProperties.getProperty("workspace.home");

      // container specific
      CONTAINER = ftestProperties.getProperty("container", "jboss5");
      CONTAINER_LOCATION = ftestProperties.getProperty(CONTAINER + ".home");
      DEPLOY_TIMEOUT = Integer.parseInt(ftestProperties.getProperty(CONTAINER + ".deploy.waittime")) * 1000; // miliseconds
      DELETE_PROJECT = Boolean.valueOf(ftestProperties.getProperty("seamgen.delete.project", "false"));

      // load selenium constants
      SELENIUM_HOST = ftestProperties.getProperty("selenium.host");
      SELENIUM_BROWSER = ftestProperties.getProperty("selenium.browser");
      SELENIUM_BROWSER_URL = ftestProperties.getProperty("selenium.browser.url");
      SELENIUM_SERVER_PORT = Integer.parseInt(ftestProperties.getProperty("selenium.server.port"));
      SELENIUM_SPEED = ftestProperties.getProperty("selenium.speed");
      SELENIUM_TIMEOUT = ftestProperties.getProperty("selenium.timeout");
      SELENIUM_ICEFACES_WAIT_TIME = Long.valueOf(ftestProperties.getProperty("selenium.icefaces.wait.time", "2000"));
   }

   private void setSeamGenProperties()
   {
      seamGenProperties = new Properties();
      
      String[] propertiesToCopy = { "database.type", "database.exists", "database.drop", "driver.jar", "driver.license.jar", "hibernate.connection.username", "hibernate.connection.password", "hibernate.connection.driver_class", "hibernate.connection.dataSource_class", "hibernate.cache.provider_class", "hibernate.default_catalog.null", "hibernate.default_schema.null", "hibernate.dialect", "hibernate.connection.url", "model.package", "action.package", "test.package", "richfaces.skin", "icefaces.home", "jboss.home" };

      for (String property : propertiesToCopy)
      {
         if (ftestProperties.get(property) != null)
         {
            seamGenProperties.put(property, ftestProperties.get(property));
         }
      }

      // override with ftest.properties
      seamGenProperties.put("workspace.home", WORKSPACE);
      seamGenProperties.put("jboss.home", CONTAINER_LOCATION);
      seamGenProperties.put("icefaces", ICEFACES ? "y" : "n");
      seamGenProperties.put("project.type", WAR ? "war" : "ear");
      seamGenProperties.put("project.name", APP_NAME);
   }

   private void startSeleniumServer() throws Exception
   {
      RemoteControlConfiguration rcc = new RemoteControlConfiguration();
      rcc.setPort(SELENIUM_SERVER_PORT);
      rcc.setLogOutFileName(OUTPUT_DIR + "/selenium-server.log");
      seleniumServer = new SeleniumServer(rcc);
      seleniumServer.start();
   }

   private void createOutputDir()
   {
      File dir = new File(OUTPUT_DIR);
      if (!dir.exists())
      {
         dir.mkdir();
      }
   }
}
