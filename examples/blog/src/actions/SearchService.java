package actions;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.Session;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;

import domain.BlogEntry;

/**
 * Pulls the search results
 *
 * @author Gavin King
 */
@Name("searchService")
public class SearchService 
{
   
   @In
   private EntityManager entityManager;
   
   private String searchPattern;
   
   @Factory("searchResults")
   public List<BlogEntry> getSearchResults()
   {
      if (searchPattern==null)
      {
         return null;
      }
      else if ("".equals(searchPattern) ) {
         searchPattern = null;
         return entityManager.createQuery("select be from BlogEntry be order by date desc").getResultList();
      }
      else
      {
         Map<String,Float> boostPerField = new HashMap<String,Float>();
         boostPerField.put( "title", 4f );
         boostPerField.put( "body", 1f );
         String[] productFields = {"title", "body"};
         QueryParser parser = new MultiFieldQueryParser(productFields, new StandardAnalyzer(), boostPerField);
         parser.setAllowLeadingWildcard(true);
         org.apache.lucene.search.Query luceneQuery;
         try
         {
            luceneQuery = parser.parse(searchPattern);
         }
         catch (ParseException e)
         {
            return null;
         }
         FullTextSession session = (FullTextSession) entityManager.getDelegate();
         return session.createFullTextQuery(luceneQuery, BlogEntry.class)
               .setMaxResults(100)
               .list();
      }
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
