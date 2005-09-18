/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.util.Map;

public interface Search
{
    public boolean getHasResults();
    public Map<String,Integer> getCategories();

    public String start();
    public String nextPage();
    public String prevPage(); 
    public boolean isLastPage();
    public boolean isFirstPage();

    public String doSearch();
    public String addToCart();
    public String checkout();

    public void destroy();
}
