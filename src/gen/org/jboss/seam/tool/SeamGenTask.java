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
public class SeamGenTask extends Task
{
   private String pageName;

   private String projectName;

   private String actionName;

   private String command;
   
   private String destination;
   
   private String destinationType;
   
   private enum Command
   {
     new_project, deploy_project, new_stateless_action, new_conversation, new_bpm_action, new_entity, new_mdb
   }

   private static Logger logger = Logger.getLogger(SeamGenTask.class.getName());

   @Override
   public void execute() throws BuildException
   {
      String wtpFlag = getProperty("eclipse.wtp");

      // new project logic. flips a magic wtp flag so script knows whether WTP
      // or non-WTP property should be set. (hack cause ant "if" sucks)
      if (this.command.equals(Command.new_project.toString()))
      {
         newProject(wtpFlag);
      }     

      else if (!newJavaClass()){
         throw new BuildException("Invalid Command");
      }      
   }

   /**
    * Create a new SFSB or SLSB with interface, facelet and testng test case.
    * Or, a new mdb.
    * Or a new entity with testng test case.
    * Or a new bpm action handler.
    */
   private boolean newJavaClass()
   {
      JavaClassGenerator actionGen = new JavaClassGenerator(this.actionName,
            this.projectName);
      FaceletGenerator faceletGen = new FaceletGenerator(this.pageName,
            this.projectName, this.actionName);
      
      boolean classGenerated = false;

      try
      {         
         if (this.command.equals(Command.new_stateless_action.toString()))
         {
            actionGen.newStatelessAction();
            actionGen.newTestcase();
            faceletGen.newPage();
            logger.info("New SLSB, interface, facelet and testng testcase created");
            classGenerated = true;
         }
         else if (this.command.equals(Command.new_conversation.toString()))
         {
            actionGen.newConversation();
            actionGen.newTestcase();
            faceletGen.newPage();
            logger.info("New SFSB, interface, facelet and testng testcase created");
            classGenerated = true;
         }
         else if (this.command.equals(Command.new_bpm_action.toString())){
            actionGen.newBpmAction();
            logger.info("New jBPM ActionHandler and testng testcase created");
            classGenerated = true;
         }
         else if (this.command.equals(Command.new_entity.toString())){
            actionGen.newTestcase();
            actionGen.newEntity();
            logger.info("New entity bean and testng testcase created");
            classGenerated = true;
         }
         else if (this.command.equals(Command.new_mdb.toString())){
            
            //must set mdb attributes
            actionGen.setMdbDestination(this.destination);
            actionGen.setMdbDestinationType(this.destinationType);
            
            actionGen.newMdb();
            logger.info("New mdb and testng testcase created");
            classGenerated = true;
         }        
         
      } catch (Exception e)
      {
         throw new RuntimeException(e);
      }

     return classGenerated;
      
   }

  
   /**
    * Convienence method for getting a property from the build script.
    * 
    * @param name
    * @return
    */
   private String getProperty(String name)
   {
      return this.getProject().getProperty(name);
   }

   /**
    * tell the build script that there is a "wtp" property or not. This allows
    * "if" flag to be used to decide wtp or no-wtp style.
    * 
    * @param wtpFlag
    */
   private void newProject(String wtpFlag)
   {
      if (wtpFlag.equals("Y"))
      {
         this.getProject().setProperty("wtp", "wtp");
      }

      logger.info("New project named: " + this.projectName);
   }

   public String getActionName()
   {
      return actionName;
   }

   public void setActionName(String actionName)
   {
      this.actionName = actionName;
   }

   public String getPageName()
   {
      return pageName;
   }

   public void setPageName(String pageName)
   {
      this.pageName = pageName;
   }

   public String getCommand()
   {
      return command;
   }

   public void setCommand(String command)
   {
      this.command = command;
   }

   public String getProjectName()
   {
      return projectName;
   }

   public void setProjectName(String projectName)
   {
      this.projectName = projectName;
   }

   public String getDestination()
   {
      return destination;
   }

   public void setDestination(String destination)
   {
      this.destination = destination;
   }

   public String getDestinationType()
   {
      return destinationType;
   }

   public void setDestinationType(String destinationType)
   {
      this.destinationType = destinationType;
   }
   
   

}
