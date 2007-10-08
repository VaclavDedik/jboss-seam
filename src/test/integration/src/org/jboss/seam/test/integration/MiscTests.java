package org.jboss.seam.test.integration;

import org.jboss.seam.Component;
import org.jboss.seam.core.Events;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class MiscTests extends SeamTest {

    @Test
    public void eventChain() throws Exception {

        new FacesRequest("/index.xhtml") {

            @Override
            protected void invokeApplication() throws Exception {
                BeanA beanA = (BeanA) Component.getInstance("beanA");
                BeanB beanB = (BeanB) Component.getInstance("beanB");
                
                System.out.println("beanA: " + beanA.hashCode());
                System.out.println("beanB: " + beanB.hashCode());

                assert "Foo".equals(beanA.getMyValue());
                assert beanB.getMyValue() == null;

                Events.instance().raiseEvent("BeanA.refreshMyValue");

                beanA = (BeanA) Component.getInstance("beanA");
                
                assert "Bar".equals(beanA.getMyValue());        
            }
            
            @Override
            protected void renderResponse() throws Exception
            {
               BeanB beanB = (BeanB) Component.getInstance("beanB");
               assert "Bar".equals(beanB.getMyValue());
            }
        }.run();
    }

}


