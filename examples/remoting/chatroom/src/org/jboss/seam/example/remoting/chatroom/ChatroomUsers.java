package org.jboss.seam.example.remoting.chatroom;

import java.util.HashSet;
import java.util.Set;

import org.jboss.cache.CacheException;
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
   @In
   private PojoCache pojoCache;
   
   @Unwrap
   public Set<String> getUsers() throws CacheException
   {
      Set<String> userList = (Set<String>) pojoCache.get("chatroom", "userList");
      if (userList==null) 
      {
         userList = new HashSet<String>();
         pojoCache.put("chatroom", "userList", userList);
      }
      return userList;
   }

}
