package org.jboss.seam.deployment;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.MemberValue;

/**
 * @author Pete Muir
 *
 */
public abstract class AbstractDeploymentHandler implements DeploymentHandler
{
   
   public static String filenameToClassname(String filename)
   {
      return filename.substring( 0, filename.lastIndexOf(".class") )
            .replace('/', '.').replace('\\', '.');
   }
   
   protected ClassFile getClassFile(String name, ClassLoader classLoader) throws IOException 
   {
      InputStream stream = classLoader.getResourceAsStream(name);
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
   
   protected boolean hasAnnotation(ClassFile cf, Class<? extends Annotation> annotationType)
   { 
      AnnotationsAttribute visible = (AnnotationsAttribute) cf.getAttribute( AnnotationsAttribute.visibleTag ); 
      if ( visible != null ) 
      {
         return visible.getAnnotation( annotationType.getName() ) != null; 
      } 
      return false; 
   }
   
   protected String getAnnotationValue(ClassFile cf, Class<? extends Annotation> annotationType, String memberName)
   { 
      AnnotationsAttribute visible = (AnnotationsAttribute) cf.getAttribute( AnnotationsAttribute.visibleTag ); 
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
   
   public static String filenameToPackage(String filename)
   {
      return filename.substring( 0, filename.lastIndexOf(".class") )
            .replace('/', '.').replace('\\', '.');
   }
  

}
