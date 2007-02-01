package org.jboss.seam.wiki.core.node;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import javax.persistence.Query;

import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.links.WikiLinkResolver;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.ScopeType;

import java.util.List;

@Name("documentHome")
public class DocumentHome extends EntityHome<Document> {

    @RequestParameter
    private Long docId;

    @RequestParameter
    private Long parentDirId;

    // Pages need this for rendering
    @Out(required = true, scope = ScopeType.CONVERSATION, value = "currentDirectory")
    Directory parentDirectory;

    @In(create=true)
    private FacesMessages facesMessages;

    @In(create=true)
    private WikiLinkResolver wikiLinkResolver;

    private String formContent;
    boolean enabledPreview = false;

    @Override
    public Object getId() {

        if (docId == null) {
            return super.getId();
        } else {
            return docId;
        }
    }

    @Begin(flushMode = FlushModeType.MANUAL)
    @Transactional
    public void create() {
        super.create();

        // Load the parent directory
        getEntityManager().joinTransaction();
        parentDirectory = getEntityManager().find(Directory.class, parentDirId);
    }

    public String persist() {

        // Validate
        if (!isUniqueWikinameInDirectory(null) ||
            !isUniqueWikinameInArea()) return null;

        // Link the document with a directory
        parentDirectory.addChild(getInstance());

        // Set its area number
        getInstance().setAreaNumber(parentDirectory.getAreaNumber());

        // Convert and set form content onto entity instance
        getInstance().setContent(
            wikiLinkResolver.convertToWikiLinks(parentDirectory, getFormContent())
        );

        return super.persist();
    }


    public String update() {

        // Validate
        if (!isUniqueWikinameInDirectory(getInstance()) ||
            !isUniqueWikinameInArea()) return null;

        // Convert and set form content onto entity instance
        getInstance().setContent(
            wikiLinkResolver.convertToWikiLinks(parentDirectory, getFormContent())
        );

        return super.update();
    }

    public String remove() {

        // Unlink the document from its directory
        getInstance().getParent().removeChild(getInstance());

        return super.remove();
    }

    public String getFormContent() {
        // Load the document content and resolve links
        if (formContent == null)
            formContent = wikiLinkResolver.convertFromWikiLinks(getInstance().getContent());
        return formContent;
    }

    public void setFormContent(String formContent) {
        this.formContent = formContent;
    }

    public boolean isEnabledPreview() {
        return enabledPreview;
    }

    public void setEnabledPreview(boolean enabledPreview) {
        this.enabledPreview = enabledPreview;
        // Convert and set form content onto entity instance
        getInstance().setContent(
            wikiLinkResolver.convertToWikiLinks(parentDirectory, getFormContent())
        );
    }

    // Validation rules for persist(), update(), and remove();

    @Transactional
    private boolean isUniqueWikinameInDirectory(Document ignore) {
        getEntityManager().joinTransaction();

        String queryString = "select n from Node n where n.parent = :parent and n.wikiname = :wikiname";
        if (ignore != null)  queryString = queryString + " and not n = :ignore";

        Query q = getEntityManager().createQuery(queryString);
        if (ignore != null) q.setParameter("ignore", ignore);

        // Unique directory name within parent
        List existingChildren = q
                .setParameter("parent", parentDirectory)
                .setParameter("wikiname", getInstance().getWikiname())
                .getResultList();
        if (existingChildren.size() >0) {
            facesMessages.addFromResourceBundle(
                "name",
                SEVERITY_ERROR,
                getMessageKeyPrefix() + "duplicateName",
                "Directory or document with that name already exists."
            );
            return false;
        }
        return true;
    }

    @Transactional
    private boolean isUniqueWikinameInArea() {
        getEntityManager().joinTransaction();
        // Unique document name within area
        Document foundDocument = wikiLinkResolver.findDocumentInArea(parentDirectory, getInstance().getWikiname());
        if ( foundDocument != null && foundDocument != getInstance()) {
            facesMessages.addFromResourceBundle(
                "name",
                SEVERITY_ERROR,
                getMessageKeyPrefix() + "duplicateNameInArea",
                "Document with that name already exists in this area."
            );
            return false;
        }
        return true;
    }

}
