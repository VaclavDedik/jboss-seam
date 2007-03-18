package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.Component;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.ui.EntityConverter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

public class Converters {
 
    @Name("accessLevelConverter")
    @org.jboss.seam.annotations.jsf.Converter(forClass = Role.AccessLevel.class)
    public static class AccessLevelConverter implements Converter, Serializable {

        @Transactional
        public Object getAsObject(FacesContext arg0,
                                  UIComponent arg1,
                                  String arg2) throws ConverterException {
            if (arg2 == null) return null;
            try {
                List<Role.AccessLevel> accessLevels = (List<Role.AccessLevel>)Component.getInstance("accessLevelsList");
                return accessLevels.get(accessLevels.indexOf(new Role.AccessLevel(Integer.valueOf(arg2), null)));
            } catch (NumberFormatException e) {
                throw new ConverterException("Cannot find selected access level", e);
            }
        }

        public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) throws ConverterException {
            if (arg2 instanceof Role.AccessLevel) {
                Role.AccessLevel accessLevel = (Role.AccessLevel)arg2;
                return accessLevel.getAccessLevel().toString();
            } else {
                return null;
            }
        }
    }


}
