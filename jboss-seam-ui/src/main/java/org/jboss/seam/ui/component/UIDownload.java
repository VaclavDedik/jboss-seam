package org.jboss.seam.ui.component;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;

/**
 * 
 * @author Daniel Roth
 * 
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Download",value="Link for downloading a file"),
family="org.jboss.seam.ui.Download", type="org.jboss.seam.ui.Download",generate="org.jboss.seam.ui.component.html.HtmlDownload", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="download"), 
renderer = @JsfRenderer(generate="false",type="org.jboss.seam.ui.renderkit.DownloadRendererBase", family="org.jboss.seam.ui.DownloadRenderer"),
attributes = {"core-props.xml", "link.xml", "download.xml" })
public abstract class UIDownload extends UILink
{
   @Attribute
   public abstract String getSrc();

   public abstract void setSrc(String src);

}
