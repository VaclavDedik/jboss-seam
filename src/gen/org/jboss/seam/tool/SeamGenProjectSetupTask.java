/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
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
package org.jboss.seam.tool;

import java.util.logging.Logger;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant task for seam-gen.
 * 
 * @author James Williams
 * 
 */
public class SeamGenProjectSetupTask extends Task
{
    private String workspaceHome;
    private String actionPackage;
    private String testPackage;
    private String mdbPackage;
    private String modelPackage;
    private String bpmPackage;
    private String jbossHome;
    private String wtpFlag;

   private static Logger logger = Logger.getLogger(SeamGenProjectSetupTask.class.getName());

   @Override
   public void execute() throws BuildException
   {
      try
      {
        BuildPropertiesBean projectProps = new BuildPropertiesBean(
               this.workspaceHome, this.jbossHome,
               this.actionPackage, this.modelPackage,
               this.testPackage, this.bpmPackage, this.mdbPackage,
               this.wtpFlag);
               
         BuildPropertiesGenerator propsGen = new BuildPropertiesGenerator(
               projectProps);
         propsGen.generate();
      } catch (Exception e)
      {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
   }

   public String getActionPackage()
   {
      return actionPackage;
   }

   public void setActionPackage(String actionPackage)
   {
      this.actionPackage = actionPackage;
   }

   public String getJbossHome()
   {
      return jbossHome;
   }

   public void setJbossHome(String jbossHome)
   {
      this.jbossHome = jbossHome;
   }

   public String getMdbPackage()
   {
      return mdbPackage;
   }

   public void setMdbPackage(String mdbPackage)
   {
      this.mdbPackage = mdbPackage;
   }

   public String getTestPackage()
   {
      return testPackage;
   }

   public void setTestPackage(String testPackage)
   {
      this.testPackage = testPackage;
   }

   public String getWorkspaceHome()
   {
      return workspaceHome;
   }

   public void setWorkspaceHome(String workspaceHome)
   {
      this.workspaceHome = workspaceHome;
   }

   public String getWtpFlag()
   {
      return wtpFlag;
   }

   public void setWtpFlag(String wtpFlag)
   {
      this.wtpFlag = wtpFlag;
   }

   public String getBpmPackage()
   {
      return bpmPackage;
   }

   public void setBpmPackage(String bpmPackage)
   {
      this.bpmPackage = bpmPackage;
   }

   public String getModelPackage()
   {
      return modelPackage;
   }

   public void setModelPackage(String modelPackage)
   {
      this.modelPackage = modelPackage;
   }
   
   
}
