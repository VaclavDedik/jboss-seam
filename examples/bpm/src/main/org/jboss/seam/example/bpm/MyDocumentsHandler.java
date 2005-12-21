package org.jboss.seam.example.bpm;

import java.util.List;

import javax.ejb.Interceptor;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
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
   @PersistenceContext
   private EntityManager entityManager;

   @Out(required=false)
   private Document document;
   
   @DataModel
   private List<Document> documents;
   @DataModelSelectionIndex
   private int documentIndex;

   @In
   private User user;
   
   @Begin @Factory("documents")
   public void find()
   {
      documents = entityManager.createNamedQuery("myDocuments")
            .setParameter( "user", user )
            .getResultList();
   }
   
   public String select()
   {
      document = documents.get(documentIndex);
      return "detail";
   }
   
   @Destroy @Remove
   public void destroy() {}
}
