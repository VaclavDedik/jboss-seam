//$Id$
package org.jboss.seam;

/**
 * Rules for when Seam will intercept invocations upon a
 * component to perform bijection, validation, context
 * demarcation, etc. For JavaBeans and session beans, the 
 * default interception type is <tt>INVOKE_APPLICATION</tt>.
 * All entity beans have interception type <tt>NEVER</tt>.
 * 
 * @author Gavin King
 */
public enum InterceptionType
{
   /**
    * Seam never intercepts this invocations upon this component
    */
   NEVER,
   /**
    * Seam intercepts invocations upon this component only during
    * the invoke application phase
    */
   INVOKE_APPLICATION,
   /**
    * Seam always intercepts invocations upon this component
    */
   ALWAYS
}
