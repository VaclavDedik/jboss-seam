package test;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.RenderParameters;
import org.jboss.seam.jsf.SeamExtendedManagedPersistencePhaseListener;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

import actions.BlogService;
import actions.EntryAction;
import actions.PostAction;
import actions.SearchService;
import domain.Blog;
import domain.BlogEntry;

public class BlogTest extends SeamTest
{
   
   @Test
   public void testPost() throws Exception
   {
      
      new Script()
      {

         @Override
         protected void updateModelValues() throws Exception
         {
            BlogEntry entry = (BlogEntry) Component.getInstance("blogEntry");
            entry.setId("testing");
            entry.setTitle("Integration testing Seam applications");
            entry.setBody("This post is about SeamTest...");
         }
         
         @Override
         protected void invokeApplication() throws Exception
         {
            PostAction action = (PostAction) Component.getInstance(PostAction.class);
            assert action.post().equals("/index.xhtml");
         }
         
      }.run();

      new Script()
      {

         @Override
         protected boolean isGetRequest()
         {
            return true;
         }

         @Override
         protected void renderResponse() throws Exception
         {
            List<BlogEntry> blogEntries = ( (Blog) Component.getInstance(BlogService.class, true) ).getBlogEntries();
            assert blogEntries.size()==4;
            BlogEntry blogEntry = blogEntries.get(0);
            assert blogEntry.getId().equals("testing");
            assert blogEntry.getBody().equals("This post is about SeamTest...");
            assert blogEntry.getTitle().equals("Integration testing Seam applications is easy!");
         }

      }.run();
      
      new Script()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            ( (EntityManager) getInstance("entityManager") ).createQuery("delete from BlogEntry where id='testing'").executeUpdate();
         }  
      }.run();

   }
   
   @Test
   public void testLatest() throws Exception
   {
      new Script()
      {

         @Override
         protected boolean isGetRequest()
         {
            return true;
         }

         @Override
         protected void renderResponse() throws Exception
         {
            assert ( (Blog) Component.getInstance(BlogService.class, true) ).getBlogEntries().size()==3;
         }
         
      }.run();
   }
   
   @Test
   public void testEntry() throws Exception
   {
      new Script()
      {
         
         @Override
         protected void setup()
         {
            getParameters().put("blogEntryId", new String[] {"i18n"});
         }

         @Override
         protected boolean isGetRequest()
         {
            return true;
         }

         //TODO: workaround for the fact that page actions don't get called!
         @Override
         protected void invokeApplication() throws Exception
         {
            ( (EntryAction) Component.getInstance(EntryAction.class, true) ).loadBlogEntry("i18n");
         }

         @Override
         protected void renderResponse() throws Exception
         {
            BlogEntry blogEntry = (BlogEntry) Contexts.getEventContext().get("blogEntry");
            assert blogEntry!=null;
            assert blogEntry.getId().equals("i18n");
         }
         
      }.run();
   }
   
   @Test
   public void testSearch() throws Exception
   {
      String id = new Script()
      {
         
         @Override
         protected void updateModelValues() throws Exception
         {
            ( (Map) Component.getInstance(RenderParameters.class, true) ).put("searchPattern", "seam");
         }
         
         @Override
         protected String getInvokeApplicationOutcome()
         {
            return "/search.xhtml";
         }

         @Override
         protected void renderResponse() { assert false; }
         
      }.run();

      new Script(id)
      {
         
         @Override
         protected boolean isGetRequest()
         {
            return true;
         }

         @Override
         protected void setup()
         {
            getParameters().put("searchPattern", new String[]{"seam"});
         }

         @Override
         protected void renderResponse() throws Exception
         {
            List<BlogEntry> results = (List<BlogEntry>) Component.getInstance(SearchService.class, true);
            assert results.size()==1;
         }
         
      }.run();
   }

   @Override
   protected SeamPhaseListener createPhaseListener()
   {
      return new SeamExtendedManagedPersistencePhaseListener();
   }

}
