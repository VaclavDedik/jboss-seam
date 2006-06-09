package org.jboss.seam.example.remoting.chatroom;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.jboss.seam.annotations.ComponentLog;
import org.jboss.seam.annotations.Name;

@MessageDriven(activationConfig={
      @ActivationConfigProperty(propertyName="messagingType", propertyValue="javax.jms.MessageListener"),
      @ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Topic"),
      @ActivationConfigProperty(propertyName="Destination", propertyValue="topic/chatroomTopic"),
      @ActivationConfigProperty(propertyName="ConnectionFactoryName", propertyValue="UIL2ConnectionFactory")
   })
@Name("logger")
public class LoggerBean implements MessageListener
{
   
   @ComponentLog Log log;

   public void onMessage(Message msg)
   {
      try
      {
         ChatroomEvent event = (ChatroomEvent) ( (ObjectMessage) msg ).getObject();
         log.info( event.getUser() + ": " + ( event.getData()==null ? event.getAction() : event.getData() ) );
      }
      catch (JMSException jmse)
      {
         throw new RuntimeException(jmse);
      }
   }

}
