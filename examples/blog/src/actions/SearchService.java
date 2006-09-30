package actions;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import domain.BlogEntry;

/**
 * Pulls the search results
 *
 * @author Gavin King
 */
@Name("searchService")
public class SearchService 
{
   
   @In(create=true)
   private EntityManager entityManager;
   
   private String searchPattern;
   
   @Factory("searchResults")
   public List<BlogEntry> getSearchResults()
   {
      if (searchPattern==null)
      {
         return null;
      }
      else
      {
         return entityManager.createQuery("from BlogEntry be where lower(be.title) like :searchPattern or lower(be.body) like :searchPattern order by be.date desc")
               .setParameter( "searchPattern", getSqlSearchPattern() )
               .setMaxResults(100)
               .getResultList();
      }
   }

   private String getSqlSearchPattern()
   {
      return searchPattern==null ? "" : '%' + searchPattern.toLowerCase().replace('*', '%').replace('?', '_') + '%';
   }

   public String getSearchPattern()
   {
      return searchPattern;
   }

   public void setSearchPattern(String searchPattern)
   {
      this.searchPattern = searchPattern;
   }

}
