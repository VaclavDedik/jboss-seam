package org.jboss.seam.test;

import java.io.StringReader;

import org.jboss.seam.jbpm.PageflowParser;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.testng.annotations.Test;

public class PageflowTest {
  
  JbpmConfiguration jbpmConfiguration = JbpmConfiguration.parseXmlString(
    "<jbpm-configuration />"
  );

  @Test
  public void testPageflowWithStartState() {
    JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
    
    StringReader stringReader = new StringReader(
      "<pageflow name='hoepla'>" +
      "  <start-state name='start' />" +
      "</pageflow>"
    );
    PageflowParser pageflowParser = new PageflowParser(stringReader);
    ProcessDefinition processDefinition = pageflowParser.readProcessDefinition();
    assert "start".equals(processDefinition.getStartState().getName());
    
    jbpmContext.close();
  }
  
  @Test
  public void testPageflowWithStartPage() {
    JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
    
    StringReader stringReader = new StringReader(
      "<pageflow name='hoepla'>" +
      "  <start-page name='start' />" +
      "</pageflow>"
    );
    PageflowParser pageflowParser = new PageflowParser(stringReader);
    ProcessDefinition processDefinition = pageflowParser.readProcessDefinition();
    assert "start".equals(processDefinition.getStartState().getName());
    
    jbpmContext.close();
  }
  
  @Test
  public void testPageflowWithStartPageAttribute() {
    JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
    
    StringReader stringReader = new StringReader(
      "<pageflow name='hoepla' start-page='start'>" +
      "  <page name='start' />" +
      "</pageflow>"
    );
    PageflowParser pageflowParser = new PageflowParser(stringReader);
    ProcessDefinition processDefinition = pageflowParser.readProcessDefinition();
    assert "start".equals(processDefinition.getStartState().getName());
    
    jbpmContext.close();
  }
}
