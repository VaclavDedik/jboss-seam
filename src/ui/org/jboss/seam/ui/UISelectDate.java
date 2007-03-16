package org.jboss.seam.ui;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.jboss.seam.ui.resource.WebResource;

public class UISelectDate extends UIComponentBase
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UISelectDate";

   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.SelectDate";

   private String forField;

   private String dateFormat = "MM/dd/yyyy";

   private int startYear = -1;

   private int endYear = -1;

   public String getDateFormat()
   {
      ValueBinding vb = getValueBinding("dateFormat");
      return (vb != null) ? JSF.getStringValue(getFacesContext(), vb) : dateFormat;
   }

   public void setDateFormat(String dateFormat)
   {
      this.dateFormat = dateFormat;
   }

   public String getFor()
   {
      ValueBinding vb = getValueBinding("for");
      return (vb != null) ? JSF.getStringValue(getFacesContext(), vb) : forField;
   }

   public void setFor(String forField)
   {
      this.forField = forField;
   }

   public int getStartYear()
   {
      return startYear;
   }

   public void setStartYear(int startYear)
   {
      this.startYear = startYear;
   }

   public int getEndYear()
   {
      return endYear;
   }

   public void setEndYear(int endYear)
   {
      this.endYear = endYear;
   }

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }

   @Override
   public boolean getRendersChildren()
   {
      return false;
   }

   @Override
   public void restoreState(FacesContext context, Object state)
   {
      Object[] values = (Object[]) state;
      super.restoreState(context, values[0]);
      forField = (String) values[1];
      dateFormat = (String) values[2];
   }

   @Override
   public Object saveState(FacesContext context)
   {
      Object[] values = new Object[3];
      values[0] = super.saveState(context);
      values[1] = forField;
      values[2] = dateFormat;
      return values;
   }

   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      UIComponent forComponent = findComponent(getFor());
      if (forComponent==null)
      {
         throw new IllegalStateException("could not find component with id: " + getFor());
      }
      writeScript(context);
      ResponseWriter response = context.getResponseWriter();
      response.startElement("span", this);
      response.writeAttribute("onclick", "__selectDate('" + forComponent.getClientId(context)
               + "', '" + forComponent.getClientId(context) + "');", null);
   }

   @Override
   public void encodeEnd(FacesContext context) throws IOException
   {
      ResponseWriter response = context.getResponseWriter();
      response.endElement("span");
      response.flush();
      super.encodeEnd(context);
   }

   public void writeScript(FacesContext context) throws IOException
   {
      Map request = context.getExternalContext().getRequestMap();
      if (request.get("SELECTDATE_SCRIPT") != null)
      {
         // already written out
         return;
      }

      request.put("SELECTDATE_SCRIPT", null);

      ResponseWriter response = context.getResponseWriter();
      writeLocaleInformation(response, context.getViewRoot().getLocale());

      response.startElement("script", null);
      response.writeAttribute("type", "text/javascript", null);
      response.writeAttribute("src", context.getExternalContext().getRequestContextPath()
               + WebResource.WEB_RESOURCE_PATH + "/date/calendar.js", null);
      response.endElement("script");
   }

   private void writeLocaleInformation(ResponseWriter response, Locale locale) throws IOException
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
      response.write("var CAL_DATE_FORMAT = '" + getDateFormat() + "';\r");
      
      if (startYear != -1 && endYear != -1)
      {
         response.write("var CAL_START_YEAR = " + startYear + ";\r");
         response.write("var CAL_END_YEAR = " + endYear + ";\r");
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
