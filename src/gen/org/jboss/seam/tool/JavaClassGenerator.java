/*******************************************************************************
 *    JBoss, Home of Professional Open Source
 *    Copyright 2006, JBoss Inc., and individual contributors as indicated
 *    by the @authors tag. See the copyright.txt in the distribution for a
 *    full listing of individual contributors.
 *   
 *    This is free software; you can redistribute it and/or modify it
 *    under the terms of the GNU Lesser General Public License as
 *    published by the Free Software Foundation; either version 2.1 of
 *    the License, or (at your option) any later version.
 *   
 *    This software is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *   
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this software; if not, write to the Free
 *    Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *    02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *******************************************************************************/
package org.jboss.seam.tool;

import java.io.IOException;
import java.util.HashMap;

import freemarker.template.TemplateException;

public class JavaClassGenerator extends AbstractSeamGen {

	private String interfaceName;
	private String actionName;
   private String componentName;
	private String projectName;
	
	//for mdb
	private String mdbDestination;
	private String mdbDestinationType;

	public JavaClassGenerator() {};

	public JavaClassGenerator(String[] args) throws IOException {
		this.projectName = args[1];
		this.actionName = args[2];
		this.interfaceName = args[2];	    
      //make the component name same as action name but first char lower case
      this.componentName = this.actionName.substring(0,1).toLowerCase() + this.actionName.substring(1);
	}
	
	private HashMap getModel() throws IOException {
		HashMap map = new HashMap();
		map.put("projectName", this.projectName);
		map.put("actionName", this.actionName);
      map.put("componentName", this.componentName);
		map.put("interfaceName", this.interfaceName);
		map.put("packageName", this.projectProps.getActionPackage());
		map.put("testPackageName", this.projectProps.getTestPackage());
		map.put("mdbPackage", this.projectProps.getMdbPackage());
		map.put("entityPackage", this.projectProps.getModelPackage());
		return map;
	}

	public void newStatefulAction() throws IOException, TemplateException {
		logger.info("Generating a new SFSB and interface");
		String sfsbAction = getJavaFilePath(this.projectProps.getWorkspaceHome(),
				this.projectName, this.projectProps.getActionDir(),
				this.actionName+"Action",  this.projectProps.getWtp());
		String sfsbInterface = getJavaFilePath(this.projectProps.getWorkspaceHome(),
				this.projectName, this.projectProps.getActionDir(),
				this.interfaceName, this.projectProps.getWtp());		

		generateFile("CreateSFSBAction.ftl", sfsbAction, getModel());
		generateFile("CreateSFSBInterface.ftl", sfsbInterface, getModel());
	}	
	
	public void newStatelessAction() throws IOException, TemplateException {
		logger.info("Generating a new SLSB and interface");
		String slsbAction = getJavaFilePath(this.projectProps.getWorkspaceHome(),
				this.projectName, this.projectProps.getActionDir(),
				this.actionName+"Action",  this.projectProps.getWtp());
		String slsbInterface = getJavaFilePath(this.projectProps.getWorkspaceHome(),
				this.projectName, this.projectProps.getActionDir(),
				this.interfaceName, this.projectProps.getWtp());		

		generateFile("CreateSLSBAction.ftl", slsbAction, getModel());
		generateFile("CreateInterface.ftl", slsbInterface, getModel());
	}	
	
	public void newConversation() throws IOException, TemplateException {
		logger.info("Generating a new SFSB and interface");
		String sfsbAction = getJavaFilePath(this.projectProps.getWorkspaceHome(),
				this.projectName, this.projectProps.getActionDir(),
				this.actionName+"Action",  this.projectProps.getWtp());
		String sfsbInterface = getJavaFilePath(this.projectProps.getWorkspaceHome(),
				this.projectName, this.projectProps.getActionDir(),
				this.interfaceName, this.projectProps.getWtp());		

		generateFile("CreateSFSBConversation.ftl", sfsbAction, getModel());
		generateFile("CreateInterfaceConversation.ftl", sfsbInterface, getModel());
	}	
	
	public void newTestcase() throws IOException, TemplateException {
		logger.info("Generating a new TestNG test case");
		String testAction = getJavaFilePath(this.projectProps.getWorkspaceHome(),
				this.projectName, this.projectProps.getTestDir(),
				this.actionName+"Test",  this.projectProps.getWtp());
		
		String testNgXml = getJavaFilePath(this.projectProps.getWorkspaceHome(),
				this.projectName, this.projectProps.getTestDir(),
				this.actionName+"Test",  this.projectProps.getWtp(), true);
		
		generateFile("TestCase.ftl", testAction, getModel());
		generateFile("testng.ftl", testNgXml, getModel());
	}
	
	public void newEntity() throws IOException, TemplateException {
		logger.info("Generating a new entity bean");
		String entity = getJavaFilePath(this.projectProps.getWorkspaceHome(),
				this.projectName, this.projectProps.getModelDir(),
				this.actionName,  this.projectProps.getWtp());
		
		generateFile("Entity.ftl", entity, getModel());
	}
	
   public void newEntityHome() throws IOException, TemplateException {
      logger.info("Generating a new EntityHome SFSB");
      String entity = getJavaFilePath(this.projectProps.getWorkspaceHome(),
            this.projectName, this.projectProps.getActionDir(),
            this.actionName + "Home",  this.projectProps.getWtp());
      
      generateFile("EntityHome.ftl", entity, getModel());
   }
   
	public void newMdb() throws IOException, TemplateException {
		logger.info("Generating a new MDB");
		HashMap model = getModel();
		String mdb = getJavaFilePath(this.projectProps.getWorkspaceHome(),
				this.projectName, this.projectProps.getMdbDir(),
				this.actionName,  this.projectProps.getWtp());
		model.put("destination", this.mdbDestination);
		model.put("destinationType", this.mdbDestinationType);	
					
		generateFile("Mdb.ftl", mdb, model);
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getMdbDestination() {
		return mdbDestination;
	}

	public void setMdbDestination(String mdbDestination) {
		this.mdbDestination = mdbDestination;
	}

	public String getMdbDestinationType() {
		return mdbDestinationType;
	}

	public void setMdbDestinationType(String mdbDestinationType) {
		this.mdbDestinationType = mdbDestinationType;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

   public String getComponentName()
   {
      return componentName;
   }

   public void setComponentName(String componentName)
   {
      this.componentName = componentName;
   }
}
