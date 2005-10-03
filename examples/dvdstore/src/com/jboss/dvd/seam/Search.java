/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.util.List;
import java.util.Map;

public interface Search
{
    public List getSearchResults();

    public String getActor();
    public void   setActor(String actor);

    public String getTitle();
    public void   setTitle(String title);

    public Integer getCategory();
    public void    setCategory(Integer category);

    public boolean getHasResults();
    public Map<String,Integer> getCategories();

    public String nextPage();
    public String prevPage(); 

    public boolean isLastPage();
    public boolean isFirstPage();

    public String doSearch();
    public String addToCart();

    public String reset();
    public void   destroy();
}
