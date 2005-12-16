//$Id$
package org.jboss.seam.microcontainer;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.util.ReflectHelper;

/**
 * A factory that bootstraps a Hibernate SessionFactory.
 * <p>
 * Loads Hibernate configuration options by checking:
 * <li>hibernate.properties in root of the classpath
 * <li>hibernate.cfg.xml in root of the classpath
 * <li>cfgResourceName as location of a cfg.xml file
 * <li>factory-suplied cfgProperties options
 * <p>
 * Note that this factory only supports cfg.xml files <b>or</b>
 * programmatic <tt>cfgProperties</tt> supplied to the factory.
 * Any <tt>hibernate.properties</tt> are always loaded from the
 * classpath.
 * <p>
 * Mapping metadata can be supplied through:
 * <li>mappingClasses: equivalent to &lt;mapping class="..."/>
 * <li>mappingFiles: equivalent to &lt;mapping file="..."/>
 * <li>mappingJars: equivalent to &lt;mapping jar="..."/>
 * <li>mappingPackages: equivalent to &lt;mapping package="..."/>
 * <li>mappingResources: equivalent to &lt;mapping resource="..."/>
 * <p>
 * or through cfg.xml files.
 * <p>
 * The <tt>jndiProperties</tt> are convenience, the factory will
 * automatically prefix regular JNDI properties for use as Hibernate
 * configuration properties.
 *
 * @author Gavin King
 * @author Christian Bauer
 */
public class HibernateFactory {

    private String cfgResourceName;
    private Properties cfgProperties;
    private Hashtable jndiProperties;

    private List<String> mappingClasses;
    private List<String> mappingFiles;
    private List<String> mappingJars;
    private List<String> mappingPackages;
    private List<String> mappingResources;

    public Object getSessionFactory() throws Exception {
        AnnotationConfiguration acfg = new AnnotationConfiguration();

        // Programmatic configuration
        if (cfgProperties != null) {
            acfg.setProperties(cfgProperties);
        }

        // Prefix regular JNDI properties for Hibernate
        if (jndiProperties != null) {
            for (Iterator it = jndiProperties.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry)it.next();
                acfg.setProperty(Environment.JNDI_PREFIX + "." + entry.getKey(),
                                 (String)entry.getValue() );
            }
        }

        // hibernate.cfg.xml configuration
        if (cfgProperties == null && cfgResourceName == null) {
            acfg.configure();
        } else if (cfgProperties == null && cfgResourceName != null) {
            acfg.configure(cfgResourceName);
        }

        // Mapping metadata
        if (mappingClasses != null)
            for(String className: mappingClasses)
                acfg.addAnnotatedClass(ReflectHelper.classForName(className));

        if (mappingFiles != null)
            for(String fileName: mappingFiles)
                acfg.addFile(fileName);

        if (mappingJars != null)
            for(String jarName: mappingJars)
                acfg.addJar( new File(jarName) );

        if (mappingPackages != null)
            for(String packageName: mappingPackages)
                acfg.addPackage(packageName);

        if (mappingResources != null)
            for(String resourceName: mappingResources)
                acfg.addResource(resourceName);

        return acfg.buildSessionFactory();
    }

    public String getCfgResourceName() {
        return cfgResourceName;
    }

    public void setCfgResourceName(String cfgFileName) {
        this.cfgResourceName = cfgFileName;
    }

    public Properties getCfgProperties() {
        return cfgProperties;
    }

    public void setCfgProperties(Properties cfgProperties) {
        this.cfgProperties = cfgProperties;
    }

    public List<String> getMappingClasses() {
        return mappingClasses;
    }

    public void setMappingClasses(List<String> mappingClasses) {
        this.mappingClasses = mappingClasses;
    }

    public List<String> getMappingFiles() {
        return mappingFiles;
    }

    public void setMappingFiles(List<String> mappingFiles) {
        this.mappingFiles = mappingFiles;
    }

    public List<String> getMappingJars() {
        return mappingJars;
    }

    public void setMappingJars(List<String> mappingJars) {
        this.mappingJars = mappingJars;
    }

    public List<String> getMappingPackages() {
        return mappingPackages;
    }

    public void setMappingPackages(List<String> mappingPackages) {
        this.mappingPackages = mappingPackages;
    }

    public List<String> getMappingResources() {
        return mappingResources;
    }

    public void setMappingResources(List<String> mappingResources) {
        this.mappingResources = mappingResources;
    }

    public Hashtable getJndiProperties() {
        return jndiProperties;
    }

    public void setJndiProperties(Hashtable jndiProperties) {
        this.jndiProperties = jndiProperties;
    }

}
