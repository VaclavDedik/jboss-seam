package org.jboss.seam.deployment;

/**
 * An accelerated version of the underlying strategy that uses the SimpleURLScanner
 * to determine the timestamp of the latest file.
 * 
 * @author Dan Allen
 */
public abstract class TimestampCheckStrategy extends DeploymentStrategy
{
   private Scanner scanner;

   @Override
   public ClassLoader getClassLoader()
   {
      return getDelegateStrategy().getClassLoader();
   }

   @Override
   protected String getDeploymentHandlersKey()
   {
      return getDelegateStrategy().getDeploymentHandlersKey();
   }
   
   public abstract DeploymentStrategy getDelegateStrategy();

   public boolean changedSince(long mark)
   {
      scan();
      return getTimestamp() > mark;
   }

   @Override
   public Scanner getScanner()
   {
      if (scanner == null)
      {
         initScanner();
      }
      return scanner;
   }

   protected void initScanner()
   {
      scanner = new TimestampURLScanner(this);
   }

}