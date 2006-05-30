package com.jboss.dvd.seam.test;

import java.util.*;
import javax.faces.model.*;
import org.testng.annotations.Test;

import com.jboss.dvd.seam.*;

import static org.testng.AssertJUnit.*;

public class BestSellersTest 
    extends BaseTest
{
    @Test
    public void testTopProducts() 
        throws Exception
    {
        
        String id =  new Script() {
            protected void renderResponse()
            {
                ListDataModel model = (ListDataModel) getInstance("topProducts");

                assertNotNull("topProducts", model);
                assertEquals("topProducts size",  8, model.getRowCount());

                // check sort order
                // check data against import.sql?
            }               
        }.run();
    }

}
