package org.jboss.seam.ui.tag;

import javax.faces.component.UIComponent;

import org.jboss.seam.ui.FileUpload;

/**
 * File upload tag attributes
 * 
 * @author Shane Bryzak
 */
public class FileUploadTag extends UIComponentTagBase
{ 
   @Override
   public String getComponentType()
   {
       return FileUpload.COMPONENT_TYPE;
   }

   @Override
   public String getRendererType()
   {
       return null;
   }

   @Override
   protected void setProperties(UIComponent component)
   {
       super.setProperties(component);
   }
}
