package org.jboss.seam.example.seamspace;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.annotations.Scope;

@Stateful
@Name("blog")
@Scope(ScopeType.EVENT)
public class BlogAction implements BlogLocal
{    
   @RequestParameter
   private String name;   
   
   @In(create=true)
   private EntityManager entityManager;
   
   @In(required = false)
   private Member selectedMember;   
   
   @Out(required = false)
   private List memberBlogs;
   
   public List getLatestBlogs()
   {
      return entityManager.createQuery(
           "from MemberBlog b where b.member = :member order by b.entryDate desc")
           .setParameter("member", selectedMember)
           .setMaxResults(5)
           .getResultList();
   }
   
   @Factory("memberBlogs")
   public void getMemberBlogs()
   {
      memberBlogs = entityManager.createQuery(
            "from MemberBlog b where b.member.name = :name order by b.entryDate desc")
            .setParameter("name", name)
            .getResultList();
   }
   
   @Remove @Destroy
   public void destroy() { }     
}
