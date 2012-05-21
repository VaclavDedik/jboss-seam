package org.jboss.seam.ui.graphicImage;

import javax.faces.component.html.HtmlGraphicImage;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;

@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.graphicImage.GraphicImage",value="Output an image to the view. You can specify the file as a path, a java.io.File, java.io.InputStream, java.net.URL or byte[]"),
family="org.jboss.seam.ui.graphicImage.GraphicImage", type="org.jboss.seam.ui.graphicImage.GraphicImage",generate="org.jboss.seam.ui.component.html.HtmlGraphicImage", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="graphicImage"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.graphicImage.GraphicImageRenderer", family="org.jboss.seam.ui.GraphicImageRenderer"),
attributes = {"core-props.xml", "javax.faces.component.UIGraphic.xml", "javax.faces.component.UIOutput.xml", "graphicImage.xml" })
public abstract class UIGraphicImage extends HtmlGraphicImage
{

   public static final String FAMILY = "org.jboss.seam.ui.UIGraphicImage";

   @Attribute
   public abstract String getFileName();

   @Attribute
   public abstract String getStyle();
   
   @Attribute
   public abstract Object getValue();
}
