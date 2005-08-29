/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.jboss.logging.Logger;
import org.jboss.seam.Seam;
import org.jboss.seam.Component;
import org.jboss.seam.core.JbpmProcess;
import org.jboss.seam.core.JbpmTask;
import org.jboss.seam.core.Init;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Exposes a jbpm variable context instance for reading/writing.
 *
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
public class BusinessProcessContext implements Context {

   private static final Logger log = Logger.getLogger(BusinessProcessContext.class);

   private Map tempContext = new HashMap();
   private Set<String> removed = new HashSet<String>();
   private ContextInstance contextInstance;
   private Init settings;
   private boolean resolvingJbpmContext;

   /**
    * Constructs a new instance of BusinessProcessContext.
    */
   public BusinessProcessContext() {
      settings = Init.instance();
      log.debug( "Begin BusinessProcessContext" );
   }

   /**
    * Retrieves a map of all data needed to recover the current state of this context
    * upon a later request (within the same conversation).
    *
    * @return the recoverable (temp) state
    */
   public Map getRecoverableState() {
      if ( log.isTraceEnabled() )
      {
         log.trace( "recoverable state : " + tempContext );
      }
      return tempContext;
   }

   public void recover(Map previousState) {
      if ( log.isTraceEnabled() ) {
         log.trace( "recovering from state : " + previousState );
      }

      tempContext.putAll( previousState );
   }



   // Context impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

   public Object get(String name) {
      Object value = tempContext.get( name );
      if ( value == null )
      {
         ContextInstance jbpmContext = getJbpmContext();
         if ( jbpmContext != null )
         {
            value = jbpmContext.getVariable( name );
         }
      }
      return value;
   }

   public void set(String name, Object value) {
      tempContext.put( name, value );
   }

   public boolean isSet(String name) {
      if ( tempContext.containsKey( name ) )
      {
         return true;
      }

      ContextInstance jbpmContext = getJbpmContext();
      if ( jbpmContext != null )
      {
         jbpmContext.hasVariable( name );
      }

      return false;
   }

   public void remove(String name) {
      tempContext.remove( name );
      removed.add( name );
   }

   public String[] getNames() {
      Set keys = new HashSet();
      keys.addAll( tempContext.keySet() );

      ContextInstance jbpmContext = getJbpmContext();
      if ( jbpmContext != null )
      {
         keys.addAll( jbpmContext.getVariables().keySet() );
      }

      return ( String[] ) keys.toArray( new String[] {} );
   }


   public Object get(Class clazz) {
      return get( Seam.getComponentName( clazz ) );
   }

   public void flush()
   {
      ContextInstance jbpmContext = getJbpmContext();
      if ( jbpmContext == null )
      {
         log.debug( "no jBPM context to which to flush" );
      }
      else
      {
         log.debug( "flushing in-memory vars to jBPM context [" + jbpmContext.getProcessInstance().getId() + "]" );

         for ( Object entry1 : tempContext.entrySet() )
         {
            final Map.Entry entry = ( Map.Entry ) entry1;
            jbpmContext.setVariable( ( String ) entry.getKey(), entry.getValue() );
         }

         for ( String remove : removed )
         {
            jbpmContext.deleteVariable( remove );
         }

         tempContext.clear();
      }
   }

   private ContextInstance getJbpmContext()
   {
      if ( settings.getJbpmSessionFactoryName() == null ) return null;

      if ( resolvingJbpmContext ) return null;
      resolvingJbpmContext = true;

      if ( contextInstance == null )
      {
         log.trace( "trying to locate jBPM ContextInstance source (task/process)" );
         // try to look up a process in event context first
         ProcessInstance process = ( ProcessInstance ) Component.getInstance( JbpmProcess.class, true );
         if ( process != null )
         {
            contextInstance = process.getContextInstance();
         }
         else
         {
            // else try to look up a task in event context
            TaskInstance task = ( TaskInstance ) Component.getInstance( JbpmTask.class, true );
            if ( task != null )
            {
               contextInstance = task.getTaskMgmtInstance().getProcessInstance().getContextInstance();
            }
         }
      }

      resolvingJbpmContext = false;
      return contextInstance;
   }
}
