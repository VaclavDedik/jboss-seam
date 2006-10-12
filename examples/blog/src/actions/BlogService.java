package actions;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

import domain.Blog;

/**
 * Singleton for the blog instance.
 *
 * @author Gavin King
 */
@Name("blog")
@Scope(ScopeType.STATELESS)
public class BlogService 
{
   
   @In(create=true)
   private EntityManager entityManager;
  
   @Unwrap
   public Blog getBlog()
   {
      return (Blog) entityManager.createQuery("select b from Blog b left join fetch b.blogEntries")
            .setHint("org.hibernate.cacheable", true)
            .getSingleResult();
   }

}
