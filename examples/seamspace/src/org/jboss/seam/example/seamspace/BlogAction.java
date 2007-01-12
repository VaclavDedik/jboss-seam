package org.jboss.seam.example.seamspace;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RequestParameter;

@Stateful
@Name("blog")
public class BlogAction implements BlogLocal
{    
   @RequestParameter
   private String name;   
   
   @RequestParameter
   private Integer blogId;
   
   @In(create=true)
   private EntityManager entityManager;
   
   @In(required = false)
   private Member selectedMember;   
   
   @Out(required = false)
   private List memberBlogs;
   
   @Out(required = false)
   private MemberBlog selectedBlog;
   
   /**
    * Returns the 5 latest blog entries for a member
    */
   public List getLatestBlogs()
   {
      return entityManager.createQuery(
           "from MemberBlog b where b.member = :member order by b.entryDate desc")
           .setParameter("member", selectedMember)
           .setMaxResults(5)
           .getResultList();
   }
   
   /**
    * Used to read all blog entries for a member
    */
   @Factory("memberBlogs")
   public void getMemberBlogs()
   {
      memberBlogs = entityManager.createQuery(
            "from MemberBlog b where b.member.name = :name order by b.entryDate desc")
            .setParameter("name", name)
            .getResultList();
   }
   
   /**
    * Used to read a single blog entry for a member
    */
   @Factory("selectedBlog")
   public void getBlog()
   {
      try
      {
         selectedBlog = (MemberBlog) entityManager.createQuery(
           "from MemberBlog b where b.blogId = :blogId and b.member.name = :name")
           .setParameter("blogId", blogId)
           .setParameter("name", name)
           .getSingleResult();
      }
      catch (NoResultException ex) { }
   }
   
   @Remove @Destroy
   public void destroy() { }     
}
