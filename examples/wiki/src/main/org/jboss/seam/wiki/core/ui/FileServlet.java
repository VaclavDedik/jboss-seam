package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.wiki.core.model.File;
import org.jboss.seam.util.Transactions;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.annotations.In;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.swing.*;
import javax.transaction.UserTransaction;
import javax.persistence.EntityManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class FileServlet extends HttpServlet {

    private static final String DOWNLOAD_PATH = "/download";

    /**
     * The maximum width allowed for image rescaling
     */
    private static final int MAX_IMAGE_WIDTH = 3000;

    private byte[] noImage;

    public FileServlet() {
        InputStream in = getClass().getResourceAsStream("/img/filenotfound.png");
        if (in != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            try {
                int read = in.read(buffer);
                while (read != -1) {
                    out.write(buffer, 0, read);
                    read = in.read(buffer);
                }

                noImage = out.toByteArray();
            }
            catch (IOException e) {
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (DOWNLOAD_PATH.equals(request.getPathInfo())) {

            String id = request.getParameter("fileId");
            File file;

            // TODO: Seam should use its transaction interceptor for java beans: http://jira.jboss.com/jira/browse/JBSEAM-957
            UserTransaction userTx = null;
            boolean startedTx = false;
            try {
                userTx = Transactions.getUserTransaction();
                if (userTx.getStatus() != javax.transaction.Status.STATUS_ACTIVE) {
                    startedTx = true;
                    userTx.begin();
                }

                EntityManager em = ((EntityManager)org.jboss.seam.Component.getInstance("entityManager"));
                em.joinTransaction();

                file = (!"".equals(id)) ? em.find(File.class, Long.parseLong(id)) : null;

                if (startedTx) userTx.commit();
            } catch (Exception ex) {
                try {
                    if (startedTx) userTx.rollback();
                } catch (Exception rbEx) {
                    rbEx.printStackTrace();
                }
                throw new RuntimeException(ex);
            }

            String contentType = null;
            byte[] data = null;


            if (file != null && file.getData() != null && file.getData().length > 0) {
                contentType = file.getContentType();
                data = file.getData();
            } else if (noImage != null) {
                contentType = "image/png";
                data = noImage;
            }

            if (data != null) {
                response.setContentType(contentType);

                boolean rescale = false;
                int width = 0;
                ImageIcon icon = null;

                // Check if the image needs to be rescaled (and if the file is an image)
                if (request.getParameter("width") != null && file.getImageMetaInfo() != null) {
                    width = Math.min(MAX_IMAGE_WIDTH, Integer.parseInt(request.getParameter("width")));
                    icon = new ImageIcon(data);
                    if (width > 0 && width != icon.getIconWidth())
                        rescale = true;
                }

                // Rescale the image if required
                if (rescale) {
                    double ratio = (double) width / icon.getIconWidth();
                    int height = (int) (icon.getIconHeight() * ratio);

                    int imageType = "image/png".equals(contentType) ?
                            BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
                    BufferedImage bImg = new BufferedImage(width, height, imageType);
                    Graphics2D g2d = bImg.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2d.drawImage(icon.getImage(), 0, 0, width, height, null);
                    g2d.dispose();

                    String formatName = "";
                    if ("image/png".equals(contentType))
                        formatName = "png";
                    else if ("image/jpeg".equals(contentType))
                        formatName = "jpeg";

                    ImageIO.write(bImg, formatName, response.getOutputStream());
                } else {
                    response.getOutputStream().write(data);
                }
            }

            response.getOutputStream().flush();
        }
    }
}
