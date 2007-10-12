package org.jboss.seam.test.unit;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.Init;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class NamespaceTest 
    extends SeamTest 
{
    @Override
    protected void startJbossEmbeddedIfNecessary() 
          throws org.jboss.deployers.spi.DeploymentException,
                 java.io.IOException 
    {
       // don't deploy   
    }
    
    
    @Test
    public void nameSpaceComponent() 
        throws Exception 
    {
        new ComponentTest() {
            @Override
            protected void testComponents() throws Exception {
                assert getValue("#{elTest.fooFactory}") != null;
            }
        }.run();
    }

    @Test
    public void nameSpaceFactory() 
        throws Exception 
    {
        new ComponentTest() {
            @Override
            protected void testComponents() throws Exception {
                assert getValue("#{elTest.ns1.factory}") != null;
            }
        }.run();
    }
    

    @Test
    public void namespaceOutjection() 
        throws Exception 
    {
        new ComponentTest() {
            @Override
            protected void testComponents() throws Exception {
                FooFactory factory = (FooFactory) getValue("#{elTest.fooFactory}");
                factory.someMethod();
                assert getValue("#{elTest.ns2.outject}") != null;
            }
        }.run();
    }

    
    @Test
    public void factoryMethodExpression() 
        throws Exception 
    {
        new ComponentTest() {
            @Override
            protected void testComponents() throws Exception {
                Init init = Init.instance();
                init.addFactoryMethodExpression("elTest.ns3.factory", "#{elTest.fooFactory.createFoo}", ScopeType.SESSION);
                
                assert getValue("#{elTest.ns3.factory}") != null;
            }
        }.run();
    }
    
    @Test
    public void factoryValueExpression() 
        throws Exception 
    {
        new ComponentTest() {
            @Override
            protected void testComponents() throws Exception {
                Init init = Init.instance();
                init.addFactoryValueExpression("elTest.ns4.factory", "#{elTest.fooFactory.createFoo()}", ScopeType.SESSION);
                
                assert getValue("#{elTest.ns4.factory}") != null;
            }
        }.run();
    }


    @Name("elTest.fooFactory")
    static public class FooFactory {
        public class Foo {}
        
        @Factory("elTest.ns1.factory")
        public Foo createFoo() {
            return new Foo();
        }        

        @Out("elTest.ns2.outject")
        public Foo outjectFoo() {
            return new Foo();
        }
        
        public void someMethod() {
        }
    }
   
}
