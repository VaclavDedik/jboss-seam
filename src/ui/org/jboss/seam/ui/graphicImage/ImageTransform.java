package org.jboss.seam.ui.graphicImage;

import java.io.IOException;

import javax.faces.component.UIComponentBase;

import org.jboss.seam.core.Image;

public interface ImageTransform
{
   public static final String FAMILY = "org.jboss.seam.ui.UIImageTransform";
   
   public abstract void applyTransform(Image image, UIGraphicImage cmp) throws IOException;
}
