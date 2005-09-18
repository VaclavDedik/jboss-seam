package org.jboss.seam.example.bpm;

import java.io.Serializable;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.ejb.SeamInterceptor;

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
   private Long documentId;

   @Out
   private Document document;

   @BeginTask
   public String details()
   {
      document = entityManager.find( Document.class, documentId );
      return "detail";
   }

}
