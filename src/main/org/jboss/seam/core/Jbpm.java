package org.jboss.seam.core;

import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Intercept;
import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.InterceptionType.NEVER;
import org.jboss.seam.Component;
import org.jboss.seam.core.ManagedJbpmSession;
import org.jbpm.db.JbpmSession;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
@Name( "jbpm" )
@Scope( EVENT )
@Intercept( NEVER )
public class Jbpm
{
   /**
    * Exposes a users task list as a map to be more easily usable within
    * JSF EL.  Only the {@link java.util.Map#get} method is supported, where the
    * given key is expected to be the actorId for which to retreive
    * the task list: jbpmSession.taskLists['admin']
    *
    * @return the sorta map :)
    */
   public Map<String, List> getTaskLists()
   {
      return new Map<String, List>()
      {

         public List get(Object key)
         {
            return getTaskInstanceList( ( String ) key );
         }

         public int size()
         {
            throw new UnsupportedOperationException( "only a map to workaround jsf el limitation..." );
         }

         public boolean isEmpty()
         {
            throw new UnsupportedOperationException( "only a map to workaround jsf el limitation..." );
         }

         public boolean containsKey(Object key)
         {
            throw new UnsupportedOperationException( "only a map to workaround jsf el limitation..." );
         }

         public boolean containsValue(Object value)
         {
            throw new UnsupportedOperationException( "only a map to workaround jsf el limitation..." );
         }

         public List put(String key, List value)
         {
            throw new UnsupportedOperationException( "only a map to workaround jsf el limitation..." );
         }

         public List remove(Object key)
         {
            throw new UnsupportedOperationException( "only a map to workaround jsf el limitation..." );
         }

         public void putAll(Map<? extends String, ? extends List> t)
         {
            throw new UnsupportedOperationException( "only a map to workaround jsf el limitation..." );
         }

         public void clear()
         {
            throw new UnsupportedOperationException( "only a map to workaround jsf el limitation..." );
         }

         public Set<String> keySet()
         {
            throw new UnsupportedOperationException( "only a map to workaround jsf el limitation..." );
         }

         public Collection<List> values()
         {
            throw new UnsupportedOperationException( "only a map to workaround jsf el limitation..." );
         }

         public Set<Entry<String, List>> entrySet()
         {
            throw new UnsupportedOperationException( "only a map to workaround jsf el limitation..." );
         }
      };

   }

   public List getTaskInstanceList(String username)
   {
      if ( username == null )
      {
         return null;
      }
      // TODO: in a real ootb component we'd also need to consider any pooled tasks
      // associated with the user...
      JbpmSession jbpmSession = ( JbpmSession ) Component.getInstance( ManagedJbpmSession.class, true );
      return jbpmSession.getTaskMgmtSession().findTaskInstances( username );
   }
}
