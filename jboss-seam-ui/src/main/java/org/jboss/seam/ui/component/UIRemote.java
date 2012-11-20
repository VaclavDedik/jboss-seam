package org.jboss.seam.ui.component;

import javax.faces.component.UIComponentBase;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;

/**
 * Tag that auto-generates script imports for Seam Remote
 *  
 * @author Shane Bryzak
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Remote",value="Write out script element for use with Seam Remote"),
family="org.jboss.seam.ui.Remote", type="org.jboss.seam.ui.Remote",generate="org.jboss.seam.ui.component.html.HtmlRemote", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="remote"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.RemoteRenderer", family="org.jboss.seam.ui.RemoteRenderer"),
attributes = {"javax.faces.component.UIComponent.xml", "core-props.xml" })
public abstract class UIRemote extends UIComponentBase
{
   
   @Attribute(description = @Description("The Seam components to include in the Seam Remoting JS interface stubs"))
   public abstract String getInclude();

   public abstract void setInclude(String include);
   
}
