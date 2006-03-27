package actions;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.annotations.Unwrap;

import domain.BlogEntry;

/**
 * Pulls the search results
 *
 * @author Gavin King
 */
@Name("searchResults")
public class SearchService 
{
   
   @In(create=true)
   private EntityManager entityManager;
   
   @RequestParameter
   private String searchPattern;
   
   private List<BlogEntry> searchResults;
   
   @Create
   public void initSearchResults()
   {
      searchResults = entityManager.createQuery("from BlogEntry be where lower(be.title) like :searchPattern or lower(be.body) like :searchPattern order by be.date desc")
            .setParameter( "searchPattern", getSqlSearchPattern() )
            .setMaxResults(100)
            .getResultList();
   }

   private String getSqlSearchPattern()
   {
      return searchPattern==null ? "" : '%' + searchPattern.toLowerCase().replace('*', '%').replace('?', '_') + '%';
   }

   @Unwrap
   public List<BlogEntry> getSearchResults()
   {
      return searchResults;
   }

}
