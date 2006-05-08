package test;

import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Redirect;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

import actions.BlogService;
import actions.EntryAction;
import actions.SearchAction;
import actions.SearchService;
import domain.Blog;
import domain.BlogEntry;

public class BlogTest extends SeamTest
{
   
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
            ( (EntryAction) Component.getInstance(EntryAction.class, true) ).getBlogEntry();
         }

         @Override
         protected void renderResponse() throws Exception
         {
            BlogEntry blogEntry = (BlogEntry) Contexts.getEventContext().get("blogEntry");
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
            ( (SearchAction) Component.getInstance(SearchAction.class, true) ).setSearchPattern("seam");
         }

         @Override
         protected void invokeApplication() throws Exception
         {
            ( (SearchAction) Component.getInstance(SearchAction.class, false) ).search();
            assert Redirect.instance().getViewId().equals("/search.xhtml");
            assert Redirect.instance().getParameters().get("searchPattern").equals("seam");
            assert FacesContext.getCurrentInstance().getResponseComplete();
         }
         
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
   public void initServletContext(Map initParams)
   {
      initParams.put(Init.COMPONENT_CLASSES, "org.jboss.seam.core.Ejb");
      initParams.put(Init.JNDI_PATTERN, "#{ejbName}/local");
      initParams.put(Init.MANAGED_PERSISTENCE_CONTEXTS, "entityManager");
      initParams.put("entityManager.persistenceUnitJndiName", "java:/blogEntityManagerFactory");
   }

}
