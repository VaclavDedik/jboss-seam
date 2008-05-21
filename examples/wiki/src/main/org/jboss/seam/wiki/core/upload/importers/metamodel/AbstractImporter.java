package org.jboss.seam.wiki.core.upload.importers.metamodel;

import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.wiki.core.model.WikiUpload;

import javax.persistence.EntityManager;
import java.util.Set;

@Scope(ScopeType.APPLICATION)
public abstract class AbstractImporter {

    @Observer("Importers.addImporter")
    public void add(Set<AbstractImporter> importerComponents) {
        importerComponents.add(this);
    }

    protected StatusMessages getStatusMessages() {
        return StatusMessages.instance();
    }

    public abstract boolean handleImport(EntityManager em, WikiUpload file);

}
