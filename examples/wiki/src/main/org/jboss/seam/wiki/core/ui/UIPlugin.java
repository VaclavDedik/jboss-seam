package org.jboss.seam.wiki.core.ui;

import javax.faces.component.UINamingContainer;

public class UIPlugin extends UINamingContainer
{
   
   public static final String COMPONENT_FAMILY = "org.jboss.seam.wiki.core.ui.UIPlugin";

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }
   
}
