package org.jboss.seam.example.seampay.test;

import java.math.BigDecimal;
import java.util.List;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.seam.example.seampay.Account;
import org.jboss.seam.example.seampay.Payment;
import org.jboss.seam.example.seampay.Payment.Frequency;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 *  embedded ejb3 doesn't support timer service, so we are fairly limited on what we can test.
 */
@RunWith(Arquillian.class)
public class AccountTest 
    extends JUnitSeamTest
{
   @Deployment(name="AccountTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive er = Deployments.seamPayDeployment();
      WebArchive web = er.getAsType(WebArchive.class, "seampay-web.war");
      web.addClasses(AccountTest.class);
      return er;
   }
    
    @Test
    public void listAccounts() throws Exception {
        String id = new FacesRequest("/search.xhtml") {
            
            @Override
            @SuppressWarnings("unchecked")
            protected void renderResponse() throws Exception {
                List<Account> accounts = (List<Account>) getValue("#{accounts.resultList}");
                
                assert accounts.size() == 5;                
                // check ASC?
            }          
        }.run(); 
    }
    
    @Test
    public void selectAccount() throws Exception {        
        String id = new FacesRequest("/search.xhtml") {        
            @Override
            @SuppressWarnings("unchecked")
            protected void renderResponse() throws Exception {           
                assert !((Boolean)getValue("#{accountHome.idDefined}"));
            }          
        }.run();
        
        new FacesRequest("/search.xhtml", id) {
            @Override
            protected void beforeRequest() {
                setParameter("accountId", "1");
            }

            @Override
            protected void renderResponse() throws Exception {
                assert ((Boolean)getValue("#{accountHome.idDefined}"));
                
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
        
        
        new FacesRequest("/search.xhtml", id) {
            @Override
            protected void beforeRequest() {
                setParameter("accountId", "1");
            }
            
            @Override
            protected void applyRequestValues() throws Exception {
                setValue("#{newPayment.payee}", "IRS"); 
                setValue("#{newPayment.amount}", new BigDecimal("100.00"));
                setValue("#{newPayment.paymentFrequency}", Frequency.ONCE);
            }

            @Override
            protected void invokeApplication() throws Exception {
                invokeMethod("#{paymentHome.saveAndSchedule}");
            }

            @Override
            protected void renderResponse() throws Exception {
                assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                Account account = (Account) getValue("#{selectedAccount}");                
                assert account !=null;
                assert account.getId() == 1;
                assert account.getPayments().size() == 1;               
                
                Payment payment = (Payment) getValue("#{newPayment}");
                assert payment.getPayee().equals("IRS");
                assert payment.getAmount().equals(new BigDecimal("100.00"));
                assert payment.getAccount() != null;
                assert payment.getAccount().getId() == 1;
            }            
        }.run();
        
        
        // test that the payment is around
        new FacesRequest("/search.xhtml") {
            @Override
            protected void beforeRequest() {
                setParameter("accountId", "1");
            }
            
            @Override
            protected void renderResponse() throws Exception {
                assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                Account account = (Account) getValue("#{selectedAccount}");                
                assert account !=null;
                assert account.getId() == 1;
                assert account.getPayments().size() == 1;        
            }            
        }.run();
    }
    
}
