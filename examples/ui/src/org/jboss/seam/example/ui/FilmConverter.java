package org.jboss.seam.example.ui;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;

@Name("filmConverter")
@org.jboss.seam.annotations.jsf.Converter(forClass=Film.class)
public class FilmConverter implements Converter, Serializable
{
   
   @In
   private EntityManager entityManager;

   @Transactional
   public Object getAsObject(FacesContext context, UIComponent component, String value)
   {
      if (value != null)
      {
         try 
         {
            Integer id = Integer.parseInt(value);
            if (id != null)
            {
               return entityManager.find(Film.class, id);
            }
         } 
         catch (NumberFormatException e) {
         }
      }
      return null;     
   }

   public String getAsString(FacesContext context, UIComponent component, Object value)
   {
      if (value instanceof Film)
      {
         Film film = (Film) value;
         return film.getId().toString();
      }
      else
      {
         return null;
      }
   }

}
