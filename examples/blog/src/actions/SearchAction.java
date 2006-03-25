package actions;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import domain.BlogEntry;

/**
 * Provides access to blogs.
 *
 * @author    Gavin King
 */
@Name("searchAction")
@Scope(ScopeType.CONVERSATION)
public class SearchAction 
{
   
   @In(create=true)
   private EntityManager entityManager;

   private String searchPattern;
   
   private List<BlogEntry> searchResults;
   
   public void search()
   {
      searchResults = entityManager.createQuery("from BlogEntry be where lower(be.title) like :searchPattern or lower(be.body) like :searchPattern order by be.date desc")
            .setParameter( "searchPattern", getSqlSearchPattern() )
            .setMaxResults(100)
            .getResultList();
   }

   private String getSqlSearchPattern()
   {
      return '%' + searchPattern.toLowerCase().replace('*', '%').replace('?', '_') + '%';
   }

   public String getSearchPattern()
   {
      return searchPattern;
   }

   public void setSearchPattern(String searchPattern)
   {
      this.searchPattern = searchPattern;
   }

   public List<BlogEntry> getSearchResults()
   {
      return searchResults;
   }

}
