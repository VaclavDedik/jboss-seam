package org.jboss.seam.example.bpm;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.CompleteTask;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.ResumeTask;
import org.jboss.seam.ejb.SeamInterceptor;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
@Stateless
@Name( "documentTasks" )
@LoggedIn
@Interceptor( SeamInterceptor.class )
public class DocumentTaskHandler implements DocumentTask
{
   @PersistenceContext
   private EntityManager entityManager;

   @In(required=false) @Out
   private Document document;

   @In(required=false)
   private Long documentId;

   @In
   private User user;

   @ResumeTask
   public String details()
   {
      document = entityManager.find( Document.class, documentId );
      return "review";
   }

   @CompleteTask
   public String approve()
   {
      document.approve( user );
      document = entityManager.merge( document );
      return "approved";
   }

   @CompleteTask
   public String reject()
   {
      document.reject( user );
      document = entityManager.merge( document );
      return "rejected";
   }
}
