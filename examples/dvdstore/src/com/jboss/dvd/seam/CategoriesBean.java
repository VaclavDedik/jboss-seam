/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Stateful
@Name("catbean")
@Scope(ScopeType.EVENT)
public class CategoriesBean
    implements Categories,
               Serializable 
{
    List<Category>       categories;
    Map<String,Category> categoryMap;
    
    @PersistenceContext 
    EntityManager em;

    @Create
    public void loadData() {
        categories = em.createQuery("select c from Category c")
              .setHint("org.hibernate.cacheable", true)
              .getResultList();

        Map<String,Category> results = new TreeMap<String,Category>();
        
        for (Category category: categories) {
            results.put(category.getName(),category);
        }
        
        categoryMap = results;
    }

    public Map<String,Category> getCategories() {
        return categoryMap;
    }

    public Converter getConverter() {
        return new CategoryConverter(categories);
    }

    public Category getNullCategory() {
        return new Category();
    }

    static public class CategoryConverter 
        implements Converter, 
                   Serializable
    {
        List<Category> categories;
        
        public CategoryConverter(List<Category> categories) {
            this.categories = categories;
        }
        
        public String getAsString(FacesContext facesContext,
                                  UIComponent  component, 
                                  Object       obj) 
        {
            if (obj == null) return null;

            Category category = (Category) obj;
            String val = String.valueOf(category.getCategoryId());

            return val;
        }
        
        public Object getAsObject(FacesContext facesContext,
                                  UIComponent  component, 
                                  String       str) 
            throws ConverterException 
        {
            if (str == null || str.length()==0) {
                return null;
            }

            int id = Integer.valueOf(str).intValue();
            for (Category category : categories) {
                if (category.getCategoryId() == id) {
                    return category;
                }
            }

            return null;
        }
    }
    
    @Remove @Destroy 
    public void destroy() {}
}
