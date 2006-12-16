package org.jboss.seam.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public final class Log4JProvider implements LogProvider
{
   private final Logger logger;
   
   private static final String FQCN = LogImpl.class.getName();
   
   Log4JProvider(String category)
   {
      logger = Logger.getLogger(category);
   }
   
   public void debug(Object object)
   {
      logger.log(FQCN, Level.DEBUG, object, null);
   }

   public void debug(Object object, Throwable t)
   {
      logger.log(FQCN, Level.DEBUG, object, t);
   }

   public void error(Object object)
   {
      logger.log(FQCN, Level.ERROR, object, null);
   }

   public void error(Object object, Throwable t)
   {
      logger.log(FQCN, Level.ERROR, object, t);
   }

   public void fatal(Object object)
   {
      logger.log(FQCN, Level.FATAL, object, null);
   }

   public void fatal(Object object, Throwable t)
   {
      logger.log(FQCN, Level.DEBUG, object, t);
   }

   public void info(Object object)
   {
      logger.log(FQCN, Level.INFO, object, null);
   }

   public void info(Object object, Throwable t)
   {
      logger.log(FQCN, Level.INFO, object, t);
   }

   public boolean isDebugEnabled()
   {
      return logger.isEnabledFor(Level.DEBUG);
   }

   public boolean isErrorEnabled()
   {
      return logger.isEnabledFor(Level.ERROR);
   }

   public boolean isFatalEnabled()
   {
      return logger.isEnabledFor(Level.FATAL);
   }

   public boolean isInfoEnabled()
   {
      return logger.isEnabledFor(Level.INFO);
   }

   public boolean isTraceEnabled()
   {
      return logger.isEnabledFor(Level.DEBUG);
   }

   public boolean isWarnEnabled()
   {
      return logger.isEnabledFor(Level.WARN);
   }

   public void trace(Object object)
   {
      logger.log(FQCN, Level.DEBUG, object, null);
   }

   public void trace(Object object, Throwable t)
   {
      logger.log(FQCN, Level.DEBUG, object, t);
   }

   public void warn(Object object)
   {
      logger.log(FQCN, Level.WARN, object, null);
   }

   public void warn(Object object, Throwable t)
   {
      logger.log(FQCN, Level.WARN, object, t);
   }

}
