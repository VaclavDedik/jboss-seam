//$Id$
package org.jboss.seam.util;

import java.beans.PropertyEditorSupport;

/**
 * PropertyEditor for String array valued properties
 * 
 * @author Gavin King
 */
public class StringArrayPropertyEditor extends PropertyEditorSupport
{
   @Override
   public void setAsText(String text) throws IllegalArgumentException
   {
      setValue( Strings.split(text, ", ") );
   }
   @Override
   public String getAsText()
   {
      return Strings.toString( (Object[]) getValue() );
   }
}