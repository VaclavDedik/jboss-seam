package org.jboss.seam.wicket.ioc;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.CtField.Initializer;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.wicket.WicketComponent;


public class JavassistInstrumentor
{
   
   private static LogProvider log = Logging.getLogProvider(JavassistInstrumentor.class);
   
   private ClassPool classPool;
   
   public JavassistInstrumentor(ClassPool classPool)
   {
       this.classPool = classPool;
   }
  
   public CtClass instrumentClass(String className) throws NotFoundException, CannotCompileException
   {
      log.debug("Instrumenting " + className);
      CtClass implementation = classPool.get(className);
      if (isInstrumentable(implementation))
      {
         CtClass handlerClass = classPool.get(WicketHandler.class.getName());
         CtClass componentClass = classPool.get(WicketComponent.class.getName());
         
         CtField handlerField = new CtField(handlerClass, "handler", implementation);
         Initializer handlerInitializer = Initializer.byCall(handlerClass, "create");
         implementation.addField(handlerField, handlerInitializer);
         
         CtField wicketComponentField = new CtField(componentClass,"component",implementation);
         wicketComponentField.setModifiers(Modifier.STATIC);
         Initializer componentInit = Initializer.byExpr("new org.jboss.seam.wicket.WicketComponent(" + className + ".class)");
         implementation.addField(wicketComponentField,componentInit);


         CtClass exception = classPool.get(Exception.class.getName());
         
         CtClass instrumentedComponent = classPool.get(InstrumentedComponent.class.getName());
         implementation.addInterface(instrumentedComponent);
         CtMethod getHandlerMethod = CtNewMethod.getter("getHandler", handlerField);
         CtMethod getEnclosingInstance = CtNewMethod.make("public " + InstrumentedComponent.class.getName() +" getEnclosingInstance() { return handler == null ? null : handler.getEnclosingInstance(this); }", implementation);
         implementation.addMethod(getEnclosingInstance);
         implementation.addMethod(getHandlerMethod);
         
         for (CtMethod method : implementation.getDeclaredMethods())
         {
            if (!Modifier.isStatic(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers()))
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
      return implementation;
   }
   
   
   private static String createBody(CtClass clazz, CtMethod method, CtMethod newMethod) throws NotFoundException
   {
      String src = "{" + createMethodObject(clazz,method) + "if (this.handler != null) this.handler.beforeInvoke(this, method);" + createMethodDelegation(newMethod) + "if (this.handler != null) result = ($r) this.handler.afterInvoke(this, method, ($w) result); return ($r) result;}";

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
   
   private static String createMethodObject(CtClass clazz, CtMethod method) throws NotFoundException
   {
      String src = createParameterTypesArray(method);
      src += "java.lang.reflect.Method method = " + clazz.getName() +".class.getDeclaredMethod(\""+ method.getName() + "\", parameterTypes);";
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
      if (Modifier.isInterface(modifiers) || Modifier.isEnum(modifiers))
      {
         return false;
      }
      
      try 
      { 
	      for (Object a : clazz.getAnnotations())
	      {
	         if (a instanceof Name)
	         {
	            return false;
	         }
	      }
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }
      return true;
   }
   
}
