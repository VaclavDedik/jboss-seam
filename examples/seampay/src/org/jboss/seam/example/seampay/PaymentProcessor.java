package org.jboss.seam.example.seampay;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.timer.*;
import org.jboss.seam.log.Log;

import java.util.Date;
import javax.persistence.*;
import javax.ejb.Timer;

import java.math.BigDecimal;


@Name("processor")
public class PaymentProcessor {
    @In(create=true) EntityManager entityManager;

    @Logger Log log;

    @Asynchronous
    @Transactional
    public Timer schedulePayment(@Expiration Date when, 
                                 @IntervalDuration long interval, 
                                 Payment payment) 
    { 
        payment = entityManager.merge(payment);
        
        log.info("[#0] Processing payment #1", System.currentTimeMillis(), payment.getId());
        log.info("Timer handle is #0", payment.getTimerHandle());

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
