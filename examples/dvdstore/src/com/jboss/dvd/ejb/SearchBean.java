/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Interceptor;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelectionIndex;

import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Name("search")
@Conversational(ifNotBegunOutcome="browse")
@Interceptor(SeamInterceptor.class)
public class SearchBean
    implements Search,
               Serializable
{
    @In(create=true)
    ShoppingCart cart;

    @PersistenceContext(unitName="dvd")
    EntityManager em;
    
    Integer category;
    String  title;
    String  actor;
    int     pageSize = 10;
    int     currentPage = 1; 
    boolean hasMore = false;

//     @Out
//     String testValue = "xyz";

    @DataModel
    List<SelectableItem<Product>> searchResults;

    List<Category>      categories;
    Map<String,Integer> categoryMap;


    public SearchBean() {
        System.out.println("!!!!!!!!!!!!!!!!!!! CREATE SEARCHBEAN " + this);
    }


    public boolean getHasResults() {
        return (searchResults != null) && (searchResults.size()>0);
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

//     public List<SelectableItem<Product>> getSearchResults() {
//         return results;
//     }


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

    @Begin    
    public String doSearch() {
        currentPage=0;
        updateResults();
        System.out.println("!!! DO SEARCH");
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

        if (items.size() > pageSize) { 
            searchResults = new ArrayList(items.subList(0,pageSize));
            hasMore = true;
        } else {
            searchResults = items;
            hasMore = false;
        }
        
        System.out.println("RESULTS: " + searchResults);
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
        for (SelectableItem<Product> item: searchResults) {
            if (item.getSelected()) {
                item.setSelected(false);

                cart.addProduct(item.getItem(), 1);
            }
        }

        return null;
    }


    @End
    public String checkout() {
        return "checkout";
    }

    @End @Destroy
    public void destroy() {
        System.out.println("!! SEARCH BEAN IS SO DEAD: " + this);
    }
}
