package org.jboss.seam.example.takeaway;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.mail.Message;

import org.jboss.annotation.ejb.ResourceAdapter;
import org.jboss.resource.adapter.mail.inflow.MailListener;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.BusinessProcess;
import org.jboss.seam.core.Renderer;
import org.jboss.seam.log.Log;

@MessageDriven(activationConfig={
         @ActivationConfigProperty(propertyName="mailServer", propertyValue="localhost"),
         @ActivationConfigProperty(propertyName="mailFolder", propertyValue="INBOX"),
         @ActivationConfigProperty(propertyName="storeProtocol", propertyValue="pop3"),
         @ActivationConfigProperty(propertyName="userName", propertyValue="takeaway"),
         @ActivationConfigProperty(propertyName="password", propertyValue="takeaway"),
         @ActivationConfigProperty(propertyName="port", propertyValue="11011")
})
@ResourceAdapter("mail-ra.rar")
@Name("mailListener")
public class MailListenerMDB implements MailListener
{
   @Logger
   private Log log;
   
   @In(create=true)
   private Takeaway takeaway;

   public void onMessage(Message message)
   {
     // Start the lifecycle manually as the interceptors aren't called
      Lifecycle.beginCall();
      Renderer.instance().render("/mail/type.xhtml");
      Lifecycle.endCall();
      
   }
   
}
