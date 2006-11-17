package actions;

import java.io.IOException;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import domain.Blog;
import domain.BlogEntry;

/**
 * Handles submission of a new blog entry
 * 
 * @author Gavin King
 */
@Name("postAction")
@Scope(ScopeType.STATELESS)
public class PostAction
{
   @In(create=true) Blog blog;
   
   @In EntityManager entityManager;
   
   @In(required=false) BlogEntry blogEntry;
   
   public String post() throws IOException
   {
      blogEntry.setDate( new Date() );
      blog.getBlogEntries().add(blogEntry);
      entityManager.persist(blogEntry);
      return "/index.xhtml";
   }
   
}
