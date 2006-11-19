package org.jboss.seam.jms;

import static org.jboss.seam.InterceptionType.NEVER;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.NamingException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

/**
 * Manager for a JMS QueueSession
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.EVENT)
@Intercept(NEVER)
@Name("org.jboss.seam.jms.queueSession")
@Install(genericDependencies=ManagedQueueSender.class)
public class QueueSession
{
   
   private javax.jms.QueueSession queueSession;
   
   @Create
   public void create() throws JMSException, NamingException
   {
      //TODO: i really want a transactional session!
      queueSession = QueueConnection.instance().createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
   }
   
   @Destroy
   public void destroy() throws JMSException
   {
      queueSession.close();
   }
   
   @Unwrap
   public javax.jms.QueueSession getQueueSession()
   {
      return queueSession;
   }
   
   public static javax.jms.QueueSession instance()
   {
      return (javax.jms.QueueSession) Component.getInstance(QueueSession.class, true );
   }
   
}
