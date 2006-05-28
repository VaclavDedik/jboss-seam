package org.jboss.seam.example.remoting.chatroom;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.jboss.cache.CacheException;
import org.jboss.cache.Fqn;
import org.jboss.cache.TreeCache;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Stateful
@Name("chatroomAction")
@Scope(CONVERSATION)
public class ChatRoomAction implements ChatRoomActionWebRemote
{

   @In(create=true)
   private transient TopicPublisher topicPublisher;   
   @In(create=true)
   private transient TopicSession topicSession;

   private String username;
   
   @In(create=true) 
   private transient TreeCache treeCache;

   public Set<String> getUsers()
   {
      try
      {
         Set<String> users = (Set<String>) treeCache.get( new Fqn("users"), "set" );
         if (users==null) 
         {
            users = Collections.synchronizedSet( new HashSet<String>() );
            treeCache.put( new Fqn("users"), "set", users );
         }
         return users;
      }
      catch (CacheException ce)
      {
         throw new RuntimeException(ce);
      }
   }

   @Begin
   public boolean connect(String username)
   {
      this.username = username;
      boolean added = getUsers().add(username);
      if (added)
      {
         publish( new ChatroomEvent("connect", username) );
      }
      return added;
   }

   public void sendMessage(String message)
   {
      publish( new ChatroomEvent("message", username, message) );
   }

   @End
   public void disconnect()
   {
      getUsers().remove(username);
      publish( new ChatroomEvent("disconnect", username) );
   }

   public Set<String> listUsers()
   {
      return getUsers();
   }

   private void publish(ChatroomEvent message)
   {
      try
      {
         topicPublisher.publish( topicSession.createObjectMessage(message) );
      } 
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      } 
   }
   
   @Destroy
   @Remove
   public void destroy() {}

}
