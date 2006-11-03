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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class AbstractSeamGen {

	protected static Logger logger = Logger.getLogger("SeamGen");

	protected BuildPropertiesBean projectProps = new BuildPropertiesBean();

	private static Level logLevel = Level.INFO;

	private static Configuration cfg = new Configuration();
   
   protected String lower(String name)
   {
      return name.substring(0, 1).toLowerCase() + name.substring(1);
   }

	public AbstractSeamGen() {
		logger.setLevel(logLevel);
		try {
			init();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	public void init() throws IOException {
		cfg.setDirectoryForTemplateLoading(new File("freemarker-templates"));
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		setBuildPropertiesBean();
	}

	public void setBuildPropertiesBean() throws IOException {
		Properties buildProps = new Properties();
		buildProps.load(new FileInputStream("build.properties"));

		this.projectProps.setActionDir(buildProps.getProperty("action.dir"));
		this.projectProps.setActionPackage(buildProps
				.getProperty("action.package"));
		this.projectProps.setJbossHome(buildProps.getProperty("jboss.home"));
		this.projectProps.setMdbDir(buildProps.getProperty("mdb.dir"));
		this.projectProps.setMdbPackage(buildProps.getProperty("mdb.package"));
		this.projectProps.setModelDir(buildProps.getProperty("model.dir"));
		this.projectProps.setModelPackage(buildProps
				.getProperty("model.package"));
		this.projectProps.setTestDir(buildProps.getProperty("test.dir"));
		this.projectProps
				.setTestPackage(buildProps.getProperty("test.package"));
		this.projectProps.setWorkspaceHome(buildProps
				.getProperty("workspace.home"));
		this.projectProps.setWtp(buildProps.getProperty("eclipse.wtp"));
	}

	protected String getJavaFilePath(String workspaceHome, String projectName,
			String packageDir, String fileName, String wtp) {

		// This isn't a testng.xml file
		return getJavaFilePath(workspaceHome, projectName, packageDir,
				fileName, wtp, false);

	}

	protected String getJavaFilePath(String workspaceHome, String projectName,
			String packageDir, String fileName, String wtp, boolean isTestNgXml) {
		StringBuffer buff = new StringBuffer(workspaceHome);
		buff.append("/");

		buff.append(projectName);
		if (wtp.equalsIgnoreCase("Y")) {
			buff.append("EJB/ejbModule");
		}

		else {
			buff.append("/src");
		}

		buff.append("/");
		buff.append(packageDir);
		buff.append("/");
		buff.append(fileName);

		if (isTestNgXml)
			buff.append(".xml");
		else
			buff.append(".java");

		return buff.toString();
	}

	protected String getFaceletPath(String workspaceHome, String projectName,
			String fileName, String wtp) {
		StringBuffer buff = new StringBuffer(workspaceHome);
		buff.append("/");

		buff.append(projectName);
		if (wtp.equalsIgnoreCase("Y")) {
			buff.append("Web/WebContent/");
		} else {
			buff.append("/view/");
		}

		buff.append(fileName);
		buff.append(".xhtml");

		return buff.toString();
	}

	protected static void generateFile(String templateName, String outputFile,
			HashMap templateVariables) throws IOException, TemplateException {
		Template template;

		// Get or create a template
		template = cfg.getTemplate(templateName);

		// create output file path
		File outFile = new File(outputFile);
		File basePath = outFile.getParentFile();
		
		//we do want to overwrite build.properties file only in 
		//all cases for "set-properties" seamgen task
		if (!outFile.exists() || outFile.getName() == "build.properties") {
			if (basePath != null) {
				basePath.mkdirs();
			}

			// Merge data model with template
			Writer out = new FileWriter(outputFile);
			template.process(templateVariables, out);
			out.flush();
			logger
					.info("New File Generated by Seam Gen using: "
							+ templateName);
			logger.info("File path is: " + outFile.getPath());
		} else {
			logger.info("No file generated because it already exists");
		}
	}

}
