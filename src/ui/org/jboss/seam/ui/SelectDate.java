package org.jboss.seam.ui;

import java.io.*;
import java.util.*;

import javax.faces.application.*;
import javax.faces.component.*;
import javax.faces.context.*;
import javax.faces.el.ValueBinding;

public class SelectDate 
    extends UIComponentBase
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.ui.SelectDate";
    public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.SelectDate";
    
    public static final String SELECTDATE_JS    = "org/jboss/seam/ui/selectDate.js";

    private String forField;
    
    
    public String getFor()
    {
        return forField;
    }
    
    public void setFor(String forField)
    {
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
   }

   @Override
   public Object saveState(FacesContext context) {
      Object[] values = new Object[2];
      values[0] = super.saveState(context);
      values[1] = forField;
      return values;
   }
    
    @Override
    public void encodeBegin(FacesContext context) throws IOException
    {
        UIComponent forComponent = findComponent(forField);

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
        
        ViewHandler handler = context.getApplication().getViewHandler();

        ResponseWriter response = context.getResponseWriter();
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
                            
}
