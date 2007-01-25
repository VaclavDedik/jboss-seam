package org.jboss.seam.deployment;

import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.annotations.Namespace;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

public class NamespaceScanner
    extends Scanner 
{
    private static final LogProvider log = Logging.getLogProvider(NamespaceScanner.class);

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
        if ( name.endsWith("/package-info.class") ) 
        {
            String packageName = filenameToPackageName(name);
            Package pkg = getPackage(packageName);
            if (pkg == null) 
            {
                log.warn("Cannot load package Dinfo for " + packageName);
            } 
            else 
            {
                if (pkg.getAnnotation(Namespace.class) != null) 
                {
                    packages.add(pkg);
                }
            }
        }
    }

    protected Package getPackage(String name) 
    {
        try 
        {
            Class c = classLoader.loadClass(name + ".package-info");
            return c != null ? c.getPackage() : null;
        } 
        catch (Exception e) 
        {
            return null;
        }
    }

}

