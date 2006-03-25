package actions;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * Provides access to blogs.
 *
 * @author Gavin King
 */
@Name("searchAction")
public class SearchAction 
{
   
   private String searchPattern;
   
   @In private FacesContext facesContext;
   
   public void search() throws IOException
   {
      String searchUrl = facesContext.getApplication().getViewHandler()
            .getActionURL( facesContext, "/search.xhtml" ) 
                  + "?searchPattern=" 
                  + searchPattern;
      facesContext.getExternalContext().redirect( facesContext.getExternalContext().encodeActionURL(searchUrl) );
      facesContext.responseComplete();
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
