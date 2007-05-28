package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.persistence.PersistenceProvider;

@Name("org.jboss.seam.core.persistenceContexts")
@Scope(ScopeType.CONVERSATION)
@Intercept(NEVER)
@Install(precedence=BUILT_IN)
public class PersistenceContexts extends AbstractMutable implements Serializable
{
   private static final long serialVersionUID = -4897350516435283182L;
   private Set<String> set = new HashSet<String>();
   private FlushModeType flushMode = FlushModeType.AUTO;
   private FlushModeType actualFlushMode = FlushModeType.AUTO;
 
   public FlushModeType getFlushMode()
   {
      return flushMode;
   }
   
   public Set<String> getTouchedContexts()
   {
      return Collections.unmodifiableSet(set);
   }
   
   public void touch(String context)
   {
      if ( set.add(context) ) setDirty();
   }
   
   public void untouch(String context)
   {
      if ( set.remove(context) ) setDirty();
   }
   
   public static PersistenceContexts instance()
   {
      if ( Contexts.isConversationContextActive() )
      {
         return (PersistenceContexts) Component.getInstance(PersistenceContexts.class);
      }
      else
      {
         return null;
      }
   }
   
   public void changeFlushMode(FlushModeType flushMode)
   {
      this.flushMode = flushMode;
      this.actualFlushMode = flushMode;
      changeFlushModes();   
   }

   private void changeFlushModes()
   {
      for (String name: set)
      {
         PersistenceContextManager pcm = (PersistenceContextManager) Contexts.getConversationContext().get(name);
         if (pcm!=null)
         {
            pcm.changeFlushMode(flushMode);
         }
      }
   }
   
   public void beforeRender()
   {
      PersistenceProvider pp = PersistenceProvider.instance();
      flushMode = pp==null ? FlushModeType.MANUAL : pp.getRenderFlushMode();
      changeFlushModes();
   }
   
   public void afterRender()
   {
      flushMode = actualFlushMode;
      changeFlushModes();
   }
   
}
