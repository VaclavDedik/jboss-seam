package domain;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;

/**
 * Provides access to blogs.
 *
 * @author    Simon Brown
 */
@Name("blog")
@Startup
@Scope(ScopeType.APPLICATION)
public class BlogService 
{
   
   private Blog blog;

   @Create
   public void initBlog()
   {
      blog = new Blog();
      blog.setName("Webapp framework blog");
      blog.setDescription("Comparison of J2EE web application frameworks");
      blog.setLocale(new Locale("en", "AU"));
      blog.setTimeZone(TimeZone.getTimeZone("PST"));
      blog.addBlogEntry(new BlogEntry("3", "Iñtërnâtiônàlízætiøn", "<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>", "<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>", new Date(1130963020998L)));
      blog.addBlogEntry(new BlogEntry("2", "\u6807\u9898", null, "<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>", new Date(1130844685580L)));
      blog.addBlogEntry(new BlogEntry("1", "Title", null, "<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>", new Date(1130838311844L)));
   }
  
  @Unwrap
  public Blog getBlog()
  {
     return blog;
  }

}
