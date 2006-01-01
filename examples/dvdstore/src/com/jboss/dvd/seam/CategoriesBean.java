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

import javax.ejb.Interceptor;
import javax.ejb.Stateful;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Name("catbean")
@Scope(ScopeType.APPLICATION)
@Interceptor(SeamInterceptor.class)
public class CategoriesBean
    implements Categories,
               Serializable 
{
    List<Category>      categories;
    Map<String,Category> categoryMap;
    
    @PersistenceContext 
    EntityManager em;

    @Create
    public void loadData() {
        System.out.println("CATBEAN CREATED " + this);
        categories = em.createQuery("from Category c").getResultList();

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
            
            String val = String.valueOf(((Category) obj).getCategoryId());
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

}
