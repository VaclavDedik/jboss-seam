/**
 *
 */
package org.jboss.seam.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Metadata about an active conversation. Also used
 * by the conversation list and breadcrumbs.
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
   private String viewId;
   private List<String> conversationIdStack;
   private String initiatorComponentName;
   private Integer timeout;
   private boolean removeAfterRedirect;
   private boolean ended;
   
   private ConversationEntries entries;
   
   private ReentrantLock lock;

   public ConversationEntry(String id, List<String> stack, ConversationEntries entries)
   {
      this.id = id;
      this.conversationIdStack = stack==null ? 
            null : Collections.unmodifiableList(stack);
      this.startDatetime = new Date();
      this.entries = entries;
      
      if ( conversationIdStack.size()>1 )
      {
         // get the root conversation entry lock (we want to share the same lock  
         // among all nested conversations in the same conversation stack)
         lock = entries.getConversationEntry( conversationIdStack.get( conversationIdStack.size()-1 ) ).lock;
      }
      else
      {
         lock = new ReentrantLock(true);
      }

      touch();
   }

   public String getDescription() {
      if ( isCurrent() )
      {
         String desc = Conversation.instance().description;
         if (desc!=null) return desc;
      }
      return description;
   }

   void setDescription(String description) {
      entries.setDirty(this.description, description);
      this.description = description;
   }

   public synchronized long getLastRequestTime() {
      return lastRequestTime;
   }

   synchronized void touch() {
      entries.setDirty();
      lastRequestTime = System.currentTimeMillis();
      lastDatetime = new Date();
   }

   public String getId() {
      return id;
   }

   public Date getStartDatetime() {
      return startDatetime;
   }

   public void destroy() {
      boolean success = Manager.instance().switchConversation( getId() );
      if (success) Manager.instance().endConversation(false);
   }

   public void select() {
      switchConversation();
   }
   
   public boolean switchConversation()
   {
      boolean success = Manager.instance().switchConversation( getId() );
      if (success)
      {
         String viewId = getViewId();
         if (viewId!=null)
         {
            Manager.instance().redirect(viewId);
            return true;
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }

   void setViewId(String viewId) {
      entries.setDirty(this.viewId, viewId);
      this.viewId = viewId;
   }

   public String getViewId()
   {
      if ( isCurrent() )
      {
         String out = Conversation.instance().viewId;
         if (out!=null) return out;
      }
      return viewId;
   }

   public synchronized Date getLastDatetime() {
      return lastDatetime;
   }

   public List<String> getConversationIdStack() {
      return conversationIdStack;
   }

   public String getInitiatorComponentName() {
      return initiatorComponentName;
   }

   void setInitiatorComponentName(String ownerComponentName) {
      entries.setDirty(this.initiatorComponentName, ownerComponentName);
      this.initiatorComponentName = ownerComponentName;
   }

   public boolean isDisplayable() {
      return !isEnded() && getDescription()!=null;
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

   public int getTimeout() {
      return timeout==null ?
            Manager.instance().getConversationTimeout() : timeout;
   }

   void setTimeout(int conversationTimeout) {
      entries.setDirty(this.timeout, timeout);
      this.timeout = conversationTimeout;
   }

   public boolean isRemoveAfterRedirect() {
      return removeAfterRedirect;
   }

   public void setRemoveAfterRedirect(boolean removeAfterRedirect) {
      entries.setDirty();
      this.removeAfterRedirect = removeAfterRedirect;
   }
   
   void setId(String id)
   {
      this.id = id;
   }
   
   public boolean lockNoWait() //not synchronized!
   {
      return lock.tryLock();
   }

   public boolean lock() //not synchronized!
   {
      try
      {
         return lock.tryLock( Manager.instance().getConcurrentRequestTimeout(), TimeUnit.MILLISECONDS );
      }
      catch (InterruptedException ie)
      {
         throw new RuntimeException(ie);
      }
   }
   
   public void unlock() //not synchronized!
   {
      lock.unlock();
   }
   
   public void end()
   {
      ended = true;
   }
   
   public boolean isEnded()
   {
      return ended;
   }
   
   public boolean isNested()
   {
      return conversationIdStack.size()>1;
   }

   @Override
   public String toString()
   {
      return "ConversationEntry(" + id + ")";
   }

}