package org.jboss.seam.rss.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.contexts.Contexts;
import javax.faces.context.FacesContext;

import yarfraw.core.datamodel.ChannelFeed;
import yarfraw.core.datamodel.FeedFormat;
import yarfraw.core.datamodel.YarfrawException;
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
      Writer responseWriter = ((HttpServletResponse)facesContext.getExternalContext().getResponse()).getWriter();
      HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
      response.setContentType(MIMETYPE);
      response.setContentLength(byteStream.size());
      responseWriter.write(byteStream.toString());
      response.flushBuffer();
      facesContext.responseComplete();
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
