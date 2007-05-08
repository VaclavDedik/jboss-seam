package org.jboss.seam.ui.converter;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.jsf.Converter;

@Name("org.jboss.seam.ui.EnumConverter")
@Scope(ScopeType.EVENT)
@Install(precedence = BUILT_IN)
@Converter
@Intercept(NEVER)
public abstract class EnumConverter implements javax.faces.convert.Converter, StateHolder
{
   public Object getAsObject(FacesContext context, UIComponent comp, String value)
            throws ConverterException
   {
      Class enumType = comp.getValueBinding("value").getType(context);
      return Enum.valueOf(enumType, value);
   }

   public String getAsString(FacesContext context, UIComponent component, Object object)
            throws ConverterException
   {
      if (object == null)
      {
         return null;
      }

      return ((Enum) object).name();
   }
   
   public Object saveState(FacesContext context)
   {
      // TODO Auto-generated method stub
      return null;
   }

}
