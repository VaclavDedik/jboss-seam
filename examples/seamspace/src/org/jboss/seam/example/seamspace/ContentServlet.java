package org.jboss.seam.example.seamspace;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.jboss.seam.Component;

/**
 * Serves images and other member content
 * 
 * @author Shane Bryzak
 */
public class ContentServlet extends HttpServlet
{
   private static final long serialVersionUID = -8461940507242022217L;

   private static final String IMAGES_PATH = "/images";

   /**
    * The maximum width allowed for image rescaling
    */
   private static final int MAX_IMAGE_WIDTH = 1024;

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException
   {
      if (IMAGES_PATH.equals(request.getPathInfo()))
      {
         ContentLocal contentAction = (ContentLocal) Component.getInstance(ContentAction.class);

         MemberImage mi = contentAction.getImage(Integer.parseInt(request.getParameter("id")));

         if (mi != null)
         {
            response.setContentType(mi.getContentType());

            boolean rescale = false;
            int width = 0;
            ImageIcon icon = null;

            // Check if the image needs to be rescaled
            if (request.getParameter("width") != null)
            {
               width = Math.min(MAX_IMAGE_WIDTH, Integer.parseInt(request
                     .getParameter("width")));
               icon = new ImageIcon(mi.getData());
               if (width > 0 && width != icon.getIconWidth())
                  rescale = true;
            }

            // Rescale the image if required
            if (rescale)
            {
               double ratio = (double) width / icon.getIconWidth();
               int height = (int) (icon.getIconHeight() * ratio);
               BufferedImage bImg = new BufferedImage(width, height,
                     BufferedImage.TYPE_INT_ARGB);
               Graphics2D g2d = bImg.createGraphics();
               g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                     RenderingHints.VALUE_INTERPOLATION_BICUBIC);
               g2d.drawImage(icon.getImage(), 0, 0, width, height, null);
               g2d.dispose();
               String formatName = "";

               if ("image/png".equals(mi.getContentType()))
                  formatName = "png";
               else if ("image/jpeg".equals(mi.getContentType()))
                  formatName = "jpg";

               ImageIO.write(bImg, formatName, response.getOutputStream());
            }
            else
               response.getOutputStream().write(mi.getData());

            response.getOutputStream().flush();
         }
      }
   }
}
