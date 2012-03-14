package com.jboss.dvd.seam.test;


import java.io.File;

import javax.faces.model.ListDataModel;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jboss.dvd.seam.FullTextSearch;
import com.jboss.dvd.seam.Product;

@RunWith(Arquillian.class)
public class SearchTest 
   extends JUnitSeamTest
{   
   @Deployment(name = "SearchTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive er = ShrinkWrap.create(ZipImporter.class, "seam-dvdstore.ear").importFrom(new File("../dvdstore-ear/target/seam-dvdstore.ear")).as(EnterpriseArchive.class);
      WebArchive web = er.getAsType(WebArchive.class, "dvdstore-web.war");
      web.addClasses(SearchTest.class);

      return er;
   }
   
    @Test
    public void testNoParamSearch() 
        throws Exception
    {
        
        new FacesRequest() {
           FullTextSearch search;
            @Override
            protected void updateModelValues()
            {
                search = (FullTextSearch) getInstance("search");
                search.setSearchQuery("king");
            }
            @Override
            protected void invokeApplication()
            {
                String outcome = search.doSearch();
                Assert.assertEquals("search outcome", "browse", outcome);
            }
            @Override
            protected void renderResponse()
            {
                ListDataModel model = (ListDataModel) lookup("searchResults");
                //exact number of matches depends on search algorithm,
                //so we only check that at least something was found:
                Assert.assertEquals("should have found something",model.isRowAvailable());
                Product firstMatch = (Product) model.getRowData();
                Assert.assertEquals("at least top match should have keyword in title",
                      firstMatch.getTitle().toLowerCase().contains("king"));
                Assert.assertEquals("in conversation", isLongRunningConversation());
            }
        }.run();
    }
    
}
