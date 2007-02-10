package org.jboss.seam.mail.ui.context;

import java.util.Iterator;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;


public class MailFacesContextImpl extends FacesContext
{
   
   private FacesContext delegate;
   private ExternalContext externalContext;
   
   public MailFacesContextImpl(FacesContext delegate)
   {
      this.delegate = delegate;
      externalContext = new MailExternalContextImpl(delegate.getExternalContext());
   }
   
   public MailFacesContextImpl(FacesContext delegate, String urlBase)
   {
      this.delegate = delegate;
      externalContext = new MailExternalContextImpl(delegate.getExternalContext(), urlBase);
   }

   @Override
   public void addMessage(String clientId, FacesMessage message)
   {
      delegate.addMessage(clientId, message);
   }

   @Override
   public Application getApplication()
   {
     return delegate.getApplication();
   }

   @Override
   public Iterator getClientIdsWithMessages()
   {
     return delegate.getClientIdsWithMessages();
   }

   @Override
   public ExternalContext getExternalContext()
   {
      return externalContext;
   }

   @Override
   public Severity getMaximumSeverity()
   {
      return delegate.getMaximumSeverity();
   }

   @Override
   public Iterator getMessages()
   {
     return delegate.getMessages();
   }

   @Override
   public Iterator getMessages(String clientId)
   {
      return delegate.getMessages(clientId);
   }

   @Override
   public RenderKit getRenderKit()
   {
     return delegate.getRenderKit();
   }

   @Override
   public boolean getRenderResponse()
   {
      return delegate.getRenderResponse();
   }

   @Override
   public boolean getResponseComplete()
   {
     return delegate.getResponseComplete();
   }

   @Override
   public ResponseStream getResponseStream()
   {
      return delegate.getResponseStream();
   }

   @Override
   public ResponseWriter getResponseWriter()
   {
      return delegate.getResponseWriter();
   }

   @Override
   public UIViewRoot getViewRoot()
   {
     return delegate.getViewRoot();
   }

   @Override
   public void release()
   {
      delegate.release();
   }

   @Override
   public void renderResponse()
   {
     delegate.renderResponse();
   }

   @Override
   public void responseComplete()
   {
      delegate.responseComplete();
   }

   @Override
   public void setResponseStream(ResponseStream responseStream)
   {
      delegate.setResponseStream(responseStream);
   }

   @Override
   public void setResponseWriter(ResponseWriter responseWriter)
   {
      delegate.setResponseWriter(responseWriter);
   }

   @Override
   public void setViewRoot(UIViewRoot root)
   {
      delegate.setViewRoot(root);
   }
   
   public FacesContext getDelegate() {
      return delegate;
   }
   
   public static void start(String urlBase) {
      FacesContext mailFacesContext = new MailFacesContextImpl(getCurrentInstance(), urlBase);
      setCurrentInstance(mailFacesContext);
   }
   
   public static void stop() {
      if (getCurrentInstance() instanceof MailFacesContextImpl)
      {
         MailFacesContextImpl mailFacesContextImpl = (MailFacesContextImpl) getCurrentInstance();
         setCurrentInstance(mailFacesContextImpl.getDelegate());
         
      }
   }

}
