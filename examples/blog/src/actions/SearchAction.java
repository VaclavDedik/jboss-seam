package actions;

import java.io.IOException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Redirect;

/**
 * Handles submission of the search box,
 * and redirect to the results page.
 *
 * @author Gavin King
 */
@Name("searchAction")
public class SearchAction 
{
   
   @In(create=true) 
   private Redirect redirect;
   
   private String searchPattern;
   
   public void search() throws IOException
   {
      redirect.setViewId("/search.xhtml");
      redirect.setParameter("searchPattern", searchPattern);
      redirect.execute();
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
