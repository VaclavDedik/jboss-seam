package org.jboss.seam.jbpm;

import org.dom4j.Element;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.jpdl.xml.Parsable;
import org.jbpm.taskmgmt.exe.TaskInstance;

public class Page extends Node implements Parsable {
  
  // This classname is configured in the jbpm configuration 
  // file : org/jbpm/graph/node/node.types.xml inside 
  // the jbpm-{version}.jar
  
  // In case it would be necessary, that file, can be customized
  // by updating the reference to it in the central jbpm configuration 
  // file 'jbpm.cfg.xml'

  private static final long serialVersionUID = 1L;
  
  String url;
  boolean isConversationEnd = false;
  String outcome;

  /**
   * parses the dom4j element that corresponds to this page.
   */
  public void read(Element pageElement, JpdlXmlReader jpdlXmlReader) {
    url = pageElement.attributeValue("url");
    Element conversationEndElement = pageElement.element("conversation-end");
    if (conversationEndElement!=null) {
      isConversationEnd = true;
      outcome = conversationEndElement.attributeValue("outcome");
    }
  }

  /**
   * is executed when execution arrives in this page at runtime.
   */
  public void execute(ExecutionContext executionContext) {
    if (isConversationEnd) {
      // get the outer business process task instance
      ContextInstance contextInstance = executionContext.getContextInstance();
      String variableName = "taskInstance";
      TaskInstance taskInstance = (TaskInstance) contextInstance.getVariable(variableName);
      
      // complete the task
      if (outcome==null) {
        taskInstance.end();
      } else {
        taskInstance.end(outcome);
      }
    }
  }

  public boolean isConversationEnd() {
    return isConversationEnd;
  }
  public String getOutcome() {
    return outcome;
  }
  public String getUrl() {
    return url;
  }
}
