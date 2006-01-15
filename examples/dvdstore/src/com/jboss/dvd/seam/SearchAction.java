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

import javax.ejb.Interceptors;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Name("search")
@Conversational(ifNotBegunOutcome="browse")
@Scope(ScopeType.CONVERSATION)
@LoggedIn
@Interceptors(SeamInterceptor.class)
public class SearchAction
    implements Search,
               Serializable
{
    static final long serialVersionUID = -6536629890251170098L;

    @In(create=true)
    ShoppingCart cart;

    @PersistenceContext
    EntityManager em;

    int     pageSize    = 10;
    int     currentPage = 0; 
    boolean hasMore     = false;

    Category category = null;
    String   title    = null;
    String   actor    = null;

    @Out(scope=ScopeType.CONVERSATION,required=false)
    List<Product> searchResults;

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

    @Begin(join=true, pageflow="shopping")
    public String doSearch() {
        currentPage=0;
        updateResults();

        return "browse";
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
        } 
        else {
            searchResults = items;
            hasMore = false;
        }

        searchSelections = new HashMap<Product, Boolean>();

    }


    private Query searchQuery(String title, String actor, Category category) {
        title = (title == null) ? "%" : "%" + title.toLowerCase() + "%";
        actor = (actor == null) ? "%" : "%" + actor.toLowerCase() + "%";

        if (category == null) {
            return em.createQuery("from Product p where lower(p.title) like :title " + 
                                  "and lower(p.actor) LIKE :actor")
                .setParameter("title", title)
                .setParameter("actor", actor);
        } 
        else { 
            return em.createQuery("from Product p where lower(p.title) like :title " + 
                                  "and lower(p.actor) like :actor " + 
                                  "and p.category = :category")
                .setParameter("title", title)
                .setParameter("actor", actor)
                .setParameter("category", category);
        }
    }

    public String addToCart() {
        for (Product item: searchResults) {
            Boolean selected = searchSelections.get(item);
            if ( selected!=null && selected ) {
                searchSelections.put(item, false);
                cart.addProduct(item, 1);
            }
        }

        return "browse";
    }

    public int getPageSize() {
       return pageSize;
    }
    
    public void setPageSize(int pageSize) {
       this.pageSize = pageSize;
    }

    @End
    public String reset() {
        return "browse";
    }

    @Destroy 
    @Remove
    public void destroy() {}
    
}
