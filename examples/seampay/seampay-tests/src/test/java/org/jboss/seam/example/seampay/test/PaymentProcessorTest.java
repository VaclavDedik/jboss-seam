package org.jboss.seam.example.seampay.test;

import java.math.BigDecimal;
import java.util.Date;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.seam.example.seampay.Account;
import org.jboss.seam.example.seampay.Payment;
import org.jboss.seam.example.seampay.PaymentProcessor;
import org.jboss.seam.example.seampay.Payment.Frequency;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PaymentProcessorTest 
    extends JUnitSeamTest
{
    static final String     ACCOUNT_NUMBER = "X12345";
    static final BigDecimal INITIAL_BALANCE = new BigDecimal("1000");

    @Deployment(name="PaymentProcessorTest")
    @OverProtocol("Servlet 3.0")
    public static Archive<?> createDeployment()
    {
        EnterpriseArchive er = Deployments.seamPayDeployment();
        WebArchive web = er.getAsType(WebArchive.class, "seampay-web.war");
        web.addClasses(PaymentProcessorTest.class);
        return er;
    }
    
    @Test
    public void testInactive() {
        PaymentProcessor processor = new PaymentProcessor();
        
        Payment payment = createTestPayment(new BigDecimal("100"), Frequency.ONCE);
        payment.setActive(false);                
                
        assert payment.getAccount().getBalance().equals(INITIAL_BALANCE);        
               
        processor.processPayment(payment);
        
        assert payment.getAccount().getBalance().equals(INITIAL_BALANCE);
        assert payment.getLastPaid() == null;
    }
    
    @Test 
    public void testPayOnce() {
        PaymentProcessor processor = new PaymentProcessor();
        
        Payment payment = createTestPayment(new BigDecimal("100"), Frequency.ONCE);

        assert payment.getAccount().getBalance().equals(INITIAL_BALANCE); 

        processor.processPayment(payment);
               
        assert payment.getAccount().getBalance().equals(new BigDecimal("900"));
        assert !payment.getActive();
        assert payment.getLastPaid() != null;
    }
    
    @Test 
    public void testPayMultiple() {
        PaymentProcessor processor = new PaymentProcessor();
        
        Payment payment = createTestPayment(new BigDecimal("100"), Frequency.WEEKLY);

        assert payment.getAccount().getBalance().equals(INITIAL_BALANCE); 

        processor.processPayment(payment);
               
        assert payment.getAccount().getBalance().equals(new BigDecimal("900"));
        assert payment.getActive();
        assert payment.getLastPaid() != null;
        
        Date firstPayment = payment.getLastPaid();
        
        pause(); // just need to make sure we are some small time in the future
        
        processor.processPayment(payment);
     
        assert payment.getAccount().getBalance().equals(new BigDecimal("800"));
        assert payment.getActive();
        assert payment.getLastPaid().after(firstPayment);
    }
  
    
    private void pause() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            
        }                
    }

    protected Payment createTestPayment(BigDecimal amount, Frequency frequency) {
        Account account = new Account();
        account.setAccountNumber(ACCOUNT_NUMBER);
        setField(account, "balance", INITIAL_BALANCE);
        
        Payment payment = new Payment();
        payment.setAccount(account);
        payment.setAmount(amount);
        payment.setPaymentFrequency(frequency);        
        
        return payment;
    }
    
    
}
