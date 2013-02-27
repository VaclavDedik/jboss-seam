package org.jboss.seam.jsf;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.util.FacesUrlTransformer;

/**
 * Allows the JSF view locale to be integrated with
 * the locale coming from Seam internationalization.
 *
 * @see org.jboss.seam.international.LocaleSelector
 *
 * @author Gavin King
 *
 */
public class SeamViewHandler extends ViewHandlerWrapper
{
   private static enum Source
   {
      ACTION, BOOKMARKABLE, REDIRECT, RESOURCE
   }

   private ViewHandler viewHandler;
   private static final ThreadLocal<Source> source = new ThreadLocal<Source>();

   public SeamViewHandler(ViewHandler viewHandler)
   {
      this.viewHandler = viewHandler;
   }

   @Override
   public String calculateCharacterEncoding(FacesContext context)
   {
      return viewHandler.calculateCharacterEncoding(context);
   }

   @Override
   public void initView(FacesContext context) throws FacesException
   {
      viewHandler.initView(context);
   }

   @Override
   public Locale calculateLocale(FacesContext facesContext)
   {
      Locale jsfLocale = viewHandler.calculateLocale(facesContext);
      if ( !Contexts.isSessionContextActive() )
      {
         return jsfLocale;
      }
      else
      {
         return LocaleSelector.instance().calculateLocale(jsfLocale);
      }
   }

   @Override
   public String calculateRenderKitId(FacesContext ctx)
   {
      return viewHandler.calculateRenderKitId(ctx);
   }

   @Override
   public UIViewRoot createView(FacesContext ctx, String viewId)
   {
      return viewHandler.createView(ctx, viewId);
   }

   /**
    * Allow the delegate to produce the action URL. If the conversation is
    * long-running, append the conversation id request parameter to the query
    * string part of the URL, but only if the request parameter is not already
    * present.
    * <p/>
    * This covers form actions Ajax calls, and redirect URLs (which we want) and
    * link hrefs (which we don't)
    *
    * @see {@link ViewHandler#getActionURL(FacesContext, String)}
    */
   @Override
   public String getActionURL(FacesContext facesContext, String viewId) {
       String actionUrl = super.getActionURL(facesContext, viewId);
       Conversation conversation = Conversation.instance();
       Manager manager = Manager.instance();
       String conversationIdParameter = manager.getConversationIdParameter();

       if (!getSource().equals(Source.BOOKMARKABLE) && !getSource().equals(Source.REDIRECT) )
       {
          if ( !conversation.isNested() || conversation.isLongRunning() )
          {
             return new FacesUrlTransformer(actionUrl, facesContext)
             .appendConversationIdIfNecessary(conversationIdParameter, conversation.getId())
             .getUrl();
          }
          else
          {
             return new FacesUrlTransformer(actionUrl, facesContext)
             .appendConversationIdIfNecessary(conversationIdParameter, conversation.getParentId())
             .getUrl();
          }

       } else {
           return actionUrl;
       }
   }

   /* (non-Javadoc)
    * @see javax.faces.application.ViewHandlerWrapper#getRedirectURL(javax.faces.context.FacesContext, java.lang.String, java.util.Map, boolean)
    */
   @Override
   public String getRedirectURL(FacesContext context, String viewId, Map<String, List<String>> parameters, boolean includeViewParams)
   {
      try
      {
         source.set(Source.REDIRECT);
         return super.getRedirectURL(context, viewId, parameters, includeViewParams);
      }
      finally
      {
         source.remove();
      }
   }

   /* (non-Javadoc)
    * @see javax.faces.application.ViewHandlerWrapper#getBookmarkableURL(javax.faces.context.FacesContext, java.lang.String, java.util.Map, boolean)
    */
   @Override
   public String getBookmarkableURL(FacesContext context, String viewId, Map<String, List<String>> parameters, boolean includeViewParams)
   {
      try
      {
         source.set(Source.BOOKMARKABLE);
         return super.getBookmarkableURL(context, viewId, parameters, includeViewParams);
      }
      finally
      {
         source.remove();
      }
   }

   private Source getSource()
   {
      if (source.get() == null)
      {
         return Source.ACTION;
      }
      else
      {
         return source.get();
      }
   }

   @Override
   public String getResourceURL(FacesContext ctx, String path)
   {
      try
      {
         source.set(Source.RESOURCE);
         return super.getResourceURL(ctx, path);
      }
      finally
      {
         source.remove();
      }
   }

   @Override
   public void renderView(FacesContext ctx, UIViewRoot viewRoot)
         throws IOException, FacesException
   {
      viewHandler.renderView(ctx, viewRoot);
   }

   @Override
   public UIViewRoot restoreView(FacesContext ctx, String viewId)
   {
      UIViewRoot viewRoot =viewHandler.restoreView(ctx, viewId);
      if (viewRoot != null)
      {
         viewRoot.setViewId(viewHandler.deriveViewId(ctx,viewId));
      }
      return viewRoot;
   }

   @Override
   public void writeState(FacesContext ctx) throws IOException
   {
      viewHandler.writeState(ctx);
   }

   @Override
   public ViewHandler getWrapped()
   {
      return viewHandler;
   }

}
