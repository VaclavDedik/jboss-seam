package org.jboss.seam.security.permission;

import static org.jboss.seam.ScopeType.SESSION;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Scope(SESSION)
@BypassInterceptors
public class ResolverChain
{
   private List<PermissionResolver> resolvers = new ArrayList<PermissionResolver>();
   
   public List<PermissionResolver> getResolvers()
   {
      return resolvers;
   }
   
   public void setResolvers(List<PermissionResolver> resolvers)
   {
      this.resolvers = resolvers;
   }   
}
