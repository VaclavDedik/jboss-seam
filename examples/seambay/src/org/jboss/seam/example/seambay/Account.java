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
   private int feedbackScore;
   private float feedbackPercent;
   private Date memberSince;
   private String location;
   
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
   
   public int getFeedbackScore()
   {
      return feedbackScore;
   }
   
   public void setFeedbackScore(int score)
   {
      this.feedbackScore = score;
   }
   
   public float getFeedbackPercent()
   {
      return feedbackPercent;
   }
   
   public void setFeedbackPercent(float percent)
   {
      this.feedbackPercent = percent;
   }
   
   public Date getMemberSince()
   {
      return memberSince;
   }
   
   public void setMemberSince(Date memberSince)
   {
      this.memberSince = memberSince;
   }
   
   public String getLocation()
   {
      return location;
   }
   
   public void setLocation(String location)
   {
      this.location = location;
   }
}
