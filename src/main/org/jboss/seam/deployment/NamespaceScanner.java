package org.jboss.seam.deployment;

import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.annotations.Namespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NamespaceScanner
    extends Scanner 
{
    private static final Log log = LogFactory.getLog(NamespaceScanner.class);

    private Set<Package> packages;
    
    public NamespaceScanner(String resourceName)
    {
        super(resourceName);
    }
    
    public NamespaceScanner(String resourceName, ClassLoader classLoader)
    {
        super(resourceName,classLoader);
    }
    
    /**
     * Returns packages with @Namespace declarations
     */
    public Set<Package> getPackages()
    {
        if (packages == null) {
            packages = new HashSet<Package>();
            scan();
        } 
        return packages;
    }

    public static String filenameToPackageName(String filename)
    {
        return filename.substring(0, filename.lastIndexOf("/package-info.class"))
            .replace('/', '.').replace('\\', '.');
    }
    
    @Override
    protected void handleItem(String name)
    {
        if (name.endsWith("/package-info.class")) {
            String packageName = filenameToPackageName(name);
            Package pkg = Package.getPackage(packageName);
            if (pkg == null) {
                log.warn("Cannot load package Dinfo for " + packageName);
            } else {
                if (pkg.getAnnotation(Namespace.class) != null) {
                    packages.add(pkg);
                }
            }
        }
    }


    
}
