package org.jboss.seam.example.quartz.test;

import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.quartz.Account;
import org.jboss.seam.example.quartz.Payment;
import org.jboss.seam.mock.DBJUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author Pete Muir
 *
 */
@RunWith(Arquillian.class)
public class AccountTest 
    extends DBJUnitSeamTest 
{
    @Deployment(name="AccountTest")
    @OverProtocol("Servlet 3.0")
    public static Archive<?> createDeployment()
    {
        EnterpriseArchive er = Deployments.quartzDeployment();
        WebArchive web = er.getAsType(WebArchive.class, "quartz-web.war");
        web.addClasses(AccountTest.class);
        return er;
    }
    
    @Override
    protected void prepareDBUnitOperations() {
       
        setDatabase("HSQL");
        setDatasourceJndiName("java:jboss/datasources/ExampleDS");
       
        beforeTestOperations.add(
                new DataSetOperation("BaseData.xml")
        );
    }
    
    @Test
    public void listAccounts() throws Exception 
    {
        new FacesRequest("/search.xhtml") 
        {
            
            @Override
            @SuppressWarnings("unchecked")
            protected void renderResponse() throws Exception 
            {
                List<Account> accounts = (List<Account>) getValue("#{accounts.resultList}");
                
                assert accounts.size() == 5;
            }
            
        }.run(); 
    }
    
    @Test
    public void selectAccount() throws Exception 
    {        
        String id = new FacesRequest("/search.xhtml") 
        {        
        
            @Override
            @SuppressWarnings("unchecked")
            protected void renderResponse() throws Exception 
            {
                assert !((Boolean)getValue("#{accountHome.idDefined}"));
            }          
        }.run();
        
        new FacesRequest("/search.xhtml", id) 
        {
            
            @Override
            protected void beforeRequest() 
            {
                setParameter("accountId", "1");
            }

            @Override
            protected void renderResponse() throws Exception 
            {
                assert ((Boolean) getValue("#{accountHome.idDefined}"));
                
                Account account = (Account) getValue("#{selectedAccount}");
                assert account !=null;
                assert account.getId() == 1;
                assert account.getPayments().size() == 0;
               
                Payment payment = (Payment) getValue("#{newPayment}");
                assert payment.getPayee().equals("Somebody");
                assert payment.getAccount() != null;
                assert payment.getAccount().getId() == 1;
                
            }            
        }.run();
        
        
        
    }
    
}
