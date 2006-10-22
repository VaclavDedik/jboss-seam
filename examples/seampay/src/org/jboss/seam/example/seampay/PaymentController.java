package org.jboss.seam.example.seampay;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.timer.*;
import org.jboss.seam.framework.*;
import org.jboss.seam.core.*;
import org.jboss.seam.log.Log;

import javax.persistence.*;

import java.util.Date;
import javax.ejb.*;


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
        
        TimerHandle handle = Dispatcher.instance().getHandle(timer);
        payment.setTimerHandle(handle);

        return result;
    }

    public Object getId() {
        return paymentId;
    }

    public void cancel() {
        Payment payment = getInstance();

        TimerHandle handle = payment.getTimerHandle();
        payment.setTimerHandle(null);
        
        Timer timer = Dispatcher.instance().getTimer(handle);
        Dispatcher.instance().cancel(timer);

        payment.setTimerHandle(null);
    }
    
}
