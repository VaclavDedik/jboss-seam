package org.jboss.seam.example.seamspace;

import javax.ejb.Local;

@Local
public interface FriendLocal
{
   void createComment();
   void saveComment();
   
   void createRequest();
   void saveRequest();
   
   void destroy(); 
}
