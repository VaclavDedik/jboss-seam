package org.jboss.seam.deployment;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.MemberValue;

/**
 * Abstract base class for {@link DeploymentHandler} providing common functionality
 * 
 * @author Pete Muir
 *
 */
public abstract class AbstractDeploymentHandler implements DeploymentHandler
{
   /**
    * Convert a path to a class file to a class name
    */
   protected static String filenameToClassname(String filename)
   {
      return filename.substring( 0, filename.lastIndexOf(".class") )
            .replace('/', '.').replace('\\', '.');
   }
   
   /**
    * Get a Javassist {@link ClassFile} for a given class name from the classLoader
    */
   protected ClassFile getClassFile(String name, ClassLoader classLoader) throws IOException 
   {
      if (name == null)
      {
         throw new NullPointerException("name cannot be null");
      }
      InputStream stream = classLoader.getResourceAsStream(name);
      if (stream == null)
      {
         throw new IllegalStateException("Cannot load " + name + " from " + classLoader + " (using getResourceAsStream() returned null)");
      }
      DataInputStream dstream = new DataInputStream(stream);

      try 
      { 
         return new ClassFile(dstream); 
      } 
      finally 
      { 
         dstream.close(); 
         stream.close(); 
      }
   }
   
   /**
    * Check if the Javassist {@link ClassFile} has the specfied annotation
    */
   protected boolean hasAnnotation(ClassFile classFile, Class<? extends Annotation> annotationType)
   { 
      AnnotationsAttribute visible = (AnnotationsAttribute) classFile.getAttribute( AnnotationsAttribute.visibleTag ); 
      if ( visible != null ) 
      {
         return visible.getAnnotation( annotationType.getName() ) != null; 
      } 
      return false; 
   }
   
   /**
    * Get the value of the annotation on the Javassist {@link ClassFile}, or null
    * if the class doesn't have that annotation
    */
   protected String getAnnotationValue(ClassFile classFile, Class<? extends Annotation> annotationType, String memberName)
   { 
      AnnotationsAttribute visible = (AnnotationsAttribute) classFile.getAttribute( AnnotationsAttribute.visibleTag ); 
      if ( visible != null ) 
      {
         javassist.bytecode.annotation.Annotation annotation = visible.getAnnotation( annotationType.getName() );
         if (annotation==null)
         {
            return null;
         }
         else
         {
            MemberValue memberValue = annotation.getMemberValue(memberName);
            return memberValue==null ? null : memberValue.toString(); //TODO: toString() here is probably Bad ;-)
         }
      }
      else
      {
         return null;
      }
   }
   

   @Override
   public String toString()
   {
      return getName();
   }  

}
