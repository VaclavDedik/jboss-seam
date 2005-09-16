package org.jboss.seam.example.bpm;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.CreateProcess;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.ejb.SeamInterceptor;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
@Stateless
@Name( "documentCreator" )
@LoggedIn
@Interceptor( SeamInterceptor.class )
public class DocumentCreationHandler implements DocumentCreation
{
   @PersistenceContext
   private EntityManager entityManager;

//   @In
//   private User user;

   @In
   private Document document;

   @Begin
   @CreateProcess( definition = "DocumentSubmission" )
   public String create() throws Exception
   {
//      document.setSubmitter( user );
//      document.setSubmittedTimestamp( new Date() );
      entityManager.persist( document );
      Contexts.getBusinessProcessContext().set( "documentId", document.getId() );
      Contexts.getBusinessProcessContext().set( "description", document.getTitle() );
      return "success";
   }
}
