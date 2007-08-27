package org.jboss.seam.wiki.core.importers;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.In;
import org.jboss.seam.wiki.core.importers.metamodel.AbstractImporter;
import org.jboss.seam.wiki.core.importers.annotations.FileImporter;
import org.jboss.seam.wiki.core.model.File;
import org.jboss.seam.wiki.core.model.ImageMetaInfo;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.ui.FileMetaMap;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.log.Log;

import javax.persistence.EntityManager;
import javax.faces.application.FacesMessage;
import javax.swing.*;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.Date;
import java.util.Map;
import java.io.*;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatchNotFoundException;

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
    NodeDAO nodeDAO;

    @In
    Map<String, FileMetaMap.FileMetaInfo> fileMetaMap;

    public boolean handleImport(EntityManager em, File zipFile) {
        if (zipFile.getData().length == 0) return true;

        ByteArrayInputStream byteStream = null;
        ZipInputStream zipInputStream = null;
        try {
            byteStream = new ByteArrayInputStream(zipFile.getData());
            zipInputStream = new ZipInputStream(new BufferedInputStream(byteStream));

            int                   bufferSize = 1024;
            ZipEntry              ze;
            ByteArrayOutputStream baos;
            byte[]                buffer = new byte[bufferSize];
            byte[]                uncommpressedBytes;
            int                   bytesRead;

            while ((ze = zipInputStream.getNextEntry()) != null) {
                log.debug("extracting zip entry: " + ze.getName());

                File wikiFile = new File();
                wikiFile.setFilename(ze.getName());

                wikiFile.setName(wikiFile.getFilenameWithoutExtension());
                wikiFile.setWikiname(WikiUtil.convertToWikiName(wikiFile.getName()));
                wikiFile.setAreaNumber(zipFile.getAreaNumber());
                wikiFile.setCreatedBy(zipFile.getCreatedBy());
                wikiFile.setLastModifiedBy(wikiFile.getCreatedBy());
                wikiFile.setCreatedOn(new Date(ze.getTime()));
                wikiFile.setLastModifiedOn(new Date());
                wikiFile.setReadAccessLevel(zipFile.getReadAccessLevel());
                wikiFile.setWriteAccessLevel(zipFile.getWriteAccessLevel());

                if ( !nodeDAO.isUniqueWikiname(zipFile.getAreaNumber(), wikiFile.getWikiname()) ) {
                    getFacesMessages().addFromResourceBundleOrDefault(
                        FacesMessage.SEVERITY_ERROR,
                        "duplicateImportedName",
                        "Skipping file '" + ze.getName() + "', name is already used in this area..."
                    );
                    continue;
                }

                if (ze.getName().contains("/")) {
                    getFacesMessages().addFromResourceBundleOrDefault(
                        FacesMessage.SEVERITY_ERROR,
                        "notImportingDirectory",
                        "Skipping directory '" + ze.getName() + "', importing not supported..."
                    );
                    continue;
                }
                baos = new ByteArrayOutputStream();
                while ((bytesRead = zipInputStream.read(buffer, 0, bufferSize)) > 0) {
                    baos.write(buffer, 0, bytesRead);
                }
                baos.close();
                uncommpressedBytes = baos.toByteArray();

                log.debug("detecting mime type of zip entry: " + ze.getName());
                String mimeType;
                try {
                    mimeType = Magic.getMagicMatch(uncommpressedBytes).getMimeType();
                } catch (MagicMatchNotFoundException ex) {
                    mimeType = "application/octet-stream"; // Default to binary
                }

                log.debug("creating new file data from zip entry: " + ze.getName());
                wikiFile.setContentType(mimeType);
                wikiFile.setData(uncommpressedBytes);
                wikiFile.setFilesize(uncommpressedBytes.length);

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

                zipFile.getParent().addChild(wikiFile);
                em.persist(wikiFile);

                getFacesMessages().addFromResourceBundleOrDefault(
                    FacesMessage.SEVERITY_INFO,
                    "importedFile",
                    "Created file '" + ze.getName() + "' in current directory"
                );

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

        return true;
    }

}
