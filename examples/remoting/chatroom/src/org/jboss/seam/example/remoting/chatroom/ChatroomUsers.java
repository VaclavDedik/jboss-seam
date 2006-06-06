package org.jboss.seam.example.remoting.chatroom;

import java.util.HashSet;
import java.util.Set;

import org.jboss.cache.CacheException;
import org.jboss.cache.Fqn;
import org.jboss.cache.aop.PojoCache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

@Name("chatroomUsers")
@Scope(ScopeType.STATELESS)
public class ChatroomUsers
{
   @In(create=true) 
   private transient PojoCache pojoCache;
   
   @Unwrap
   public Set<String> getUsers()
   {
      try
      {
         Set<String> users = (Set<String>) pojoCache.get( new Fqn("chatroom"), "users" );
         if (users==null) 
         {
            users = new HashSet<String>();
            pojoCache.put( new Fqn("chatroom"), "users", users );
         }
         return users;
      }
      catch (CacheException ce)
      {
         throw new RuntimeException(ce);
      }
   }

}
