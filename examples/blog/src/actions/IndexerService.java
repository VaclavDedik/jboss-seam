//$Id$

import java.util.List;
import javax.ejb.Stateful;
import javax.ejb.Remove;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.ScopeType;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.Session;

/**
 * Index Blog entry at startup
 *
 * @author Emmanuel Bernard
 */
@Name("indexerService")
@Scope(ScopeType.APPLICATION)
@Startup
public class IndexerService
{
   @In
   private EntityManager entityManager;

   @Create
   public void index() {
      List blogEntries = entityManager.createQuery("select be from BlogEntry be").getResultList();
      FullTextSession session = (FullTextSession) entityManager.getDelegate();
      for (Object be : blogEntries) {
         session.index(be);   
      }
   }

   @Remove
   @Destroy
   public void stop() {}
}
