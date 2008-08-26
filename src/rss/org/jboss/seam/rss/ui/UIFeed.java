package org.jboss.seam.rss.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.document.DocumentData;
import org.jboss.seam.document.DocumentStore;
import org.jboss.seam.document.DocumentData.DocumentType;
import org.jboss.seam.log.Logging;
import org.jboss.seam.navigation.Pages;

import yarfraw.core.datamodel.ChannelFeed;
import yarfraw.core.datamodel.FeedFormat;
import yarfraw.core.datamodel.Text;
import yarfraw.core.datamodel.YarfrawException;
import yarfraw.core.datamodel.Text.TextType;
import yarfraw.io.FeedWriter;

/*
 * atomFeed =
   element atom:feed {
      atomCommonAttributes,
      (atomAuthor*
       & atomCategory*
       & atomContributor*
       & atomGenerator?
       & atomIcon?
       & atomId
       & atomLink*
       & atomLogo?
       & atomRights?
       & atomSubtitle?
       & atomTitle
       & atomUpdated
       & extensionElement*),
      atomEntry*
   }
 */

public class UIFeed extends SyndicationComponent
{
   private static final String COMPONENT_TYPE = "org.jboss.seam.rss.ui.UIFeed";
   private static final String EXTENSION = "xml";
   private static final String MIMETYPE = "text/xml";
   private static final FeedFormat DEFAULT_FEED_FORMAT = FeedFormat.ATOM10;

   private boolean sendRedirect = true;

   private FeedFormat feedFormat = DEFAULT_FEED_FORMAT;
   private String uid;
   private String title;
   private String subtitle;
   private Date updated;
   private String link;


   @SuppressWarnings("unchecked")
   @Override
   public void encodeBegin(FacesContext facesContext) throws IOException
   {
      ChannelFeed channelFeed = new ChannelFeed();
      channelFeed.setUid(getUid());
      channelFeed.setTitle(getTitle());
      channelFeed.setDescriptionOrSubtitle(getSubtitle());
      channelFeed.setPubDate(getUpdated(), new SimpleDateFormat(ATOM_DATE_FORMAT));
      channelFeed.addLink(getLink());
      Contexts.getEventContext().set(FEED_IMPL_KEY, channelFeed);
   }

   @Override
   public void encodeEnd(FacesContext facesContext) throws IOException
   {
      ChannelFeed channelFeed = (ChannelFeed) Contexts.getEventContext().get(FEED_IMPL_KEY);
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      try {
         FeedWriter.writeChannel(DEFAULT_FEED_FORMAT, channelFeed, byteStream);
      } catch (YarfrawException e) {
         throw new IOException("Could not create feed", e);
      }
      byteStream.flush();
      String x = byteStream.toString();
      DocumentType documentType = new DocumentData.DocumentType(EXTENSION, MIMETYPE);

      String viewId = Pages.getViewId(facesContext);
      String baseName = baseNameForViewId(viewId);

      DocumentData documentData = new DocumentData(baseName, documentType, byteStream.toByteArray());

      if (sendRedirect)
      {
         DocumentStore store = DocumentStore.instance();
         String id = store.newId();

         String url = store.preferredUrlForContent(baseName, documentType.getExtension(), id);
         url = Manager.instance().encodeConversationId(url, viewId);

         store.saveData(id, documentData);

         facesContext.getExternalContext().redirect(url);

      }
      else
      {
         UIComponent parent = getParent();

         if (parent instanceof ValueHolder)
         {
            ValueHolder holder = (ValueHolder) parent;
            holder.setValue(documentData);
         }
      }
   }

   public static String baseNameForViewId(String viewId)
   {
      int pos = viewId.lastIndexOf("/");
      if (pos != -1)
      {
         viewId = viewId.substring(pos + 1);
      }

      pos = viewId.lastIndexOf(".");
      if (pos != -1)
      {
         viewId = viewId.substring(0, pos);
      }

      return viewId;
   }

   public boolean isSendRedirect()
   {
      return sendRedirect;
   }

   public void setSendRedirect(boolean sendRedirect)
   {
      this.sendRedirect = sendRedirect;
   }

   @Override
   public String getFamily()
   {
      return COMPONENT_TYPE;
   }

   public String getTitle()
   {
      return (String) valueOf("title", title);
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public String getSubtitle()
   {
      return (String) valueOf("subtitle", subtitle);
   }

   public void setSubtitle(String subtitle)
   {
      this.subtitle = subtitle;
   }

   public Date getUpdated()
   {
      return (Date) valueOf("updated", updated);
   }

   public void setUpdated(Date updated)
   {
      this.updated = updated;
   }

   public String getLink()
   {
      return (String) valueOf("link", link);
   }

   public void setLink(String link)
   {
      this.link = link;
   }

   public String getFeedFormat()
   {
      return (String) valueOf("feedFormat", feedFormat);
   }

   public void setFeedFormat(FeedFormat feedFormat)
   {
      this.feedFormat = feedFormat;
   }

   public String getUid()
   {
      return (String) valueOf("uid", uid);
   }

   public void setUid(String uid)
   {
      this.uid = uid;
   }


}
