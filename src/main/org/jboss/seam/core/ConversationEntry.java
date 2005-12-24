/**
 * 
 */
package org.jboss.seam.core;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Gavin King
 *
 */
public final class ConversationEntry implements Serializable
{
   private long lastRequestTime;
   private String description;
   private String id;
   private Date startDatetime;
   private Date lastDatetime;
   private String outcome;
   
   public ConversationEntry(String id) {
      super();
      this.id = id;
      startDatetime = new Date();
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public long getLastRequestTime() {
      return lastRequestTime;
   }

   public void touch() {
      this.lastRequestTime = System.currentTimeMillis();
      lastDatetime = new Date();
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public Date getStartDatetime() {
      return startDatetime;
   }

   public void setStartDatetime(Date created) {
      this.startDatetime = created;
   }

   public String outcome() {
      return outcome;
   }

   public void setOutcome(String outcome) {
      this.outcome = outcome;
   }
   
   public String getOutcome()
   {
      return outcome;
   }

   public Date getLastDatetime() {
      return lastDatetime;
   }

   public void setLastDatetime(Date lastDatetime) {
      this.lastDatetime = lastDatetime;
   }
}