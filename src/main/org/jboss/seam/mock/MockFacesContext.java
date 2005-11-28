/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class MockFacesContext extends FacesContext
{
   
   private UIViewRoot viewRoot = new UIViewRoot();
   private Map<FacesMessage, String> messages = new HashMap<FacesMessage, String>();
   private ExternalContext externalContext;
   
   public MockFacesContext(ExternalContext externalContext)
   {
      this.externalContext = externalContext;
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
      return messages.values().iterator();
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
      return messages.keySet().iterator();
   }

   @Override
   public Iterator getMessages(String clientId)
   {
      List list = new ArrayList();
      for (Map.Entry<FacesMessage, String> entry: messages.entrySet())
      {
         if ( clientId.equals( entry.getValue() ) )
         {
            list.add( entry.getKey() );
         }
      }
      return list.iterator();
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
      messages.put(msg, clientId);
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
