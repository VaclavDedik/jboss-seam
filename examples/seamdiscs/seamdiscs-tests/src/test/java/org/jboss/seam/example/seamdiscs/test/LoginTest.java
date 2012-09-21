package org.jboss.seam.example.seamdiscs.test;

import static org.jboss.seam.example.seamdiscs.test.TestStrings.PASSWORD;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.USERNAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.WRONG_PASSWORD;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.WRONG_USERNAME;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.mock.DBJUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;
import org.junit.Test;

/**
 * @author Pete Muir
 *
 */
@RunWith(Arquillian.class)
public class LoginTest extends DBJUnitSeamTest {

    @Deployment(name="LoginTest")
    @OverProtocol("Servlet 3.0")
    public static Archive<?> createDeployment()
    {
        EnterpriseArchive er = Deployments.seamdiscsDeployment();
        WebArchive web = er.getAsType(WebArchive.class, "seamdiscs-web.war");
        web.addClasses(LoginTest.class);
        return er;
    }

    @Override
    protected void prepareDBUnitOperations() {
        setDatabase("HSQL");
        setDatasourceJndiName("java:/jboss/seamdiscsDatasource");
        
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/example/seamdiscs/test/BaseData.xml")
        );
    }
    
    @Test
    public void testLogin() throws Exception
    {
        new FacesRequest("/login.xhtml")
        {
            
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{identity.username}", USERNAME);
                setValue("#{identity.password}", PASSWORD);
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
            }
        }.run();
        
        new FacesRequest("/login.xhtml")
        {
            
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{identity.username}", USERNAME);
                setValue("#{identity.password}", WRONG_PASSWORD);
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                invokeAction("#{identity.login}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert !((Boolean) getValue("#{identity.loggedIn}"));
            }
        }.run();
        
        new FacesRequest("/login.xhtml")
        {
            
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{identity.username}", WRONG_USERNAME);
                setValue("#{identity.password}", PASSWORD);
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                invokeAction("#{identity.login}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert !((Boolean) getValue("#{identity.loggedIn}"));
            }
        }.run();
        
        new FacesRequest("/login.xhtml")
        {
            
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{identity.username}", WRONG_USERNAME);
                setValue("#{identity.password}", WRONG_PASSWORD);
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                invokeAction("#{identity.login}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert !((Boolean) getValue("#{identity.loggedIn}"));
            }
        }.run();
    }

}
