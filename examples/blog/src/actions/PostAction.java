package actions;

import java.io.IOException;
import java.util.Date;

import javax.persistence.EntityManager;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Valid;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.core.FacesMessages;

import domain.Blog;
import domain.BlogEntry;

@Name("postAction")
//@Scope(ScopeType.STATELESS)
public class PostAction
{
   @In(create=true) private Blog blog;
   @In(create=true) private EntityManager entityManager;
   
   @In(required=false) 
   @Out(scope=ScopeType.EVENT)
   @Valid
   private BlogEntry newBlogEntry;
   
   @NotNull @Length(min=5, max=50)
   private String password;
   
   @In(create=true) FacesMessages facesMessages;
   
   @Factory("newBlogEntry")
   public void createBlogEntry()
   {
      newBlogEntry = new BlogEntry(blog);
   }
   
   @IfInvalid(outcome=Outcome.REDISPLAY)
   public String post() throws IOException
   {
      if ( blog.getPassword().equals(password) )
      {
         newBlogEntry.setDate( new Date() );
         blog.getBlogEntries().add(newBlogEntry);
         entityManager.persist(newBlogEntry);
         return "success";
      }
      else
      {
         facesMessages.add("incorrect password");
         return null;
      }
   }

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }
   
}
