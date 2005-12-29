/**
 * 
 */
package org.jboss.seam.core;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Gavin King
 *
 */
public final class ConversationEntry implements Serializable, Comparable<ConversationEntry>
{
   private long lastRequestTime;
   private String description;
   private String id;
   private Date startDatetime;
   private Date lastDatetime;
   private String outcome;
   private LinkedList<String> conversationIdStack;
   private String ownerComponentName;
   
   public ConversationEntry(String id, LinkedList<String> stack) 
   {
      super();
      this.id = id;
      conversationIdStack = stack;
      startDatetime = new Date();
   }
   
   public ConversationEntry(String id) 
   {
      super();
      this.id = id;
      conversationIdStack = new LinkedList<String>();
      conversationIdStack.add(id);
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

   public LinkedList<String> getConversationIdStack() {
      return conversationIdStack;
   }

   public String getOwnerComponentName() {
      return ownerComponentName;
   }

   public void setOwnerComponentName(String ownerComponentName) {
      this.ownerComponentName = ownerComponentName;
   }
   
   public boolean isCurrent()
   {
      Manager manager = Manager.instance();
      if ( manager.isLongRunningConversation() )
      {
         return id.equals( manager.getCurrentConversationId() );
      }
      else
      {
         List<String> stack = manager.getCurrentConversationIdStack();
         return stack!=null && stack.size()>1 && stack.get(1).equals(id);
      }
   }

   public int compareTo(ConversationEntry entry) {
      int result = new Long ( getLastRequestTime() ).compareTo( entry.getLastRequestTime() );
      return - ( result==0 ? getId().compareTo( entry.getId() ) : result );
   }
}