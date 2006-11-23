package org.jboss.seam.ui;

import java.util.TimeZone;

import javax.faces.convert.DateTimeConverter;

import org.jboss.seam.contexts.Contexts;

public class ConvertDateTime extends DateTimeConverter
{

   public ConvertDateTime()
   {
      setTimeZone( getTimeZone() );
   }

   @Override
   public TimeZone getTimeZone()
   {
      if ( Contexts.isApplicationContextActive() )
      {
         return org.jboss.seam.core.TimeZone.instance();
      }
      else
      {
         return TimeZone.getDefault();
      }
   }

}
