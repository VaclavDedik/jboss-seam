package org.jboss.seam.example.bpm;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.ResumeTask;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.CompleteTask;
import org.jboss.seam.ejb.SeamInterceptor;

import javax.ejb.Stateful;
import javax.ejb.Interceptor;
import javax.ejb.Remove;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
@Stateful
@Name("documentView")
@Interceptor( SeamInterceptor.class)
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
