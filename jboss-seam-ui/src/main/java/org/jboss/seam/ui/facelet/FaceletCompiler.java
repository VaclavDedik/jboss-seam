package org.jboss.seam.ui.facelet;

import static org.jboss.seam.ScopeType.APPLICATION;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.facelets.compiler.SAXCompiler;

@Name("org.jboss.seam.ui.faces.facelet.faceletCompiler")
@Scope(APPLICATION)
@BypassInterceptors
@AutoCreate
@Install(value = true, precedence = Install.BUILT_IN, classDependencies="com.sun.faces.facelets.Facelet")
public class FaceletCompiler
{
   
   private com.sun.faces.facelets.compiler.Compiler compiler;
   
   @Create
   public void create()
   {
	   ApplicationAssociate applicationAssociate = ApplicationAssociate.getCurrentInstance();
	   if (applicationAssociate != null)
	   {
		   compiler = applicationAssociate.getCompiler();
	   }
	   else 
	   {
		   // TODO: this requires to initialize custom tag library
		   compiler = new SAXCompiler();
	   }
   }
     
   
   @Unwrap
   public com.sun.faces.facelets.compiler.Compiler unwrap()
   {
      return compiler;
   }
   
   public static com.sun.faces.facelets.compiler.Compiler instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application scope");
      }
      return (com.sun.faces.facelets.compiler.Compiler) Component.getInstance(FaceletCompiler.class, ScopeType.APPLICATION);
   }
   
}

