package org.jboss.seam.example.seampay;

import javax.persistence.*;
import org.hibernate.validator.*;

import java.io.Serializable;
import java.util.Date;

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

    private boolean paid = false;


    public Long getId()
    {
        return id;
    }
    
    public float getAmount()
    {
        return amount;
    }
    public void setAmount(float amount)
    {
        this.amount = amount;
    }
    
    public String getPayee()
    {
        return payee;
    }

    public void setPayee(String payee)
    {
        this.payee = payee;
    }

    public Account getAccount()
    {
        return account;
    }
    
    public void setAccount(Account account)
    {
        this.account = account;
        account.getPayments().add(this);
    }

    public Date getPaymentDate()
    {
        return paymentDate;
    }
    public void setPaymentDate(Date paymentDate)
    {
        this.paymentDate = paymentDate;
    }  

    public Date getCreatedDate()
    {
        return createdDate;
    }    
    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }  
        
    public boolean getPaid() { return paid;} 
    public void setPaid(boolean paid) { this.paid = paid; }
}
