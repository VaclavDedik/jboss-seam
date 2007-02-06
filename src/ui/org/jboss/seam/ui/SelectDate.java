package org.jboss.seam.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.jboss.seam.core.Messages;

public class SelectDate 
    extends UIComponentBase
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.ui.SelectDate";
    public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.SelectDate";
    
    public static final String SELECTDATE_JS    = "org/jboss/seam/ui/selectDate.js";

    private String forField;
    
    private String dateFormat = "MM/dd/yyyy";
    
    public String getDateFormat(){
        ValueBinding vb = getValueBinding("dateFormat");
        return (vb != null) ? JSF.getStringValue(getFacesContext(), vb) : dateFormat;
    }
    public void setDateFormat(String dateFormat){
        this.dateFormat = dateFormat;
    }
    
    public String getFor(){
        ValueBinding vb = getValueBinding("for");
        return (vb != null) ? JSF.getStringValue(getFacesContext(), vb) : forField;
    }    
    public void setFor(String forField) {
        this.forField = forField;
    }    

    
    @Override
    public String getFamily()
    {
        return COMPONENT_FAMILY;
    }
    
    @Override
    public boolean getRendersChildren() {
        return false;
    }


   @Override
   public void restoreState(FacesContext context, Object state) {
      Object[] values = (Object[]) state;
      super.restoreState(context, values[0]);
      forField = (String) values[1];
      dateFormat = (String) values[2];
   }

   @Override
   public Object saveState(FacesContext context) {
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

        writeScript(context, SELECTDATE_JS);
        ResponseWriter response = context.getResponseWriter();
        response.startElement("span", this);
        response.writeAttribute("onclick", 
                                "displayDatePicker('" + forComponent.getClientId(context) + "');", 
                                null);
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException
    {
        ResponseWriter response = context.getResponseWriter();
        response.endElement("span");
        response.flush();
        super.encodeEnd(context);
    }


    public void writeScript(FacesContext context,
                            String scriptResource) 
        throws IOException
    {
        Map request = context.getExternalContext().getRequestMap();
        if (request.get("SELECTDATE_SCRIPT") != null) {
            // already written out
            return;
        }
            
        request.put("SELECTDATE_SCRIPT", scriptResource);
        
        ResponseWriter response = context.getResponseWriter();

        writeLocaleInformation(response, context.getViewRoot().getLocale());        
        writeScriptFromResource(response, scriptResource);
    }

    private void writeScriptFromResource(ResponseWriter response, 
                                         String scriptResource) 
        throws IOException 
    {
        response.startElement("script", null);
        response.writeAttribute("type", "text/javascript", null);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(scriptResource);
        
        if (is == null) {
            throw new IOException("resource " + scriptResource + " not found");
        }

        try {
            InputStreamReader reader = new InputStreamReader(is);
            char[] buf = new char[512];
            int nread; 
            while ((nread = reader.read(buf,0,512))!= -1) {
                response.write(buf, 0, nread);
            }
        } finally {
            is.close();
        }

        response.endElement("script");
    }
   


    private void writeLocaleInformation(ResponseWriter response, 
                                        Locale locale) throws IOException 
    {
        response.startElement("script", null);
        response.writeAttribute("type", "text/javascript", null);
        
        Calendar cal = Calendar.getInstance(locale);
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
                
        // Note: Calendar and DateFormatSymbols use 1 for the first day of the week, not 0.
        
        response.write("\r");
        response.write("var dayArrayShort = " + getArray(symbols.getShortWeekdays(), 1) + ";\r");
        response.write("var dayArrayMed = " + getArray(symbols.getShortWeekdays(), 1) + ";\r");
        response.write("var dayArrayLong = " + getArray(symbols.getWeekdays(), 1) + ";\r");
        response.write("var monthArrayShort = " + getArray(symbols.getShortMonths(), 0) + ";\r");
        response.write("var monthArrayMed = " + getArray(symbols.getShortMonths(), 0) + ";\r");
        response.write("var monthArrayLong = " + getArray(symbols.getMonths(), 0) + ";\r");
        response.write("var firstDayInWeek = " + (cal.getFirstDayOfWeek() - 1) + ";\r");

        response.write("var dateFormat = '" + getDateFormat() + "';\r");
        
        response.write("var thisMonthButton = '" + messageForKey(COMPONENT_TYPE + ".thisMonth", "this month") + "';\r");
        response.write("var closeButton = '" + messageForKey(COMPONENT_TYPE + ".close", "close") + "';\r");
        
        response.endElement("script");
    }

    private String messageForKey(String key, String defaultTranslation) {
        String translation = (String) Messages.instance().get(key);
        if (key.equals(translation)) {
            translation = defaultTranslation; 
        }
        return translation;
    }

    
    private StringBuilder getArray(String[] values, int start) throws IOException {
        StringBuilder s = new StringBuilder();
        s.append("new Array(");
        for (int i = start; i < values.length; i++) {
            if (i > start) {
                s.append(", ");
            }
            s.append("'").append(values[i]).append("'");
        }
        s.append(")");
        return s;
    }
                         
}
