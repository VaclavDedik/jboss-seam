package org.jboss.seam.example.seamspace;

import java.util.Date;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.security.Identity;

@Stateful
@Name("friend")
public class FriendAction implements FriendLocal
{
   @RequestParameter("name")
   private String name;
   
   @Out(required = false)
   private FriendComment friendComment;
   
   @In(required = false)
   private Member authenticatedMember;
   
   @In(create = true)
   private EntityManager entityManager;
   
   @Factory("friendComment") @Begin
   public void createComment()
   {      
      try
      {
         Member member = (Member) entityManager.createQuery(
         "from Member where memberName = :memberName")
         .setParameter("memberName", name)
         .getSingleResult();
                  
         Contexts.getMethodContext().set("friends", member.getFriends());
         Identity.instance().checkRestriction("#{s:hasPermission('friendComment', 'create', friends)}");

         friendComment = new FriendComment();
         friendComment.setFriend(authenticatedMember);
         friendComment.setMember(member);
      }
      catch (NoResultException ex) 
      { 
         FacesMessages.instance().add("Member not found.");
      }
   }
   
   @End
   public void saveComment()
   {
      friendComment.setCommentDate(new Date());
      entityManager.persist(friendComment);
   }
   
   @Remove @Destroy
   public void destroy() { }    
}
