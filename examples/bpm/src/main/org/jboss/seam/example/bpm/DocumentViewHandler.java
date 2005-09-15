package org.jboss.seam.example.bpm;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.ResumeTask;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.ejb.SeamInterceptor;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
@Stateless
@Name( "documentViewer" )
@LoggedIn
@Interceptor( SeamInterceptor.class )
public class DocumentViewHandler implements DocumentView, Serializable
{
   @PersistenceContext
   private EntityManager entityManager;

   @In
   private String documentId;

   @Out
   private Document document;

   @ResumeTask
   public String details()
   {
      document = entityManager.find( Document.class, documentId );
      return "detail";
   }

}
