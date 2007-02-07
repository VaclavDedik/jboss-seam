package org.jboss.seam.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.el.ValueBinding;

/**
 * This class provides a chainable converter for JSF.
 * 
 * This has been done to support noSelections on s:selectItems.
 * 
 * Converters that are not first/last (the converter specfied on the component)
 *  need careful implementation
 * 
 * A converter can be placed in the chain with a priority, the order in which
 * converters with the same priority is run is not specified.
 * 
 * The chain will be processed in ascending order for getAsString, descending order
 * for getAsObject
 * 
 */
public class ConverterChain implements Converter, StateHolder
{

   /**
    * This places the converter at the end of the chain. No garuntee is made
    * about the order converters which are placed on the queue with this
    * priority will be run
    */
   public static final int CHAIN_END = Integer.MAX_VALUE;

   /**
    * This places the converter at the head of the chain. No garuntee is made
    * about the order converters which are placed on the queue with this
    * priority will be run
    */
   public static final int CHAIN_START = 0;

   private List<PrioritizableConverter> converters;

   public ConverterChain()
   {
      // A Priority Queue would be nice but JSF has issues serializing that
      converters = new ArrayList<PrioritizableConverter>();
   }

   /**
    * Set up a ConverterChain for this component.
    * 
    * This replaces any existing converter with a ConverterChain with the
    * existing Converter at the end of the chain
    * 
    * @param component
    */
   public ConverterChain(UIComponent component)
   {
      this();
      if (component instanceof ValueHolder)
      {
         ValueHolder valueHolder = (ValueHolder) component;
         ValueBinding vb =component.getValueBinding("converter");
         if (vb != null) {
            addConverterToChain(vb);
         } else {
            addConverterToChain(valueHolder.getConverter());
         }
         valueHolder.setConverter(this);
      }
   }

   public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException
   {
      Object result = null;
      Collections.sort(converters);
      Collections.reverse(converters);
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
      Collections.sort(converters);
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
    * Add a converter to the end of the chain
    */
   public boolean addConverterToChain(ValueBinding c)
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
   
   /**
    * Add a converter to the chain with a defined priority
    */
   public boolean addConverterToChain(ValueBinding c, int priority)
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

   private boolean _transient;

   public boolean isTransient()
   {
      return _transient;
   }

   public void restoreState(FacesContext context, Object state)
   {
      Object[] values = (Object[]) state;
      converters = (List<PrioritizableConverter>) UIComponentBase.restoreAttachedState(context,
               values[0]);
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
