package org.jboss.seam.wicket.ioc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
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
      if (isInstrumentable(implementation))
      {
         CtClass handlerClass = classPool.get(WicketHandler.class.getName());
         
         CtField handlerField = new CtField(handlerClass, "handler", implementation);
         Initializer handlerInitializer = Initializer.byCall(handlerClass, "create");
         implementation.addField(handlerField, handlerInitializer);
         
         CtClass exception = classPool.get(Exception.class.getName());
         
         CtClass instrumentedComponent = classPool.get(InstrumentedComponent.class.getName());
         implementation.addInterface(instrumentedComponent);
         CtMethod getHandlerMethod = CtNewMethod.getter("getHandler", handlerField);
         CtMethod getEnclosingInstance = CtNewMethod.make("public " + InstrumentedComponent.class.getName() +" getEnclosingInstance() { return handler == null ? null : handler.getEnclosingInstance(this); }", implementation);
         implementation.addMethod(getEnclosingInstance);
         implementation.addMethod(getHandlerMethod);
         
         for (CtMethod method : implementation.getDeclaredMethods())
         {
            if (!Modifier.isStatic(method.getModifiers()))
            {
               if (!("getHandler".equals(method.getName()) || "getEnclosingInstance".equals(method.getName())))
               {                  
                  String newName = implementation.makeUniqueName(method.getName());
                  
                  CtMethod newMethod = CtNewMethod.copy(method, newName, implementation, null);
                  newMethod.setModifiers(Modifier.PRIVATE);
                  implementation.addMethod(newMethod);
                  method.setBody(createBody(implementation, method, newMethod));
                  log.trace("instrumented method " + method.getName());
               }
            }
         }
         for (CtConstructor constructor : implementation.getConstructors())
         {
            if (constructor.isConstructor())
            {
               {
                  String constructorObject = createConstructorObject(className,constructor);
                  constructor.insertBeforeBody(constructorObject + "handler.beforeInvoke(this, constructor);");
                  constructor.addCatch("{" + constructorObject + "throw new RuntimeException(handler.handleException(this, constructor, e));}", exception, "e");
                  constructor.insertAfter(constructorObject + "handler.afterInvoke(this, constructor);");
                  log.trace("instrumented constructor " + constructor.getName());
               }
            }
         }
      }
      classes.add(implementation.getName());
     
   }
   
   public ClassLoader getClassLoader()
   {
      return classLoader;
   }
   
   private static String createBody(CtClass clazz, CtMethod method, CtMethod newMethod) throws NotFoundException
   {
      String src = "{" + createMethodObject(method) + "if (this.handler != null) this.handler.beforeInvoke(this, method);" + createMethodDelegation(newMethod) + "if (this.handler != null) result = ($r) this.handler.afterInvoke(this, method, ($w) result); return ($r) result;}";

      log.trace("Creating method " + clazz.getName() + "." + newMethod.getName() + "(" + newMethod.getSignature() + ")" + src);
      return src;
   }
   
   private static String createMethodDelegation(CtMethod method) throws NotFoundException
   {
      CtClass returnType = method.getReturnType(); 
      if (returnType.equals(CtClass.voidType))
      {
         return "Object result = null; " + wrapInExceptionHandler(method.getName() + "($$);");
      } 
      else
      {
         String src = returnType.getName() + " result;";
         src += wrapInExceptionHandler("result = " + method.getName() + "($$);");
         return src;
      }
   }
   
   private static String wrapInExceptionHandler(String src)
   {
      return "try {" + src + "} catch (Exception e) { throw new RuntimeException(this.handler == null ? e : this.handler.handleException(this, method, e)); }";
   }
   
   private static String createParameterTypesArray(CtBehavior behavior) throws NotFoundException
   {
      String src = "Class[] parameterTypes = new Class[" + behavior.getParameterTypes().length + "];";
      for (int i = 0; i < behavior.getParameterTypes().length; i++)
      {
         src += "parameterTypes[" + i + "] = " + behavior.getParameterTypes()[i].getName() + ".class;"; 
      }
      return src;
   }
   
   private static String createMethodObject(CtMethod method) throws NotFoundException
   {
      String src = createParameterTypesArray(method);
      src += "java.lang.reflect.Method method = this.getClass().getDeclaredMethod(\""+ method.getName() + "\", parameterTypes);";
      return src;
   }
   
   private static String createConstructorObject(String className, CtConstructor constructor) throws NotFoundException
   {
      String src = createParameterTypesArray(constructor);
      src += "java.lang.reflect.Constructor constructor = " + className + ".class.getDeclaredConstructor(parameterTypes);";
      return src;
   }

   private static boolean isInstrumentable(CtClass clazz)
   {
      int modifiers = clazz.getModifiers();
      return !(Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers) || Modifier.isEnum(modifiers));
   }
   
}
