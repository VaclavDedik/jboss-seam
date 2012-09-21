package org.jboss.seam.example.quartz.test;

import static org.jboss.seam.example.quartz.Payment.Frequency.ONCE;

import java.math.BigDecimal;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.quartz.Account;
import org.jboss.seam.example.quartz.Payment;
import org.jboss.seam.mock.DBJUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author Pete Muir
 *
 */

// Actually this doesn't happen in the example, but lets test it here to keep
// all quartz tests in one place
@RunWith(Arquillian.class)
public class EventsTest extends DBJUnitSeamTest 
{
    @Deployment(name="EventsTest")
    @OverProtocol("Servlet 3.0")
    public static Archive<?> createDeployment()
    {
        EnterpriseArchive er = Deployments.quartzDeployment();
        WebArchive web = er.getAsType(WebArchive.class, "quartz-web.war");
        web.addClasses(EventsTest.class);
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
    public void testAsynchronousEvent() throws Exception
    {
            String id = new FacesRequest("/search.xhtml") 
            {
                @Override
                protected void beforeRequest() 
                {
                    setParameter("accountId", "1");
                }
                
                @Override
                protected void updateModelValues() throws Exception 
                {
                    setValue("#{newPayment.payee}", "IRS"); 
                    setValue("#{newPayment.amount}", new BigDecimal("110.00"));
                    setValue("#{newPayment.paymentFrequency}", ONCE);
                }
    
                @Override
                protected void invokeApplication() throws Exception 
                {
                    invokeMethod("#{paymentHome.scheduleAndSaveUsingAsynchronousEvent}");
                }
    
                @Override
                protected void renderResponse() throws Exception 
                {
                    assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                    Account account = (Account) getValue("#{selectedAccount}");                
                    assert account !=null;
                    assert account.getId() == 1;
                    assert account.getPayments().size() == 1;               
                }
                
            }.run();
            
            // Wait, let quartz execute the async method which schedules the job
            // for immediate execution
            pause(50);
            
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
                    System.out.println("running renderResponse");
                    assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                    Account account = (Account) getValue("#{selectedAccount}");                
                    assert account !=null;
                    assert account.getId() == 1;
                    assert account.getPayments().size() == 1;
                    Payment payment = account.getPayments().get(0);
                    assert new BigDecimal("110.00").equals(payment.getAmount());
                    //assert !payment.getActive();
                    //assert payment.getLastPaid() != null;
                    assert new BigDecimal("891.46").equals(account.getBalance());
                }
                
            }.run();
    }

    
    @Test
    public void testTransactionSuccessEvents() throws Exception
    {
            String id = new FacesRequest("/search.xhtml") 
            {
                @Override
                protected void beforeRequest() 
                {
                    setParameter("accountId", "1");
                }
                
                @Override
                protected void updateModelValues() throws Exception 
                {
                    setValue("#{newPayment.payee}", "IRS"); 
                    setValue("#{newPayment.amount}", new BigDecimal("110.00"));
                    setValue("#{newPayment.paymentFrequency}", ONCE);
                }
    
                @Override
                protected void invokeApplication() throws Exception 
                {
                    invokeMethod("#{paymentHome.scheduleAndSaveWithTransactionEvents}");
                }
    
                @Override
                protected void renderResponse() throws Exception 
                {
                    assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                    Account account = (Account) getValue("#{selectedAccount}");                
                    assert account !=null;
                    assert account.getId() == 1;
                    assert account.getPayments().size() == 1;               
                }
                
            }.run();
            
            // Wait, let quartz execute the async method which schedules the job
            // for immediate execution
            pause(50);
            
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
                    System.out.println("running renderResponse");
                    assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                    Account account = (Account) getValue("#{selectedAccount}");                
                    assert account !=null;
                    assert account.getId() == 1;
                    assert account.getPayments().size() == 1;
                    Payment payment = account.getPayments().get(0);
                    assert new BigDecimal("110.00").equals(payment.getAmount());
                    //assert !payment.getActive();
                    //assert payment.getLastPaid() != null;
                    assert new BigDecimal("891.46").equals(account.getBalance());
                    assert (Boolean) getValue("#{transactionStatus.transactionCompleted}");
                    assert (Boolean) getValue("#{transactionStatus.transactionSucceded}"); 
                    assert payment.getId().equals(getValue("#{transactionStatus.id}"));
                }
                
            }.run();
    }
    
    @Test
    public void testTimedEvent() throws Exception
    {
            String id = new FacesRequest("/search.xhtml") 
            {
                @Override
                protected void beforeRequest() 
                {
                    setParameter("accountId", "1");
                }
                
                @Override
                protected void updateModelValues() throws Exception 
                {
                    setValue("#{newPayment.payee}", "IRS"); 
                    setValue("#{newPayment.amount}", new BigDecimal("120.00"));
                    setValue("#{newPayment.paymentFrequency}", ONCE);
                }
    
                @Override
                protected void invokeApplication() throws Exception 
                {
                    invokeMethod("#{paymentHome.scheduleAndSaveUsingTimedEvent}");
                }
    
                @Override
                protected void renderResponse() throws Exception 
                {
		    Thread.sleep(1000);
                    assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                    Account account = (Account) getValue("#{selectedAccount}");                
                    assert account !=null;
                    assert account.getId() == 1;
                    assert account.getPayments().size() == 1;               
                }
                
            }.run();
            
            // Wait, let quartz execute the async method which schedules the job
            // for immediate execution
            pause(1000l);
            
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
                    System.out.println("running renderResponse");
                    assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                    Account account = (Account) getValue("#{selectedAccount}");                
                    assert account != null;
                    assert account.getId() == 1;
                    assert account.getPayments().size() == 1;
                    Payment payment = account.getPayments().get(0);
                    assert new BigDecimal("120.00").equals(payment.getAmount()) : "Invalid payment amount: " + payment.getAmount();
                    //assert !payment.getActive();
                    //assert payment.getLastPaid() != null;
                    assert new BigDecimal("881.46").equals(account.getBalance()) : "Invalid account balance: " + account.getBalance();
                }
                
            }.run();
    }

    
    
    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            
        }                
    }

    
    
}
