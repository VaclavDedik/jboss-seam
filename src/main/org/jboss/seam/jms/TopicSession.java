package org.jboss.seam.jms;

import static org.jboss.seam.InterceptionType.NEVER;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.NamingException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

/**
 * Manager for a JMS TopicSession
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.EVENT)
@Intercept(NEVER)
@Name("org.jboss.seam.core.topicSession")
public class TopicSession
{
   
   private javax.jms.TopicSession topicSession;
   
   @Create
   public void create() throws JMSException, NamingException
   {
      //TODO: i really want a transactional session!
      topicSession = TopicConnection.instance().createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
   }
   
   @Destroy
   public void destroy() throws JMSException
   {
      topicSession.close();
   }
   
   @Unwrap
   public javax.jms.TopicSession getTopicSession()
   {
      return topicSession;
   }
   
   public static javax.jms.TopicSession instance()
   {
      return (javax.jms.TopicSession) Component.getInstance(TopicSession.class, true );
   }
   
}
