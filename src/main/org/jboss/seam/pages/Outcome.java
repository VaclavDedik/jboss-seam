/**
 * 
 */
package org.jboss.seam.pages;

import javax.faces.context.FacesContext;

import org.jboss.seam.core.Expressions.ValueBinding;

public final class Outcome
{
   private String value;
   private ValueBinding expression;
   private ConversationControl conversationControl = new ConversationControl();
   private NavigationHandler navigationHandler = new NavigationHandler() { 
      @Override
      public void navigate(FacesContext context) {}
   };

   public boolean matches(String actualValue)
   {
      return ( actualValue!=null || expression!=null ) &&
            ( value==null || value.equals(actualValue) ) &&
            ( expression==null || Boolean.TRUE.equals( expression.getValue() ) );
   }
   
   public NavigationHandler getNavigationHandler()
   {
      return navigationHandler;
   }

   public void setNavigationHandler(NavigationHandler result)
   {
      this.navigationHandler = result;
   }

   public ConversationControl getConversationControl()
   {
      return conversationControl;
   }

   public ValueBinding getExpression()
   {
      return expression;
   }

   public void setExpression(ValueBinding expression)
   {
      this.expression = expression;
   }

   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }
}