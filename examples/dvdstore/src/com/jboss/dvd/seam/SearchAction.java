/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 

package com.jboss.dvd.seam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.*;

@Stateful
@Name("search")
@Conversational(ifNotBegunOutcome="customer")
public class SearchAction
    implements Search,
               Serializable
{
    static final long serialVersionUID = -6536629890251170098L;

    @In(create=true)
    ShoppingCart cart;

    @PersistenceContext
    EntityManager em;

    @RequestParameter
    Long id;

    int     pageSize    = 15;
    int     currentPage = 0; 
    boolean hasMore     = false;

    Category category = null;
    String   title    = null;
    String   actor    = null;

    @DataModel
    List<Product> searchResults;

    @DataModelSelection
    Product selectedProduct;

    @Out(required=false)
    Product dvd;

    @Out(scope=ScopeType.CONVERSATION,required=false)
    Map<Product, Boolean> searchSelections;

    public void setCategory(Category category) {
        this.category = category ; 
    }
    public Category getCategory() {
        return category;
    }

    
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
    public String getActor() {
        return actor;
    }

    @Begin(join=true)
    public String doSearch() {
        currentPage=0;
        updateResults();

        return "browse";
    }

    public void nextPage() {
        if (!isLastPage()) {
            currentPage++;
            updateResults();
        }
    }

    public void prevPage() {
        if (!isFirstPage()) {
            currentPage--;
            updateResults();
        }
    }
    
    @Begin(join=true)
    public void selectFromRequest() {
        if (id != null) {
            dvd = em.find(Product.class, id);
        }  else if (selectedProduct != null) {
            dvd = selectedProduct;
        }
    }

    public boolean isLastPage() {
        return (searchResults != null) && !hasMore;
    }
    public boolean isFirstPage() {
        return (searchResults != null) && (currentPage == 0);
    }

    private void updateResults() {
        List<Product> items = searchQuery(getTitle(),
                                          getActor(),
                                          getCategory())
            .setMaxResults(pageSize+1)
            .setFirstResult(pageSize*currentPage)
            .getResultList();

        if (items.size() > pageSize) { 
            searchResults    = new ArrayList(items.subList(0,pageSize));
            hasMore = true;
        } else {
            searchResults = items;
            hasMore = false;
        }

        searchSelections = new HashMap<Product, Boolean>();
    }


    private Query searchQuery(String title, String actor, Category category) {
        title = (title == null) ? "%" : "%" + title.toLowerCase() + "%";
        actor = (actor == null) ? "%" : "%" + actor.toLowerCase() + "%";

        if (category == null || category.getCategoryId()==0) {
            return em.createQuery("from Product p where lower(p.title) like :title " + 
                                  "and lower(p.actor) LIKE :actor order by p.title")
                .setParameter("title", title)
                .setParameter("actor", actor);
        } else { 
            return em.createQuery("from Product p where lower(p.title) like :title " + 
                                  "and lower(p.actor) like :actor " + 
                                  "and :category member of p.categories order by p.title")
                .setParameter("title", title)
                .setParameter("actor", actor)
                .setParameter("category", category);
        }
    }

    /**
     *  Add the selected DVD to the cart
     */
    public void addToCart() {
        cart.addProduct(dvd,1);
    }


    /**
     *  Add many items to cart
     */
    public void addAllToCart() {
        for (Product item: searchResults) {
            Boolean selected = searchSelections.get(item);
            if (selected!=null && selected) {
                searchSelections.put(item, false);
                cart.addProduct(item, 1);
            }
        }
    }

    public int getPageSize() {
       return pageSize;
    }
    
    public void setPageSize(int pageSize) {
       this.pageSize = pageSize;
    }

    @End
    public void reset() {}

    @Destroy 
    @Remove
    public void destroy() {}
    
}
