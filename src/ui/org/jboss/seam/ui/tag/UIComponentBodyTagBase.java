/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.ui.tag;

import java.io.IOException;
import java.io.Reader;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentBodyTag;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date: 2005-05-11 12:45:06 -0400 (Wed, 11 May
 *          2005) $
 */
public abstract class UIComponentBodyTagBase extends UIComponentBodyTag
{
   private static final Log log = LogFactory.getLog(UIComponentBodyTagBase.class);

   @Override
   public int doEndTag() throws JspException
   {
      if (log.isWarnEnabled())
      {
         UIComponent component = getComponentInstance();
         if (component != null && component.getRendersChildren()
               && !isBodyContentEmpty())
         {
            log.warn("Component with id '"
                        + component.getClientId(getFacesContext())
                        + "' ("
                        + getClass().getName()
                        + " tag) renders it's children, but has embedded JSP or HTML code. Use the <f:verbatim> tag for nested HTML. For comments use <%/* */%> style JSP comments instead of <!-- --> style HTML comments."
                        + "\n BodyContent:\n"
                        + getBodyContent().getString().trim());
         }
      }
      return super.doEndTag();
   }

   /**
    * TODO: Ignore <!-- --> comments
    */
   private boolean isBodyContentEmpty()
   {
      BodyContent bodyContent = getBodyContent();
      if (bodyContent == null)
      {
         return true;
      }
      try
      {
         Reader reader = bodyContent.getReader();
         int c;
         while ((c = reader.read()) != -1)
         {
            if (!Character.isWhitespace((char) c))
            {
               return false;
            }
         }
         return true;
      } catch (IOException e)
      {
         log.error("Error inspecting BodyContent", e);
         return false;
      }
   }

   // -------- rest is identical to UIComponentTagBase ------------------

   // Special UIComponent attributes (ValueHolder, ConvertibleValueHolder)
   private String _value;

   private String _converter;

   // attributes id, rendered and binding are handled by UIComponentTag

   @Override
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);

      // rendererType already handled by UIComponentTag

      setValueProperty(component, _value);
      setConverterProperty(component, _converter);
   }

   public void setValue(String value)
   {
      _value = value;
   }

   public void setConverter(String converter)
   {
      _converter = converter;
   }

   // sub class helpers

   protected void setIntegerProperty(UIComponent component, String propName,
         String value)
   {
      UIComponentTagBase.setIntegerProperty(getFacesContext(), component,
            propName, value);
   }

   protected void setStringProperty(UIComponent component, String propName,
         String value)
   {
      UIComponentTagBase.setStringProperty(getFacesContext(), component,
            propName, value);
   }

   protected void setBooleanProperty(UIComponent component, String propName,
         String value)
   {
      UIComponentTagBase.setBooleanProperty(getFacesContext(), component,
            propName, value);
   }

   protected void setValueProperty(UIComponent component, String value)
   {
      UIComponentTagBase.setValueProperty(getFacesContext(), component, value);
   }

   private void setConverterProperty(UIComponent component, String value)
   {
      UIComponentTagBase.setConverterProperty(getFacesContext(), component,
            value);
   }

   protected void setValidatorProperty(UIComponent component, String value)
   {
      UIComponentTagBase.setValidatorProperty(getFacesContext(), component,
            value);
   }

   protected void setActionProperty(UIComponent component, String action)
   {
      UIComponentTagBase
            .setActionProperty(getFacesContext(), component, action);
   }

   protected void setActionListenerProperty(UIComponent component,
         String actionListener)
   {
      UIComponentTagBase.setActionListenerProperty(getFacesContext(),
            component, actionListener);
   }

   protected void setValueChangedListenerProperty(UIComponent component,
         String valueChangedListener)
   {
      UIComponentTagBase.setValueChangedListenerProperty(getFacesContext(),
            component, valueChangedListener);
   }

   protected void setValueBinding(UIComponent component, String propName,
         String value)
   {
      UIComponentTagBase.setValueBinding(getFacesContext(), component,
            propName, value);
   }

}
