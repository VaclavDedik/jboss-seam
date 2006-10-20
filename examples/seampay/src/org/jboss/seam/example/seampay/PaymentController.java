package org.jboss.seam.example.seampay;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.timer.*;
import org.jboss.seam.framework.*;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.log.Log;

import javax.persistence.*;

import java.util.Date;
import javax.ejb.Timer;


public class PaymentController 
    extends EntityHome<Payment>
{
    @In(create=true) PaymentProcessor processor;

    @Logger Log log;

    public String saveAndSchedule()
    {
        String result = persist();
        
        Payment payment = getInstance();
        log.info("scheduling instance #0", payment);
        processor.schedulePayment(payment.getPaymentDate(), payment);

        return result;
    }
}
