package org.jboss.seam.example.seambay;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Account implements Serializable
{
   private static final long serialVersionUID = 8444287111124328025L;
   
   private Integer accountId;
   private String name;
   private int feedbackCount;
   private float feedbackScore;
   private Date memberSince;
   
   @Id @GeneratedValue
   public Integer getAccountId()
   {
      return accountId;
   }
   
   public void setAccountId(Integer accountId)
   {
      this.accountId = accountId;
   }
   
   public String getName()
   {
      return name;
   }
   
   public void setName(String name)
   {
      this.name = name;
   }
   
   public int getFeedbackCount()
   {
      return feedbackCount;
   }
   
   public void setFeedbackCount(int count)
   {
      this.feedbackCount = count;
   }
   
   public float getFeedbackScore()
   {
      return feedbackScore;
   }
   
   public void setFeedbackScore(float score)
   {
      this.feedbackScore = score;
   }
   
   public Date getMemberSince()
   {
      return memberSince;
   }
   
   public void setMemberSince(Date memberSince)
   {
      this.memberSince = memberSince;
   }
}
