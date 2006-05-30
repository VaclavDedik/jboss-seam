package com.jboss.dvd.seam.test;

import java.util.*;
import javax.faces.model.*;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

import com.jboss.dvd.seam.*;

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
