/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.ejb;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;


@Name("searchparams")
@Scope(ScopeType.EVENT)
public class SearchParameters
    implements Serializable
{
    Integer category = new Integer(0);
    String  title    = null;
    String  actor    = null;

    public void setCategory(Integer category) {
        this.category = category ; 
    }
    public Integer getCategory() {
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
}
