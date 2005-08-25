//$Id$
package org.jboss.seam.mock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.servlet.http.HttpServletRequest;

public class MockFacesContext extends FacesContext
{
   
   private UIViewRoot viewRoot = new UIViewRoot();
   private Map<String, FacesMessage> mesages = new HashMap<String, FacesMessage>();
   private ExternalContext externalContext;
   
   public MockFacesContext(HttpServletRequest request)
   {
      externalContext = new MockExternalContext(request);
   }

   @Override
   public Application getApplication()
   {
      //TODO
      return null;
   }

   @Override
   public Iterator getClientIdsWithMessages()
   {
      //TODO
      return null;
   }

   @Override
   public ExternalContext getExternalContext()
   {
      return externalContext;
   }

   @Override
   public Severity getMaximumSeverity()
   {
      //TODO
      return null;
   }

   @Override
   public Iterator getMessages()
   {
      return mesages.values().iterator();
   }

   @Override
   public Iterator getMessages(String arg0)
   {
      return null;
   }

   @Override
   public RenderKit getRenderKit()
   {
      //TODO
      return null;
   }

   @Override
   public boolean getRenderResponse()
   {
      //TODO
      return false;
   }

   @Override
   public boolean getResponseComplete()
   {
      //TODO
      return false;
   }

   @Override
   public ResponseStream getResponseStream()
   {
      //TODO
      return null;
   }

   @Override
   public void setResponseStream(ResponseStream arg0)
   {
      //TODO

   }

   @Override
   public ResponseWriter getResponseWriter()
   {
      //TODO
      return null;
   }

   @Override
   public void setResponseWriter(ResponseWriter arg0)
   {
      //TODO

   }

   @Override
   public UIViewRoot getViewRoot()
   {
      return viewRoot;
   }

   @Override
   public void setViewRoot(UIViewRoot vr)
   {
      viewRoot = vr;
   }

   @Override
   public void addMessage(String clientId, FacesMessage msg)
   {
      mesages.put(clientId, msg);
   }

   @Override
   public void release()
   {
      //TODO

   }

   @Override
   public void renderResponse()
   {
      //TODO

   }

   @Override
   public void responseComplete()
   {
      //TODO

   }
   
   public void setCurrent()
   {
      setCurrentInstance(this);
   }

}
