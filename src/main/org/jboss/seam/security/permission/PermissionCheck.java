package org.jboss.seam.security.permission;

/**
 * Used to assert permission requirements into a WorkingMemory when evaluating
 * a @Restrict expression.  The consequence of the rule is responsible for
 * granting the permission.
 *
 * @author Shane Bryzak
 */
public class PermissionCheck
{
   private Object target;

   @Deprecated
   private String name;

   private String action;
   private boolean granted;
   
   public PermissionCheck(Object target, String action)
   {
      if (target instanceof String)
      {
         this.name = (String) target;
      }
      
      this.target = target;
      this.action = action;
      granted = false;
   }
   
   public Object getTarget()
   {
      return target;
   }   

   @Deprecated
   public String getName() 
   {
      return name;
   }

   public String getAction() 
   {
      return action;
   }

   public void grant() 
   {
      this.granted = true;
   }

   public void revoke() 
   {
      this.granted = false;
   }

   public boolean isGranted() 
   {
      return granted;
   }
}
