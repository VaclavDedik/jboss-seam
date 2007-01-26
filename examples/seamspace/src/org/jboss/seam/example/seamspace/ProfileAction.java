package org.jboss.seam.example.seamspace;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.annotations.Scope;

@Stateful
@Name("profile")
@Scope(ScopeType.EVENT)
public class ProfileAction implements ProfileLocal
{
   @RequestParameter
   private String name;

   @Out(required = false)
   private Member selectedMember;
   
   @In(required = false)
   private Member authenticatedMember;
   
   @Out(required = false)
   private List newMembers;
   
   @Out(required = false)
   private List memberBlogs;   
   
   @In(create=true)
   private EntityManager entityManager;

   @Factory("selectedMember")
   public void display()
   {      
      if (name == null && authenticatedMember != null)
      {
         selectedMember = (Member) entityManager.find(Member.class, 
               authenticatedMember.getMemberId());
      }
      else if (name != null)
      {
         try
         {
            selectedMember = (Member) entityManager.createQuery(
            "from Member where memberName = :memberName")
            .setParameter("memberName", name)
            .getSingleResult(); 
         }
         catch (NoResultException ex) { }
      }
   }
   
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
      if (name == null && authenticatedMember != null)
      {
         name = authenticatedMember.getMemberName();
      }      
      
      memberBlogs = entityManager.createQuery(
            "from MemberBlog b where b.member.memberName = :memberName order by b.entryDate desc")
            .setParameter("memberName", name)
            .getResultList();
   }   
   
   @Factory("newMembers")
   public void newMembers()
   {
      newMembers = entityManager.createQuery(
            "from Member order by memberSince desc")
            .setMaxResults(3)
            .getResultList();
   }
   
   @Remove @Destroy
   public void destroy() { }   
}
