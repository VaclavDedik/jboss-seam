//$Id$
package org.jboss.seam.annotations;

public class Outcome
{
   /**
    * Annotations may not specify a null String. This
    * value lets us specify a null outcome in an
    * annotation such as @Invalid
    */
   public static final String REDISPLAY = "org.jboss.seam.outcome.null";
}
