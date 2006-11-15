package org.jboss.seam.example.seampay;

import javax.ejb.Timer;
import javax.ejb.TimerHandle;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

@Name("paymentHome")
public class PaymentController 
    extends EntityHome<Payment>
{
    @RequestParameter Long paymentId;
    @In(create=true) PaymentProcessor processor;
    
    @Logger Log log;

    public String saveAndSchedule()
    {
        String result = persist();
        
        Payment payment = getInstance();
        log.info("scheduling instance #0", payment);

        Timer timer = processor.schedulePayment(payment.getPaymentDate(), 
                                                payment.getPaymentFrequency().getInterval(), 
                                                payment);
        
        payment.setTimerHandle( timer.getHandle() );

        return result;
    }

    public Object getId() {
        return paymentId;
    }

    @Transactional
    public void cancel() {
        Payment payment = getInstance();
        
        TimerHandle handle = payment.getTimerHandle();
        payment.setTimerHandle(null);
        payment.setActive(false);
        
        handle.getTimer().cancel();
    }
    
}
