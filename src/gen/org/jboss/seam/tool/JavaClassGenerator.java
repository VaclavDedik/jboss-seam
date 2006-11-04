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
		projectName = args[1];
		actionName = args[2];
		interfaceName = actionName;
      componentName = lower(actionName);
	}
	
	private HashMap getModel() throws IOException {
		HashMap map = new HashMap();
		map.put("projectName", projectName);
		map.put("actionName", actionName);
      map.put("componentName", componentName);
		map.put("interfaceName", interfaceName);
		map.put("packageName", projectProps.getActionPackage());
		map.put("testPackageName", projectProps.getTestPackage());
		map.put("mdbPackage", projectProps.getMdbPackage());
		map.put("entityPackage", projectProps.getModelPackage());
		return map;
	}

	public void newStatefulAction() throws IOException, TemplateException {
		logger.info("Generating a new SFSB and interface");
		String sfsbAction = getJavaFilePath(projectProps.getWorkspaceHome(),
				projectName, projectProps.getActionDir(),
				actionName+"Action",  projectProps.getWtp());
		String sfsbInterface = getJavaFilePath(projectProps.getWorkspaceHome(),
				projectName, projectProps.getActionDir(),
				interfaceName, projectProps.getWtp());		

		generateFile("FormActionBean.ftl", sfsbAction, getModel());
		generateFile("FormAction.ftl", sfsbInterface, getModel());
	}	
	
	public void newStatelessAction() throws IOException, TemplateException {
		logger.info("Generating a new SLSB and interface");
		String slsbAction = getJavaFilePath(projectProps.getWorkspaceHome(),
				projectName, projectProps.getActionDir(),
				actionName+"Action",  projectProps.getWtp());
		String slsbInterface = getJavaFilePath(projectProps.getWorkspaceHome(),
				projectName, projectProps.getActionDir(),
				interfaceName, projectProps.getWtp());		

		generateFile("Action.ftl", slsbAction, getModel());
		generateFile("ActionBean.ftl", slsbInterface, getModel());
	}	
	
	public void newConversation() throws IOException, TemplateException {
		logger.info("Generating a new SFSB and interface");
		String sfsbAction = getJavaFilePath(projectProps.getWorkspaceHome(),
				projectName, projectProps.getActionDir(),
				actionName+"Action",  projectProps.getWtp());
		String sfsbInterface = getJavaFilePath(projectProps.getWorkspaceHome(),
				projectName, projectProps.getActionDir(),
				interfaceName, projectProps.getWtp());		

		generateFile("ConversationBean.ftl", sfsbAction, getModel());
		generateFile("Conversation.ftl", sfsbInterface, getModel());
	}	
	
	public void newTestcase() throws IOException, TemplateException {
		logger.info("Generating a new TestNG test case");
		String testAction = getJavaFilePath(projectProps.getWorkspaceHome(),
				projectName, projectProps.getTestDir(),
				actionName+"Test",  projectProps.getWtp());
		
		String testNgXml = getJavaFilePath(projectProps.getWorkspaceHome(),
				projectName, projectProps.getTestDir(),
				actionName+"Test",  projectProps.getWtp(), true);
		
		generateFile("TestCase.ftl", testAction, getModel());
		generateFile("testng.ftl", testNgXml, getModel());
	}
	
	public void newEntity() throws IOException, TemplateException {
		logger.info("Generating a new entity bean");
		String entity = getJavaFilePath(projectProps.getWorkspaceHome(),
				projectName, projectProps.getModelDir(),
				actionName,  projectProps.getWtp());
		
		generateFile("Entity.ftl", entity, getModel());
	}
	
   public void newEntityHome() throws IOException, TemplateException {
      logger.info("Generating a new EntityHome");
      String entity = getJavaFilePath(projectProps.getWorkspaceHome(),
            projectName, projectProps.getActionDir(),
            actionName + "Home",  projectProps.getWtp());
      
      generateFile("EntityHome.ftl", entity, getModel());
   }
   
   public void newEntityList() throws IOException, TemplateException {
      logger.info("Generating a new EntityList");
      String entity = getJavaFilePath(projectProps.getWorkspaceHome(),
            projectName, projectProps.getActionDir(),
            actionName + "List",  projectProps.getWtp());
      
      generateFile("EntityList.ftl", entity, getModel());
   }
   
	public void newMdb() throws IOException, TemplateException {
		logger.info("Generating a new MDB");
		HashMap model = getModel();
		String mdb = getJavaFilePath(projectProps.getWorkspaceHome(),
				projectName, projectProps.getMdbDir(),
				actionName,  projectProps.getWtp());
		model.put("destination", mdbDestination);
		model.put("destinationType", mdbDestinationType);	
					
		generateFile("MessageDrivenBean.ftl", mdb, model);
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
