/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.ejb;

import java.util.*;

import javax.ejb.*;
import javax.annotation.*;
import javax.persistence.*;

import com.jboss.dvd.ejb.*;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.JndiInject;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.jboss.seam.ejb.SeamInterceptor;

import java.io.Serializable;

@Stateful
@Name("search")
@Scope(ScopeType.SESSION)
@LocalBinding(jndiBinding="search")
@Interceptor(SeamInterceptor.class)
public class SearchBean
    implements Search,
               Serializable
{
    @In
    ShoppingCart cart;

    @PersistenceContext(unitName="dvd")
    EntityManager em;
    
    Integer category;
    String  title;
    String  actor;
    int     pageSize = 10;
    int     currentPage = 1; 
    int     totalResults = 0;

    List<SelectableItem<Product>> results;

    List<Category>      categories;
    Map<String,Integer> categoryMap;


    public SearchBean() {
        System.out.println("!!!!!!!!!!!!!!!!!!! CREATE SEARCHBEAN " + this);
        reset();
    }

    public void reset() {
        category = new Integer(0);

        title    = "";
        actor    = "";    

        results = null;
    }

    public boolean getHasResults() {
        return (results != null) && (results.size()>0);
    }

    public Map<String,Integer> getCategories() {
        if (categories == null) {
            categories = em.createQuery("from Category c").getResultList();
            Map<String,Integer> results = new HashMap<String, Integer>();
            
            results.put("Any", new Integer(0));
            for (Category category: categories) {
                results.put(category.getCategoryName(),category.getCategory());
            }
            categoryMap = results;
        }
        
        return categoryMap;
    }

    private Category categoryForNum(int value) {
        if (categories != null) {
            for (Category category: categories) {
                if (category.getCategory() == value) {
                    return category;
                }
            }
        }
        return null;
    }
    
    public void setBrowseCategory(Integer category) {
        this.category = category ; 
    }
    public Integer getBrowseCategory() {
        return category;
    }

    public void setBrowseTitle(String title) {
        this.title = title;
    }
    public String getBrowseTitle() {
        return title;
    }

    public void setBrowseActor(String actor) {
        this.actor = actor;
    }
    public String getBrowseActor() {
        return actor;
    }

    public List<SelectableItem<Product>> getSearchResults() {
        if ((results != null) && (results.size() > pageSize)) {
            return results.subList(0, pageSize);
        } else {
            return results;
        }
    }


    public String nextPage() {
        if (!isLastPage()) {
            currentPage++;
            updateResults();
        }
        return null;
    }

    public String prevPage() {
        if (!isFirstPage()) {
            currentPage--;
            updateResults();
        }
        return null;
    }

    public boolean isLastPage() {
        return (results != null) && (results.size() <= pageSize);
    }
    public boolean isFirstPage() {
        return (results != null) && (currentPage == 0);
    }
    
    public String doSearch() {
        currentPage=0;
        updateResults();
        return null;
    }

    private void updateResults() {
        List<SelectableItem<Product>> items = new ArrayList<SelectableItem<Product>>();

        List<Product> products = searchQuery(title,actor,categoryForNum(getBrowseCategory()))
            .setMaxResults(pageSize+1) 
            .setFirstResult(pageSize*currentPage)
            .getResultList();
        
        for (Product product: products) {
            items.add(new SelectableItem(product));
        }

        results = items;
    }


    private Query searchQuery(String title, String actor, Category category) {
        title = (title == null) ? "%" : "%" + title + "%";
        actor = (actor == null) ? "%" : "%" + actor + "%";

        if (category == null) {
            return em.createQuery("from Product p where p.title LIKE :title and p.actor LIKE :actor")
                .setParameter("title", title)
                .setParameter("actor", actor);
        } else { 
            return em.createQuery("from Product p where p.title LIKE :title and p.actor LIKE :actor " + 
                                  "and p.category = :category")
                .setParameter("title", title)
                .setParameter("actor", actor)
                .setParameter("category", category);
        }
    }



    public String addToCart() {
        System.out.println("Search -> cart " + this);
        for (SelectableItem<Product> item: results) {
            if (item.getSelected()) {
                item.setSelected(false);

                System.out.println("--------------- CART:: " + cart);
                System.out.println("Adding to cart: " + item.getItem());
                cart.addProduct(item.getItem(), 1);
            }
        }

        return null;
    }

    public String checkout() {
        reset();
        return "checkout";
    }
}
