package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.security.Identity;
import org.jboss.seam.web.Session;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiUpload;
import org.jboss.seam.wiki.core.model.WikiUploadImage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileServlet extends HttpServlet {

    private static final String DOWNLOAD_PATH = "/download.seam";

    private byte[] fileNotFoundImage;

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

                fileNotFoundImage = out.toByteArray();
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
            WikiUpload file = null;

            if (!"".equals(id)) {
                // TODO: Seam should use its transaction interceptor for java beans: http://jira.jboss.com/jira/browse/JBSEAM-957
                UserTransaction userTx = null;
                boolean startedTx = false;
                try {

                    userTx = (UserTransaction)org.jboss.seam.Component.getInstance("org.jboss.seam.transaction.transaction");
                    if (userTx.getStatus() != javax.transaction.Status.STATUS_ACTIVE) {
                        startedTx = true;
                        userTx.begin();
                    }

                    WikiNodeDAO wikiNodeDAO = (WikiNodeDAO)org.jboss.seam.Component.getInstance(WikiNodeDAO.class);
                    file = wikiNodeDAO.findWikiUpload(Long.parseLong(id));

                    if (startedTx) userTx.commit();
                } catch (Exception ex) {
                    try {
                        if (startedTx && userTx.getStatus() != javax.transaction.Status.STATUS_MARKED_ROLLBACK)
                            userTx.rollback();
                    } catch (Exception rbEx) {
                        rbEx.printStackTrace();
                    }
                    throw new RuntimeException(ex);
                }
            }

            String contentType = null;
            byte[] data = null;

            String thumbnail = request.getParameter("thumbnail");

            if (file != null
                && thumbnail != null
                && Boolean.valueOf(thumbnail)
                && file.isInstance(WikiUploadImage.class)
                && ((WikiUploadImage)file).getThumbnailData() != null
                && ((WikiUploadImage)file).getThumbnailData().length >0) {

                // Render thumbnail picture
                contentType = file.getContentType();
                data = ((WikiUploadImage)file).getThumbnailData();

            } else if (file != null && file.getData() != null && file.getData().length > 0) {

                // Render file regularly
                contentType = file.getContentType();
                data = file.getData();

            } else if (fileNotFoundImage != null) {

                contentType = "image/png";
                data = fileNotFoundImage;

            }

            if (data != null) {
                response.setContentType(contentType);
                response.setContentLength(data.length);
                // If it's not a picture or if it's a picture that is an attachment, tell the browser to download
                // the file instead of displaying it
                // TODO: What about PDFs? Lot's of people want to show PDFs inline...
                if ( file != null &&
                    ( !file.isInstance(WikiUploadImage.class) || ( ((WikiUploadImage)file).getThumbnail() == 'A') )
                   ) {
                    response.setHeader("Content-Disposition", "attachement; filename=\"" + file.getFilename() + "\"" );
                }
                response.getOutputStream().write(data);
            }

            response.getOutputStream().flush();
        }

        /* TODO: This breaks stuff...
        // If the user is not logged in, we might as well destroy the session immediately, saving some memory
        if (!Identity.instance().isLoggedIn()) {
            Session.instance().invalidate();
        }
        */

    }
}
