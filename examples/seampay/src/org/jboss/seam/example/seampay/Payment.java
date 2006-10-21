package org.jboss.seam.example.seampay;

import javax.persistence.*;
import org.hibernate.validator.*;

import java.io.Serializable;
import java.util.*;

@Entity
public class Payment
    implements Serializable
{



    @Id @GeneratedValue 
    private Long id;

    // neither @Min or @Pattern work here
    private float amount;
    @NotNull @Length(min=1)
    private String payee;

    @NotNull @ManyToOne
    private Account account;
   
    @NotNull
    private Date paymentDate;
    @NotNull
    private Date createdDate;
    private Date lastPaid;

    private boolean active = true;

    private Frequency paymentFrequency = Frequency.DAILY;


    public Long getId() {
        return id;
    }
    
    public float getAmount() {
        return amount;
    }
    public void setAmount(float amount) {
        this.amount = amount;
    }
    
    public String getPayee()
    {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public Account getAccount() {
        return account;
    }
    
    public void setAccount(Account account) {
        this.account = account;
        account.getPayments().add(this);
    }

    public Date getPaymentDate() {
        return paymentDate;
    }
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }  

    public Date getCreatedDate() {
        return createdDate;
    }    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }  


    public Date getLastPaid() {
        return lastPaid;
    }    
    public void setLastPaid(Date lastPaid) {
        this.lastPaid = lastPaid;
    }  
        
    public boolean getActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
   
    public Frequency getPaymentFrequency() { 
        return paymentFrequency; 
    }
    public void setPaymentFrequency(Frequency paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public Map<String,Frequency> getFrequencies() {
        Map<String,Frequency> result = new HashMap<String,Frequency>();
        for (Frequency frequency : Frequency.values()) {
            result.put(frequency.toString(), frequency);
        }
        return result;
    }

    public enum Frequency {
        ONCE(0), 
        EVERY_MINUTE(60*1000),
        HOURLY(60*60*1000), 
        DAILY(24*60*60*1000), 
        WEEKLY(7*24*60*60*1000);

        long interval; 

        Frequency(long interval) {
            this.interval = interval;
        }
        
        public long getInterval() {
            return interval;
        }
    };
}
