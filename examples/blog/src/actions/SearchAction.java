package actions;

import org.jboss.seam.annotations.Name;

/**
 * Handles submission of the search box,
 * and redirect to the results page.
 *
 * @author Gavin King
 */
@Name("searchAction")
public class SearchAction 
{
   
   private String searchPattern;
   
   public String getSearchPattern()
   {
      return searchPattern;
   }

   public void setSearchPattern(String searchPattern)
   {
      this.searchPattern = searchPattern;
   }

   public String search()
   {
      return "/search.xhtml?searchPattern=#{searchAction.searchPattern}";
   }

}
