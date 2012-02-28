package org.jboss.seam.test.integration;

import static org.jboss.seam.ScopeType.APPLICATION;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.mock.JUnitSeamTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.After;
import org.junit.runner.RunWith;

public class ConcurrentFactoryTest 
    extends JUnitSeamTest 
{
	
	// TODO: Implement a different way to run concurrent test for junit
    // @Test(threadPoolSize = 2, invocationCount = 2)
	@Test
	@Ignore
    public void concurrentFactoryCall() 
        throws Exception 
    {
        new ComponentTest() {
            @Override
            protected void testComponents() throws Exception {
                assert "slowly created String".equals(getValue("#{concurrentFactoryTest.component.injectedString}"));
            }
        }.run();
    }
    
    @After
    @Override
    public void end()
    {
       if (session != null) {
          // Because we run in threads. Only the first thread that finishes ends the session.
          ServletLifecycle.endSession(session);
       }
       session = null;
    }
    
    @Name("concurrentFactoryTest.component")
    static public class Component {
       @In(value = "concurrentFactoryTest.slowlyCreatedString") String injectedString;
       
       public String getInjectedString() {
          return injectedString;
       }
    }
    
    @Name("concurrentFactoryTest.SlowFactory")
    static public class SlowFactory {
        @Factory(value = "concurrentFactoryTest.slowlyCreatedString", scope = APPLICATION, autoCreate = true)
        public String slowlyCreateString() {
            try
            {
               Thread.sleep(1000);
               return "slowly created String";
            }
            catch (InterruptedException e)
            {
               e.printStackTrace();
               return null;
            }
        }        
    }
    


}
