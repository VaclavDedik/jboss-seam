package org.jboss.seam.ui.component;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;

/**
 * JSF Component for Link which is able to download a file
 * 
 * @author Daniel Roth
 * 
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Download",value="JSF Component for Link which is able to download a file"),
family="org.jboss.seam.ui.Download", type="org.jboss.seam.ui.Download",generate="org.jboss.seam.ui.component.html.HtmlDownload", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="download"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.DownloadRenderer", family="org.jboss.seam.ui.DownloadRenderer"),
attributes = {"core-props.xml", "javax.faces.component.UICommand.xml", "i18n-props.xml",
        "javax.faces.component.UIOutput.xml", "javax.faces.component.UIGraphic.xml", "accesskey-props.xml"})
public abstract class UIDownload extends UILink
{
   @Attribute(description = @Description("Source xhtml file that acts as resource holder"))
   public abstract String getSrc();

   public abstract void setSrc(String src);

   @Attribute(defaultValue = "true", description = @Description("true iff this component should be rendered"))
   public abstract boolean isRendered();

   @Attribute(description = @Description("the JSF view id to link to."))
   public abstract String getView();

   @Attribute(description = @Description("a pageflow definition to begin. (This is only useful when " +
           "propagation=\"begin\" or propagation=\"join\".)"))
   public abstract String getPageflow();

   @Attribute(defaultValue = "default",
           description = @Description("determines the conversation propagation style: begin, join, nest, none, end or endRoot."))
   public abstract String getPropagation();

   @Attribute(description = @Description("the fragment identifier to link to."))
   public abstract String getFragment();

   @Attribute(description = @Description("The outcome to use when evaluating navigation rules"))
   public abstract String getOutcome();

   @Attribute(description = @Description("If true, write the link as disabled in HTML"))
   public abstract boolean isDisabled();

   @Attribute(description = @Description("Specify the task to operate on (e.g. for @StartTask)"))
   public abstract String getTaskInstance();

   @Attribute(description = @Description("The name of the conversation for natural conversations"))
   public abstract String getConversationName();

   @Attribute(defaultValue = "true",
           description = @Description("Include page parameters defined in pages.xml when rendering the button"))
   public abstract boolean isIncludePageParams();

}
