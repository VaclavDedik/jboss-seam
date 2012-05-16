package org.jboss.seam.ui.component;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputLabel;

import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.Tag;

@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Label",value="A label associated with the nearest JSF input component"),
family="javax.faces.Output", type="org.jboss.seam.ui.Label",generate="org.jboss.seam.ui.component.html.HtmlLabel", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="label"), 
attributes = {"label.xml" })
public abstract class UILabel extends HtmlOutputLabel
{

   /**
    * A depth-first search for an EditableValueHolder
    */
   protected static UIComponent getEditableValueHolder(UIComponent component)
   {
      if (component instanceof EditableValueHolder)
      {
         return component.isRendered() ? component : null;
      }
      for (Object child: component.getChildren())
      {
         if (child instanceof UIComponent)
         {
            UIComponent evh = getEditableValueHolder( (UIComponent) child );
            if (evh!=null) return evh;
         }
      }
      return null;
   }

   private static String getInputId(UIComponent cmp)
   {
      String forId = cmp instanceof UIDecorate ?
               ( (UIDecorate) cmp ).getFor() : null;
      if (forId==null)
      {
         UIComponent evh = getEditableValueHolder(cmp);
         return evh==null ? null : evh.getId();
      }
      else
      {
         return forId;
      }
   }
   
   private static String getFor(UIComponent component)
   {
      
      if ( component.getParent()==null )
      {
         return null;
      }
      else if (component instanceof UIDecorate) 
      {
         return getInputId(component);
      }
      else
      {
         return getFor( component.getParent() );
      }
   }

   @Override
   public String getFor()
   {
      return getFor(this);
   }

}
