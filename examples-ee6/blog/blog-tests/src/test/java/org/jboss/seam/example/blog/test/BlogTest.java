package org.jboss.seam.example.blog.test;

import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.security.Identity;
import org.jboss.seam.theme.Theme;
import org.jboss.seam.theme.ThemeSelector;
import org.jboss.seam.transaction.UserTransaction;

import actions.BlogService;
import actions.EntryAction;
import actions.PostAction;
import actions.SearchService;
import domain.Blog;
import domain.BlogEntry;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BlogTest
{

   @Deployment(name="BookingTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive er = Deployments.bookingDeployment();
      WebArchive web = er.getAsType(WebArchive.class, "blog-web.war");

      web.addClasses(BlogTest.class);

      return er;
   }

   @Test
   public void testPost() throws Exception
   {
      Lifecycle.beginCall();
      Identity identity = Identity.instance();
      identity.setPassword("tokyo");
      identity.authenticate();

      BlogEntry entry = (BlogEntry) Component.getInstance("blogEntry");
      entry.setId("testing");
      entry.setTitle("Integration testing Seam applications is easy!");
      entry.setBody("This post is about Arquillian...");
      
      UserTransaction transaction = (UserTransaction)Component.getInstance("org.jboss.seam.transaction.transaction");
      transaction.begin();
      PostAction postAction = (PostAction)Component.getInstance("postAction");
      postAction.post();
      transaction.commit();
      
      Lifecycle.endCall();

      Lifecycle.beginCall();
      
      List<BlogEntry> blogEntries = ( (Blog) Component.getInstance(BlogService.class) ).getBlogEntries();
      assertEquals(4, blogEntries.size());
      
      BlogEntry blogEntry = blogEntries.get(0);
      assertEquals("testing", blogEntry.getId());
      assertEquals("This post is about Arquillian...", blogEntry.getBody());
      assertEquals("Integration testing Seam applications is easy!", blogEntry.getTitle());

      transaction = (UserTransaction)Component.getInstance("org.jboss.seam.transaction.transaction");
      transaction.begin();
      ( (EntityManager) Component.getInstance("entityManager") ).createQuery("delete from BlogEntry where id='testing'").executeUpdate();
      transaction.commit();
      
      Lifecycle.endCall();
   }
   
   @Test
   public void testLatest() throws Exception
   {
      assertEquals(3, ( (Blog) Component.getInstance(BlogService.class) ).getBlogEntries().size());
   }
   
   @Test
   public void testEntry() throws Exception
   {
      EntryAction entryAction = (EntryAction)Component.getInstance("entryAction");
      entryAction.loadBlogEntry("seamtext");
      
      BlogEntry blogEntry = (BlogEntry) Contexts.getEventContext().get("blogEntry");
      assertNotNull(blogEntry);
      assertEquals("seamtext", blogEntry.getId());

      // make sure the entry is really there
      assertTrue(blogEntry.getBody().length() > 0);
      assertEquals("Introducing Seam Text", blogEntry.getTitle());
   }
   
   @Test
   public void testSearch() throws Exception
   {
      SearchService searchService = (SearchService)Component.getInstance("searchService");
      searchService.setSearchPattern("seam text");
      
      List<BlogEntry> results = (List<BlogEntry>) Component.getInstance("searchResults");
      assertEquals("seamtext", results.get(0).getId());
   }
   
   @Test
   @Ignore // themeSelector.select uses FacesContext, which isn't available in a test
   public void testSelectTheme() throws Exception
   {
      ThemeSelector themeSelector = ThemeSelector.instance();
      List<SelectItem> themes = (List<SelectItem>) themeSelector.getThemes();
      assertEquals(3, themes.size());
      assertEquals("default", themes.get(0).getLabel());
      assertEquals("default", themeSelector.getTheme());

      Map<String, String> theme = Theme.instance();
      assertEquals("template.xhtml", theme.get("template"));
      // we can't do interpolate the value correctly in these tests
      assertEquals("/screen.css", theme.get("css"));
      assertEquals("foo", theme.get("foo"));
      
      themeSelector.setTheme("accessible");
      themeSelector.select();

      assertEquals("accessible", themeSelector.getTheme());
      
      theme = Theme.instance();
      assertEquals("/accessible.css", theme.get("css"));
      assertEquals("template.xhtml", theme.get("template"));
       
      
      themeSelector.selectTheme("printable");
      assertEquals("printable", themeSelector.getTheme());
      
      theme = Theme.instance();
      assertEquals("/printable.css", theme.get("css"));
      assertEquals("print.xhtml", theme.get("template"));
      
      theme = Theme.instance();
      assertEquals(2, theme.entrySet().size());
      
      themeSelector.setTheme("foo");
      themeSelector.select();
      assertEquals("foo", themeSelector.getTheme());
      theme = Theme.instance();
      ResourceBundle themeResources = ThemeSelector.instance().getThemeResourceBundle();
      assertFalse(themeResources.getKeys().hasMoreElements());
      assertEquals(0, theme.entrySet().size());
      boolean exception = false;
      try
      {
         themeResources.getObject("bar");
      }
      catch (MissingResourceException e) 
      {
         exception = true; 
      }
      assertTrue(exception);
      assertEquals("bar", theme.get("bar"));
   }

}
