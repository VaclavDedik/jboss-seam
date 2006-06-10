package com.jboss.dvd.seam.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import javax.faces.model.ListDataModel;

import org.testng.annotations.Test;

import com.jboss.dvd.seam.Search;

public class SearchTest 
    extends BaseTest
{   
    @Test
    public void testNoParamSearch() 
        throws Exception
    {
        
        String id =  new Script() {
            Search search;
                
            protected void updateModelValues()
            {
                search = (Search) getInstance("search");
            }

            protected void invokeApplication()
            {
                String outcome = search.doSearch();
                assertEquals("search outcome", "browse", outcome);
            }
               
            protected void renderResponse()
            {
                ListDataModel model = (ListDataModel) lookup("searchResults");
                assertEquals("page size", 15, model.getRowCount());
                assertTrue("in conversation", inConversation());
            }               
        }.run();
    }
    
}
