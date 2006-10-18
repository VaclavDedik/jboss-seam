package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Name("org.jboss.seam.core.safeActions")
public class SafeActions
{
   
   private Set<String> safeActions = Collections.synchronizedSet( new HashSet<String>() );
   
   public static String toActionId(String viewId, String expression)
   {
      return viewId.substring(1) + ':' + expression.substring( 2, expression.length()-1 );
   }
   
   public static String toAction(String id)
   {
      int loc = id.indexOf(':');
      if (loc<0) throw new IllegalArgumentException();
      return "#{" + id.substring(loc+1) + "}";
   }
   
   public void addSafeAction(String id)
   {
      safeActions.add(id);
   }
   
   public boolean isActionSafe(String id)
   {
      if ( safeActions.contains(id) ) return true;
      
      int loc = id.indexOf(':');
      if (loc<0) throw new IllegalArgumentException();
      String viewId = id.substring(0, loc);
      String action = "\"#{" + id.substring(loc+1) + "}\"";
      
      InputStream is = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(viewId);
      if (is==null) throw new IllegalStateException();
      BufferedReader reader = new BufferedReader( new InputStreamReader(is) );
      try
      {
         while ( reader.ready() ) 
         {
            if ( reader.readLine().contains(action) ) 
            {
               addSafeAction(id);
               return true;
            }
         }
         return false;
      }
      catch (IOException ioe)
      {
         throw new RuntimeException(ioe);
      }
      finally
      {
         try
         {
            reader.close();
         }
         catch (IOException ioe) {
            throw new RuntimeException(ioe);
         }
      }
   }
   
   public static SafeActions instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }
      return (SafeActions) Component.getInstance(SafeActions.class, ScopeType.APPLICATION, true);
   }
   
}
