/**
 * 
 */
package org.jboss.seam.pages;

import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Pageflow;

public class ConversationControl
{

   private boolean isBeginConversation;
   private boolean isEndConversation;
   private boolean join;
   private boolean nested;
   private FlushModeType flushMode;
   private String pageflow;
   
   public boolean isBeginConversation()
   {
      return isBeginConversation;
   }

   public void setBeginConversation(boolean isBeginConversation)
   {
      this.isBeginConversation = isBeginConversation;
   }

   public boolean isEndConversation()
   {
      return isEndConversation;
   }

   public void setEndConversation(boolean isEndConversation)
   {
      this.isEndConversation = isEndConversation;
   }
   
   public void beginOrEndConversation()
   {
      if ( isEndConversation )
      {
         Conversation.instance().end();
      }
      if ( isBeginConversation )
      {
         boolean begun = Conversation.instance().begin(join, nested);
         if (begun)
         {
            if (flushMode!=null)
            {
               Conversation.instance().changeFlushMode(flushMode);
            }
            if ( pageflow!=null  )
            {
               Pageflow.instance().begin(pageflow);
            }
         }
      }
   }

   public FlushModeType getFlushMode()
   {
      return flushMode;
   }

   public void setFlushMode(FlushModeType flushMode)
   {
      this.flushMode = flushMode;
   }

   public boolean isJoin()
   {
      return join;
   }

   public void setJoin(boolean join)
   {
      this.join = join;
   }

   public boolean isNested()
   {
      return nested;
   }

   public void setNested(boolean nested)
   {
      this.nested = nested;
   }

   public String getPageflow()
   {
      return pageflow;
   }

   public void setPageflow(String pageflow)
   {
      this.pageflow = pageflow;
   }
   
}