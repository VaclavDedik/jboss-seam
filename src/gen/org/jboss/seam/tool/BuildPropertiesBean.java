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

public class BuildPropertiesBean {
	
	private String workspaceHome;
	private String jbossHome;
	private String actionPackage;
	private String actionDir;
	private String modelPackage;
	private String modelDir;
	private String testDir;
	private String testPackage;
	private String mdbDir;
	private String mdbPackage;
	private String wtp;	

	public BuildPropertiesBean(){}
	
	public BuildPropertiesBean(String args[])
	{
		this.workspaceHome = fixPath(args[1]);
		this.jbossHome = fixPath(args[2]);
		
      this.actionPackage = args[3];
		this.actionDir = args[3].replace(".", "/");
		this.modelPackage = args[4];
		this.modelDir = args[4].replace(".", "/");
		this.testDir = args[5].replace(".", "/");
		this.testPackage = args[5];
		this.mdbDir = args[7].replace(".", "/");
		this.mdbPackage = args[7];
		this.wtp = args[8];
	}
   
   private String fixPath(String aPath)
   {
      return aPath.replace("\\", "\\\\");      
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
