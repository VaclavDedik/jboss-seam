package org.jboss.seam.ui.tag;

import java.util.Locale;
import java.util.TimeZone;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.DateTimeConverter;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.ConverterTag;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


public class ConvertDateTimeTag extends ConverterTag
{
   private static final String CONVERTER_ID = "org.jboss.seam.ui.ConvertDateTime";

   private String dateStyle = "default"; // the default value as required by the spec (default in this case)
   private String locale = null;
   private String pattern = null;
   private String timeStyle = "default"; // the default value as required by the spec (default in this case)
   private String timeZone = null;
   private String type = null;


   public void setDateStyle(String dateStyle)
   {
      this.dateStyle = dateStyle;
   }

   public void setLocale(String locale)
   {
      this.locale = locale;
   }

   public void setPattern(String pattern)
   {
      this.pattern = pattern;
   }

   public void setTimeStyle(String timeStyle)
   {
      this.timeStyle = timeStyle;
   }

   public void setTimeZone(String timeZone)
   {
      this.timeZone = timeZone;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public void setPageContext(PageContext context)
   {
       super.setPageContext(context);
       setConverterId(CONVERTER_ID);
   }

   protected Converter createConverter() throws JspException
   {
       DateTimeConverter converter = (DateTimeConverter) super.createConverter();

       FacesContext facesContext = FacesContext.getCurrentInstance();
       setConverterDateStyle(facesContext, converter, dateStyle);
       setConverterLocale(facesContext, converter, locale);
       setConverterPattern(facesContext, converter, pattern);
       setConverterTimeStyle(facesContext, converter, timeStyle);
       setConverterTimeZone(facesContext, converter, timeZone);
       setConverterType(facesContext, converter, type);

       return converter;
   }

   protected static void setConverterLocale(FacesContext facesContext,
                                            DateTimeConverter converter,
                                            String value)
   {
       if (value == null) return;
       if (UIComponentTag.isValueReference(value))
       {
           ValueBinding vb = facesContext.getApplication().createValueBinding(value);
           converter.setLocale((Locale)vb.getValue(facesContext));
       }
       else
       {
           throw new UnsupportedOperationException();
       }
   }


   private static void setConverterDateStyle(FacesContext facesContext,
                                             DateTimeConverter converter,
                                             String value)
   {
       if (value == null) return;
       if (UIComponentTag.isValueReference(value))
       {
           ValueBinding vb = facesContext.getApplication().createValueBinding(value);
           converter.setDateStyle((String)vb.getValue(facesContext));
       }
       else
       {
           converter.setDateStyle(value);
       }
   }

   private static void setConverterPattern(FacesContext facesContext,
                                           DateTimeConverter converter,
                                           String value)
   {
       if (value == null) return;
       if (UIComponentTag.isValueReference(value))
       {
           ValueBinding vb = facesContext.getApplication().createValueBinding(value);
           converter.setPattern((String)vb.getValue(facesContext));
       }
       else
       {
           converter.setPattern(value);
       }
   }

   private static void setConverterTimeStyle(FacesContext facesContext,
                                             DateTimeConverter converter,
                                             String value)
   {
       if (value == null) return;
       if (UIComponentTag.isValueReference(value))
       {
           ValueBinding vb = facesContext.getApplication().createValueBinding(value);
           converter.setTimeStyle((String)vb.getValue(facesContext));
       }
       else
       {
           converter.setTimeStyle(value);
       }
   }

   private static void setConverterTimeZone(FacesContext facesContext,
                                            DateTimeConverter converter,
                                            String value)
   {
       if (value == null) return;
       if (UIComponentTag.isValueReference(value))
       {
           ValueBinding vb = facesContext.getApplication().createValueBinding(value);
           converter.setTimeZone((TimeZone)vb.getValue(facesContext));
       }
       else
       {
           converter.setTimeZone(TimeZone.getTimeZone(value));
       }
   }

   private static void setConverterType(FacesContext facesContext,
                                        DateTimeConverter converter,
                                        String value)
   {
       if (value == null) return;
       if (UIComponentTag.isValueReference(value))
       {
           ValueBinding vb = facesContext.getApplication().createValueBinding(value);
           converter.setType((String)vb.getValue(facesContext));
       }
       else
       {
           converter.setType(value);
       }
   }
  
}
