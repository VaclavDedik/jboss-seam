/*
 * JBoss, Home of Professional Open Source
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.seam.init;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Namespace;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Roles;
import org.jboss.seam.bpm.Jbpm;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Init;
import org.jboss.seam.deployment.HotDeploymentStrategy;
import org.jboss.seam.deployment.SeamDeploymentProperties;
import org.jboss.seam.deployment.StandardDeploymentStrategy;
import org.jboss.seam.deployment.WarRootDeploymentStrategy;
import org.jboss.seam.exception.Exceptions;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.util.Conversions;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Reflections;
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.Strings;
import org.jboss.seam.util.XML;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

/**
 * Builds configuration metadata when Seam first initialized.
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @author Pete Muir
 */
public class Initialization
{
   public static final String COMPONENT_NAMESPACE = "http://jboss.com/products/seam/components";
   public static final String COMPONENT_SUFFIX = ".component";
   public static final String DUPLICATE_JARS_PATTERNS = "org.jboss.seam.init.duplicateJarsPatterns";
   
   private static final LogProvider log = Logging.getLogProvider(Initialization.class);

   private ServletContext servletContext;
   private Map<String, Conversions.PropertyValue> properties = new HashMap<String, Conversions.PropertyValue>();
   private Map<String, Set<ComponentDescriptor>> componentDescriptors = new HashMap<String, Set<ComponentDescriptor>>();
   private List<FactoryDescriptor> factoryDescriptors = new ArrayList<FactoryDescriptor>();
   private Set<Class> installedComponentClasses = new HashSet<Class>();
   //private Set<String> importedPackages = new HashSet<String>();
   private Map<String, NamespaceDescriptor> namespaceMap = new HashMap<String, NamespaceDescriptor>();
   private NamespacePackageResolver namespacePackageResolver = new NamespacePackageResolver();
   private Map<String, EventListenerDescriptor> eventListenerDescriptors = new HashMap<String, EventListenerDescriptor>();
   private Collection<String> globalImports = new ArrayList<String>();
   
   private StandardDeploymentStrategy standardDeploymentStrategy;
   private HotDeploymentStrategy hotDeploymentStrategy;
   private WarRootDeploymentStrategy warRootDeploymentStrategy;
   
   private File hotDeployDirectory;
   private File warRoot;
   
   private Set<String> nonPropertyAttributes = new HashSet<String>();
   
   {
       nonPropertyAttributes.add("name");
       nonPropertyAttributes.add("installed");
       nonPropertyAttributes.add("scope");
       nonPropertyAttributes.add("startup");
       nonPropertyAttributes.add("startupDepends");
       nonPropertyAttributes.add("class");
       nonPropertyAttributes.add("jndi-name");
       nonPropertyAttributes.add("precedence");
       nonPropertyAttributes.add("auto-create");           
   }   
   
   public Initialization(ServletContext servletContext)
   {
      this.servletContext = servletContext;
      this.warRoot = getRealFile(servletContext, "/");
      this.hotDeployDirectory = getRealFile(servletContext, HotDeploymentStrategy.DEFAULT_HOT_DEPLOYMENT_DIRECTORY_PATH);
   }
   
   public Initialization create()
   {
      standardDeploymentStrategy = new StandardDeploymentStrategy(Thread.currentThread().getContextClassLoader());
      standardDeploymentStrategy.scan();
      addNamespaces();
      initComponentsFromXmlDocument("/WEB-INF/components.xml");
      initComponentsFromXmlDocument("/WEB-INF/events.xml"); //deprecated
      initComponentsFromXmlDocuments();
      initPropertiesFromServletContext();
      initPropertiesFromResource();
      initJndiProperties();
      return this;
   }

   private void initComponentsFromXmlDocuments()
   {
      Enumeration<URL> resources;
      try
      {
         resources = Thread.currentThread().getContextClassLoader().getResources("META-INF/components.xml");
      }
      catch (IOException ioe)
      {
         throw new RuntimeException("error scanning META-INF/components.xml files", ioe);
      }

      List<String> seenDocuments = new ArrayList<String>();
      
      Properties replacements = getReplacements();
      Set<Pattern> duplicateJarPatterns = new HashSet<Pattern>();
      for (String duplicateJarRegex : new HashSet<String>(new SeamDeploymentProperties(Thread.currentThread().getContextClassLoader()).getPropertyValues(DUPLICATE_JARS_PATTERNS)))
      {
         duplicateJarPatterns.add(Pattern.compile(duplicateJarRegex));
      }
      while (resources.hasMoreElements())
      {
         URL url = resources.nextElement();
         boolean skip = false;
         String path = url.getPath();
         String prefixPattern = "^(\\S*/)([\\S^/]*.jar)(\\S*)$";
         Pattern pattern = Pattern.compile(prefixPattern);
         Matcher matcher = pattern.matcher(path);
         String jarName;
         String fileName;
         if (matcher.matches())
         {
            jarName = matcher.group(2);
            fileName = matcher.group(3);
            Set<String> documentNames = new HashSet<String>();
            for (Pattern duplicateJarPattern : duplicateJarPatterns)
            {
               Matcher duplicateMatcher = duplicateJarPattern.matcher(jarName);
               
               if (duplicateMatcher.matches())
               {
                  jarName = duplicateMatcher.group(1);
               }
               
               documentNames.add(jarName + fileName);
            }
            for (String documentName : documentNames)
            {
               if (seenDocuments.contains(documentName))
               {
                  skip = true;
               }
            }
            seenDocuments.addAll(documentNames);
         }
         
         if (!skip)
         {
            try
            {
               log.debug("reading " + url);
               installComponentsFromXmlElements( XML.getRootElement( url.openStream() ), replacements );
            }
            catch (Exception e)
            {
               throw new RuntimeException("error while reading " + url, e);
            }
         }
         else
         {
            log.trace("skipping read of duplicate components.xml " + url);
         }
      }

   }

   private void initComponentsFromXmlDocument(String resource)
   {
      InputStream stream = Resources.getResourceAsStream(resource, servletContext);
      if (stream != null)
      {
         log.info("reading " + resource);
         try
         {
            installComponentsFromXmlElements( XML.getRootElement(stream), getReplacements() );
         }
         catch (Exception e)
         {
            throw new RuntimeException("error while reading /WEB-INF/components.xml", e);
         }
      }
   }

   private Properties getReplacements()
   {
      try
      {
         Properties replacements = new Properties();
         InputStream replaceStream = Resources.getResourceAsStream("/components.properties", servletContext);
         if (replaceStream != null) replacements.load(replaceStream);
         return replacements;
      }
      catch (IOException ioe)
      {
         throw new RuntimeException("error reading components.properties", ioe);
      }
   }

   private List<Element> elements(Element rootElement, String name) {
       return rootElement.elements(name);
   }
   
   @SuppressWarnings("unchecked")
   private void installComponentsFromXmlElements(Element rootElement, Properties replacements)
   throws DocumentException, ClassNotFoundException
   {
	   /*List<Element> importJavaElements = rootElement.elements("import-java-package");
        for (Element importElement : importJavaElements)
        {
            String pkgName = importElement.getTextTrim();
            importedPackages.add(pkgName);
            addNamespace( Package.getPackage(pkgName) );
        }*/

	   for (Element importElement: elements(rootElement,"import")) {
		   globalImports.add( importElement.getTextTrim() );
	   }

	   for (Element component: elements(rootElement,"component")) {
		   installComponentFromXmlElement(component, 
				   component.attributeValue("name"), 
				   component.attributeValue("class"), 
				   replacements);
	   }

	   for (Element factory: elements(rootElement,"factory")) {
		   installFactoryFromXmlElement(factory);
	   }

	   for (Element event: elements(rootElement, "event")) {
		   installEventListenerFromXmlElement(event);
	   }

	   for (Element elem : (List<Element>) rootElement.elements()) {
		   String ns = elem.getNamespace().getURI();
		   NamespaceDescriptor nsInfo = resolveNamespace(ns);
		   if (nsInfo == null) {
			   if (!ns.equals(COMPONENT_NAMESPACE)) {
			       log.warn("namespace declared in components.xml does not resolve to a package: " + ns);
			   }
		   } else {
			   String name = elem.attributeValue("name");
			   String elemName = toCamelCase(elem.getName(), true);

			   String className = elem.attributeValue("class");
			   if (className == null) 
			   {
			      for (String packageName : nsInfo.getPackageNames())
			      {
			         try
			         {
			            // Try each of the packages in the namespace descriptor for a matching class
			            className = packageName + '.' + elemName;
			            Reflections.classForName(className);
			            break;
			         }
			         catch (ClassNotFoundException ex)
			         {
			            className = null;
			         }
			      }				   				   
			   }

			   try {
				   //get the class implied by the namespaced XML element name
				   Class<Object> clazz = Reflections.classForName(className);
				   Name nameAnnotation = clazz.getAnnotation(Name.class);

				   //if the name attribute is not explicitly specified in the XML,
				   //imply the name from the @Name annotation on the class implied
				   //by the XML element name
				   if (name == null && nameAnnotation!=null) {
					   name = nameAnnotation.value();
				   }

				   //if this class already has the @Name annotation, the XML element 
				   //is just adding configuration to the existing component, don't
				   //add another ComponentDescriptor (this is super-important to
				   //allow overriding!)
				   if (nameAnnotation!=null && nameAnnotation.value().equals(name)) {
					   Install install = clazz.getAnnotation(Install.class);
					   if (install == null || install.value()) {
						   className = null;
					   }
				   }
			   } catch (ClassNotFoundException cnfe) {
				   //there is no class implied by the XML element name so the
				   //component must be defined some other way, assume that we are
				   //just adding configuration, don't add a ComponentDescriptor 
				   //TODO: this is problematic, it results in elements getting 
				   //      ignored when mis-spelled or given the wrong namespace!!
				   className = null;
			   }
			   catch (Exception e) 
			   {
               throw new RuntimeException("Error loading element " + elemName + " with component name " + name + " and component class " + className);
            }

			   //finally, if we could not get the name from the XML name attribute,
			   //or from an @Name annotation on the class, imply it
			   if (name == null) {
				   String prefix = nsInfo.getComponentPrefix();
				   String componentName = toCamelCase(elem.getName(), false);
				   name = Strings.isEmpty(prefix) ? 
						   componentName : prefix + '.' + componentName;
			   }
   
			   installComponentFromXmlElement(elem, name, className, replacements);
		   }
	   }
   }

	private NamespaceDescriptor resolveNamespace(String namespace) {
		if (Strings.isEmpty(namespace) || namespace.equals(COMPONENT_NAMESPACE)) {
			return null;
		}
		
		NamespaceDescriptor descriptor = namespaceMap.get(namespace);	
		if (descriptor == null) {
			try {
				String packageName = namespacePackageResolver.resolve(namespace);
				descriptor = new NamespaceDescriptor(namespace, packageName);
				namespaceMap.put(namespace, descriptor);
			} catch (Exception e) {
				log.warn("Could not determine java package for namespace: " + namespace, e);
			}
		}
		
		return descriptor;
	}

   @SuppressWarnings("unchecked")
   private void installEventListenerFromXmlElement(Element event)
   {
      String type = event.attributeValue("type");
      if (type==null)
      {
         throw new IllegalArgumentException("must specify type for <event/> declaration");
      }
      EventListenerDescriptor eventListener = eventListenerDescriptors.get(type);
      if (eventListener==null) 
      {
         eventListener = new EventListenerDescriptor(type);
         eventListenerDescriptors.put(type, eventListener);
      }
      
      List<Element> actions = event.elements("action");
      for (Element action: actions)
      {
         String execute = action.attributeValue("execute");
         if (execute==null)
         {
            String actionExpression = action.attributeValue("expression");
            if (actionExpression!=null)
            {
                log.warn("<action expression=\"" + actionExpression + "\" /> has been deprecated, use <action execute=\"" + actionExpression + "\" /> instead");
                execute = actionExpression;
            }
            else
            {    
                throw new IllegalArgumentException("must specify execute for <action/> declaration");
            }
         }
         eventListener.getListenerMethodBindings().add(execute);
      }
   }

   private void installFactoryFromXmlElement(Element factory)
   {
      String scopeName = factory.attributeValue("scope");
      String name = factory.attributeValue("name");
      if (name == null)
      {
         throw new IllegalArgumentException("must specify name in <factory/> declaration");
      }
      String method = factory.attributeValue("method");
      String value = factory.attributeValue("value");
      if (method == null && value == null)
      {
         throw new IllegalArgumentException(
                  "must specify either method or value in <factory/> declaration for variable: "
                           + name);
      }
      ScopeType scope = scopeName == null ? ScopeType.UNSPECIFIED : ScopeType.valueOf(scopeName
               .toUpperCase());
      boolean autoCreate = "true".equals(factory.attributeValue("auto-create"));
      factoryDescriptors.add(new FactoryDescriptor(name, scope, method, value, autoCreate));
   }

   private String replace(String value, Properties replacements)
   {
      if (value.startsWith("@"))
      {
         value = replacements.getProperty(value.substring(1, value.length() - 1));
      }
      return value;
   }

   @SuppressWarnings("unchecked")
   private void installComponentFromXmlElement(Element component, String name, String className,
            Properties replacements) throws ClassNotFoundException
   {  
      String installText = component.attributeValue("installed");
      boolean installed = false;
      if (installText == null || "true".equals(replace(installText, replacements)))
      {
         installed = true;
      }

      String scopeName = component.attributeValue("scope");
      String jndiName = component.attributeValue("jndi-name");
      String precedenceString = component.attributeValue("precedence");
      int precedence = precedenceString==null ? Install.APPLICATION : Integer.valueOf(precedenceString);
      ScopeType scope = scopeName == null ? null : ScopeType.valueOf(scopeName.toUpperCase());
      String autocreateAttribute = component.attributeValue("auto-create");
      Boolean autoCreate = autocreateAttribute==null ? null : "true".equals(autocreateAttribute);
      String startupAttribute = component.attributeValue("startup");
      Boolean startup = startupAttribute==null ? null : "true".equals(startupAttribute);
      String startupDependsAttribute = component.attributeValue("startupDepends");
      String[] startupDepends = startupDependsAttribute==null ? new String[0] : startupDependsAttribute.split(" ");

      if (className != null)
      {
         Class<?> clazz = getClassUsingImports(className);

         if (name == null)
         {
            if ( !clazz.isAnnotationPresent(Name.class) )
            {
               throw new IllegalArgumentException(
                        "Component class must have @Name annotation or name must be specified in components.xml: " +
                        clazz.getName());
            }
            
            name = clazz.getAnnotation(Name.class).value();
         }

         ComponentDescriptor descriptor = new ComponentDescriptor(name, clazz, scope, autoCreate, startup, startupDepends, jndiName, installed, precedence);
         addComponentDescriptor(descriptor);
         installedComponentClasses.add(clazz);
      }
      else if (name == null)
      {
         throw new IllegalArgumentException("must specify either class or name in <component/> declaration");
      }

      for ( Element prop : (List<Element>) component.elements() )
      {
         String propName = prop.attributeValue("name");
         if (propName == null)
         {
            propName = prop.getQName().getName();
         }
         String qualifiedPropName = name + '.' + toCamelCase(propName, false);
         properties.put( qualifiedPropName, getPropertyValue(prop, qualifiedPropName, replacements) );
      }
      
      for ( Attribute prop: (List<Attribute>) component.attributes() )
      {
         String attributeName = prop.getName();
         if (isProperty(prop.getNamespaceURI(),attributeName))
         {
            String qualifiedPropName = name + '.' + toCamelCase( prop.getQName().getName(), false );
            Conversions.PropertyValue propValue = null;
            try
            {
               propValue = getPropertyValue(prop, replacements);
               properties.put( qualifiedPropName, propValue );
            }
            catch (Exception ex)
            {
               throw new IllegalArgumentException(String.format(
                        "Exception setting property %s on component %s.  Expression %s evaluated to %s.",
                        qualifiedPropName, name, prop.getValue(), propValue), ex);
                        
            }
         }
      }
   }

   /**
    * component properties are non-namespaced and not in the reserved attribute list
    */
   private boolean isProperty(String namespaceURI, String attributeName) {
       return (namespaceURI == null || namespaceURI.length()==0) && 
              !nonPropertyAttributes.contains(attributeName);
   }

   private Class<?> getClassUsingImports(String className) throws ClassNotFoundException
   {
      Class<?> clazz = null;
      /*try
      {*/
         clazz = Reflections.classForName(className);
      /*}
      catch (ClassNotFoundException cnfe)
      {
         for (String pkg : importedPackages)
         {
            try
            {
               clazz = Reflections.classForName(pkg + '.' + className);
               break;
            }
            catch (Exception e)
            {
            }
         }
         if (clazz == null) throw cnfe;
      }*/
      return clazz;
   }

   private void addComponentDescriptor(ComponentDescriptor descriptor)
   {
      String name = descriptor.getName();
      
      Set<ComponentDescriptor> set = componentDescriptors.get(name);
      if (set==null)
      {
         set = new TreeSet<ComponentDescriptor>(new ComponentDescriptor.PrecedenceComparator());
         componentDescriptors.put(name, set);
      }
      if ( !set.isEmpty() )
      {
         log.info("two components with same name, higher precedence wins: " + name);
      }
      if ( !set.add(descriptor) )
      {
         ComponentDescriptor other = null;
         for (ComponentDescriptor d : set)
         {
            if (descriptor.compareTo(d) == 0)
            {
               other = d;
               break;
            }
         }
         
         throw new IllegalStateException("Two components with the same name and precedence - " +
               "component name: " + name + ", component classes: " + 
               descriptor.getComponentClass().getName() + ", " +
               (other != null ? other.getComponentClass().getName() : "<unknown>"));
      }
   }

   private Conversions.PropertyValue getPropertyValue(Attribute prop, Properties replacements)
   {
      return new Conversions.FlatPropertyValue( trimmedText(prop, replacements) );
   }
   
   @SuppressWarnings("unchecked")
   private Conversions.PropertyValue getPropertyValue(Element prop, String propName,
            Properties replacements)
   {
      List<Element> keyElements = prop.elements("key");
      List<Element> valueElements = prop.elements("value");

      if (valueElements.isEmpty() && keyElements.isEmpty())
      {
         return new Conversions.FlatPropertyValue(
                  trimmedText(prop, propName, replacements));
      }
      else if (keyElements.isEmpty())
      {
         // a list-like structure
         int len = valueElements.size();
         String[] values = new String[len];
         for (int i = 0; i < len; i++)
         {
            values[i] = trimmedText(valueElements.get(i), propName, replacements);
         }
         return new Conversions.MultiPropertyValue(values);
      }
      else
      {
         // a map-like structure
         if (valueElements.size() != keyElements.size())
         {
            throw new IllegalArgumentException("value elements must match key elements: "
                     + propName);
         }
         Map<String, String> keyedValues = new HashMap<String, String>();
         for (int i = 0; i < keyElements.size(); i++)
         {
            String key = trimmedText(keyElements.get(i), propName, replacements);
            String value = trimmedText(valueElements.get(i), propName, replacements);
            keyedValues.put(key, value);
         }
         return new Conversions.AssociativePropertyValue(keyedValues);
      }
   }

   private String trimmedText(Element element, String propName, Properties replacements)
   {
      String text = element.getTextTrim();
      if (text == null)
      {
         throw new IllegalArgumentException("property value must be specified in element body: "
                  + propName);
      }
      return replace(text, replacements);
   }

   private String trimmedText(Attribute attribute, Properties replacements)
   {
      return replace( attribute.getText(), replacements );
   }

   public Initialization setProperty(String name, Conversions.PropertyValue value)
   {
      properties.put(name, value);
      return this;
   }
   
   public Initialization init()
   {
      log.info("initializing Seam");
      if (standardDeploymentStrategy == null)
      {
         throw new IllegalStateException("No deployment strategy!");
      }
      ServletLifecycle.beginInitialization();
      Contexts.getApplicationContext().set(Component.PROPERTIES, properties);
      hotDeploymentStrategy = createHotDeployment(Thread.currentThread().getContextClassLoader());
      scanForComponents();
      addComponent( new ComponentDescriptor(Init.class), Contexts.getApplicationContext());
      Init init = (Init) Component.getInstance(Init.class, ScopeType.APPLICATION);
      init.setHotDeployPaths( hotDeploymentStrategy.getHotDeploymentPaths() );
      ComponentDescriptor desc = findDescriptor(Jbpm.class);
      if (desc != null && desc.isInstalled())
      {
         init.setJbpmInstalled(true);
      }
      init.checkDefaultInterceptors();
      init.setTimestamp( System.currentTimeMillis() );
      addSpecialComponents(init);
      
      // Add the war root deployment
      warRootDeploymentStrategy = new WarRootDeploymentStrategy(Thread.currentThread().getContextClassLoader(), warRoot);
      warRootDeploymentStrategy.scan();
      
      // Make the deployment strategies available in the contexts. This gives 
      // access to custom deployment handlers for processing custom annotations
      // etc.
      Contexts.getEventContext().set(StandardDeploymentStrategy.NAME, standardDeploymentStrategy);
      Contexts.getEventContext().set(HotDeploymentStrategy.NAME, hotDeploymentStrategy);
      Contexts.getEventContext().set(WarRootDeploymentStrategy.NAME, warRootDeploymentStrategy);
      
      if (hotDeploymentStrategy.isEnabled())
      {
         hotDeploymentStrategy.scan();
         if (hotDeploymentStrategy.isHotDeployClassLoaderEnabled())
         {
            installHotDeployableComponents();
         }
      }
      
      installComponents(init);
           
      for (String globalImport: globalImports)
      {
         init.importNamespace(globalImport);
      }
      
      ServletLifecycle.endInitialization();
      log.info("done initializing Seam");
      return this;
   }

   public Initialization redeploy(HttpServletRequest request)
   {
      ServletLifecycle.beginReinitialization(request);
      hotDeploymentStrategy = createHotDeployment(Thread.currentThread().getContextClassLoader());
      if (hotDeploymentStrategy.isEnabled())
      {
         hotDeploymentStrategy.scan();
         Init init = Init.instance();
         
         if (init.getTimestamp() < hotDeploymentStrategy.getTimestamp())
         {
            log.info("redeploying");
            Seam.clearComponentNameCache();
            for ( String name: init.getHotDeployableComponents() )
            {
               Component component = Component.forName(name);
               if (component!=null)
               {
                  ScopeType scope = component.getScope();
                  if ( scope!=ScopeType.STATELESS && scope.isContextActive() )
                  {
                     scope.getContext().remove(name);
                  }
                  init.removeObserverMethods(component);
               }
               Contexts.getApplicationContext().remove(name + COMPONENT_SUFFIX);
            }
         
            if (hotDeploymentStrategy.isHotDeployClassLoaderEnabled())
            {
               installHotDeployableComponents();
            }
            Contexts.getEventContext().set(HotDeploymentStrategy.NAME, hotDeploymentStrategy);
            init.setTimestamp( System.currentTimeMillis() );
            installComponents(init);
            log.info("done redeploying");
         }
         
         WarRootDeploymentStrategy warRootDeploymentStrategy = new WarRootDeploymentStrategy(Thread.currentThread().getContextClassLoader(), warRoot);
         warRootDeploymentStrategy.scan();
         Contexts.getEventContext().set(WarRootDeploymentStrategy.NAME, warRootDeploymentStrategy);
         Pages pages = Pages.instance();
         if (pages!= null) {
             pages.initialize();
         }
      
         Contexts.getApplicationContext().remove(Seam.getComponentName(Exceptions.class));
      }
      ServletLifecycle.endReinitialization();
      return this;   
   }

   private void installHotDeployableComponents()
   {
      for (Class<Object> scannedClass: hotDeploymentStrategy.getScannedComponentClasses() )
      {
         installScannedComponentAndRoles(scannedClass);
      }
   }
   
   private HotDeploymentStrategy createHotDeployment(ClassLoader classLoader)
   {
      if (isGroovyPresent())
      {
         log.debug("Using Java + Groovy hot deploy");
         return HotDeploymentStrategy.createInstance("org.jboss.seam.deployment.GroovyHotDeploymentStrategy", classLoader, hotDeployDirectory, isDebugEnabled());
      }
      else 
      {
         log.debug("Using Java hot deploy");
         return new HotDeploymentStrategy(classLoader, hotDeployDirectory, isDebugEnabled());
      }
   }
   
   private static File getRealFile(ServletContext servletContext, String path)
   {
      String realPath = servletContext.getRealPath(path);
      if (realPath==null) //WebLogic!
      {
         try 
         {
            URL resourcePath = servletContext.getResource(path);
            if (resourcePath.getProtocol().equals("file")) 
            {
               realPath = resourcePath.getPath();
            }
         }
         catch (MalformedURLException e) {}

      }
      
      if (realPath != null)
      {
         File file = new File(realPath);
         if (file.exists())
         {
            return file;
         }
      }
      return null;
   }
   
   private static boolean isDebugEnabled()
   {
      return Resources.getResource("META-INF/debug.xhtml", null) != null;
   }
   
   private static boolean isGroovyPresent()
   {
      try 
      {
         Reflections.classForName( "groovy.lang.GroovyObject" );
         return true;
      }
      catch (ClassNotFoundException e)
      {
         //groovy is not there
         return false;
      }
   }

   private void scanForComponents()
   {
      for ( Class<Object> scannedClass: standardDeploymentStrategy.getScannedComponentClasses() ) 
      {
          installScannedComponentAndRoles(scannedClass);
      }
      
      
      for ( String name: standardDeploymentStrategy.getScannedComponentResources() ) 
      {
          installComponentsFromDescriptor( name, standardDeploymentStrategy.getClassLoader() );              
      }
   }

   private static String classFilenameFromDescriptor(String descriptor) {
       int pos = descriptor.lastIndexOf(".component.xml");
       if (pos==-1) {
           return null;
       }
       
       return descriptor.substring(0,pos).replace('/', '.').replace('\\', '.');
   }

   private void installComponentsFromDescriptor(String fileName, ClassLoader loader)
   {
      //note: this is correct, we do not need to scan other classloaders!
      InputStream stream = loader.getResourceAsStream(fileName); 
      if (stream != null) 
      {
         try 
         {
            Properties replacements = getReplacements();
            Element root = XML.getRootElement(stream);
            if (root.getName().equals("components")) 
            {
               installComponentsFromXmlElements(root, replacements);
            } 
            else
            {
                installComponentFromXmlElement(root, 
                        root.attributeValue("name"), 
                        classFilenameFromDescriptor(fileName),
                        replacements);
            }
         } 
         catch (Exception e) 
         {
            throw new RuntimeException("error while reading " + fileName, e);
         }
      }
   }

   private void installScannedComponentAndRoles(Class<Object> scannedClass)
   {
      try {
         if ( scannedClass.isAnnotationPresent(Name.class) )
         {
            addComponentDescriptor( new ComponentDescriptor(scannedClass) );
         }
         if ( scannedClass.isAnnotationPresent(Role.class) )
         {
            installRole( scannedClass, scannedClass.getAnnotation(Role.class) );
         }
         if ( scannedClass.isAnnotationPresent(Roles.class) )
         {
            Role[] roles = scannedClass.getAnnotation(Roles.class).value();
            for (Role role : roles)
            {
               installRole(scannedClass, role);
            }
         }
      } catch(TypeNotPresentException e) {
         log.info("Failed to install "+scannedClass.getName()+": "+e.getMessage());
      }
   }

   private void installRole(Class<Object> scannedClass, Role role)
   {
      ScopeType scope = Seam.getComponentRoleScope(scannedClass, role);
      addComponentDescriptor( new ComponentDescriptor( role.name(), scannedClass, scope ) );
   }

   private void addNamespace(Package pkg)
   {
	   if (pkg != null) {
		   Namespace ns = pkg.getAnnotation(Namespace.class);
		   if (ns != null) {
			   log.info("Namespace: " + ns.value() + ", package: " + pkg.getName() + 
					   ", prefix: " + ns.prefix());

			   NamespaceDescriptor descriptor = namespaceMap.get(ns.value());
			   if (descriptor != null)
			   {
			      descriptor.addPackageName(pkg.getName());
			   }
			   else
			   {
			      namespaceMap.put(ns.value(), new NamespaceDescriptor(ns, pkg));
			   }
		   }
	   }
   }

   private void addNamespaces()
   {
	   for (Package pkg: standardDeploymentStrategy.getScannedNamespaces()) {
		   addNamespace(pkg);
	   }
   }

   private void initPropertiesFromServletContext()
   {
      Enumeration params = servletContext.getInitParameterNames();
      while (params.hasMoreElements())
      {
         String name = (String) params.nextElement();
         properties.put(name, new Conversions.FlatPropertyValue(servletContext
                  .getInitParameter(name)));
      }
   }

   private void initPropertiesFromResource()
   {
      Properties props = loadFromResource("/seam.properties");
      for (Map.Entry me : props.entrySet())
      {
         properties.put((String) me.getKey(), new Conversions.FlatPropertyValue((String) me
                  .getValue()));
      }
   }

   private void initJndiProperties()
   {
      Properties jndiProperties = new Properties();
      jndiProperties.putAll(loadFromResource("/jndi.properties"));
      jndiProperties.putAll(loadFromResource("/seam-jndi.properties"));
      Naming.setInitialContextProperties(jndiProperties);
   }

   private Properties loadFromResource(String resource)
   {
      Properties props = new Properties();
      InputStream stream = Resources.getResourceAsStream(resource, servletContext);
      if (stream != null)
      {
         try
         {
            log.info("reading properties from: " + resource);
            try
            {
               props.load(stream);
            }
            catch (IOException ioe)
            {
               log.error("could not read " + resource, ioe);
            }
         }
         finally
         {
            try
            {
               stream.close();
            }
            catch (IOException ex) { } // swallow
         }
      }
      else
      {
         log.debug("not found: " + resource);
      }
      return props;
   }

   protected ComponentDescriptor findDescriptor(Class<?> componentClass)
   {
      for (Set<ComponentDescriptor> components : componentDescriptors.values())
      {
         for (ComponentDescriptor component: components)
         {
            if ( component.getComponentClass().equals(componentClass) )
            {
               return component;
            }
         }
      }
      return null;
   }

   private void addSpecialComponents(Init init)
   {}

   private void installComponents(Init init)
   {
      log.info("Installing components...");
      Context context = Contexts.getApplicationContext();

      DependencyManager manager = new DependencyManager(componentDescriptors);

      Set<ComponentDescriptor> installable = manager.installedSet();      
      for (ComponentDescriptor componentDescriptor: installable) 
      {
          String compName = componentDescriptor.getName() + COMPONENT_SUFFIX;

          if ( !context.isSet(compName) ) 
          {
              addComponent(componentDescriptor, context);

              if ( componentDescriptor.isAutoCreate() ) 
              {
                  init.addAutocreateVariable( componentDescriptor.getName() );
              }

              if ( componentDescriptor.isFilter() ) 
              {
                  init.addInstalledFilter( componentDescriptor.getName() );
              }

              if ( componentDescriptor.isResourceProvider() ) 
              {
                 if (!componentDescriptor.getScope().equals(ScopeType.APPLICATION))
                 {
                    throw new RuntimeException("Resource providers must be application-scoped components");
                 }
                 
                 init.addResourceProvider( componentDescriptor.getName() );
              }
              
              if ( componentDescriptor.isPermissionResolver() )
              {
                 init.addPermissionResolver( componentDescriptor.getName() );
              }
          }
      }


      for (FactoryDescriptor factoryDescriptor : factoryDescriptors)
      {
         if (factoryDescriptor.isValueBinding())
         {
            init.addFactoryValueExpression(factoryDescriptor.getName(), factoryDescriptor.getValue(),
                     factoryDescriptor.getScope());
         }
         else
         {
            init.addFactoryMethodExpression(factoryDescriptor.getName(),
                     factoryDescriptor.getMethod(), factoryDescriptor.getScope());
         }
         if (factoryDescriptor.isAutoCreate())
         {
            init.addAutocreateVariable(factoryDescriptor.getName());
         }
      }
      
      for (EventListenerDescriptor listenerDescriptor: eventListenerDescriptors.values())
      {
         for (String expression: listenerDescriptor.getListenerMethodBindings())
         {
            init.addObserverMethodExpression( listenerDescriptor.getType(), Expressions.instance().createMethodExpression(expression) );
         }
      }
   }

  
   /**
    * This actually creates a real Component and should only be called when
    * we want to install a component
    */
   protected void addComponent(ComponentDescriptor descriptor, Context context)
   {
      String name = descriptor.getName();
      String componentName = name + COMPONENT_SUFFIX;
      try
      {
         Component component = new Component(
               descriptor.getComponentClass(), 
               name, 
               descriptor.getScope(), 
               descriptor.isStartup(),
               descriptor.getStartupDependencies(),
               descriptor.getJndiName()
            );
         context.set(componentName, component);
         if ( hotDeploymentStrategy.isEnabled() && hotDeploymentStrategy.isFromHotDeployClassLoader( descriptor.getComponentClass() ) )
         {
            Init.instance().addHotDeployableComponent( component.getName() );
         }
      }
      catch (Throwable e)
      {
         throw new RuntimeException("Could not create Component: " + name, e);
      }      
   }

   private static String toCamelCase(String hyphenated, boolean initialUpper)
   {
      StringTokenizer tokens = new StringTokenizer(hyphenated, "-");
      StringBuilder result = new StringBuilder( hyphenated.length() );
      String firstToken = tokens.nextToken();
      if (initialUpper)
      {
         result.append( Character.toUpperCase( firstToken.charAt(0) ) )
         .append( firstToken.substring(1) );         
      }
      else
      {
         result.append(firstToken);
      }
      while ( tokens.hasMoreTokens() )
      {
         String token = tokens.nextToken();
         result.append( Character.toUpperCase( token.charAt(0) ) )
               .append( token.substring(1) );
      }
      return result.toString();
   }

   
   
   private static class EventListenerDescriptor
   {
      private String type;
      private List<String> listenerMethodBindings = new ArrayList<String>();
      
      EventListenerDescriptor(String type)
      {
         this.type = type;
      }
      
      public String getType()
      {
         return type;
      }

      public List<String> getListenerMethodBindings()
      {
         return listenerMethodBindings;
      }
      
      @Override
      public String toString()
      {
         return "EventListenerDescriptor(" + type + ')';
      }
   }

   
}
