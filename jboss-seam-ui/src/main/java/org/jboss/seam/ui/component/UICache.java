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
 * JSF component class
 * 
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Cache",value="Cache the rendered page fragment using the installed cache provider."),
family="org.jboss.seam.ui.Cache", type="org.jboss.seam.ui.Cache",generate="org.jboss.seam.ui.component.html.HtmlCache", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="cache"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.CacheRenderer", family="org.jboss.seam.ui.CacheRenderer"),
attributes = {"base-props.xml", "cache.xml" })
public abstract class UICache extends UIComponentBase
{

   @Attribute
   public abstract boolean isEnabled();
   
   @Attribute
   public abstract String getKey();
   
   @Attribute
   public abstract String getRegion();
   
   @Attribute
   public abstract CacheProvider getCacheProvider();
   
}
