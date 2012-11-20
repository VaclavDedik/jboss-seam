/**
 * License Agreement.
 * 
 * Ajax4jsf 1.1 - Natural Ajax for Java Server Faces (JSF)
 * 
 * Copyright (C) 2007 Exadel, Inc.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1 as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.jboss.seam.ui.component;

import javax.faces.component.UIComponentBase;

import org.jboss.seam.cache.CacheProvider;
import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;


/**
 * JSF component class for Seam UICache
 * 
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Cache",value="Cache the rendered page fragment using the installed cache provider."),
family="org.jboss.seam.ui.Cache", type="org.jboss.seam.ui.Cache",generate="org.jboss.seam.ui.component.html.HtmlCache", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="cache"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.CacheRenderer", family="org.jboss.seam.ui.CacheRenderer"),
attributes = {"base-props.xml" })
public abstract class UICache extends UIComponentBase
{

   @Attribute(defaultValue = "true", description = @Description("a value expression that determines if the cache should be used."))
   public abstract boolean isEnabled();
   
   @Attribute(description = @Description("the key to cache rendered content, often a value expression. For example, " +
           "if we were caching a page fragment that displays a document, we might use key=\"Document-#{document.id}\"."))
   public abstract String getKey();
   
   @Attribute(description = @Description("a cache node to use (different nodes can have different expiry policies)."))
   public abstract String getRegion();
   
   @Attribute(defaultValue = "org.jboss.seam.cache.CacheProvider.instance()",
           description = @Description("The cache provider to use, only needed if you install alter the default " +
           "cache provider in an application where multiple cache providers are in use"))
   public abstract CacheProvider getCacheProvider();
   
}
