/**
 * 
 */
package org.jboss.seam.ui.component;

import javax.faces.component.UIOutput;

import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;

/**
 * Simulates pressing the action source when 'enter' is pressed
 * 
 * @author mnovotny
 *
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.DefaultAction",value="Simulates pressing the action source when 'enter' is pressed."),
family="org.jboss.seam.ui.DefaultAction", type="org.jboss.seam.ui.DefaultAction",generate="org.jboss.seam.ui.component.html.HtmlDefaultAction", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="defaultAction"),
renderer = @JsfRenderer(type="org.jboss.seam.ui.DefaultActionRenderer", family="org.jboss.seam.ui.DefaultActionRenderer"))
public abstract class UIDefaultAction extends UIOutput
{

}
