package org.jboss.seam.wiki.core.upload.importers;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.upload.importers.annotations.UploadImporter;
import org.jboss.seam.wiki.core.upload.importers.metamodel.AbstractImporter;
import org.jboss.seam.wiki.core.upload.UploadType;
import org.jboss.seam.wiki.core.model.WikiUpload;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.core.Validators;
import org.hibernate.validator.InvalidValue;
import org.hibernate.validator.ClassValidator;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;

import net.sf.jmimemagic.Magic;

@Name("zipImporter")
@UploadImporter(
        handledMimeTypes = {"application/zip", "application/java-archive"},
        handledExtensions = {"zip", "jar"},
        description = "Extract archive as individual files"
)
public class ZipImporter extends AbstractImporter {

    @Logger
    Log log;

    @In
    protected WikiNodeDAO wikiNodeDAO;

    @In
    protected Map<String, UploadType> uploadTypes;

    public boolean handleImport(EntityManager em, WikiUpload zipFile) {

        if (zipFile.getData().length == 0) return true;

        Map<String, Object> newObjects = new HashMap<String, Object>();

        ByteArrayInputStream byteStream = null;
        ZipInputStream zipInputStream = null;
        try {
            byteStream = new ByteArrayInputStream(zipFile.getData());
            zipInputStream = new ZipInputStream(new BufferedInputStream(byteStream));

            int                   bufferSize = 1024;
            ZipEntry              ze;
            ByteArrayOutputStream baos;
            byte[]                buffer = new byte[bufferSize];
            byte[]                uncompressedBytes;
            int                   bytesRead;

            while ((ze = zipInputStream.getNextEntry()) != null) {
                log.debug("extracting zip entry: " + ze.getName());

                if (ze.getName().contains("/") && !handleDirectory(em, zipFile, ze)) continue;

                if (!continueUncompressing(em, zipFile, ze)) continue;

                baos = new ByteArrayOutputStream();
                while ((bytesRead = zipInputStream.read(buffer, 0, bufferSize)) > 0) {
                    baos.write(buffer, 0, bytesRead);
                }
                baos.close();
                uncompressedBytes = baos.toByteArray();

                Object newObject = createNewObject(em, zipFile, ze, uncompressedBytes);
                if (newObject != null) {
                    newObjects.put(ze.getName(), newObject);
                }

                zipInputStream.closeEntry();
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (zipInputStream != null) zipInputStream.close();
                if (byteStream != null) byteStream.close();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        // By default append them to the
        persistNewNodesSorted(
            em,
            zipFile,
            newObjects,
            new Comparator() {
                public int compare(Object o, Object o1) {
                    if ( !(o instanceof WikiNode) &&  !(o1 instanceof WikiNode) ) return 0;
                    return ((WikiNode)o).getWikiname().compareTo( ((WikiNode)o1).getWikiname() );
                }
            }
        );

        return true;
    }

    protected boolean handleDirectory(EntityManager em, WikiUpload zipFile, ZipEntry zipEntry) {
        log.debug("skipping directory: " + zipEntry.getName());
        getFacesMessages().addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_ERROR,
            "lacewiki.msg.ImportSkippingDirectory",
            "Skipping directory '{0}', importing not supported...",
            zipEntry.getName()
        );
        return false; // Not supported
    }

    protected boolean continueUncompressing(EntityManager em, WikiUpload zipFile, ZipEntry zipEntry) {
        return validateNewWikiname(zipFile, WikiUtil.convertToWikiName(zipEntry.getName()));
    }

    protected boolean validateNewWikiname(WikiUpload zipFile, String newWikiname) {
        log.debug("validating wiki name of new file: " + newWikiname);

        if (wikiNodeDAO.isUniqueWikiname(zipFile.getAreaNumber(), newWikiname) ) {
            log.debug("new name is unique and valid");
            return true;
        } else {
            log.debug("new name is not unique and invalid");
            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_ERROR,
                "lacewiki.msg.ImportDuplicateName",
                "Skipping file '{0}', name is already used in this area...",
                newWikiname
            );
            return false;
        }
    }

    protected Object createNewObject(EntityManager em, WikiUpload zipFile, ZipEntry zipEntry, byte[] uncompressedBytes) {
        log.debug("creating new file from zip entry: " + zipEntry.getName());

        WikiUpload wikiUpload = new WikiUpload();
        wikiUpload.setFilename(zipEntry.getName());
        wikiUpload.setName(zipEntry.getName());
        wikiUpload.setWikiname(WikiUtil.convertToWikiName(wikiUpload.getName()));

        wikiUpload.setAreaNumber(zipFile.getAreaNumber());
        wikiUpload.setCreatedBy(zipFile.getCreatedBy());
        wikiUpload.setLastModifiedBy(wikiUpload.getCreatedBy());
        wikiUpload.setCreatedOn(new Date(zipEntry.getTime()));
        wikiUpload.setLastModifiedOn(new Date());
        wikiUpload.setReadAccessLevel(zipFile.getReadAccessLevel());
        wikiUpload.setWriteAccessLevel(zipFile.getWriteAccessLevel());

        log.debug("detecting mime type of zip entry: " + zipEntry.getName());
        String mimeType;
        try {
            mimeType = Magic.getMagicMatch(uncompressedBytes).getMimeType();
            log.debug("mime type of zip entry is: " + mimeType);
        } catch (Exception ex) {
            log.debug("could not detect mime type, defaulting to binary");
            mimeType = "application/octet-stream";
        }
        wikiUpload.setContentType(mimeType);
        wikiUpload.setData(uncompressedBytes);
        wikiUpload.setFilesize(uncompressedBytes.length);

        // TODO: Make this generic, duplicated here and in uploadHome
        /* TODO: fixme
        if (fileMetaMap.get(wikiUpload.getContentType()) != null &&
            fileMetaMap.get(wikiUpload.getContentType()).image) {
            throw new RuntimeException("TODO: Images not supported");
            wikiUpload.setImageMetaInfo(new ImageMetaInfo());
            ImageIcon icon = new ImageIcon(wikiUpload.getData());
            int imageSizeX = icon.getImage().getWidth(null);
            int imageSizeY = icon.getImage().getHeight(null);
            wikiUpload.getImageMetaInfo().setSizeX(imageSizeX);
            wikiUpload.getImageMetaInfo().setSizeY(imageSizeY);
        }
        */

        return wikiUpload;
    }

    protected void persistNewNodesSorted(EntityManager em, WikiUpload zipFile, Map<String, Object> newObjects, Comparator comparator) {

        List<WikiNode> newNodes = new ArrayList<WikiNode>();
        for (Object newObject : newObjects.values()) {
            if (newObject instanceof WikiNode) {
                newNodes.add((WikiNode)newObject);
            }
        }
        Collections.sort(newNodes, comparator);

        int i = 0;
        for (WikiNode newNode : newNodes) {
            log.debug("validating new node");

            ClassValidator validator = Validators.instance().getValidator(newNode.getClass());
            InvalidValue[] invalidValues = validator.getInvalidValues(newNode);
            if (invalidValues != null && invalidValues.length > 0) {
                log.debug("new node is invalid: " + newNode);
                for (InvalidValue invalidValue : invalidValues) {
                    getFacesMessages().addFromResourceBundleOrDefault(
                        FacesMessage.SEVERITY_ERROR,
                        "lacewiki.msg.ImportInvalidNode",
                        "Skipping entry '{0}', invalid: {1}",
                        newNode.getName(),
                        invalidValue.getMessage()

                    );
                }
                continue;
            }

            log.debug("persisting newly imported node: " + newNode);
            newNode.setParent(zipFile.getParent());
            em.persist(newNode);
            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "lacewiki.msg.ImportOk",
                "Created file '{0}' in current directory.",
                newNode.getName()
            );

            // Batch the work (we can't clear because of nested set updates, unfortunately)
            i++;
            if (i==100) {
                em.flush();
                i = 0;
            }
        }
    }


}
