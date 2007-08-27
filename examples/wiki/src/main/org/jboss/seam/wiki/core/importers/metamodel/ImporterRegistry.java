package org.jboss.seam.wiki.core.importers.metamodel;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.importers.annotations.FileImporter;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;

import java.util.*;

@Name("importerRegistry")
@Scope(ScopeType.APPLICATION)
public class ImporterRegistry {

    @Logger
    static Log log;

    Map<String, Importer> importersByName = new HashMap<String, Importer>();
    List<Importer> importers = new ArrayList<Importer>();

    @Observer("Wiki.started")
    public void scanForFileImporters() {

        log.debug("initializing file importer registry");
        importers.clear();
        importersByName.clear();

        // Fire an event and let all listeners add themself into the given collection
        Set<AbstractImporter> importerComponents = new HashSet<AbstractImporter>();
        Events.instance().raiseEvent("Importers.addImporter", importerComponents);

        log.debug("found file importer components: " + importerComponents.size());

        for (AbstractImporter importerComponent : importerComponents) {
            if (importerComponent.getClass().isAnnotationPresent(FileImporter.class)) {
                Importer importer = new Importer(importerComponent.getClass());
                importers.add(importer);
                importersByName.put(importer.getComponentName(), importer);
            }
        }
        log.debug("added file importers to registry: " + importers.size());

        // Sort entities
        Collections.sort(importers);

        if (log.isTraceEnabled()) {
            for (Importer importer : importers) {
                log.trace(importer);
            }
        }
    }

    public Map<String, Importer> getImportersByName() {
        return importersByName;
    }

    public List<Importer> getImporters() {
        return importers;
    }
}
