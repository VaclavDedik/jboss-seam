package org.jboss.seam.example.bpm;

import static org.jboss.seam.ScopeType.PROCESS;

import java.util.Date;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.validator.Valid;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.CreateProcess;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Outcome;
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

   @In
   private User user;

   @In @Out @Valid
   private Document document;
   
   @Out(scope=PROCESS, required=false)
   private Long documentId;
   @Out(scope=PROCESS, required=false)
   private String description;
   @Out(scope=PROCESS, required=false)
   private String submitter;

   @CreateProcess( definition = "DocumentSubmission" )
   @IfInvalid(outcome=Outcome.REDISPLAY)
   @Begin
   public String create()
   {
      document.setSubmitter( user );
      document.setSubmittedTimestamp( new Date() );
      entityManager.persist( document );
      
      documentId = document.getId();
      description = document.getTitle();
      submitter = document.getSubmitter().getUsername();
      
      return "detail";
   }

   @IfInvalid(outcome=Outcome.REDISPLAY)
   public String save()
   {
      document = entityManager.merge( document );
      return "detail";
   }

}
