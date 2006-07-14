package org.jboss.seam.ui.tag;

import javax.faces.component.UIComponent;

import org.jboss.seam.ui.UICache;


public class CacheTag extends UIComponentTagBase
{
   public String getComponentType()
   {
      return UICache.COMPONENT_TYPE;
   }

   public String getRendererType()
   {
      return null;
   }

   private String key;
   private String enabled;
   private String region;

   public String getKey()
   {
      return key;
   }

   public void setKey(String key)
   {
      this.key = key;
   }

   public String getRegion()
   {
      return region;
   }

   public void setRegion(String region)
   {
      this.region = region;
   }

   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      setStringProperty(component, "key", key);
      setStringProperty(component, "region", region);
      setValueBinding(component, "enabled", enabled);
    }

}
