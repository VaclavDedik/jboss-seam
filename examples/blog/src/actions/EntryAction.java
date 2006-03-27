package actions;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.annotations.Scope;

import domain.Blog;
import domain.BlogEntry;

/**
 * Processes a request for a particular entry,
 * and sends a 404 if none is found.
 * 
 * @author Gavin King
 */
@Name("entryAction")
@Scope(ScopeType.STATELESS)
public class EntryAction
{
   @In(create=true) private Blog blog;
   @In private FacesContext facesContext;
   
   @RequestParameter
   private String blogEntryId;
   
   @Out(scope=ScopeType.EVENT, required=false)
   private BlogEntry blogEntry;

   
   public void getBlogEntry() throws IOException
   {
      blogEntry = blog.getBlogEntry(blogEntryId);
      if (blogEntry==null)
      {
         HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
         response.sendError(HttpServletResponse.SC_NOT_FOUND, "Blog entry not found");
         facesContext.responseComplete();
      }
   }
   
}
