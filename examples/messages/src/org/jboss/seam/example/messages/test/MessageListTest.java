//$Id$
package org.jboss.seam.example.messages.test;
import javax.faces.model.DataModel;

import org.jboss.seam.Component;
import org.jboss.seam.example.messages.Message;
import org.jboss.seam.example.messages.MessageManager;
import org.jboss.seam.example.messages.MessageManagerBean;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class MessageListTest extends SeamTest
{
   @Test
   public void testMessageList() throws Exception 
   {
      new NonFacesRequest()
      {

         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) Component.getInstance("messageList", true);
            assert list.getRowCount()==2;
         }
         
      }.run();

      new FacesRequest()
      {

         @Override
         protected void updateModelValues() throws Exception {
            DataModel list = (DataModel) Component.getInstance("messageList", true);
            assert list.getRowCount()==2;
            list.setRowIndex(1);
         }
         
         
         @Override
         protected void invokeApplication() throws Exception {
            MessageManager ml = (MessageManager) Component.getInstance(MessageManagerBean.class, true);
            ml.select();
         }


         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) Component.getInstance("messageList", true);
            assert list.getRowCount()==2;
            Message message = (Message) Component.getInstance(Message.class, false);
            assert message.getTitle().equals("Hello World");
            assert message.isRead();
         }
         
      }.run();

      new FacesRequest()
      {

         @Override
         protected void updateModelValues() throws Exception {
            DataModel list = (DataModel) Component.getInstance("messageList", true);
            assert list.getRowCount()==2;
            list.setRowIndex(0);
         }
         
         
         @Override
         protected void invokeApplication() throws Exception {
            MessageManager ml = (MessageManager) Component.getInstance(MessageManagerBean.class, true);
            ml.delete();
         }


         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) Component.getInstance("messageList", true);
            assert list.getRowCount()==1;
         }
         
      }.run();

      new NonFacesRequest()
      {

         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) Component.getInstance("messageList", true);
            assert list.getRowCount()==1;
         }
         
      }.run();

   }
   
}
