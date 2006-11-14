/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.seam.ui.tag;

import javax.faces.component.UIComponent;

import org.jboss.seam.ui.HtmlButton;


public class ButtonTag extends HtmlOutputButtonTagBase
{
    @Override
    public String getComponentType()
    {
        return HtmlButton.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType()
    {
        return null;
    }

    private String view;
    private String action;
    private String propagation;
    private String pageflow;
    private String taskInstance;
    private String fragment;

    @Override
    protected void setProperties(UIComponent component)
    {
        super.setProperties(component);
        setStringProperty(component, "view", view);
        setStringProperty(component, "action", action);
        setValueBinding(component, "taskInstance", taskInstance);
        setStringProperty(component, "propagation", propagation);
        setStringProperty(component, "pageflow", pageflow);
        setStringProperty(component, "fragment", fragment);
    }

    public void setAction(String action)
    {
        this.action = action;
    }

   public void setPageflow(String pageflow)
   {
      this.pageflow = pageflow;
   }

   public void setPropagation(String propagation)
   {
      this.propagation = propagation;
   }

   public void setView(String view)
   {
      this.view = view;
   }

   public void setTaskInstance(String task)
   {
      this.taskInstance = task;
   }

   public void setFragment(String fragment)
   {
      this.fragment = fragment;
   }
}
