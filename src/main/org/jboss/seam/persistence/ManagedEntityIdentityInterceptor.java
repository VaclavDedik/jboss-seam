package org.jboss.seam.persistence;

import static org.jboss.seam.ScopeType.CONVERSATION;

import org.jboss.seam.annotations.intercept.AroundInvoke;
import org.jboss.seam.annotations.intercept.Interceptor;
import org.jboss.seam.core.BijectionInterceptor;
import org.jboss.seam.intercept.AbstractInterceptor;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.transaction.Transaction;

/**
 * Swizzles entity references around each invocation, maintaining
 * referential integrity even across passivation of the stateful 
 * bean or Seam-managed extended persistence context, and allowing 
 * for more efficient replication.
 * 
 * @author Gavin King
 *
 */
@Interceptor(around=BijectionInterceptor.class)
public class ManagedEntityIdentityInterceptor extends AbstractInterceptor
{
    
    private static ManagedEntityStateManager managedEntityStateManager = new ManagedEntityStateManager();
   
    private boolean reentrant;
    //TODO: cache the non-ignored fields, probably on Component
    
    public boolean isInterceptorEnabled()
    {
       return getComponent().getScope() == CONVERSATION;
    }
   
    @AroundInvoke
    public Object aroundInvoke(InvocationContext ctx) throws Exception
    {
        if (reentrant) {
            return ctx.proceed();
        } else {
            reentrant = true;
            managedEntityStateManager.entityIdsToRefs(ctx.getTarget(), getComponent());
            try  {
                return ctx.proceed();
            } finally {
                if (!isTransactionRolledBackOrMarkedRollback()) {
                    managedEntityStateManager.entityRefsToIds(ctx.getTarget(), getComponent());
                    reentrant = false;
                }
            }
        }
    }
    
   private static boolean isTransactionRolledBackOrMarkedRollback()
   {
      try
      {
         return Transaction.instance().isRolledBackOrMarkedRollback();
      }
      catch (Exception e)
      {
         return false;
      }
   }
   
   
}
