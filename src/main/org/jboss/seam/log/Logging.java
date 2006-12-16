package org.jboss.seam.log;

public class Logging
{
   
   private static final boolean isLog4JAvailable;
   
   static
   {
      boolean available;
      try
      {
         Class.forName("org.apache.log4j.Logger");
         available = true;
      }
      catch (ClassNotFoundException cnfe)
      {
         available = false;
      }
      isLog4JAvailable = available;
   }
   
   public static Log getLog(String category)
   {
      return new LogImpl(category);
   }
   
   public static Log getLog(Class clazz)
   {
      return new LogImpl( clazz.getName() );
   }
   
   public static LogProvider getLogProvider(String category)
   {
      return isLog4JAvailable ? 
               new Log4JProvider(category) : 
               new JDKProvider(category);
   }
   
}
