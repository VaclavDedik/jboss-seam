package org.jboss.seam.wiki.core.upload.importers.metamodel;

import org.jboss.seam.wiki.core.upload.importers.annotations.UploadImporter;
import org.jboss.seam.annotations.Name;

public class Importer implements Comparable {

    private Class clazz;
    private String componentName;
    private String[] handledMimeTypes;
    private String[] handledExtensions;
    private String description;

    public Importer(Class<?> clazz) {
        this.clazz = clazz;

        this.componentName = clazz.getAnnotation(Name.class).value();
        this.handledMimeTypes = clazz.getAnnotation(UploadImporter.class).handledMimeTypes();
        this.handledExtensions = clazz.getAnnotation(UploadImporter.class).handledExtensions();
        this.description = clazz.getAnnotation(UploadImporter.class).description();
    }

    public Class getClazz() {
        return clazz;
    }

    public String getComponentName() {
        return componentName;
    }

    public String[] getHandledMimeTypes() {
        return handledMimeTypes;
    }

    public String[] getHandledExtensions() {
        return handledExtensions;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        return "importer class: " + getClazz().getName();
    }

    public int compareTo(Object o) {
        Importer other = (Importer)o;
        return this.getDescription().compareTo(other.getDescription());
    }
}
