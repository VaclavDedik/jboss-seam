package org.jboss.seam.deployment;

import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.annotations.Namespace;

public class NamespaceScanner
    extends Scanner 
{
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
            // XXX - can't get package info from a classloader directly?
            Package pkg = Package.getPackage(packageName);
            if (pkg.getAnnotation(Namespace.class) != null) {
                packages.add(pkg);
            }
        }
    }


    
}
