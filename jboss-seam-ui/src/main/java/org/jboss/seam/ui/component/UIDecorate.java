package org.jboss.seam.ui.component;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.jboss.seam.ui.util.Decoration;
import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;


/**
 * "Decorates" a JSF input field when validation fails or when required="true" is set
 * @author mnovotny
 *
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.Decorate",value="\"Decorate\" a JSF input field when validation fails or when required=\"true\" is set."),
family="org.jboss.seam.ui.Decorate", type="org.jboss.seam.ui.Decorate",generate="org.jboss.seam.ui.component.html.HtmlDecorate", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="decorate", handler="org.jboss.seam.ui.handler.DecorateHandler"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.DecorateRenderer", family="org.jboss.seam.ui.DecorateRenderer"),
attributes = {"javax.faces.component.UIComponent.xml", "core-props.xml" })
public abstract class UIDecorate extends UIComponentBase implements NamingContainer
{
   
   private static final String COMPONENT_TYPE = "org.jboss.seam.ui.Decorate";

   public boolean hasMessage()
   {
      String clientId = getInputClientId();
      if (clientId==null)
      {
         return false;
      }
      else
      {
         return getFacesContext().getMessages(clientId).hasNext();
      }
   }

   public String getInputId()
   {
      String id = getFor();
      if (id==null)
      {
         UIComponent evh = Decoration.getEditableValueHolder(this);
         return evh==null ? null : evh.getId();
      }
      else
      {
         return id;
      }
   }

   private String getInputClientId()
   {
      String id = getFor();
      if (id==null)
      {
         UIComponent evh = Decoration.getEditableValueHolder(this);
         return evh==null ? null : evh.getClientId( getFacesContext() );
      }
      else
      {
         // As UIDecorate implements NamingContainer it alters the search 
         // algorithm used by JSF
         UIComponent component = null;
         if (getParent() != null)
         {
             component = getParent().findComponent(id);
         }
         return component==null ? null : component.getClientId( getFacesContext() );
      }
   }

   @Attribute(description = @Description("Id of the input field to decorate"))
   public abstract String getFor();
   

   public abstract void setFor(String forId);
   
   @Attribute   
   public abstract String getStyleClass();
   
   public abstract void setStyleClass(String styleClass);
   
   @Attribute   
   public abstract String getStyle();
   
   public abstract void setStyle(String style);

   @Attribute(defaultValue="true", description = @Description("if true, the template used to decorate the input " +
           "field is enclosed by the element specified with the \"element\" attribute. By default this is a div element."))
   public abstract boolean isEnclose();
      
   public abstract void setEnclose(boolean enclose);
      
   @Attribute(defaultValue = "div", description = @Description("the element to enclose the template used to decorate " +
           "the input field. By default, the template is enclosed with a div element."))
   public abstract String getElement();
     
   public abstract void setElement(String element);  

   @Attribute
   public UIComponent getDecoration(String name)
   {
      return Decoration.getDecoration(name, this);
   }
   
   public static UIDecorate newInstance()
   {
      return (UIDecorate) FacesContext.getCurrentInstance().getApplication().createComponent(COMPONENT_TYPE);
   }

   @Attribute(description = @Description("XHTML template to use to decorate the input field"))
   public abstract String getTemplate();
}
