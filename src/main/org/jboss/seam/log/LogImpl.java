package org.jboss.seam.log;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.jboss.seam.core.Interpolator;

/**
 * Implementation of the Log interface using commons logging.
 * 
 * @author Gavin King
 */
public class LogImpl implements Log, Serializable
{
   
   private transient org.apache.commons.logging.Log log;

   public LogImpl(Class clazz)
   {
      this.log = LogFactory.getLog(clazz);
   }

   public LogImpl(String category)
   {
      this.log = LogFactory.getLog(category);
   }

   public boolean isDebugEnabled()
   {
      return log.isDebugEnabled();
   }

   public boolean isErrorEnabled()
   {
      return log.isErrorEnabled();
   }

   public boolean isFatalEnabled()
   {
      return log.isFatalEnabled();
   }

   public boolean isInfoEnabled()
   {
      return log.isInfoEnabled();
   }

   public boolean isTraceEnabled()
   {
      return log.isTraceEnabled();
   }

   public boolean isWarnEnabled()
   {
      return log.isWarnEnabled();
   }

   public void trace(Object object, Object... params)
   {
      if ( isTraceEnabled() )
      {
         log.trace(  interpolate(object, params) );
      }
   }

   public void trace(Object object, Throwable t, Object... params)
   {
      if ( isTraceEnabled() )
      {
         log.trace(  interpolate(object, params), t );
      }
   }

   public void debug(Object object, Object... params)
   {
      if ( isDebugEnabled() )
      {
         log.debug(  interpolate(object, params) );
      }
   }

   public void debug(Object object, Throwable t, Object... params)
   {
      if ( isDebugEnabled() )
      {
         log.debug(  interpolate(object, params), t );
      }
   }

   public void info(Object object, Object... params)
   {
      if ( isInfoEnabled() )
      {
         log.info( interpolate(object, params) );
      }
   }

   public void info(Object object, Throwable t, Object... params)
   {
      if ( isInfoEnabled() )
      {
         log.info( interpolate(object, params), t );
      }
   }

   public void warn(Object object, Object... params)
   {
      if ( isWarnEnabled() )
      {
         log.warn( interpolate(object, params) );
      }
   }

   public void warn(Object object, Throwable t, Object... params)
   {
      if ( isWarnEnabled() )
      {
         log.warn( interpolate(object, params), t );
      }
   }

   public void error(Object object, Object... params)
   {
      if ( isErrorEnabled() )
      {
         log.error( interpolate(object, params) );
      }
   }

   public void error(Object object, Throwable t, Object... params)
   {
      if ( isErrorEnabled() )
      {
         log.error( interpolate(object, params), t );
      }
   }

   public void fatal(Object object, Object... params)
   {
      if ( isFatalEnabled() )
      {
         log.fatal( interpolate(object, params) );
      }
   }

   public void fatal(Object object, Throwable t, Object... params)
   {
      if ( isFatalEnabled() )
      {
         log.fatal( interpolate(object, params), t );
      }
   }
   
   private Object interpolate(Object object, Object... params)
   {
      if (object instanceof String)
      {
         return Interpolator.instance().interpolate( (String) object, params );
      }
      else
      {
         return object;
      }
   }

}
