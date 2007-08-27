package org.jboss.seam.wiki.core.importers.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileImporter {
    String[] handledMimeTypes();
    String[] handledExtensions();
    String description();
}
