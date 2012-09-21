package org.jboss.seam.example.booking;

import java.text.DateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("org.jboss.seam.example.booking.RecentDateConverter")
public class RecentDateConverter implements Converter
{
   @Override
   public Object getAsObject(FacesContext context, UIComponent component, String value)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getAsString(FacesContext context, UIComponent component, Object value)
   {
      Date then = (Date)value;
      Date now = new Date();
      
      long timeNow = now.getTime();
      long timeThen = then.getTime();
      
      if (timeNow - timeThen < 120000) {
         return "" + ((timeNow - timeThen) / 1000) + " seconds ago"; 
      }
      else if (timeNow - timeThen < 2*60000*60) {
         return "" + ((timeNow - timeThen) / 60000) + " minutes ago"; 
      }
      else if (timeNow - timeThen < 60000*60*24) {
         return "" + ((timeNow - timeThen) / 60000*60) + " hours ago"; 
      }
      else {
         return DateFormat.getDateInstance().format(then);
      }
   }
}
