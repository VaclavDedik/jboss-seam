/**
 * 
 */
package org.jboss.seam.example.seamdiscs.test;

import javax.el.PropertyNotFoundException;

import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.framework.Home;
import org.jboss.seam.mock.DBUnitSeamTest;
import org.testng.annotations.Test;

/**
 * @author Pete Muir
 *
 */
public class EditTest extends DBUnitSeamTest{
    
    private static final String NEW_DESCRIPTION_1 = "A great band";

    @Override
    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/example/seamdiscs/test/BaseData.xml")
        );
    }
    
    @Test
    public void testEditArtist() throws Exception
    {
        final String cid = new FacesRequest("/artist.xhtml")
        {
            @Override
            protected void beforeRequest() 
            {
                setParameter("artistId", "1");
                setParameter("conversationPropagation", "join");
            }
            
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{identity.username}", "administrator");
                setValue("#{identity.password}", "administrator");
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                invokeAction("#{identity.login}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert (Boolean) getValue("#{identity.loggedIn}");
                assert ((Integer) 1).equals(getValue("#{artistHome.id}"));
                assert "Pink Floyd".equals(getValue("#{artist.name}"));
                assert getValue("#{artist.description}") == null;
                assert isLongRunningConversation();
                assert (Boolean) getValue("#{artistHome.managed}");
                
            }
        }.run();
        
        new FacesRequest("/artist.xhtml", cid)
        {      
            
            @Override
            protected void updateModelValues() throws Exception 
            {
                setValue("#{artist.description}", NEW_DESCRIPTION_1);
                assert isLongRunningConversation();
                assert cid.equals(getConversationId());
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                assert "updated".equals(invokeAction("#{artistHome.update}"));
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert NEW_DESCRIPTION_1.equals(getValue("#{artist.description"));
                assert isLongRunningConversation();
            }
            
        }.run();
    }
    
    @Test
    public void testAddArtist() throws Exception
    {
        final String cid = new FacesRequest("/artist.xhtml")
        {
            @Override
            protected void beforeRequest() 
            {
                setParameter("conversationPropagation", "join");
            }
            
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{identity.username}", "administrator");
                setValue("#{identity.password}", "administrator");
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                invokeAction("#{identity.login}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert (Boolean) getValue("#{identity.loggedIn}");
                assert null == getValue("#{artistHome.id}");
                assert null == getValue("#{artist.name}");
                assert isLongRunningConversation();
                assert (!(Boolean) getValue("#{artistHome.managed}"));
            }
        }.run();
        
        new FacesRequest("/artist.xhtml", cid)
        {      
            
            @Override
            protected void updateModelValues() throws Exception 
            {
                setValue("#{artist.name}", "Pete Muir");
                assert isLongRunningConversation();
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                assert "persisted".equals(invokeAction("#{artistHome.persist}"));
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert "Pete Muir".equals(getValue("#{artist.name}"));
            }
            
        }.run();
        
        new FacesRequest("/artists.xhtml", cid)
        {
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert new Long("7").equals(getValue("#{artists.resultCount}"));
                assert "Pete Muir".equals(getValue("#{artists.resultList[3].name}"));
                assert "Led Zepplin".equals(getValue("#{artists.resultList[4].name}"));
            }
            
        }.run();        
    }

    @Test
    public void testAddBand() throws Exception
    {
        final String cid = new FacesRequest("/artist.xhtml")
        {
            
            @Override
            protected void beforeRequest() 
            {
                setParameter("conversationPropagation", "join");
            }
            
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{identity.username}", "administrator");
                setValue("#{identity.password}", "administrator");
                setValue("#{artistHome.type}", "band");
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                invokeAction("#{identity.login}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert (Boolean) getValue("#{identity.loggedIn}");
                assert null == getValue("#{artistHome.id}");
                assert null == getValue("#{artist.name}");
                try
                {
                    assert ((Integer) 0).equals(getValue("#{artist.bandMembers.size}"));
                }
                catch (PropertyNotFoundException e) 
                {
                    assert false;
                }
                assert isLongRunningConversation();
                assert (!(Boolean) getValue("#{artistHome.managed}"));
            }
        }.run();
        
        new FacesRequest("/artist.xhtml", cid)
        {      
            
            @Override
            protected void updateModelValues() throws Exception 
            {
                setValue("#{artist.name}", "Pete Muir's Band");
                assert isLongRunningConversation();
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                assert "persisted".equals(invokeAction("#{artistHome.persist}"));
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert "Pete Muir's Band".equals(getValue("#{artist.name}"));
                assert ((Integer) 0).equals("#{artist.bandMembers.size}");
            }
            
        }.run();
        
        new FacesRequest("/artists.xhtml", cid)
        {
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert new Long("7").equals(getValue("#{artists.resultCount}"));
                assert "Pete Muir's Band".equals(getValue("#{artists.resultList[3].name}"));
                assert "Led Zepplin".equals(getValue("#{artists.resultList[4].name}"));
            }
            
        }.run();
    }
    
    @Test
    public void testAddBandMember() throws Exception
    {
        final String cid = new FacesRequest("/artist.xhtml")
        {
            @Override
            protected void beforeRequest() 
            {
                setParameter("artistId", "1");
                setParameter("conversationPropagation", "join");
            }
            
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{identity.username}", "administrator");
                setValue("#{identity.password}", "administrator");
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                invokeAction("#{identity.login}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert "Pink Floyd".equals(getValue("#{artist.name}"));
                assert ((Integer) 3).equals(getValue("#{artist.bandMembers.size}"));
            }
        }.run();
        
        new FacesRequest("/artist.xhtml", cid)
        {
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                assert null == invokeAction("#{artistHome.addBandMember}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert ((Integer) 4).equals(getValue("#{artist.bandMembers.size}"));
            }
            
        }.run();
        
        new FacesRequest("/artist.xhtml", cid)
        {       
            
            @Override
            protected void updateModelValues() throws Exception 
            {
                setValue("#{artist.bandMembers[3].name}", "Pete Muir");
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                assert "updated" == invokeAction("#{artistHome.update}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert ((Integer) 4).equals(getValue("#{artist.size}"));
                assert "Pete Muir".equals(getValue("#{artist.bandMembers[3].name}"));
            }
            
        }.run();

    }
    
}
