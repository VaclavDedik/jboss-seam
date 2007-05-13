package org.jboss.seam.ui;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

public class EnumConverter
    implements Converter
{
    public Object getAsObject(FacesContext context,
                              UIComponent comp,
                              String value)
        throws ConverterException
    {
        Class enumType = comp.getValueExpression("value").getExpectedType();
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
