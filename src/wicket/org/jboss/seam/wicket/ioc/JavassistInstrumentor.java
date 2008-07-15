package org.jboss.seam.wicket.ioc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.CtField.Initializer;

import javax.servlet.ServletContext;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.wicket.WicketComponent;


public class JavassistInstrumentor
{
   
   private static LogProvider log = Logging.getLogProvider(JavassistInstrumentor.class);
   
   public static String DEFAULT_WICKET_COMPONENT_DIRECTORY_PATH = "WEB-INF/wicket";
   
   private ClassLoader classLoader;
   
   private final List<String> classes = new ArrayList<String>();
   private File wicketComponentDirectory;
   private ClassPool classPool = new ClassPool();
   
   public JavassistInstrumentor(ServletContext servletContext)
   {
      wicketComponentDirectory = getWicketComponentDirectory(servletContext);
   }
   
   public void instrument() throws NotFoundException, CannotCompileException, ClassNotFoundException
   {
      if (wicketComponentDirectory == null)
      {
         log.warn("No wicket components found to give Seam super powers to");
         classLoader = Thread.currentThread().getContextClassLoader();
         return;
      }
      ClassLoader parent = Thread.currentThread().getContextClassLoader();
      classPool = new ClassPool();
      classLoader = new WicketClassLoader(parent, classPool, classes, wicketComponentDirectory);
      classPool.insertClassPath(wicketComponentDirectory.getAbsolutePath());
      classPool.insertClassPath(new LoaderClassPath(parent));
      
      if (wicketComponentDirectory.exists())
      {
         // Scan for classes
         handleDirectory(wicketComponentDirectory, null);
      }
      
      // Ensure classes are instantiated, and create metadata
      for (String className : classes)
      {
         Class clazz = classLoader.loadClass(className);
         new WicketComponent(clazz);
      }
   }
   
   private static File getWicketComponentDirectory(ServletContext servletContext)
   {
      String path = servletContext.getRealPath(DEFAULT_WICKET_COMPONENT_DIRECTORY_PATH);
      if (path==null) //WebLogic!
      {
         log.debug("Could not find path for " + DEFAULT_WICKET_COMPONENT_DIRECTORY_PATH);
      }
      else
      {
         File wicketComponentDir = new File(path);
         if (wicketComponentDir.exists())
         {
            return wicketComponentDir;
         }
      }
      return null;
   }
   
   private void handleDirectory(File file, String path) throws NotFoundException, CannotCompileException
   {
      log.debug("directory: " + file);
      for ( File child: file.listFiles() )
      {
         String newPath = path==null ? child.getName() : path + '/' + child.getName();
         if ( child.isDirectory() )
         {
            handleDirectory(child, newPath);
         }
         else
         {
            handleItem(newPath);
         }
      }
   }
   
   private void handleItem(String path) throws NotFoundException, CannotCompileException
   {
      if (path.endsWith(".class"))
      {
         String className = filenameToClassname(path); 
         instrumentClass(className, classPool);
      }
   }
   
   protected static String filenameToClassname(String filename)
   {
      return filename.substring( 0, filename.lastIndexOf(".class") )
            .replace('/', '.').replace('\\', '.');
   }
   
   private void instrumentClass(String className, ClassPool classPool) throws NotFoundException, CannotCompileException
   {
      log.debug("Instrumenting " + className);
      CtClass implementation = classPool.get(className);
      CtClass handlerClass = classPool.get(WicketHandler.class.getName());
      
      CtField handlerField = new CtField(handlerClass, "handler", implementation);
      Initializer handlerInitializer = Initializer.byCall(handlerClass, "create");
      implementation.addField(handlerField, handlerInitializer);
      
      CtClass instrumentedComponent = classPool.get(InstrumentedComponent.class.getName());
      implementation.addInterface(instrumentedComponent);
      CtMethod getHandlerMethod = CtNewMethod.getter("getHandler", handlerField);
      CtMethod getEnclosingInstance = CtNewMethod.make("public " + InstrumentedComponent.class.getName() +" getEnclosingInstance() { return " + WicketHandler.class.getName() + ".getEnclosingInstance(this); }", implementation);
      implementation.addMethod(getEnclosingInstance);
      implementation.addMethod(getHandlerMethod);
      
      for (CtMethod method : implementation.getDeclaredMethods())
      {
         if (!Modifier.isStatic(method.getModifiers()))
         {
            String methodName = method.getName();
            if (!("getHandler".equals(method.getName()) || "getEnclosingInstance".equals(method.getName()) ))
            {
               String methodSignature = "";
               for (int i = 0; i < method.getParameterTypes().length; i++)
               {
                  if (i > 0)
                  {
                     methodSignature += ",";
                  }
                  methodSignature += method.getParameterTypes()[i].getName() + ".class";
               }
               String methodCall = "this.getClass().getDeclaredMethod(\""+ methodName + "\", methodParameters)";
               String methodParameters;
               if (methodSignature.length() > 0)
               {
                  methodParameters = "Class[] methodParameters = {" + methodSignature + "};";
               }
               else
               {
                  methodParameters = "Class[] methodParameters = new Class[0];";
               }
               log.trace("Method call: " + methodCall);
               
               method.insertBefore(methodParameters + "handler.beforeInvoke(this, " + methodCall + ");");
               method.insertBefore("handler.setCallInProgress(true);");
               method.insertAfter(methodParameters + "handler.afterInvoke(this, " + methodCall + ");");
               method.insertAfter("handler.setCallInProgress(false);", true);
               log.trace("instrumented method " + method.getName());
            }
         }
      }
      for (CtConstructor constructor : implementation.getConstructors())
      {
         if (constructor.isConstructor())
         {
            constructor.insertBeforeBody("handler.beforeInvoke(this);");
            constructor.insertBeforeBody("handler.setCallInProgress(true);");
            constructor.insertAfter("handler.afterInvoke(this);");
            constructor.insertAfter("handler.setCallInProgress(false);");
            log.trace("instrumented constructor " + constructor.getName());
         }
      }
      classes.add(implementation.getName());
   }
   
   public ClassLoader getClassLoader()
   {
      return classLoader;
   }

}
