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
import org.jboss.seam.security.Identity;

@Stateful
@Name("profile")
@Scope(ScopeType.EVENT)
public class ProfileAction implements ProfileLocal
{
   @RequestParameter
   private String name;

   @Out(required = false)
   private Member selectedMember;
   
   @Out(required = false)
   private List newMembers;
   
   @In(create=true)
   private EntityManager entityManager;

   @Factory("selectedMember")
   public void display()
   {      
      if (name == null && Identity.instance().isLoggedIn())
      {
         selectedMember = (Member) entityManager.createQuery(
               "from Member where username = :username")
               .setParameter("username", Identity.instance().getPrincipal().getName())
               .getSingleResult();
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
