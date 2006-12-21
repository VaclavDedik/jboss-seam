package org.jboss.seam.example.seamspace;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.security.Identity;

@Stateless
@Name("profile")
public class ProfileAction implements ProfileLocal
{
   @RequestParameter
   private String name;

   @Out(required = false)
   private Member selectedMember;
   
   @In(create=true)
   private EntityManager entityManager;

   @Factory("selectedMember")
   public void display()
   {
      if (name == null)
      {
         selectedMember = (Member) entityManager.createQuery(
               "from Member where username = :username")
               .setParameter("username", Identity.instance().getName())
               .getSingleResult();
      }
      else
      {
         try
         {
            selectedMember = (Member) entityManager.createQuery(
            "from Member where name = :name")
            .setParameter("name", name)
            .getSingleResult();
         }
         catch (NoResultException ex) 
         {
            FacesMessages.instance().add("The member name you specified does not exist.");
         }   
      }
   }
}
