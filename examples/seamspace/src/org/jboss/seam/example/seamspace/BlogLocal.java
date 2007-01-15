package org.jboss.seam.example.seamspace;

import java.util.List;

import javax.ejb.Local;

@Local
public interface BlogLocal
{
   List getLatestBlogs();
   void getMemberBlogs();
   void getBlog();
   void createComment();
   void saveComment();
   void destroy();
}
