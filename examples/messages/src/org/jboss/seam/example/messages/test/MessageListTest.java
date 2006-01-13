//$Id$
package org.jboss.seam.example.messages.test;
import java.util.Map;

import javax.faces.model.DataModel;

import org.jboss.seam.Component;
import org.jboss.seam.core.Init;
import org.jboss.seam.example.messages.Message;
import org.jboss.seam.example.messages.MessageList;
import org.jboss.seam.example.messages.MessageListBean;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class MessageListTest extends SeamTest
{
   @Test
   public void testMessageList() throws Exception 
   {
      new Script()
      {

         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) Component.getInstance("messages", true);
            assert list.getRowCount()==2;
         }
         
      }.run();

      new Script()
      {

         @Override
         protected void updateModelValues() throws Exception {
            DataModel list = (DataModel) Component.getInstance("messages", true);
            assert list.getRowCount()==2;
            list.setRowIndex(1);
         }
         
         
         @Override
         protected void invokeApplication() throws Exception {
            MessageList ml = (MessageList) Component.getInstance(MessageListBean.class, true);
            assert ml.select().equals("selected");
         }


         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) Component.getInstance("messages", true);
            assert list.getRowCount()==2;
            Message message = (Message) Component.getInstance(Message.class, false);
            assert message.getTitle().equals("Hello World");
            assert message.isRead();
         }
         
      }.run();

      new Script()
      {

         @Override
         protected void updateModelValues() throws Exception {
            DataModel list = (DataModel) Component.getInstance("messages", true);
            assert list.getRowCount()==2;
            list.setRowIndex(0);
         }
         
         
         @Override
         protected void invokeApplication() throws Exception {
            MessageList ml = (MessageList) Component.getInstance(MessageListBean.class, true);
            assert ml.delete().equals("deleted");
         }


         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) Component.getInstance("messages", true);
            assert list.getRowCount()==1;
         }
         
      }.run();

      new Script()
      {

         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) Component.getInstance("messages", true);
            assert list.getRowCount()==1;
         }
         
      }.run();

   }

   @Override
   public void initServletContext(Map initParams)
   {
      initParams.put(Init.COMPONENT_CLASSES, "org.jboss.seam.core.Ejb");
      initParams.put(Init.JNDI_PATTERN, "#{ejbName}/local");
   }
   
}
