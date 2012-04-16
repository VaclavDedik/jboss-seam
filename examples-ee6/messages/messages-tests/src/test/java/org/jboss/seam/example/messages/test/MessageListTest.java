//$Id: MessageListTest.java 2383 2006-10-26 18:53:00Z gavin $
package org.jboss.seam.example.messages.test;
import javax.faces.model.DataModel;

import java.io.File;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.seam.mock.JUnitSeamTest;

import org.junit.runner.RunWith;
import org.junit.Test;

@RunWith(Arquillian.class)
public class MessageListTest extends JUnitSeamTest
{
	@Deployment(name="MessageListTest")
	@OverProtocol("Servlet 3.0") 
	public static Archive<?> createDeployment()
	{
      EnterpriseArchive er = ShrinkWrap.create(ZipImporter.class, "seam-messages.ear").importFrom(new File("../messages-ear/target/seam-messages.ear"))
				.as(EnterpriseArchive.class);
      WebArchive web = er.getAsType(WebArchive.class, "messages-web.war");
      web.addClasses(MessageListTest.class);
      
      // Install org.jboss.seam.mock.MockSeamListener
      web.delete("/WEB-INF/web.xml");
      web.addAsWebInfResource("web.xml");

      return er;
   }
	
   @Test
   public void testMessageList() throws Exception 
   {
      new NonFacesRequest()
      {

         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==2;
         }
         
      }.run();

      new FacesRequest()
      {

         @Override
         protected void updateModelValues() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==2;
            list.setRowIndex(1);
         }
         
         
         @Override
         protected void invokeApplication() throws Exception {
            invokeMethod("#{messageManager.select}");
         }


         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==2;
            assert getValue("#{message.title}").equals("Hello World");
            assert getValue("#{message.read}").equals(true);
         }
         
      }.run();

      new FacesRequest()
      {

         @Override
         protected void updateModelValues() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==2;
            list.setRowIndex(0);
         }
         
         
         @Override
         protected void invokeApplication() throws Exception {
            invokeMethod("#{messageManager.delete}");
         }


         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==1;
         }
         
      }.run();

      new NonFacesRequest()
      {

         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==1;
         }
         
      }.run();

   }
   
}
