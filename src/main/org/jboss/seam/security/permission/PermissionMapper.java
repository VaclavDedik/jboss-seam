package org.jboss.seam.security.permission;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Init;

/**
 * Maps permission checks to resolver chains
 * 
 * @author Shane Bryzak
 */
@Scope(APPLICATION)
@Name("org.jboss.seam.security.permissionMapper")
@Install(precedence = BUILT_IN)
@BypassInterceptors
@Startup
public class PermissionMapper implements Serializable
{
   private Map<Class,Map<String,String>> resolverChains = new HashMap<Class,Map<String,String>>();
   
   private String defaultResolverChain;
   
   private static final String DEFAULT_RESOLVER_CHAIN = "org.jboss.seam.security.defaultResolverChain";
   
   private ResolverChain getResolverChain(Object target, String action)
   {
      Class targetClass = null;
      
      if (target instanceof Class)
      {
         targetClass = (Class) target;
      }
      else
      {
         // TODO target may be a component name, or an object, or a view name (or arbitrary name) -
         // we need to deal with all of these possibilities
      }
      
      if (targetClass != null)
      {
         Map<String,String> chains = resolverChains.get(target);
         if (chains != null && chains.containsKey(action))
         {
            return (ResolverChain) Component.getInstance(chains.get(action), true);
         }
      }      
      
      if (defaultResolverChain != null && !"".equals(defaultResolverChain))
      {
         return (ResolverChain) Component.getInstance(defaultResolverChain, true);   
      }
      
      return createDefaultResolverChain();
   }   
   
   public boolean resolvePermission(Object target, String action)
   {
      ResolverChain chain = getResolverChain(target, action);
      for (PermissionResolver resolver : chain.getResolvers())
      {
         if (resolver.hasPermission(target, action))
         {
            return true;
         }
      }
      
      return false;
   }   
   
   private ResolverChain createDefaultResolverChain()
   {
      ResolverChain chain = (ResolverChain) Contexts.getSessionContext().get(DEFAULT_RESOLVER_CHAIN);
      
      if (chain == null)
      {
         chain = new ResolverChain();
         
         for (String resolverName : Init.instance().getPermissionResolvers())
         {
            chain.getResolvers().add((PermissionResolver) Component.getInstance(resolverName, true)); 
         }
         
         Contexts.getSessionContext().set(DEFAULT_RESOLVER_CHAIN, chain);
      }
      
      return chain;
   }

   public static PermissionMapper instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }
   
      PermissionMapper instance = (PermissionMapper) Component.getInstance(
            PermissionMapper.class, ScopeType.APPLICATION);
   
      if (instance == null)
      {
         throw new IllegalStateException("No PermissionMapper could be created");
      }
   
      return instance;
   }
}
