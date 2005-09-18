package org.jboss.seam.example.bpm;

import java.util.List;
import javax.ejb.Stateful;
import javax.ejb.Interceptor;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelectionIndex;
import org.jboss.seam.ejb.SeamInterceptor;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
@Stateful
@Name( "myDocuments" )
@Interceptor( SeamInterceptor.class )
@LoggedIn
public class MyDocumentsHandler implements MyDocuments
{
   private static final String QRY = "select d from Document as d where d.submitter = :user";

   @PersistenceContext
   private EntityManager entityManager;

   @Out(required=false)
   private Document document;

   @Out(required=false)
   private boolean editable;

   @DataModel
   private List<Document> documents;

   @DataModelSelectionIndex
   private int documentIndex;

   @In
   private User user;

   public String find()
   {
      documents = entityManager.createQuery( QRY ).setParameter( "user", user ).getResultList();
      return "myDocuments";
   }

   public String select()
   {
      document = documents.get( documentIndex );
      editable = document.getStatus() == Status.PENDING;
      return "detail";
   }
}
