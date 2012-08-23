package org.jboss.seam.test.integration.faces;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.seam.test.integration.Deployments;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

@RunWith(Arquillian.class)
@RunAsClient
public class RestoreViewComponentAccessTest
{
   private final WebClient client = new WebClient();
   private static final String[] testScopes = {"page", "conversation", "session", "application"};
   private static final String componentNamePrefix = "sequence_";
         
   @ArquillianResource
   URL contextPath;
   
   @Deployment(name="RestoreViewComponentAccessTest")
   @OverProtocol("Servlet 3.0") 
   public static WebArchive createDeployment()
   {
      
      // This is a client test, use a real (non-mocked) Seam deployment
      WebArchive war = Deployments.realSeamDeployment()
            .addClasses(SequenceAction.class);
      
      war.delete("WEB-INF/pages.xml");
      war.delete("WEB-INF/components.xml");
      
      for (String scope : testScopes) {
         war.addAsWebResource(createView(scope), "test_" + scope + ".xhtml");
      }
      
      StringBuilder componentsXmlBuilder = new StringBuilder();
      
      componentsXmlBuilder.append("<components xmlns=\"http://jboss.org/schema/seam/components\">");
      
      for (String scope : testScopes) {
         String cname = componentNamePrefix + scope;
         componentsXmlBuilder.append("<component name='" + cname + "' scope='" + scope + "' class='org.jboss.seam.test.integration.faces.SequenceAction' />");
      }
      
      componentsXmlBuilder.append("</components>");
      
      war.addAsWebInfResource(new StringAsset(componentsXmlBuilder.toString()), "components.xml");
      
      war.addAsWebInfResource(new StringAsset(
            "<pages xmlns=\"http://jboss.org/schema/seam/pages\""+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"+
               "<page view-id='/test_conversation.xhtml'>" +
                  "<begin-conversation join='true'/>" +
               "</page>" +
            "</pages>"), "pages.xml");
      
      return war;
   }
   
   private static Asset createView(String scope) {
      String cname = componentNamePrefix + scope;
      return new StringAsset(
            "<html xmlns=\"http://www.w3.org/1999/xhtml\"" +
            " xmlns:h=\"http://java.sun.com/jsf/html\"" +
            " xmlns:f=\"http://java.sun.com/jsf/core\"" +
            " xmlns:s=\"http://jboss.org/schema/seam/taglib\"" +
            " xmlns:ui=\"http://java.sun.com/jsf/facelets\">" +
            "<h:head></h:head>" +
            "<h:body>" +
               "<h:form id='form'>" +
                  "<h:messages/>" +
                  "<h:outputText id='output' value='Sequence: #{" + cname + ".output}'/>" +
                  "<h:inputText id='input' value='#{" + cname + ".input}'>" +
                  "<f:validateLongRange minimum='#{" + cname + ".minimum}' />" +
                  "</h:inputText>" +
                  "<h:commandButton id='append' value='Append' action='#{" + cname + ".append}'/>" +
               "</h:form>" +
            "</h:body>" + 
            "</html>");
   }
   
   
   public void testBase(String scope) throws Exception {
      HtmlPage page = client.getPage(contextPath + "test_" + scope + ".seam");
      assertTrue(page.getBody().getTextContent().contains("Sequence: "));
      
      ((HtmlTextInput)page.getElementById("form:input")).setText("1");
      page = page.getElementById("form:append").click();
      
      assertTrue(page.getBody().getTextContent().contains("Sequence: 1"));
      
      ((HtmlTextInput)page.getElementById("form:input")).setText("2");
      page = page.getElementById("form:append").click();
      
      assertTrue(page.getBody().getTextContent().contains("Sequence: 1, 2"));
      
      ((HtmlTextInput)page.getElementById("form:input")).setText("1");
      page = page.getElementById("form:append").click();
      
      assertFalse(page.getBody().getTextContent().contains("Sequence: 1, 2, 1"));
      assertTrue(page.getBody().getTextContent().contains("value must be greater than or equal to 2"));
   }
   
   @Test
   @Ignore
   public void testPage() throws Exception {
      testBase("page");
   }
   
   @Test
   @Ignore
   public void testConversation() throws Exception {
      testBase("conversation");
   }
   
   @Test
   @Ignore
   public void testSession() throws Exception {
      testBase("session");
   }
   
   @Test
   @Ignore
   public void testApplication() throws Exception {
      testBase("application");
   }
}
