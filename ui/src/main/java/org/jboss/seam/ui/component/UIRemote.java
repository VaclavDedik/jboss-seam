package org.jboss.seam.ui.component;

import javax.faces.component.UIComponentBase;

/**
 * Tag that auto-generates script imports for Seam Remoting
 *  
 * @author Shane Bryzak
 */
public abstract class UIRemote extends UIComponentBase
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIRemote";

   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Remote";

   public abstract String getInclude();

   public abstract void setInclude(String include);
}
