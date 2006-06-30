package org.jboss.seam.pageflow;

import java.io.InputStream;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.util.ClassLoaderUtil;
import org.xml.sax.InputSource;

public abstract class PageflowHelper {

  static JbpmConfiguration pageflowConfiguration = JbpmConfiguration.parseResource("org/jboss/seam/pageflow/jbpm.pageflow.cfg.xml");

  public static JbpmContext createPageflowContext() {
    return pageflowConfiguration.createJbpmContext();
  }
  
  public static ProcessDefinition parseXmlString(String xml) {
    StringReader stringReader = new StringReader(xml);
    return parseInputSource(new InputSource(stringReader));
  }

  public static ProcessDefinition parseXmlResource(String xmlResource) {
    InputStream resourceStream = ClassLoaderUtil.getStream(xmlResource);
    return parseInputSource(new InputSource(resourceStream));
  }

  public static ProcessDefinition parseInputSource(InputSource inputSource) {
    JbpmContext jbpmContext = createPageflowContext();
    try {
      PageflowParser pageflowParser = new PageflowParser(inputSource); 
      return pageflowParser.readProcessDefinition();
    } finally {
      jbpmContext.close();
    }
  }
  
  public static void signal(ProcessInstance processInstance, String outcome) {
    JbpmContext jbpmContext = createPageflowContext();
    try {
      log.debug("performing pageflow nagivation for outcome "+outcome);
       processInstance.signal(outcome);
    } finally {
       jbpmContext.close();
    }
 }
  
  public static ProcessInstance newPageflowInstance(ProcessDefinition processDefinition) {
    JbpmContext jbpmContext = createPageflowContext();
    try {
      log.debug("new pageflow instance for "+processDefinition.getName());
      return processDefinition.createProcessInstance();
    } finally {
      jbpmContext.close();
    }
  }
  
  private static Log log = LogFactory.getLog(PageflowHelper.class);
}
