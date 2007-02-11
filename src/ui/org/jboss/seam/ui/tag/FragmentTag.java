package org.jboss.seam.ui.tag;

import org.jboss.seam.ui.UIFragment;

public class FragmentTag extends UIComponentTagBase 
{

   @Override
   public String getComponentType()
   {
     return UIFragment.COMPONENT_TYPE;
   }

   @Override
   public String getRendererType()
   {
     return null;
   }

}
