package org.jboss.seam.example.quartz.test;

import static org.jboss.seam.async.NthBusinessDay.BusinessDayIntervalType.WEEKLY;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.example.quartz.Account;
import org.jboss.seam.example.quartz.Payment;
import org.jboss.seam.mock.DBUnitSeamTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Pete Muir
 *
 */

public class NthBusinessDayTest 
    extends DBUnitSeamTest 
{
    private QuartzTriggerHandle quartzTriggerHandle;    

    
    @Override
    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/example/quartz/test/BaseData.xml")
        );
    }
    
    @Test
    public void scheduleNthBusinessDay() throws Exception
    {
        try
        {
            new FacesRequest("/search.xhtml") 
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
                    setValue("#{newPayment.amount}", new BigDecimal("100.00"));
                    setValue("#{newPayment.paymentNthBusinessDay.interval}", WEEKLY);
                }
    
                @Override
                protected void invokeApplication() throws Exception 
                {
                    assert "persisted".equals(invokeMethod("#{paymentHome.saveAndScheduleNthBusinessDay}"));
                }
    
                @Override
                protected void renderResponse() throws Exception 
                {
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
                    quartzTriggerHandle = payment.getQuartzTriggerHandle();
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
        finally
        {
            // Always cancel the job
            if (quartzTriggerHandle != null)
            {
                quartzTriggerHandle.cancel();
                quartzTriggerHandle = null;
            }
        }
    }
    
    // Can't do much here until we deal with business days better.
    // Could provide an Test version of NthBusinessDays that has deterministic
    // behaviour
    @Test
    public void scheduleNthBusinessDayWithStartAndEnd() throws Exception
    {
        final Date startDate = new Timestamp(System.currentTimeMillis() + 1000);
        final Date endDate = new Timestamp(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
        
        try
        {
            new FacesRequest("/search.xhtml") 
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
                    setValue("#{newPayment.amount}", new BigDecimal("100.00"));
                    setValue("#{newPayment.paymentNthBusinessDay.interval}", WEEKLY);
                    setValue("#{newPayment.paymentDate}", startDate);
                    setValue("#{newPayment.paymentEndDate}", endDate);
                }
    
                @Override
                protected void invokeApplication() throws Exception 
                {
                    assert "persisted".equals(invokeMethod("#{paymentHome.saveAndScheduleNthBusinessDay}"));
                }
    
                @Override
                protected void renderResponse() throws Exception 
                {
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
                    quartzTriggerHandle = payment.getQuartzTriggerHandle();
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
                    assert account.getPayments().get(0).getPaymentDate().equals(startDate);
                    assert account.getPayments().get(0).getPaymentEndDate().equals(endDate);
                }            
            }.run();
        }
        finally
        {
            // Always cancel the job
            if (quartzTriggerHandle != null)
            {
                quartzTriggerHandle.cancel();
                quartzTriggerHandle = null;
            }
        }
    }
    
    @Test
    public void scheduleNthBusinessDayWithStart() throws Exception
    {
        final Date startDate = new Timestamp(System.currentTimeMillis() + 1000);        
        try
        {
            new FacesRequest("/search.xhtml") 
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
                    setValue("#{newPayment.amount}", new BigDecimal("100.00"));
                    setValue("#{newPayment.paymentNthBusinessDay.interval}", WEEKLY);
                    setValue("#{newPayment.paymentDate}", startDate);
                }
    
                @Override
                protected void invokeApplication() throws Exception 
                {
                    assert "persisted".equals(invokeMethod("#{paymentHome.saveAndScheduleNthBusinessDay}"));
                }
    
                @Override
                protected void renderResponse() throws Exception 
                {
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
                    quartzTriggerHandle = payment.getQuartzTriggerHandle();
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
                    assert account.getPayments().get(0).getPaymentDate().equals(startDate);
                }            
            }.run();
        }
        finally
        {
            // Always cancel the job
            if (quartzTriggerHandle != null)
            {
                quartzTriggerHandle.cancel();
                quartzTriggerHandle = null;
            }
        }
    }
    
}
