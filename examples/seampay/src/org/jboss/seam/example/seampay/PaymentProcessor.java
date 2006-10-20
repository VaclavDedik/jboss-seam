package org.jboss.seam.example.seampay;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.timer.*;

import java.util.Date;
import javax.persistence.*;
import javax.ejb.Timer;

@Name("processor")
public class PaymentProcessor {
    @In(create=true) EntityManager entityManager;

    @Asynchronous
    @Transactional
    public Timer schedulePayment(@Expiration Date when, Payment payment) { 
        payment = entityManager.merge(payment);

        System.out.println("[" + System.currentTimeMillis() + "] Processing  " + payment); 

        if (!payment.getPaid()) {
            float balance = payment.getAccount().adjustBalance(-payment.getAmount());
            System.out.println(":: balance is now " + balance);
            payment.setPaid(true);
        }

        return null;
    }
}
