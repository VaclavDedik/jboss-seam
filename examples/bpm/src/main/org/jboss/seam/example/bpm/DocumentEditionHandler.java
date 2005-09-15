package org.jboss.seam.example.bpm;

import org.jboss.seam.annotations.CompleteTask;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.ejb.SeamInterceptor;

import javax.ejb.Remove;
import javax.ejb.Stateless;
import javax.ejb.Interceptor;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
@Stateless
@Name( "documentEditor" )
@LoggedIn
@Interceptor( SeamInterceptor.class )
public class DocumentEditionHandler implements DocumentEdition
{
   @PersistenceContext
   private EntityManager entityManager;

   @In
   @Out
   private Document document;

   public String save()
   {
      document = entityManager.merge( document );
      return "detail";
   }


   @CompleteTask
   @Remove
   public String approve()
   {
      document.approve( null );
      document = entityManager.merge( document );
      return "approved, approved";
   }

   @CompleteTask
   @Remove
   public String reject()
   {
      document.reject( null );
      document = entityManager.merge( document );
      return "rejected, rejected";
   }
}
