package org.jboss.seam.wiki.core.importers.metamodel;

import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.wiki.core.model.File;

import javax.persistence.EntityManager;
import java.util.Set;

@Scope(ScopeType.APPLICATION)
public abstract class AbstractImporter {

    @Observer("Importers.addImporter")
    public void add(Set<AbstractImporter> importerComponents) {
        importerComponents.add(this);
    }

    protected FacesMessages getFacesMessages() {
        return FacesMessages.instance();
    }

    public abstract boolean handleImport(EntityManager em, File file);

}
