package org.jboss.seam.ui;

import java.util.TimeZone;

import javax.faces.convert.DateTimeConverter;

public class ConvertDateTime extends DateTimeConverter
{

   @Override
   public TimeZone getTimeZone()
   {
      return org.jboss.seam.core.TimeZone.instance();
   }

}
