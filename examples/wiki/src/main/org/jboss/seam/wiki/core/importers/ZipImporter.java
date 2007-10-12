package org.jboss.seam.wiki.core.importers;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.In;
import org.jboss.seam.wiki.core.importers.metamodel.AbstractImporter;
import org.jboss.seam.wiki.core.importers.annotations.FileImporter;
import org.jboss.seam.wiki.core.model.File;
import org.jboss.seam.wiki.core.model.ImageMetaInfo;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.ui.FileMetaMap;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.log.Log;
import org.jboss.seam.core.Validators;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;

import javax.persistence.EntityManager;
import javax.faces.application.FacesMessage;
import javax.swing.*;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.*;
import java.io.*;

import net.sf.jmimemagic.Magic;

@Name("zipImporter")
@FileImporter(
        handledMimeTypes = {"application/zip", "application/java-archive"},
        handledExtensions = {"zip", "jar"},
        description = "Extract archive as individual files"
)
public class ZipImporter extends AbstractImporter {

    @Logger
    Log log;

    @In
    protected NodeDAO nodeDAO;

    @In
    protected Map<String, FileMetaMap.FileMetaInfo> fileMetaMap;

    public boolean handleImport(EntityManager em, File zipFile) {
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
                    if ( !(o instanceof Node) &&  !(o1 instanceof Node) ) return 0;
                    return ((Node)o).getWikiname().compareTo( ((Node)o1).getWikiname() );
                }
            }
        );

        return true;
    }

    protected boolean handleDirectory(EntityManager em, File zipFile, ZipEntry zipEntry) {
        log.debug("skipping directory: " + zipEntry.getName());
        getFacesMessages().addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_ERROR,
            "lacewiki.msg.ImportSkippingDirectory",
            "Skipping directory '{0}', importing not supported...",
            zipEntry.getName()
        );
        return false; // Not supported
    }

    protected boolean continueUncompressing(EntityManager em, File zipFile, ZipEntry zipEntry) {
        return validateNewWikiname(zipFile, WikiUtil.convertToWikiName(zipEntry.getName()));
    }

    protected boolean validateNewWikiname(File zipFile, String newWikiname) {
        log.debug("validating wiki name of new file: " + newWikiname);
        if (nodeDAO.isUniqueWikiname(zipFile.getAreaNumber(), newWikiname) ) {
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

    protected Object createNewObject(EntityManager em, File zipFile, ZipEntry zipEntry, byte[] uncompressedBytes) {
        log.debug("creating new file from zip entry: " + zipEntry.getName());

        File wikiFile = new File();
        wikiFile.setFilename(zipEntry.getName());
        wikiFile.setName(zipEntry.getName());
        wikiFile.setWikiname(WikiUtil.convertToWikiName(wikiFile.getName()));

        wikiFile.setAreaNumber(zipFile.getAreaNumber());
        wikiFile.setCreatedBy(zipFile.getCreatedBy());
        wikiFile.setLastModifiedBy(wikiFile.getCreatedBy());
        wikiFile.setCreatedOn(new Date(zipEntry.getTime()));
        wikiFile.setLastModifiedOn(new Date());
        wikiFile.setReadAccessLevel(zipFile.getReadAccessLevel());
        wikiFile.setWriteAccessLevel(zipFile.getWriteAccessLevel());

        log.debug("detecting mime type of zip entry: " + zipEntry.getName());
        String mimeType;
        try {
            mimeType = Magic.getMagicMatch(uncompressedBytes).getMimeType();
            log.debug("mime type of zip entry is: " + mimeType);
        } catch (Exception ex) {
            log.debug("could not detect mime type, defaulting to binary");
            mimeType = "application/octet-stream";
        }
        wikiFile.setContentType(mimeType);
        wikiFile.setData(uncompressedBytes);
        wikiFile.setFilesize(uncompressedBytes.length);

        // TODO: Make this generic, duplicated here and in FileHome
        if (fileMetaMap.get(wikiFile.getContentType()) != null &&
            fileMetaMap.get(wikiFile.getContentType()).image) {
            wikiFile.setImageMetaInfo(new ImageMetaInfo());
            ImageIcon icon = new ImageIcon(wikiFile.getData());
            int imageSizeX = icon.getImage().getWidth(null);
            int imageSizeY = icon.getImage().getHeight(null);
            wikiFile.getImageMetaInfo().setSizeX(imageSizeX);
            wikiFile.getImageMetaInfo().setSizeY(imageSizeY);
        }

        return wikiFile;
    }

    protected void persistNewNodesSorted(EntityManager em, File zipFile, Map<String, Object> newObjects, Comparator comparator) {

        List<Node> newNodes = new ArrayList<Node>();
        for (Object newObject : newObjects.values()) {
            if (newObject instanceof Node) {
                newNodes.add((Node)newObject);
            }
        }
        Collections.sort(newNodes, comparator);

        int i = 0;
        for (Node newNode : newNodes) {
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
            zipFile.getParent().addChild(newNode);
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
