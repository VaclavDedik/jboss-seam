package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.wiki.core.model.Role;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;
import java.io.Serializable;

public class Converters {

    @Name("roleConverter")
    @org.jboss.seam.annotations.jsf.Converter(forClass = Role.class)
    public static class RoleConverter implements Converter, Serializable {

        @Transactional
        public Object getAsObject(FacesContext arg0,
                                  UIComponent arg1,
                                  String arg2) throws ConverterException {
            if (arg2 == null) return null;
            try {
                EntityManager em = ((EntityManager)org.jboss.seam.Component.getInstance("entityManager"));
                em.joinTransaction();

                return arg2 != null ? em.find(Role.class, Long.valueOf(arg2)) : null;
            } catch (NumberFormatException e) {
                throw new ConverterException("Cannot find selected role", e);
            }
        }

        public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) throws ConverterException {
            if (arg2 instanceof Role) {
                Role role = (Role) arg2;
                return role.getId().toString();
            } else {
                return null;
            }
        }
    }

}
