/**
 * 
 */
package org.jboss.seam.pages;

import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Pageflow;
import org.jboss.seam.core.Expressions.ValueExpression;

public class ConversationControl
{

   private boolean isBeginConversation;
   private boolean isEndConversation;
   private boolean isEndConversationBeforeRedirect;
   private boolean join;
   private boolean nested;
   private FlushModeType flushMode;
   private String pageflow;
   private ValueExpression<Boolean> beginConversationCondition;
   private ValueExpression<Boolean> endConversationCondition;
   
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
      if ( endConversation() )
      {
         if (isEndConversationBeforeRedirect)
         {
            Conversation.instance().endBeforeRedirect();
         }
         else
         {
            Conversation.instance().end();
         }
      }
      if ( beginConversation() )
      {
         boolean begun = Conversation.instance().begin(join, nested);
         if (begun)
         {
            if ( flushMode!=null )
            {
               Conversation.instance().changeFlushMode(flushMode);
            }
            if ( pageflow!=null )
            {
               Pageflow.instance().begin(pageflow);
            }
         }
      }
   }

   private boolean beginConversation()
   {
      return isBeginConversation && 
         (beginConversationCondition==null || Boolean.TRUE.equals( beginConversationCondition.getValue() ) );
   }

   private boolean endConversation()
   {
      return isEndConversation && 
         (endConversationCondition==null || Boolean.TRUE.equals( endConversationCondition.getValue() ) );
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

   public boolean isEndConversationBeforeRedirect()
   {
      return isEndConversationBeforeRedirect;
   }

   public void setEndConversationBeforeRedirect(boolean isEndConversationBeforeRedirect)
   {
      this.isEndConversationBeforeRedirect = isEndConversationBeforeRedirect;
   }

   public ValueExpression<Boolean> getBeginConversationCondition()
   {
      return beginConversationCondition;
   }

   public void setBeginConversationCondition(ValueExpression<Boolean> beginConversationCondition)
   {
      this.beginConversationCondition = beginConversationCondition;
   }

   public ValueExpression<Boolean> getEndConversationCondition()
   {
      return endConversationCondition;
   }

   public void setEndConversationCondition(ValueExpression<Boolean> endConversationCondition)
   {
      this.endConversationCondition = endConversationCondition;
   }
   
}