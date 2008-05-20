package org.jboss.seam.bpm;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.jboss.seam.deployment.AbstractDeploymentHandler;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.XML;


public class PageflowDeploymentHandler extends AbstractDeploymentHandler
{

   private static LogProvider log = Logging.getLogProvider(PageflowDeploymentHandler.class);
   
   public static final String NAME = "org.jboss.seam.bpm.PageflowDeploymentHandler";

   private Set<String> pageflowDefinitions;
   
   public PageflowDeploymentHandler()
   {
      pageflowDefinitions = new HashSet<String>();
   }
   
   public String getName()
   {
      return NAME;
   }

   public void handle(String name, ClassLoader classLoader)
   {
      if (name.endsWith(".jpdl.xml"))
      {
         InputStream inputStream = Resources.getResourceAsStream(name, null);
         try
         {
            Element root = XML.getRootElementSafely(inputStream);
            if ("pageflow-definition".equals(root.getName()))
            {
               pageflowDefinitions.add(name);
            }
         }
         catch (DocumentException e)
         {
            log.debug("Unable to parse " + name, e);
         }
      }
   }
   
   public Set<String> getPageflowDefinitions()
   {
      return Collections.unmodifiableSet(pageflowDefinitions);
   }

}
