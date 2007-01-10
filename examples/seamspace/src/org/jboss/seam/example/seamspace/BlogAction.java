package org.jboss.seam.example.seamspace;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Stateful
@Name("blog")
@Scope(ScopeType.EVENT)
public class BlogAction implements BlogLocal
{    
   @In(create=true)
   private EntityManager entityManager;
   
   @In(required = false)
   private Member selectedMember;   
   
   public List getLatestBlogs()
   {
      List blogs = 
     entityManager.createQuery(
           "from MemberBlog b where b.member = :member order by b.entryDate desc")
           .setParameter("member", selectedMember)
           .setMaxResults(5)
           .getResultList();
      
      return blogs;
   }
   
   @Remove @Destroy
   public void destroy() { }     
}
