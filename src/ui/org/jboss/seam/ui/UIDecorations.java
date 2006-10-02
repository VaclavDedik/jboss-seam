package org.jboss.seam.ui;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

public class UIDecorations extends UIComponentBase
{

   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIDecorations";
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Decorations";

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }
   
   public UIComponent getBeforeInvalidDecoration()
   {
      return getFacet("beforeInvalidField");
   }

   public UIComponent getAfterInvalidDecoration()
   {
      return getFacet("afterInvalidField");
   }

   public UIComponent getAroundInvalidDecoration()
   {
      return getFacet("aroundInvalidField");
   }

   public UIComponent getBeforeDecoration()
   {
      return getFacet("beforeField");
   }

   public UIComponent getAfterDecoration()
   {
      return getFacet("afterField");
   }

   public UIComponent getAroundDecoration()
   {
      return getFacet("aroundField");
   }

}
