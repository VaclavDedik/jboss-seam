package org.jboss.seam.util;

public class JbpmAuthentication {
   public static void pushAuthenticatedActorId(String actorId)
   {
      try
      {
         Class.forName("org.jbpm.security.Authentication").getMethod("pushAuthenticatedActorId", String.class).invoke(null, actorId);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   public static void popAuthenticatedActorId()
   {
      try
      {
         Class.forName("org.jbpm.security.Authentication").getMethod("popAuthenticatedActorId").invoke(null);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
