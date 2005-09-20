/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;

public class SelectableItem<T>
    implements Serializable
{
    T item;
    boolean selected = false;

    public SelectableItem(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }
    
    public boolean getSelected() {
        return selected;
    }
    public void setSelected(boolean val) {
        this.selected=val;
    }

    public String toString() {
        return super.toString() + "[selected=" + selected + "]";
    }
    
}
