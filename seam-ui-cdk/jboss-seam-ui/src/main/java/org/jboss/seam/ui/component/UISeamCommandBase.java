package org.jboss.seam.ui.component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIOutput;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionListener;
import javax.faces.model.DataModel;

import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Pages;
import org.jboss.seam.pages.Page;

public abstract class UISeamCommandBase extends UIOutput implements ActionSource
{

   private String encodedUrl;

   private class Url
   {
      private String encodedUrl;

      private Map<String, String> parameters;

      private String fragment;

      private String characterEncoding;

      private Page page;

      public Url(String viewId, String fragment)
      {
         FacesContext facesContext = FacesContext.getCurrentInstance();
         String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext,
                  viewId);
         String encodedUrl = facesContext.getExternalContext().encodeActionURL(url);
         encodedUrl = Pages.instance().encodeScheme(viewId, facesContext, encodedUrl);
         characterEncoding = facesContext.getResponseWriter().getCharacterEncoding();
         page = Pages.instance().getPage(viewId);
         this.encodedUrl = url;
         this.fragment = fragment;
         this.parameters = new HashMap<String, String>();
      }

      private String urlEncode(String value) throws UnsupportedEncodingException
      {
         return characterEncoding == null ? URLEncoder.encode(value) : URLEncoder.encode(value,
                  characterEncoding);
      }

      public void addParameter(UIParameter parameter) throws UnsupportedEncodingException
      {
         String value = parameter.getValue() == null ? "" : parameter.getValue().toString();
         String name = parameter.getName();
         boolean append = true;
         if (name.equals(page.getConversationIdParameter().getParameterName())
                  && parameters.containsKey(name))
         {
            append = false;
         }
         if (append)
         {
            parameters.put(name, urlEncode(value));
         }
      }

      private String getParameters()
      {
         String params = "";
         for (String key : parameters.keySet())
         {
            params += "&" + key + "=" + parameters.get(key);
         }
         if (!"".equals(params))
         {
            params = "?" + params.substring(1);
         }
         return params;
      }

      private String getFragment()
      {
         if (fragment != null && !"".equals(fragment))
         {
            return "#" + fragment;
         }
         else
         {
            return "";
         }
      }

      public String getEncodedUrl()
      {
         return encodedUrl + getParameters() + getFragment();
      }
   }

   public abstract boolean isDisabled();

   public abstract void setDisabled(boolean disabled);

   public abstract String getView();

   protected String getUrl() throws UnsupportedEncodingException
   {
      if (encodedUrl == null)
      {
         FacesContext context = getFacesContext();
         String viewId = getView();
         if (viewId == null)
         {
            viewId = Pages.getViewId(getFacesContext());
         }

         Url url = new UISeamCommandBase.Url(viewId, getFragment());

         Set<String> usedParameters = new HashSet<String>();
         for (Object child : getChildren())
         {
            if (child instanceof UIParameter)
            {
               usedParameters.add(((UIParameter) child).getName());
            }
         }

         if (viewId != null)
         {
            Map<String, Object> pageParameters = Pages.instance().getConvertedParameters(context,
                     viewId, usedParameters);
            for (Map.Entry<String, Object> me : pageParameters.entrySet())
            {
               UIParameter uip = new UIParameter();
               uip.setName(me.getKey());
               uip.setValue(me.getValue());
               url.addParameter(uip);
            }
         }

         if (getAction() != null || getOutcome() != null)
         {

            UIAction uiAction = new UIAction();
            uiAction.setAction(getAction() == null ? getOutcome() : getAction()
                     .getExpressionString());
            url.addParameter(uiAction);
         }

         if ("default".equals(getPropagation()) || "join".equals(getPropagation())
                  || "nest".equals(getPropagation()) || "end".equals(getPropagation()))
         {
            UIConversationId uiConversationId = new UIConversationId();
            uiConversationId.setViewId(viewId);
            url.addParameter(uiConversationId);
            if (Conversation.instance().isLongRunning() || Conversation.instance().isNested())
            {
               url.addParameter(new UIConversationIsLongRunning());
            }
         }

         if ("join".equals(getPropagation()) || "nest".equals(getPropagation())
                  || "begin".equals(getPropagation()) || "end".equals(getPropagation()))
         {
            UIConversationPropagation uiPropagation = new UIConversationPropagation();
            uiPropagation.setType(getPropagation());
            uiPropagation.setPageflow(getPageflow());
            url.addParameter(uiPropagation);
         }

         ValueBinding taskInstanceValueBinding = getValueBinding("taskInstance");
         if (taskInstanceValueBinding != null)
         {
            UITaskId uiTaskId = new UITaskId();
            uiTaskId.setValueBinding("taskInstance", taskInstanceValueBinding);
            url.addParameter(uiTaskId);
         }

         UISelection uiSelection = getSelection();
         if (uiSelection != null)
         {
            url.addParameter(uiSelection);
         }
         encodedUrl = url.getEncodedUrl();
      }
      return encodedUrl;
   }

   public abstract void setView(String view);

   public abstract MethodBinding getAction();

   public abstract void setAction(MethodBinding action);

   public abstract String getOutcome();

   public abstract void setOutcome(String outcome);

   public abstract String getPropagation();

   public abstract void setPropagation(String propagtion);

   public abstract String getPageflow();

   public abstract void setPageflow(String pageflow);

   public abstract String getFragment();

   public abstract void setFragment(String fragment);

   public abstract String getOnclick();

   public String getOnClick() throws IOException
   {
      String onclick = getOnclick();
      if (onclick == null)
      {
         onclick = "";
      }
      else if (onclick.length() > 0 && !onclick.endsWith(";"))
      {
         onclick += ";";
      }
      if (!isDisabled())
      {
         onclick += "location.href='" + getUrl() + "'";
      }
      return onclick;
   }

   public abstract void setOnclick(String onclick);

   public UISelection getSelection()
   {
      UIData parentUIData = getParentUIData();
      if (parentUIData != null)
      {
         if (parentUIData.getValue() instanceof DataModel)
         {
            String dataModelExpression = parentUIData.getValueBinding("value")
                     .getExpressionString();
            String dataModelName = dataModelExpression.substring(2,
                     dataModelExpression.length() - 1).replace('$', '.');
            UISelection uiSelection = new UISelection();
            uiSelection.setDataModel(dataModelName);
            uiSelection.setVar(parentUIData.getVar());
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
      UIComponent parent = this.getParent();
      while (parent != null)
      {
         if (parent instanceof UIData)
         {
            return (UIData) parent;
         }
         else
         {
            parent = parent.getParent();
         }
      }
      return null;
   }

   public void addActionListener(ActionListener listener)
   {
      // TODO Auto-generated method stub
   }

   public void removeActionListener(ActionListener listener)
   {
      // TODO Auto-generated method stub
   }

   public ActionListener[] getActionListeners()
   {
      // TODO Auto-generated method stub
      return null;
   }

}
