package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.wiki.core.model.File;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
            File file = null;

            // TODO: Seam should use its transaction interceptor for java beans: http://jira.jboss.com/jira/browse/JBSEAM-957
            UserTransaction userTx = null;
            boolean startedTx = false;
            try {

                userTx = (UserTransaction)org.jboss.seam.Component.getInstance("org.jboss.seam.transaction.transaction");
                if (userTx.getStatus() != javax.transaction.Status.STATUS_ACTIVE) {
                    startedTx = true;
                    userTx.begin();
                }

                EntityManager em = ((EntityManager)org.jboss.seam.Component.getInstance("restrictedEntityManager"));
                em.joinTransaction();

                if (!"".equals(id)) {
                    try {
                        file = (File)em.createQuery("select f from File f where f.id = :fid")
                                .setParameter("fid", Long.parseLong(id))
                                .getSingleResult();
                    } catch (NoResultException ex) {
                        // ignore... we want null
                    }
                }

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

            String thumbnail = request.getParameter("thumbnail");

            if (file != null
                && thumbnail != null
                && Boolean.valueOf(thumbnail)
                && file.getImageMetaInfo() != null
                && file.getImageMetaInfo().getThumbnailData() != null
                && file.getImageMetaInfo().getThumbnailData().length >0) {

                // Render thumbnail picture
                contentType = file.getContentType();
                data = file.getImageMetaInfo().getThumbnailData();

            } else if (file != null
                       && file.getData() != null
                       && file.getData().length > 0) {

                // Render file regularly
                contentType = file.getContentType();
                data = file.getData();

            } else if (fileNotFoundImage != null) {

                contentType = "image/png";
                data = fileNotFoundImage;

            }

            if (data != null) {
                response.setContentType(contentType);
                response.getOutputStream().write(data);
            }

            response.getOutputStream().flush();
        }
    }
}
