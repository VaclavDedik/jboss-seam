
/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.util.*;
import javax.faces.convert.Converter;

public interface Categories {
    public void loadData();

    public Map<String,Category> getCategories();
    public Converter getConverter();
    public Category getNullCategory();
}
