package com.jboss.dvd.seam.test;

import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.jboss.seam.*;
import org.jboss.seam.contexts.*;
import org.jboss.seam.core.*;
import org.jboss.seam.mock.*;

import org.testng.annotations.Test;

import com.jboss.dvd.seam.*;


public class SearchTest 
    extends SeamTest
{
   
    @Test
    public void testSomething() 
        throws Exception
    {
        
        String id =  new Script() {
            Search search;
                
            @Override
            protected void applyRequestValues()
            {
                Customer customer = new Customer();
                customer.setFirstName("Joe");
                customer.setLastName("User");

                Contexts.getSessionContext().set("loggedIn", true);
                Contexts.getSessionContext().set("currentUser", customer);
            }
                
            @Override
            protected void updateModelValues() throws Exception
            {
                search = (Search) Component.getInstance("search", true);
            }

            @Override
            protected void invokeApplication()
            {
                String outcome = search.doSearch();
                assert "browse".equals(outcome);
            }
               
            @Override
            protected void renderResponse()
            {
                ListDataModel model = (ListDataModel) Contexts.getConversationContext().get("searchResults");
                assert model.getRowCount()==15;
                assert Manager.instance().isLongRunningConversation();
            }               
        }.run();
    }
    
    @Override
    public void initServletContext(Map initParams)
    {
        initParams.put(Init.COMPONENT_CLASSES, Ejb.class.getName());
        initParams.put(Init.JNDI_PATTERN, "#{ejbName}/local");
    }
}
