package org.jboss.seam.ui.renderkit;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.jboss.seam.ui.component.UISelectDate;
import org.jboss.seam.ui.resource.WebResource;
import org.jboss.seam.ui.util.cdk.RendererBase;

public class SelectDateRendererBase extends RendererBase
{

   @Override
   protected Class getComponentClass()
   {
      return UISelectDate.class;
   }

   @Override
   protected void doEncodeBegin(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
   {
      UISelectDate selectDate = (UISelectDate) component;
      
      UIComponent forComponent = selectDate.findComponent(selectDate.getFor());
      if (forComponent==null)
      {
         throw new IllegalStateException("could not find component with id: " + selectDate.getFor());
      }
      writeScript(context, selectDate);
      writer.startElement("span", selectDate);
      writer.writeAttribute("onclick", "__selectDate('" + forComponent.getClientId(context)
               + "', '" + forComponent.getClientId(context) + "');", null);
   }
   
   @Override
   protected void doEncodeEnd(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
   {
      writer.endElement("span");
      writer.flush();
   }
   
   public void writeScript(FacesContext context, UISelectDate selectDate) throws IOException
   {
      Map request = context.getExternalContext().getRequestMap();
      if (request.get("SELECTDATE_SCRIPT") != null)
      {
         // already written out
         return;
      }

      request.put("SELECTDATE_SCRIPT", null);

      ResponseWriter response = context.getResponseWriter();
      writeLocaleInformation(response, context.getViewRoot().getLocale(), selectDate);

      response.startElement("script", null);
      response.writeAttribute("type", "text/javascript", null);
      response.writeAttribute("src", context.getExternalContext().getRequestContextPath()
               + WebResource.WEB_RESOURCE_PATH + "/date/calendar.js", null);
      response.endElement("script");
   }

   private void writeLocaleInformation(ResponseWriter response, Locale locale, UISelectDate selectDate) throws IOException
   {
      response.startElement("script", null);
      response.writeAttribute("type", "text/javascript", null);

      Calendar cal = Calendar.getInstance(locale);
      DateFormatSymbols symbols = new DateFormatSymbols(locale);

      // Note: Calendar and DateFormatSymbols use 1 for the first day of the week, not 0.

      response.write("\r");
      response.write("var CAL_DAYS_SHORT = '" + commaSeparate(symbols.getShortWeekdays(), 2)
               + "';\r");
      response
               .write("var CAL_DAYS_MEDIUM = '" + commaSeparate(symbols.getShortWeekdays())
                        + "';\r");
      response.write("var CAL_DAYS_LONG = '" + commaSeparate(symbols.getWeekdays()) + "';\r");
      response
               .write("var CAL_MONTHS_MEDIUM = '" + commaSeparate(symbols.getShortMonths())
                        + "';\r");
      response.write("var CAL_MONTHS_LONG = '" + commaSeparate(symbols.getMonths()) + "';\r");
      response.write("var CAL_FIRST_DAY_OF_WEEK = " + (cal.getFirstDayOfWeek() - 1) + ";\r");
      response.write("var CAL_DATE_FORMAT = '" + selectDate.getDateFormat() + "';\r");
      
      if (selectDate.getStartYear() != -1 && selectDate.getEndYear() != -1)
      {
         response.write("var CAL_START_YEAR = " + selectDate.getStartYear() + ";\r");
         response.write("var CAL_END_YEAR = " + selectDate.getEndYear() + ";\r");
      }

      response.endElement("script");
   }

   private String commaSeparate(String[] values)
   {
      return commaSeparate(values, -1);
   }

   private String commaSeparate(String[] values, int maxLength)
   {
      StringBuilder sb = new StringBuilder();
      for (String val : values)
      {
         if (!"".equals(val))
         {
            if (sb.length() > 0) sb.append(',');
            sb.append(limitLength(val, maxLength));
         }
      }
      return sb.toString();
   }

   private String limitLength(String source, int maxLength)
   {
      if (maxLength < 0 || maxLength > source.length())
      {
         return source;
      }
      else
      {
         return source.substring(0, maxLength);
      }
   }
   
}
