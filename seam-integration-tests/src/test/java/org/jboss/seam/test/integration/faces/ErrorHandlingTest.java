package org.jboss.seam.test.integration.faces;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.seam.test.integration.Deployments;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


// JBSEAM-5045
@RunWith(Arquillian.class)
@RunAsClient
public class ErrorHandlingTest
{
   private final WebClient client = new WebClient();
   
   @ArquillianResource
   URL contextPath;
   
   @Deployment(name="ErrorHandlingTest")
   @OverProtocol("Servlet 3.0") 
   public static Archive<?> createDeployment()
   {
       // This is a client test, use a real (non-mocked) Seam deployment
	   return Deployments.realSeamDeployment()
	            .addAsWebResource(new StringAsset(
	            	 "<html xmlns=\"http://www.w3.org/1999/xhtml\"" +
	            	   " xmlns:h=\"http://java.sun.com/jsf/html\"" +
	                   " xmlns:f=\"http://java.sun.com/jsf/core\"" +
	                   " xmlns:ui=\"http://java.sun.com/jsf/facelets\">" +
	                 "<h:head></h:head>" +
	                 "<h:body>" +	
	                    "<h:form id='form'>" +
	                    	"<h:commandButton  id='server' action='#{xxxx.xxxxx}' value='Submit'/>" +
	                    	"<h:commandButton  id='ajax' action='#{xxxx.xxxxx}' value='Ajax Submit'>"+
	                    		"<f:ajax />" +
	                    	"</h:commandButton>" +
	                    "</h:form>" +
                    "</h:body>" + 
	                "</html>"), "test.xhtml")
	                
	            .addAsWebResource(new StringAsset(
            		"<html xmlns=\"http://www.w3.org/1999/xhtml\"" +
	            	   " xmlns:h=\"http://java.sun.com/jsf/html\"" +
	                   " xmlns:f=\"http://java.sun.com/jsf/core\"" +
	                   " xmlns:ui=\"http://java.sun.com/jsf/facelets\">" +
	                 "<h:head></h:head>" +
	                 "<h:body>" +	
	                    "<h:messages globalOnly=\"true\"/>"+
                     "</h:body>" + 
	                 "</html>"), "error.xhtml");
   }
   
   
   
   @Test
   public void testErrorHandling() throws Exception
   {
   
      HtmlPage page = client.getPage(contextPath + "test.seam"); 
      page = page.getElementById("form:server").click();      
     
      assertTrue(page.getUrl().toString().startsWith(contextPath + "error.seam"));
      
      page = client.getPage(contextPath + "test.seam"); 
      page.getElementById("form:ajax").click();
      
      //waiting for processing ajax e javascript
      Thread.sleep(2000);
      
      ScriptResult result = page.executeJavaScript("window.location");      
      assertTrue(result.getJavaScriptResult().toString().startsWith(contextPath + "error.seam"));      
   }
   
}