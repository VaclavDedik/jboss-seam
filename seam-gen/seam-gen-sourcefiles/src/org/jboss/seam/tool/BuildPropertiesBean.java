package org.jboss.seam.tool;

public class BuildPropertiesBean {
	
	private String workspaceHome;
	private String jbossHome;
	private String actionPackage;
	private String actionDir;
	private String modelPackage;
	private String modelDir;
	private String testDir;
	private String testPackage;
	private String bpmDir;
	private String bpmPackage;
	private String mdbDir;
	private String mdbPackage;
	private String wtp;	

	public BuildPropertiesBean(){}
	
	public BuildPropertiesBean(String args[])
	{
		this.workspaceHome = args[1];
		this.jbossHome = args[2];
		this.actionPackage = args[3];
		this.actionDir = args[3].replace(".", "/");
		this.modelPackage = args[4];
		this.modelDir = args[4].replace(".", "/");
		this.testDir = args[5].replace(".", "/");
		this.testPackage = args[5];
		this.bpmDir = args[6].replace(".", "/");
		this.bpmPackage = args[6];
		this.mdbDir = args[7].replace(".", "/");
		this.mdbPackage = args[7];
		this.wtp = args[8];
	}
	
	public String getActionDir() {
		return actionDir;
	}
	public void setActionDir(String actionDir) {
		this.actionDir = actionDir;
	}
	public String getActionPackage() {
		return actionPackage;
	}
	public void setActionPackage(String actionPackage) {
		this.actionPackage = actionPackage;
	}
	public String getBpmDir() {
		return bpmDir;
	}
	public void setBpmDir(String bpmDir) {
		this.bpmDir = bpmDir;
	}
	public String getBpmPackage() {
		return bpmPackage;
	}
	public void setBpmPackage(String bpmPackage) {
		this.bpmPackage = bpmPackage;
	}
	public String getJbossHome() {
		return jbossHome;
	}
	public void setJbossHome(String jbossHome) {
		this.jbossHome = jbossHome;
	}
	public String getMdbDir() {
		return mdbDir;
	}
	public void setMdbDir(String mdbDir) {
		this.mdbDir = mdbDir;
	}
	public String getMdbPackage() {
		return mdbPackage;
	}
	public void setMdbPackage(String mdbPackage) {
		this.mdbPackage = mdbPackage;
	}
	public String getModelDir() {
		return modelDir;
	}
	public void setModelDir(String modelDir) {
		this.modelDir = modelDir;
	}
	public String getModelPackage() {
		return modelPackage;
	}
	public void setModelPackage(String modelPackage) {
		this.modelPackage = modelPackage;
	}
	public String getTestDir() {
		return testDir;
	}
	public void setTestDir(String testDir) {
		this.testDir = testDir;
	}
	public String getTestPackage() {
		return testPackage;
	}
	public void setTestPackage(String testPackage) {
		this.testPackage = testPackage;
	}
	public String getWorkspaceHome() {
		return workspaceHome;
	}
	public void setWorkspaceHome(String workspaceHome) {
		this.workspaceHome = workspaceHome;
	}

	public String getWtp() {
		return wtp;
	}

	public void setWtp(String wtp) {
		this.wtp = wtp;
	}
	
	
	
}
