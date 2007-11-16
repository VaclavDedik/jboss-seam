package org.jboss.seam.test.integration;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class EntityPassivationTest
    extends SeamTest
{

    @Test 
    public void testEntityList() 
        throws Exception 
    {        
        String id = new FacesRequest("/test.xhtml") {
            @Override
            protected void invokeApplication()
                throws Exception 
            {
                Conversation.instance().begin(true, false);

                invokeAction("#{entitytest.someComponent.createSomeThings}");
                invokeAction("#{entitytest.someComponent.loadThings}");

            }
            @Override
            protected void renderResponse()
                throws Exception 
            {
                List things = (List) getValue("#{entitytest.someComponent.things}");
                assert things!=null && things.size() > 0;                
                assert things.get(0) != null;             
            }
        }.run();

        new FacesRequest("/test.xhtml", id) {
            // the entities should be passivated 
        }.run();

        new FacesRequest("/test.xhtml", id) {
            // passivated a second time
        }.run();

        new FacesRequest("/test.xhtml", id) {
            @Override
            protected void renderResponse()
                throws Exception 
            {
                List things = (List) getValue("#{entitytest.someComponent.things}");
                assert things!=null && things.size() > 0;                
                assert things.get(0) != null;       
                                
                Object thing = getValue("#{entitytest.someComponent.thing}");
                System.out.println("thing=" + thing);
                assert thing!=null;            
            }
            
            
        }.run();
    }
    
    
    
    @Name("entitytest.someComponent")
    @Scope(ScopeType.CONVERSATION)
    @AutoCreate
    public static class SomeComponent {
        @In EntityManager entityManager;
        
        List<UnversionedThing> things;
        UnversionedThing thing;
        
        public void loadThings() {
            things = entityManager.createQuery("select t from UnversionedThing t").getResultList();
            thing = things.get(0);
        }
        
        public List<UnversionedThing> getThings() {
            return things;
        }
        
        public UnversionedThing getThing() {
            return thing;
        }
        
        public void createSomeThings() {
            UnversionedThing thing1 = new UnversionedThing();
            thing1.setName("thing one");
            entityManager.persist(thing1);
        }
        
    }
}
