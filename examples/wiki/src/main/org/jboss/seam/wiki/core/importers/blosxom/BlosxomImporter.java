package org.jboss.seam.wiki.core.importers.blosxom;

import net.sf.jmimemagic.Magic;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.importers.ZipImporter;
import org.jboss.seam.wiki.core.importers.annotations.FileImporter;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.File;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

@Name("blosxomImporter")
@FileImporter(
        handledMimeTypes = {"application/zip", "application/java-archive"},
        handledExtensions = {"zip", "jar"},
        description = "Extract blosxom/pollxn articles with kwiki syntax"
)
public class BlosxomImporter extends ZipImporter {

    @Logger
    Log log;

    private static final String META_TITLE = "meta-title";
    private static final String META_AUTHOR = "meta-author";

    protected boolean continueUncompressing(EntityManager em, File zipFile, ZipEntry zipEntry) {
        return zipEntry.getName().endsWith(".txt"); // Skip comments for now
    }

    protected Node createNewNode(EntityManager em, File zipFile, ZipEntry zipEntry, byte[] uncompressedBytes) {
        
        log.debug("detecting mime type of zip entry: " + zipEntry.getName());
        String mimeType = null;
        try {
            mimeType = Magic.getMagicMatch(uncompressedBytes).getMimeType();
            log.debug("mime type of zip entry is: " + mimeType);
        } catch (Exception ex) {}
        if (!"text/plain".equals(mimeType)) {
            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_ERROR,
                "incorrectMimeType",
                "Skipping file '" + zipEntry.getName() + "', incorrect mime type " + mimeType + " , expected text/plain"
            );
            return null;
        }

        log.debug("parsing blog entry");
        Map<String,String> metadata = new HashMap<String,String>();
        StringBuffer content = new StringBuffer();
        // Remove carriage returns and split at linefeeds
        String[] lines = new String(uncompressedBytes).replaceAll("\r", "").split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            handleLine(metadata, content, i, line);
        }
        String documentTitle = metadata.get(META_TITLE).trim();

        if (!validateNewWikiname(zipFile, documentTitle)) return null;

        content = new StringBuffer(handleBlock(metadata, content.toString()));

        Document wikiDocument = new Document();

        wikiDocument.setName(documentTitle);
        wikiDocument.setWikiname(WikiUtil.convertToWikiName(wikiDocument .getName()));
        wikiDocument.setContent(content.toString());

        wikiDocument.setAreaNumber(zipFile.getAreaNumber());
        wikiDocument.setCreatedBy(zipFile.getCreatedBy());
        wikiDocument.setLastModifiedBy(wikiDocument.getCreatedBy());
        wikiDocument.setCreatedOn(new Date(zipEntry.getTime()));
        wikiDocument.setLastModifiedOn(new Date());
        wikiDocument.setReadAccessLevel(zipFile.getReadAccessLevel());
        wikiDocument.setWriteAccessLevel(zipFile.getWriteAccessLevel());

        wikiDocument.setEnableComments(true);
        wikiDocument.setEnableCommentForm(true);
        wikiDocument.setNameAsTitle(true);

        return wikiDocument;
    }

    void handleLine(Map<String,String> metadata, StringBuffer result, int lineNumber, String line) {

        Matcher matcher;
        StringBuffer inline;

        // Header
        if (lineNumber == 0) {
            metadata.put(META_TITLE, line);
            return;
        }
        matcher = Pattern.compile("^"+META_AUTHOR+":\\s*(.*)$").matcher(line);
        if (matcher.find()) {
            metadata.put(META_AUTHOR, matcher.group(1));
            return;
        }

        // The "# more" marker
        if (line.matches("^#\\s(more)")) {
            return;
        }

        // HR "----"
        if (line.matches("^----$")) {
            result.append("<hr/>\n");
            return;
        }

        // [=monospace]
        matcher = Pattern.compile(Pattern.quote("[=")+"([^\\s|^\\]]+)"+Pattern.quote("]")).matcher(line);
        inline = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(inline, "|$1|");
        }
        matcher.appendTail(inline);
        line = inline.toString();

        String REGEX_URI = "(?:[A-Za-z][A-Za-z0-9+.-]{1,120}:[A-Za-z0-9/](?:(?:[A-Za-z0-9$_.+!*,;/?:@&~=-])|%[A-Fa-f0-9]{2}){1,333}(?:#(?:[a-zA-Z0-9][a-zA-Z0-9$_.+!*,;/?:@&~=%-]{0,1000}))?)";

        // http://hibernate.org
        String REGEX_PLAIN_URLS =  "(" + REGEX_URI + ")";
        matcher = Pattern.compile(REGEX_PLAIN_URLS).matcher(line);
        inline = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(inline, "[=>$1]");
        }
        matcher.appendTail(inline);
        line = inline.toString();

        // [Foo Bar [=>http://hibernate.org/]]
        String REGEX_DOUBLEURLS = "\\[(.*?)\\[=>(.+?)\\]";
        matcher = Pattern.compile(REGEX_DOUBLEURLS).matcher(line);
        inline = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(inline, "[$1=>$2");
        }
        matcher.appendTail(inline);
        line = inline.toString();

        // [=>http://hibernate.org/ Foo Bar]
        String REGEX_DOUBLEREVERSEURLS = "\\[=>("+REGEX_URI+")\\s+(.+?)\\]";
        matcher = Pattern.compile(REGEX_DOUBLEREVERSEURLS).matcher(line);
        inline = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(inline, "[$2=>$1]");
        }
        matcher.appendTail(inline);
        line = inline.toString();

        // Headlines such as "== Foo" and "=== Bar"
        matcher = Pattern.compile("^([=]+)\\s(.+)$").matcher(line);
        inline = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(inline, matcher.group(1).replaceAll("=", "+") + " $2");
        }
        matcher.appendTail(inline);
        line = inline.toString();

        // Unorderered list items "= item"
        String REGEX_LISTITEM = "^([" + Pattern.quote("*") + "]+)\\s(.+)$";
        matcher = Pattern.compile(REGEX_LISTITEM).matcher(line);
        inline = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(inline, "= $2\n");
        }
        matcher.appendTail(inline);
        line = inline.toString();

        // Slashes in the middle of text " foo/bar " need escape, as well as "/foo/bar/baz"...
        if (!line.matches("^\\s+.*$")) { // Exclude code blocks
            String REGEX_SLASH = "([A-Za-z0-9\"]+?)" + Pattern.quote("/") + "([A-Za-z0-9\"]+?)";
            matcher = Pattern.compile(REGEX_SLASH).matcher(line);
            inline = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(inline, "$1\\\\/$2");
            }
            matcher.appendTail(inline);
            line = inline.toString();
        }

        String REGEX_REPAIRURLS =  "\\[(.*?)=>(.*?)\\]";
        matcher = Pattern.compile(REGEX_REPAIRURLS).matcher(line);
        inline = new StringBuffer();
        while (matcher.find()) {
            String repairedURL = matcher.group(matcher.groupCount() == 1 ? 1 : 2).replaceAll("\\/", "/");
            matcher.appendReplacement(inline, "[$1=>" + repairedURL + "]");
        }
        matcher.appendTail(inline);
        line = inline.toString();


        result.append(line);
        result.append("\n");
    }

    String handleBlock(Map<String,String> metadata, String original) {

        // Code blocks are all lines starting with a whitespace
        StringBuffer replaced = new StringBuffer(original.length());
        String[] lines = original.split("\n");
        boolean inCodeblock = false;
        for (String line : lines) {
            if (line.matches("^\\s+[^\\s]+.*$") && !inCodeblock) {
                replaced.append( line.replaceAll("^(\\s+[^\\s]+.*)$", "`\n$1") ).append("\n");
                inCodeblock = true;
            } else if (line.matches("^\\s+.*$") && inCodeblock) {
                replaced.append( line.replaceAll("^(\\s+.*)$", "$1") ).append("\n");
            } else if (line.matches("\\n|^([^\\s].*)$") && inCodeblock) {
                replaced = new StringBuffer(replaced.substring(0, replaced.length()-1));
                replaced.append("`\n\n").append( line.replaceAll("^([^\\s].*)$", "$1") ).append("\n");
                inCodeblock = false;
            } else {
                replaced.append(line).append("\n");
            }
        }

        original = replaced.toString();

        return original;
    }

}


