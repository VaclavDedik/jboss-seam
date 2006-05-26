package org.jboss.seam.jms;

import static org.jboss.seam.InterceptionType.NEVER;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.naming.NamingException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.util.Naming;

@Scope(ScopeType.EVENT)
@Intercept(NEVER)
public class ManagedTopicPublisher
{
   private String topicJndiName;
   
   private TopicPublisher topicPublisher;

   public String getTopicJndiName()
   {
      return topicJndiName;
   }

   public void setTopicJndiName(String jndiName)
   {
      this.topicJndiName = jndiName;
   }
   
   public Topic getTopic() throws NamingException
   {
      return (Topic) Naming.getInitialContext().lookup(topicJndiName);
   }
   
   @Create
   public void create() throws JMSException, NamingException
   {
      topicPublisher = org.jboss.seam.jms.TopicSession.instance().createPublisher( getTopic() );
   }
   
   @Destroy
   public void destroy() throws JMSException
   {
      topicPublisher.close();
   }
   
   @Unwrap
   public TopicPublisher getTopicPublisher()
   {
      return topicPublisher;
   }
   
}
