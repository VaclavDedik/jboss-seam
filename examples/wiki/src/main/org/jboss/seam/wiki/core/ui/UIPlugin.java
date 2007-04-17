package org.jboss.seam.wiki.core.ui;

import javax.faces.component.UINamingContainer;

public class UIPlugin extends UINamingContainer
{
   
   public static final String COMPONENT_FAMILY = "org.jboss.seam.wiki.core.ui.UIPlugin";
   
   public static final String NEXT_PLUGIN = "org.jboss.seam.wiki.core.ui.UIPlugin.nextPlugin";

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }
   
}
