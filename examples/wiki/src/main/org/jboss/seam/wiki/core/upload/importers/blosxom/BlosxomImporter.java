package org.jboss.seam.wiki.core.upload.importers.blosxom;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.upload.importers.ZipImporter;
import org.jboss.seam.wiki.core.upload.importers.annotations.UploadImporter;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.util.WikiUtil;

import static org.jboss.seam.international.StatusMessage.Severity.ERROR;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

@Name("blosxomImporter")
@UploadImporter(
        handledMimeTypes = {"application/zip", "application/java-archive"},
        handledExtensions = {"zip", "jar"},
        description = "Extract blosxom/pollxn articles with kwiki syntax"
)
public class BlosxomImporter extends ZipImporter {

    @Logger
    Log log;

    private static final String META_TITLE = "meta-title";
    private static final String META_AUTHOR = "meta-author";

    protected boolean continueUncompressing(EntityManager em, WikiUpload zipFile, ZipEntry zipEntry) {
        return zipEntry.getName().endsWith(".txt") || zipEntry.getName().contains("txt.pollxn");
    }

    protected Object createNewObject(EntityManager em, WikiUpload zipFile, ZipEntry zipEntry, byte[] uncompressedBytes) {

        /* Let's just hope for the best... jmimemagic sucks anyway and can't reliably detect a simple text file...
        log.debug("detecting mime type of zip entry: " + zipEntry.getName());
        String mimeType = null;
        try {
            mimeType = Magic.getMagicMatch(uncompressedBytes).getMimeType();
            log.debug("mime type of zip entry is: " + mimeType);
        } catch (Exception ex) {}
        if (!"text/plain".equals(mimeType)) {
            statusMessages.addFromResourceBundleOrDefault(
                ERROR,
                "incorrectMimeType",
                "Skipping file '" + zipEntry.getName() + "', incorrect mime type " + mimeType + " , expected text/plain"
            );
            return null;
        }
        */

        Object newObject = null;
        if (zipEntry.getName().endsWith(".txt")) {
            newObject = createBlogEntry(zipFile, zipEntry, uncompressedBytes);
        } else if (zipEntry.getName().contains(".txt.pollxn")){
            newObject = createComment(zipFile, zipEntry, uncompressedBytes);
        }
        return newObject;
    }

    WikiDocument createBlogEntry(WikiUpload zipFile, ZipEntry zipEntry, byte[] uncompressedBytes) {
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

        WikiDocument blogDocument = new WikiDocument();

        blogDocument.setName(documentTitle);
        blogDocument.setWikiname(WikiUtil.convertToWikiName(blogDocument .getName()));
        blogDocument.setContent(content.toString());
        /* TODO

        blogDocument.setAreaNumber(zipFile.getAreaNumber());
        blogDocument.setCreatedBy(zipFile.getCreatedBy());
        blogDocument.setLastModifiedBy(blogDocument.getCreatedBy());
        blogDocument.setCreatedOn(new Date(zipEntry.getTime()));
        blogDocument.setLastModifiedOn(new Date());
        blogDocument.setReadAccessLevel(zipFile.getReadAccessLevel());
        blogDocument.setWriteAccessLevel(zipFile.getWriteAccessLevel());
        */

        blogDocument.setEnableComments(true);
        blogDocument.setEnableCommentForm(true);
        blogDocument.setNameAsTitle(true);

        return blogDocument;
    }

    WikiComment createComment(WikiUpload zipFile, ZipEntry zipEntry, byte[] uncompressedBytes) {
        log.debug("parsing comment entry");

        ClassValidator commentValidator = new ClassValidator(WikiComment.class);
        WikiComment newComment = new WikiComment();

        Map<String,String> metadata = new HashMap<String,String>();
        StringBuffer content = new StringBuffer();
        // Remove carriage returns and split at linefeeds
        String[] lines = new String(uncompressedBytes).replaceAll("\r", "").split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (i == 0) {
                newComment.setCreatedOn(new Date(new Long(line.trim())*1000));
                continue;
            }
            if (i == 1) {
                if (line.trim().length() >0) {
                    InvalidValue[] invalidValues = commentValidator.getPotentialInvalidValues("fromUserName", line.trim());
                    if (invalidValues.length == 0) {
                        newComment.setFromUserName(line.trim().replaceAll("[^\\p{ASCII}]", ""));
                    } else {
                        newComment.setFromUserName("Anonymous Guest");
                    }
                } else {
                    newComment.setFromUserName("Anonymous Guest");
                }
                continue;
            }
            if (i == 2) {
                if (line.trim().length() >0) {
                    InvalidValue[] invalidValues = commentValidator.getPotentialInvalidValues("fromUserEmail", line.trim());
                    if (invalidValues.length == 0) newComment.setFromUserEmail(line.trim());
                }
                continue;
            }
            if (i >3) {
                content.append(line.endsWith("__endpollxn") ? line.substring(0, line.indexOf("__endpollxn")):line).append("\n");
            }
        }

        newComment.setContent(content.toString().replaceAll("[^\\p{ASCII}]", ""));
        newComment.setUseWikiText(false);

        InvalidValue[] invalidValues = commentValidator.getInvalidValues(newComment);
        if (invalidValues.length >0) {
            getStatusMessages().addFromResourceBundleOrDefault(
                ERROR,
                "commentFailedValidation",
                "Skipping file '" + zipEntry.getName() + "', comment failed validation"
            );
            return null;
        }

        return newComment;
    }

    protected void persistNewNodesSorted(EntityManager em, WikiUpload zipFile, Map<String, Object> newObjects, Comparator comparator) {

        // Link comments to document
        Map<String,WikiComment> newComments = new HashMap<String,WikiComment>();
        for (Map.Entry<String, Object> entry : newObjects.entrySet()) {
            if (entry.getValue() instanceof WikiComment) {
                newComments.put(entry.getKey(), (WikiComment)entry.getValue());
            }
        }
        for (Map.Entry<String, WikiComment> entry : newComments.entrySet()) {
            String zipEntryName = entry.getKey();
            WikiComment newComment = entry.getValue();
            newObjects.remove(zipEntryName); // Remove comment from main set of new objects
            String documentZipEntryName = zipEntryName.substring(0, zipEntryName.indexOf(".pollxn"));
            Object documentForComment = newObjects.get(documentZipEntryName);
            if (documentForComment != null && documentForComment instanceof WikiDocument) {
                newComment.setSubject(((WikiDocument)documentForComment).getName());
                // TODO: FIxme newComment.setFile((WikiDocument)documentForComment);
                // TODO: Fixme ((WikiDocument)documentForComment).getComments().add(newComment);
            } else {
                // Skip comment if we can't find a document for it
            }
        }

        /* TODO
        // Override default comparator, append to parent directory in creation date order
        comparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                if ( !(o1 instanceof Node) &&  !(o2 instanceof Node) ) return 0;
                Node node1 = (Node)o1;
                Node node2 = (Node)o2;
                if (node1.getCreatedOn().getTime() != node2.getCreatedOn().getTime()) {
                    return node1.getCreatedOn().getTime() < node2.getCreatedOn().getTime() ? -1 : 1;
                } else if (node1.getName().compareTo(node2.getName()) != 0) {
                    return node1.getName().compareTo(node2.getName());
                }
                return node1.getWikiname().compareTo(node2.getWikiname());
            }
        };
        */

        super.persistNewNodesSorted(em, zipFile, newObjects, comparator);
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
            matcher.appendReplacement(inline, matcher.group(1).replaceAll("=", "\n+") + " $2");
        }
        matcher.appendTail(inline);
        line = inline.toString();

        // Unorderered list items "= item"
        String REGEX_LISTITEM = "^([" + Pattern.quote("*") + "]+)\\s(.+)$";
        matcher = Pattern.compile(REGEX_LISTITEM).matcher(line);
        inline = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(inline, "\n= $2\n");
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
        int numberOfWhitespaces = 0;
        for (String line : lines) {
            if (line.matches("^\\s+[^\\s]+.*$") && !inCodeblock) {
                char[] chars = line.toCharArray();
                for (int i = 0; chars[i] == ' '; i++) numberOfWhitespaces++;
                replaced.append( line.replaceAll("^(\\s+[^\\s]+.*)$", "`\n$1").replaceAll("`\\n\\s+(.*)", "\n`\n$1") ).append("\n");
                inCodeblock = true;
            } else if (line.matches("^\\s+.*$") && inCodeblock) {
                replaced.append( line.replaceAll("^(\\s+.*)$", "$1").substring(numberOfWhitespaces) ).append("\n");
            } else if (line.matches("\\n|^([^\\s].*)$") && inCodeblock) {
                replaced = new StringBuffer(replaced.substring(0, replaced.length()-1));
                replaced.append("`\n\n").append( line.replaceAll("^([^\\s].*)$", "$1") ).append("\n");
                inCodeblock = false;
                numberOfWhitespaces = 0;
            } else {
                replaced.append(line).append("\n");
            }
        }

        original = replaced.toString();

        return original;
    }

}


