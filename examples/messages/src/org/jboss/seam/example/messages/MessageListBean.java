//$Id$
package org.jboss.seam.example.messages;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Interceptors;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Scope(SESSION)
@Name("messageList")
@Interceptors(SeamInterceptor.class)
public class MessageListBean implements Serializable, MessageList
{

   @DataModel
   private List<Message> messages;
   
   @Out(required=false)
   @DataModelSelection
   private Message message;
   
   @PersistenceContext
   private EntityManager em;
   
   @Factory("messages")
   public void findMessages()
   {
      messages = em.createQuery("from Message msg order by msg.datetime desc").getResultList();
   }
   
   public String select()
   {
      message.setRead(true);
      return "selected";
   }
   
   public String delete()
   {
      messages.remove(message);
      em.remove(message);
      message=null;
      return "deleted";
   }

}
