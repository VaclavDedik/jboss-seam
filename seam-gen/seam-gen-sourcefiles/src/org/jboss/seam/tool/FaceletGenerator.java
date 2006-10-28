package org.jboss.seam.tool;

import java.io.IOException;
import java.util.HashMap;

import freemarker.template.TemplateException;

public class FaceletGenerator extends AbstractSeamGen {

	private String pageName;
	private String projectName;
	private String actionName;
	
	public FaceletGenerator() {};

	public FaceletGenerator(String[] args) throws IOException {
		this.projectName = args[1];
		this.pageName = args[2];
	}
	
	private HashMap getModel() throws IOException {
		HashMap map = new HashMap();
		map.put("projectName", this.projectName);
		map.put("pageName", this.pageName);
		map.put("actionName", this.actionName);
		return map;
	}

	public void newPage() throws IOException, TemplateException {
		logger.info("Generating a new Facelet Page");
		String pageTemplate = getFaceletPath( this.projectProps.getWorkspaceHome(),
				this.projectName, "template", this.projectProps.getWtp());
		String page = getFaceletPath( this.projectProps.getWorkspaceHome(),
				this.projectName, this.pageName, this.projectProps.getWtp());		

		generateFile("template.ftl", pageTemplate, getModel());
		generateFile("page.ftl", page, getModel());
	}	
	
	public void newActionPage() throws IOException, TemplateException {
		logger.info("Generating a new action Facelet Page");
		String pageTemplate = getFaceletPath( this.projectProps.getWorkspaceHome(),
				this.projectName, "template", this.projectProps.getWtp());
		String page = getFaceletPath( this.projectProps.getWorkspaceHome(),
				this.projectName, this.pageName, this.projectProps.getWtp());		

		generateFile("template.ftl", pageTemplate, getModel());
		generateFile("action-page.ftl", page, getModel());
	}	
	
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
	
}
