package org.jboss.seam.test;

import java.io.StringReader;

import org.jboss.seam.pageflow.Page;
import org.jboss.seam.pageflow.PageflowHelper;
import org.jboss.seam.pageflow.PageflowParser;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.StartState;
import org.testng.annotations.Test;

public class PageflowTest {
  
  JbpmConfiguration jbpmConfiguration = JbpmConfiguration.parseXmlString(
    "<jbpm-configuration />"
  );

  @Test
  public void testPageflowWithStartState() {
    JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
    
    StringReader stringReader = new StringReader(
      "<pageflow-definition name='hoepla'>" +
      "  <start-state name='start' />" +
      "</pageflow-definition>"
    );
    PageflowParser pageflowParser = new PageflowParser(stringReader);
    ProcessDefinition processDefinition = pageflowParser.readProcessDefinition();
    assert "start".equals(processDefinition.getStartState().getName());
    
    jbpmContext.close();
  }
  
  @Test
  public void testPageflowWithStartPage() {
    JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
    
    ProcessDefinition processDefinition = PageflowHelper.parseXmlString(
      "<pageflow-definition name='hoepla'>" +
      "  <start-page name='start' />" +
      "</pageflow-definition>"
    );
        
    assert "start".equals(processDefinition.getStartState().getName());
    
    jbpmContext.close();
  }
  
  @Test
  public void testPageflowWithStartPageAttribute() {
    JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
    
    StringReader stringReader = new StringReader(
      "<pageflow-definition name='hoepla' start-page='start'>" +
      "  <page name='start' />" +
      "</pageflow-definition>"
    );
    PageflowParser pageflowParser = new PageflowParser(stringReader);
    ProcessDefinition processDefinition = pageflowParser.readProcessDefinition();
    assert "start".equals(processDefinition.getStartState().getName());
    
    jbpmContext.close();
  }
  
  @Test
  public void testOrderPageflow() {
    ProcessDefinition pageflowDefinition = PageflowHelper.parseXmlString(
      "<pageflow-definition name='checkout'>" +
      "  <start-state name='start'>" +
      "    <transition to='confirm'/>" +
      "  </start-state>" +
      "  <page name='confirm' view-id='/confirm.xhtml'>" +
      "    <redirect/>" +
      "    <transition name='update'   to='continue'/>" + 
      "    <transition name='purchase' to='complete'>" +
      "      <action expression='#{checkout.submitOrder}' />" +
      "    </transition>" +
      "  </page>" +
      "  <page name='complete' view-id='/complete.xhtml'>" +
      "    <redirect/>" +
      "    <end-conversation/>" +
      "  </page>" +    
      "  <page name='continue' view-id='/browse.xhtml'>" +
      "    <end-conversation/>" +
      "  </page>" +
      "</pageflow-definition>"
    );
    
    StartState start = (StartState) pageflowDefinition.getStartState();
    Page confirm = (Page) pageflowDefinition.getNode("confirm");
    Page complete = (Page) pageflowDefinition.getNode("complete");
    Page cont = (Page) pageflowDefinition.getNode("continue");
    assert confirm!=null;
    assert complete!=null;
    assert cont!=null;
    
    ProcessInstance pageflowInstance = new ProcessInstance(pageflowDefinition);
    Token token = pageflowInstance.getRootToken();
    assert start.equals(token.getNode());
    
    pageflowInstance.signal();
  }
  
}
