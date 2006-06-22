package org.jboss.seam.util;

import java.beans.PropertyEditorSupport;
import java.util.HashSet;
import java.util.Set;

/**
 * Property editor for Sets of Strings.
 *
 * @author Shane Bryzak
 */
public class SetPropertyEditor extends PropertyEditorSupport
{
   
   @Override
   public void setAsText(String text)
      throws IllegalArgumentException
   {
      Set<String> value = new HashSet();
      for (String s : Strings.split(text, ", \r\n\f\t"))
      {
         value.add(s);
       }
       setValue(value);
   }

   @Override
   public String getAsText()
   {
      StringBuilder text = new StringBuilder();
      Set<String> set = (Set<String>) getValue();
      if (set==null) return null;
      for (String s : set)
      {
         if (text.length() > 0) text.append(',');
         text.append(s);
      }
      return text.toString();
  }
   
}
