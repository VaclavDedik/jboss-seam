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

    public String   nextPage();
    public String   prevPage(); 
    public boolean  isLastPage();
    public boolean  isFirstPage();

    public String   doSearch();
    public String   addToCart();

    public String   reset();
    public void     destroy();
}
