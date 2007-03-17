package org.jboss.seam.ui;

import javax.faces.component.*;
import javax.faces.context.*;
import javax.faces.convert.*;

public class EnumConverter
    implements Converter
{
    public Object getAsObject(FacesContext context,
                              UIComponent comp,
                              String value)
        throws ConverterException
    {
        Class enumType = comp.getValueBinding("value").getType(context);
        return Enum.valueOf(enumType, value);
    }

    public String getAsString(FacesContext context,
                              UIComponent component,
                              Object object)
        throws ConverterException
    {
        if (object == null) {
            return null;
        }

        return ((Enum) object).name();
    }

}
