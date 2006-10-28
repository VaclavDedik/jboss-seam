package org.jboss.seam.tool;

import java.io.IOException;
import java.util.HashMap;

import freemarker.template.TemplateException;

public class BuildPropertiesGenerator extends AbstractSeamGen {
	
	BuildPropertiesBean projectProps;
	
	public BuildPropertiesGenerator(BuildPropertiesBean projectProps){
		this.projectProps = projectProps;
	}
	
	public void generate() throws IOException, TemplateException
	{
		logger.info("Generating a new build.properties file");
		HashMap map = new HashMap();
		map.put("projectProps", this.projectProps);
	
		generateFile("build-properties.ftl", "build.properties", map);
	}
	
	public void generateWtp() throws IOException, TemplateException
	{
		logger.info("Generating a new build.properties file");
		HashMap map = new HashMap();
		map.put("projectProps", this.projectProps);
	
		generateFile("build-properties.ftl", "build.properties", map);
	}
}
