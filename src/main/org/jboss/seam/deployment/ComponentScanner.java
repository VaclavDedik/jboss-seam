package org.jboss.seam.deployment;

import org.jboss.seam.annotations.Name;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javassist.bytecode.ClassFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ComponentScanner
    extends Scanner 
{
    private static final Log log = LogFactory.getLog(ComponentScanner.class);

    private Set<Class<Object>> classes;
    
    public ComponentScanner(String resourceName)
    {
        super(resourceName);
    }
    
    public ComponentScanner(String resourceName, ClassLoader classLoader)
    {
        super(resourceName,classLoader);
    }
    
    /**
     * Returns only Seam components (ie: classes annotated with @Name)
     */
    public Set<Class<Object>> getClasses()
    {
        if (classes == null) {
            classes = new HashSet<Class<Object>>();
            scan();
        } 
        return classes;
    }

    @Override
    protected void handleItem(String name)
    {
        if (name.endsWith(".class")) {
            String classname = filenameToClassname(name);
            String filename = Scanner.componentFilename(name);
            try {
                ClassFile classFile = getClassFile(name);
                if (hasAnnotation(classFile, Name.class) || 
                    classLoader.getResources(filename).hasMoreElements() ) 
                {
                    classes.add( (Class<Object>) classLoader.loadClass(classname) );
                }
            } catch (ClassNotFoundException cnfe) {
                log.debug( "could not load class: " + classname, cnfe );

            } catch (NoClassDefFoundError ncdfe) {
                log.debug( "could not load class (missing dependency): " + classname, ncdfe );

            } catch (IOException ioe) {
                log.debug( "could not load classfile: " + classname, ioe );
            }
        }
    }
}
