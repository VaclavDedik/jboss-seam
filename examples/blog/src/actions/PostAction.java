package actions;

import java.io.IOException;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.cache.CacheException;
import org.jboss.cache.aop.PojoCache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.FacesMessages;

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
   
   @In PojoCache pojoCache;
   
   public void post() throws IOException, CacheException
   {
      blogEntry.setDate( new Date() );
      blog.getBlogEntries().add(blogEntry);
      entityManager.persist(blogEntry);
      pojoCache.remove("pageFragments", "index");
   }
   
   public void invalid()
   {
      FacesMessages.instance().add("You are missing some information, please try again");
   }
   
}
