package org.jboss.seam.ui;

import java.util.PriorityQueue;
import java.util.Queue;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * This class provides a chainable converter for JSF.
 * 
 * A converter can be placed in the chain with a priority, the order in which
 * converters with the same priority is run is not specified.
 * 
 * The chain will be processed in a priority order, with the getAs{String,Object},
 * with the result of each conversion being piped to the next.  Every converter
 * will be run during getAsString, during getAsObject converters will be run until
 * the result of conversion is not a string
 * 
 */
public class ConverterChain implements Converter, StateHolder
{

	/**
	 * Implementation of a prioritizable converter
	 * Uses an int to indicate priority of the converter
	 *
	 */
   private class PrioritizableConverter implements Converter, Comparable<PrioritizableConverter>, StateHolder
   {

      private Converter delegate;

      private int priority;

      public PrioritizableConverter(Converter delegate, int priority)
      {
         this.delegate = delegate;
         this.priority = priority;
      }

      public Converter getDelegate()
      {
         return delegate;
      }

      public int getPriority()
      {
         return priority;
      }
      
      public Object getAsObject(FacesContext context, UIComponent component, String value)
               throws ConverterException
      {
         return delegate.getAsObject(context, component, value);
      }

      public String getAsString(FacesContext context, UIComponent component, Object value)
               throws ConverterException
      {
         return delegate.getAsString(context, component, value);
      }

      public int compareTo(PrioritizableConverter o)
      {
         return this.getPriority() - o.getPriority();
      }
      
      /*
       **********************************
       * Implementation of StateHolder             *
       ********************************** 
       */
      
      private boolean _transient;

      public boolean isTransient()
      {
         return _transient;
      }

      public void restoreState(FacesContext context, Object state)
      {
         Object[] values = (Object[] ) state;
         delegate = (Converter) UIComponentBase.restoreAttachedState(context,values[0]);
         priority = (Integer) values[1];
         
      }

      public Object saveState(FacesContext context)
      {
        Object[] values = new Object[2];
        values[0] = UIComponentBase.saveAttachedState(context, delegate);
        values[1] = priority;
        return converters;
      }

      public void setTransient(boolean newTransientValue)
      {
        this._transient = newTransientValue;
         
      }
   }

   /**
    * This places the converter at the end of the chain.  
    * No garuntee is made about the order converters which are placed
    * on the queue with this priority will be run
    */
   public static final int CHAIN_END = Integer.MAX_VALUE;

   /**
    * This places the converter at the head of the chain.  
    * No garuntee is made about the order converters which are placed
    * on the queue with this priority will be run
    */
   public static final int CHAIN_START = Integer.MIN_VALUE;

   private Queue<PrioritizableConverter> converters;

   
   public ConverterChain()
   {
      converters = new PriorityQueue<PrioritizableConverter>();
   }

   /**
    * Set up a ConverterChain for this component.
    * 
    * This replaces any existing converter with a ConverterChain
    * with the existing Converter at the end of the chain
    * @param component
    */
   public ConverterChain(UIComponent component)
   {
      this();
      if (component instanceof ValueHolder)
      {
         ValueHolder valueHolder = (ValueHolder) component;
         addConverterToChain(valueHolder.getConverter());
         valueHolder.setConverter(this);
      }
   }

   public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException
   {
      Object result = null;
      for (Converter converter : converters)
      {
         result = converter.getAsObject(context, component, value);
         // We can only process more converters if we still have a string
         if (!(result instanceof String))
         {
            break;
         }
         else
         {
            value = result.toString();
         }
      }
      return result;
   }

   public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException
   {
      for (Converter converter : converters)
      {
         value = converter.getAsString(context, component, value);
         if (value instanceof String)
         {
            break;
         }
      }
      if (value == null)
      {
         return null;
      }
      else
      {
         return value.toString();
      }
   }

   /**
    * Add a converter to the end of the chain
    */
   public boolean addConverterToChain(Converter c)
   {
      return addConverterToChain(c, CHAIN_END);
   }

   /**
    * Add a converter to the chain with a defined priority
    */
   public boolean addConverterToChain(Converter c, int priority)
   {
      if (c != null)
      {
         return converters.add(new PrioritizableConverter(c, priority));
      }
      else
      {
         return false;
      }
   }

   /*
    **********************************
    * Implementation of StateHolder             *
    ********************************** 
    */
   
   private boolean _transient;
   
   public boolean isTransient()
   {
     return _transient;
   }

   public void restoreState(FacesContext context, Object state)
   {
      Object[] values = (Object[] ) state;
      converters = (Queue<PrioritizableConverter>) UIComponentBase.restoreAttachedState(context,values[0]);
      
   }

   public Object saveState(FacesContext context)
   {
     Object[] values = new Object[1];
     values[0] = UIComponentBase.saveAttachedState(context, converters);
     return values;
   }

   public void setTransient(boolean newTransientValue)
   {
      this._transient = newTransientValue;
      
   }

}
