package org.jboss.seam.example.bpm;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.CreateProcess;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.ejb.SeamInterceptor;

import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.ejb.Stateless;
import javax.ejb.Interceptor;
import java.util.Date;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
@Stateless
@Name( "documentCreator" )
@Interceptor( SeamInterceptor.class )
public class DocumentCreationHandler implements DocumentCreation
{
   @PersistenceContext
   private EntityManager entityManager;

//   @In
//   private User user;

   @In
   private Document document;

   @CreateProcess( definition = "DocumentSubmission" )
   public String create() throws Exception
   {
//      document.setSubmitter( user );
//      document.setSubmittedTimestamp( new Date() );
      entityManager.persist( document );
      return "success";
   }
}
