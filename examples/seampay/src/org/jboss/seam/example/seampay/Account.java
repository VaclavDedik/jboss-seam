package org.jboss.seam.example.seampay;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import javax.persistence.*;
import org.hibernate.validator.Length;

@Entity
public class Account
    implements Serializable
{
    @Id @GeneratedValue 
    private Long id;

    float  balance;
    String accountNumber;
    //String login;
    //String password;
       
    @OneToMany(mappedBy="account", cascade=CascadeType.REMOVE)
    //@OrderBy("paymentDate")
    private List<Payment> payments;
   
    public Long getId()
    {
        return id;
    }
    public void setId(Long id)
    {
        this.id = id;
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber)
    {
        this.accountNumber = accountNumber;
    }


    public float getBalance()
    {
        return balance;
    }

    public float adjustBalance(float amount) {
        balance += amount;
        return balance;
    }

    public List<Payment> getPayments() 
    {
        return payments;
    }
}
