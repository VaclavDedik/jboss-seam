/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

public interface Search
{
    public String   getActor();
    public void     setActor(String actor);
    public String   getTitle();
    public void     setTitle(String title);
    public Category getCategory();
    public void     setCategory(Category category);

    public void     nextPage();
    public void     prevPage(); 
    public boolean  isLastPage();
    public boolean  isFirstPage();

    public void     doSearch();
    public void     selectFromRequest();
    public void     addToCart();
    public void     addAllToCart();
    
    public int      getPageSize();
    public void     setPageSize(int pageSize);

    public void     reset();
    public void     destroy();
}
