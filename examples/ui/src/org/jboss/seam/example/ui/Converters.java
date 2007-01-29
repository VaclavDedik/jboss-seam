package org.jboss.seam.example.ui;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;

@Name("converters")
public class Converters
{
   
   
   @Transactional
   public Converter getCountryConverter() {
      return new Converter() {

         @Transactional
         public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) throws ConverterException
         {
            if (arg2 == null) {
               return null;
            }
            try {
               return  ((EntityManager) Component.getInstance("entityManager")).find(Country.class, Integer.valueOf(arg2));
            } catch (NumberFormatException e) {
              throw new ConverterException("Cannot find selected country", e);
            }
         }

         @Transactional
         public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) throws ConverterException
         {
           if (arg2 instanceof Country)
            {
               Country country = (Country) arg2;
               return country.getId().toString();
            }
           else
           {
              return null;
           }
         }
         
      };
   }

}
