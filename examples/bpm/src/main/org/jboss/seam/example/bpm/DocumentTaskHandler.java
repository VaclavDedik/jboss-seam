package org.jboss.seam.example.bpm;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.EndTask;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.core.Transition;
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
   
   @In(create=true)
   private Transition transition;

   @In
   private User user;

   @BeginTask
   public String details()
   {
      document = entityManager.find( Document.class, documentId );
      return "review";
   }

   @EndTask
   public String approve()
   {
      document.approve( user );
      document = entityManager.merge( document );
      transition.setName("approve");
      return "approved";
   }

   @EndTask
   public String reject()
   {
      document.reject( user );
      document = entityManager.merge( document );
      transition.setName("reject");
      return "rejected";
   }
}
