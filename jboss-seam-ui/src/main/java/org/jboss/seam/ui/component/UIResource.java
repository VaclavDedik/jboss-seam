package org.jboss.seam.ui.component;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;

/**
 * JSF Component which can use a data in form as inputstream, 
 * java.util.File or byte[] and a content-type, this tag sends the data to the browser
 * @author Daniel Roth
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Resource",value="Given a data in form of an inputstream, java.util.File or byte[] and a content-type, this tag sends the data to the browser"),
family="org.jboss.seam.ui.Resource", type="org.jboss.seam.ui.Resource",generate="org.jboss.seam.ui.component.html.HtmlResource", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="resource"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.ResourceRenderer", family="org.jboss.seam.ui.ResourceRenderer"),
attributes = {"resource.xml" })
public abstract class UIResource extends UIComponentBase
{

   @Attribute
   public abstract Object getData();

   public abstract void setData(Object data);

   @Attribute
   public abstract String getContentType();

   public abstract void setContentType(String contentType);

   @Attribute
   public abstract String getDisposition();

   public abstract void setDisposition(String disposition);

   @Attribute
   public abstract String getFileName();

   public abstract void setFileName(String fileName);

   @Override
   public void encodeBegin(FacesContext arg0) throws IOException
   {
      if (!(getParent() instanceof UIViewRoot || getParent() instanceof UIDownload))  
      {
         throw new IllegalArgumentException("s:remote must be nested in a s:download or alone in the page");
      }
      super.encodeBegin(arg0);
   }

}
