package org.jboss.seam.example.quartz;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Asynchronous;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.timer.Expiration;
import org.jboss.seam.annotations.timer.IntervalDuration;
import org.jboss.seam.log.Log;
import org.jboss.seam.core.QuartzDispatcher.QuartzTriggerHandle;


@Name("processor")
@AutoCreate
public class PaymentProcessor {
    
    @In 
    EntityManager entityManager;

    @Logger Log log;

    @Asynchronous
    @Transactional
    public QuartzTriggerHandle schedulePayment(@Expiration Date when, 
                                 @IntervalDuration Long interval, 
                                 Payment payment) 
    { 
        payment = entityManager.merge(payment);
        
        log.info("[#0] Processing payment #1", System.currentTimeMillis(), payment.getId());

        if (payment.getActive()) {
            BigDecimal balance = payment.getAccount().adjustBalance(payment.getAmount().negate());
            log.info(":: balance is now #0", balance);
            payment.setLastPaid(new Date());

            if (payment.getPaymentFrequency().equals(Payment.Frequency.ONCE)) {
                payment.setActive(false);
            }
        }

        return null;
    }
}
