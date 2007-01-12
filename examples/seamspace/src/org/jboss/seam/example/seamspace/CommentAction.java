package org.jboss.seam.example.seamspace;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.security.Restrict;

@Stateful
@Name("commentAction")
public class CommentAction implements CommentLocal
{   
   @Out
   private BlogComment comment;
   
   @In
   private EntityManager entityManager;
   
   @Begin @Restrict
   public void create(MemberBlog blog)
   {
      comment = new BlogComment();
      comment.setBlog(blog);
   }
   
   @End
   public void save()
   {      
      entityManager.persist(comment);
   }   
   
   @Remove @Destroy
   public void destroy() { }
}
