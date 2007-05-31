package org.jboss.seam.example.seampay;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Timer;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Asynchronous;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.timer.Expiration;
import org.jboss.seam.annotations.timer.IntervalDuration;
import org.jboss.seam.example.seampay.Payment;
import org.jboss.seam.log.Log;


@Name("processor")
@AutoCreate
public class PaymentProcessor {
    
    @In 
    EntityManager entityManager;

    @Logger Log log;

    @Asynchronous
    @Transactional
    public Timer schedulePayment(@Expiration Date when, 
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
