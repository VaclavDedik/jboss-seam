/**
 * License Agreement.
 *
 * Ajax4jsf 1.1 - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.jboss.seam.ui.util.cdk;

import javax.faces.component.ActionSource;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIGraphic;
import javax.faces.component.UIParameter;
import javax.faces.component.UISelectBoolean;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.webapp.UIComponentTag;

/**
 * Base class with utility functions for all JSF tags. Get from apache MyFaces
 * @author asmirnov@exadel.com (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 */
@Deprecated
public abstract class UIComponentTagBase extends UIComponentTag {

    //Special UIComponent attributes (ValueHolder, ConvertibleValueHolder)
    private String _value;
    private String _converter;
    //attributes id, rendered and binding are handled by UIComponentTag

    /**
    * @param converter The converter to set.
    */
   public void setConverter(String converter) {
      _converter = converter;
   }

   /**
    * @param value The value to set.
    */
   public void setValue(String value) {
      _value = value;
   }

   @Override
   public void release() {
        super.release();

        _value=null;
        _converter=null;
    }

    @Override
   protected void setProperties(UIComponent component)
    {
        super.setProperties(component);


        //rendererType already handled by UIComponentTag

        setValueProperty(component, _value);
        setConverterProperty(component, _converter);
    }


    private static final Class[] VALIDATOR_ARGS = {FacesContext.class,
                                                   UIComponent.class,
                                                   Object.class};
    private static final Class[] ACTION_LISTENER_ARGS = {ActionEvent.class};
    private static final Class[] VALUE_LISTENER_ARGS = {ValueChangeEvent.class};

    protected void setCharterProperty(UIComponent component, String propName, String value)
    {
        if (value != null)
        {
            if (isValueReference(value))
            {
                ValueBinding vb = getFacesContext().getApplication().createValueBinding(value);
                component.setValueBinding(propName, vb);
            }
            else
            {
                //FIXME: should use converter maybe?
                component.getAttributes().put(propName, new Character(value.charAt(0)));
            }
        }
    }
    protected void setIntegerProperty(UIComponent component, String propName, String value)
    {
        if (value != null)
        {
            if (isValueReference(value))
            {
                ValueBinding vb = getFacesContext().getApplication().createValueBinding(value);
                component.setValueBinding(propName, vb);
            }
            else
            {
                //FIXME: should use converter maybe?
                component.getAttributes().put(propName, Integer.valueOf(value));
            }
        }
    }

    protected void setLongProperty(UIComponent component, String propName, String value)
    {
        if (value != null)
        {
            if (isValueReference(value))
            {
                ValueBinding vb = getFacesContext().getApplication().createValueBinding(value);
                component.setValueBinding(propName, vb);
            }
            else
            {
                //FIXME: should use converter maybe?
                component.getAttributes().put(propName, Long.valueOf(value));
            }
        }
    }

    protected void setFloatProperty(UIComponent component, String propName, String value)
    {
        if (value != null)
        {
            if (isValueReference(value))
            {
                ValueBinding vb = getFacesContext().getApplication().createValueBinding(value);
                component.setValueBinding(propName, vb);
            }
            else
            {
                //FIXME: should use converter maybe?
                component.getAttributes().put(propName, Float.valueOf(value));
            }
        }
    }
    protected void setDoubleProperty(UIComponent component, String propName, String value)
    {
        if (value != null)
        {
            if (isValueReference(value))
            {
                ValueBinding vb = getFacesContext().getApplication().createValueBinding(value);
                component.setValueBinding(propName, vb);
            }
            else
            {
                //FIXME: should use converter maybe?
                component.getAttributes().put(propName, Double.valueOf(value));
            }
        }
    }
    
    protected void setStringProperty(UIComponent component, String propName, String value)
    {
        if (value != null)
        {
            if (isValueReference(value))
            {
                ValueBinding vb = getFacesContext().getApplication().createValueBinding(value);
                component.setValueBinding(propName, vb);
            }
            else
            {
                //TODO: Warning if component has no such property (with reflection)
                component.getAttributes().put(propName, value);
            }
        }
    }

    protected void setBooleanProperty(UIComponent component, String propName, String value)
    {
        if (value != null)
        {
            if (isValueReference(value))
            {
                ValueBinding vb = getFacesContext().getApplication().createValueBinding(value);
                component.setValueBinding(propName, vb);
            }
            else
            {
                //TODO: More sophisticated way to convert boolean value (yes/no, 1/0, on/off, etc.)
                component.getAttributes().put(propName, Boolean.valueOf(value));
            }
        }
    }

    protected void setValueProperty(UIComponent component, String value)
    {
        if (value != null)
        {
            if (isValueReference(value))
            {
                ValueBinding vb = getFacesContext().getApplication().createValueBinding(value);
                component.setValueBinding("value", vb);
            }
            else if (component instanceof UICommand)
            {
                ((UICommand)component).setValue(value);
            }
            else if (component instanceof UIParameter)
            {
                ((UIParameter)component).setValue(value);
            }
            else if (component instanceof UISelectBoolean)
            {
                ((UISelectBoolean)component).setValue(Boolean.valueOf(value));
            }
            else if (component instanceof UIGraphic)
            {
                ((UIGraphic)component).setValue(value);
            }
            //Since many input components are ValueHolders the special components
            //must come first, ValueHolder is the last resort.
            else if (component instanceof ValueHolder)
            {
                ((ValueHolder)component).setValue(value);
            }
            else
            {
               component.getAttributes().put("value", value);
            }
        }
    }

    protected void setConverterProperty(UIComponent component, String value)
    {
        if (value != null)
        {
            if (component instanceof ValueHolder)
            {
                if (isValueReference(value))
                {
                    ValueBinding vb = getFacesContext().getApplication().createValueBinding(value);
                    component.setValueBinding("converter", vb);
                }
                else
                {
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    Converter converter = facesContext.getApplication().createConverter(value);
                    ((ValueHolder)component).setConverter(converter);
                }
            }

        }
    }

    protected void setValidatorProperty(UIComponent component, String validator)
    {
        if (validator != null)
        {
            if (!(component instanceof EditableValueHolder))
            {
                throw new IllegalArgumentException();
            }
            if (isValueReference(validator))
            {
                MethodBinding mb = getFacesContext().getApplication().createMethodBinding(validator,
                                                                                VALIDATOR_ARGS);
                ((EditableValueHolder)component).setValidator(mb);
            }

        }
    }

    protected void setActionProperty(UIComponent component, String action)
    {
        if (action != null)
        {
            if (!(component instanceof ActionSource))
            {
                throw new IllegalArgumentException();
            }
            MethodBinding mb;
            if (isValueReference(action))
            {
                mb = getFacesContext().getApplication().createMethodBinding(action, null);
            }
            else
            {
                mb = new SimpleActionMethodBinding(action);
            }
            ((ActionSource)component).setAction(mb);
        }
    }

    protected void setActionListenerProperty(UIComponent component, String actionListener)
    {
        if (actionListener != null)
        {
            if (!(component instanceof ActionSource))
            {
                throw new IllegalArgumentException();
            }
            if (isValueReference(actionListener))
            {
                MethodBinding mb = getFacesContext().getApplication().createMethodBinding(actionListener,
                                                                                ACTION_LISTENER_ARGS);
                ((ActionSource)component).setActionListener(mb);
            }

        }
    }

    protected void setValueChangedListenerProperty(UIComponent component, String valueChangedListener)
    {
        if (valueChangedListener != null)
        {
            if (!(component instanceof EditableValueHolder))
            {
                throw new IllegalArgumentException();
            }
            if (isValueReference(valueChangedListener))
            {
                MethodBinding mb = getFacesContext().getApplication().createMethodBinding(valueChangedListener,
                                                                                VALUE_LISTENER_ARGS);
                ((EditableValueHolder)component).setValueChangeListener(mb);
            }

        }
    }

    protected void setValueBinding(UIComponent component,
                                   String propName,
                                   String value)
    {
        if (value != null)
        {
            if (isValueReference(value))
            {
                ValueBinding vb = getFacesContext().getApplication().createValueBinding(value);
                component.setValueBinding(propName, vb);
            }

        }
    }



}

