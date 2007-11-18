package org.jboss.seam.test.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                Object thing = getValue("#{entitytest.someComponent.thing}");
                assert thing!=null;      
                
                List thingList = (List) getValue("#{entitytest.someComponent.thingsAsList}");
                assert thingList!=null && !thingList.isEmpty();            
                assert thingList.get(0) != null;       
            
                
                Set thingSet = (Set) getValue("#{entitytest.someComponent.thingsAsSet}");
                assert thingSet!=null && thingSet.size() > 0;                
                assert thingSet.iterator().next() != null;    
                
                Map thingMap = (Map) getValue("#{entitytest.someComponent.thingsAsMap}");
                assert thingMap!=null && thingMap.size() > 0;
                System.out.println("MAP: " + thingMap);
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
                Object thing = getValue("#{entitytest.someComponent.thing}");
                assert thing!=null;      
                
                List thingList = (List) getValue("#{entitytest.someComponent.thingsAsList}");
                assert thingList!=null && !thingList.isEmpty();            
                assert thingList.get(0) != null;       
            
                
                Set thingSet = (Set) getValue("#{entitytest.someComponent.thingsAsSet}");
                assert thingSet!=null && thingSet.size() > 0;                
                assert thingSet.iterator().next() != null;
                
                Map thingMap = (Map) getValue("#{entitytest.someComponent.thingsAsMap}");
                assert thingMap!=null && thingMap.size() > 0;
            }
            
            
        }.run();
    }
    
    
    
    @Name("entitytest.someComponent")
    @Scope(ScopeType.CONVERSATION)
    @AutoCreate
    public static class SomeComponent {
        @In EntityManager entityManager;
        
        
        Set<UnversionedThing> thingSet;
        List<UnversionedThing> thingList;
        Map<Long,UnversionedThing> thingMap;
        UnversionedThing thing;
        
        public void loadThings() {
            thingList = entityManager.createQuery("select t from UnversionedThing t").getResultList();
            thingSet = new HashSet<UnversionedThing>(thingList);
            
            thingMap = new HashMap<Long,UnversionedThing>();
            for (UnversionedThing thing: thingList) {
                thingMap.put(thing.getId(),thing);
            }
            
            thing = thingList.get(0);
        }
        
        public List<UnversionedThing> getThingsAsList() {
            return thingList;
        }
        
        public Set<UnversionedThing> getThingsAsSet() {
            return thingSet;
        }
        
        public Map<Long,UnversionedThing> getThingsAsMap() {
            return thingMap;
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
