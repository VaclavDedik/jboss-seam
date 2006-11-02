package com.jboss.dvd.seam.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import javax.faces.model.ListDataModel;

import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

import com.jboss.dvd.seam.Search;

public class SearchTest 
    extends SeamTest
{   
    @Test
    public void testNoParamSearch() 
        throws Exception
    {
        
        new FacesRequest() {
            Search search;
            @Override
            protected void updateModelValues()
            {
                search = (Search) getInstance("search");
            }
            @Override
            protected void invokeApplication()
            {
                String outcome = search.doSearch();
                assertEquals("search outcome", "browse", outcome);
            }
            @Override
            protected void renderResponse()
            {
                ListDataModel model = (ListDataModel) lookup("searchResults");
                assertEquals("page size", 15, model.getRowCount());
                assertTrue("in conversation", isLongRunningConversation());
            }               
        }.run();
    }
    
}
