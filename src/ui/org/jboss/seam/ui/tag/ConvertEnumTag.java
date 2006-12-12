package org.jboss.seam.ui.tag;

import javax.faces.convert.Converter;
import javax.faces.webapp.ConverterTag;
import javax.servlet.jsp.JspException;

import org.jboss.seam.ui.EnumConverter;

public class ConvertEnumTag
    extends ConverterTag
{
    private static final String CONVERTER_ID = "org.jboss.seam.ui.EnumConverter";

    public ConvertEnumTag() {
        setConverterId(CONVERTER_ID);
    }
    
    @Override
    protected Converter createConverter() 
        throws JspException
    {
        return new EnumConverter();
    }
    
}
