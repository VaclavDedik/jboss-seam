package org.jboss.seam.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;

import org.jboss.seam.core.Manager;

public class HtmlLink extends HtmlOutputLink
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.HtmlLink";

   private String view;
   private String action;
   private String pageflow;
   private String propagation = "default";

   private UISelection getSelection()
   {
      UIData parentUIData = getParentUIData();
      if (parentUIData!=null)
      {
         if ( parentUIData.getValue() instanceof DataModel )
         {
            String dataModelExpression = parentUIData.getValueBinding("value").getExpressionString();
            String dataModelName = dataModelExpression.substring(2, dataModelExpression.length()-1);
            UISelection uiSelection = new UISelection();
            uiSelection.setDataModel(dataModelName);
            return uiSelection;
         }
         else
         {
            return null;
         }
      }
      else
      {
         return null;
      }
   }
   
   public UIData getParentUIData()
   {
      UIComponent parent = (UIComponent) this.getParent();
      while (parent!=null)
      {
         if (parent instanceof UIData)
         {
            return (UIData) parent;
         }
         else if (parent instanceof UIComponent)
         {
            parent = parent.getParent();
         }
         else
         {
            return null;
         }
      }
      return null;
   }

   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      ResponseWriter writer = context.getResponseWriter();
      writer.startElement("a", this);
      writer.writeAttribute("id", getClientId(context), null);
      String viewId = view==null ? context.getViewRoot().getViewId() : view;
      String url = context.getApplication().getViewHandler().getActionURL(context, viewId);
      String encodedUrl = context.getExternalContext().encodeActionURL(url);
      String characterEncoding = context.getResponseWriter().getCharacterEncoding();
      boolean first = true;
      for (Object child: getChildren())
      {
         if (child instanceof UIParameter)
         {
            encodedUrl += getParameterString(characterEncoding, (UIParameter) child, first);
            first = false;
         }
      }
      
      ValueBinding actionValueBinding = getValueBinding("action");
      if (actionValueBinding!=null || action!=null)
      {
         UIAction uiAction = new UIAction();
         uiAction.setValueBinding( "action", actionValueBinding );
         uiAction.setAction(action);
         encodedUrl += getParameterString(characterEncoding, uiAction, first);
         first = false;
      }
      
      if ( "default".equals(propagation) || "join".equals(propagation) || "nest".equals(propagation) || "end".equals(propagation) )
      {
         if ( Manager.instance().isLongRunningConversation() )
         {
            encodedUrl += getParameterString(characterEncoding, new UIConversationId(), first);
            first = false;
         }
      }
      
      if ( "join".equals(propagation) || "nest".equals("propagation") || "begin".equals("propagation") || "end".equals(propagation) )
      {
         UIConversationPropagation uiPropagation = new UIConversationPropagation();
         uiPropagation.setType(propagation);
         uiPropagation.setPageflow(pageflow);
         encodedUrl  += getParameterString(characterEncoding, uiPropagation, first);
         first = false;
      }
      
      ValueBinding taskInstanceValueBinding = getValueBinding("taskInstance");
      if (taskInstanceValueBinding!=null)
      {
         UITaskId uiTaskId = new UITaskId();
         uiTaskId.setValueBinding("taskInstance", taskInstanceValueBinding);
         encodedUrl  += getParameterString(characterEncoding, uiTaskId, first);
         first = false;
      }
      
      UISelection uiSelection = getSelection();
      if (uiSelection!=null)
      {
         encodedUrl += getParameterString(characterEncoding, uiSelection, first);
         first = false;
      }
      
      writer.writeAttribute("href", encodedUrl, null);
      HTML.renderHTMLAttributes(writer, this, HTML.ANCHOR_PASSTHROUGH_ATTRIBUTES);
      writer.flush();
      
      Object label = getValue();
      if (label!=null) writer.writeText( label, null );
   }
   
   

   private String getParameterString(String characterEncoding, UIParameter param, boolean first) 
         throws UnsupportedEncodingException
   {
      Object value = param.getValue();
      return (first ? '?' : '&') + 
            param.getName() + '=' + 
            URLEncoder.encode( value==null ? "" : value.toString(), characterEncoding );
   }

   public String getView()
   {
      return view;
   }

   public void setView(String viewId)
   {
      this.view = viewId;
   }

   @Override
   public void restoreState(FacesContext context, Object state) {
      Object[] values = (Object[]) state;
      super.restoreState(context, values[0]);
      view = (String) values[1];
      pageflow = (String) values[2];
      propagation = (String) values[3];
      action =  (String) values[4];
   }

   @Override
   public Object saveState(FacesContext context) {
      Object[] values = new Object[5];
      values[0] = super.saveState(context);
      values[1] = view;
      values[2] = pageflow;
      values[3] = propagation;
      values[4] = action;
      return values;
   }

   public String getPageflow()
   {
      return pageflow;
   }

   public String getPropagation()
   {
      return propagation;
   }

   public void setPageflow(String pageflow)
   {
      this.pageflow = pageflow;
   }

   public void setPropagation(String propagation)
   {
      this.propagation = propagation;
   }

   public String getAction()
   {
      return action;
   }

   public void setAction(String action)
   {
      this.action = action;
   }

}
