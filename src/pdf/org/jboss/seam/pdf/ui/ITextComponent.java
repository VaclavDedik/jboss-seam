package org.jboss.seam.pdf.ui;


import javax.faces.*;
import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.convert.*;
import javax.faces.component.*;
import javax.servlet.http.*;

import java.awt.Color;
import java.io.*;
import java.util.List;

import org.jboss.seam.ui.JSF;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public abstract class ITextComponent
    extends UIComponentBase
{
    public static final String COMPONENT_FAMILY = "org.jboss.seam.pdf";    

  
    /**
     * get the current Itext object
     */
    abstract public Object getITextObject();

    /**
     * signal that the component should create it's managed object
     */
    abstract public void createITextObject();

    /** 
     * subcomponents should implement this
     */ 
    abstract public void add(Object other);


    /**
     *  look up the tree for an itext font
     */
    public Font getFont() {
        UIFont fontComponent = (UIFont) findITextParent(this, UIFont.class);
        return fontComponent == null ? null : fontComponent.getFont();
    }

    /**
     * look up the tree for the itext document
     */
    public Document findDocument() {
        ITextComponent parent = findITextParent(this, UIDocument.class);
        if (parent != null) {
            return (Document) parent.getITextObject();
        } else {
            return null;
        }
    }


    /**
     * find the first parent that is an itext component);
     */
    public ITextComponent findITextParent(UIComponent parent) {
        return findITextParent(parent, null);
    }

    /**
     * find the first parent that is an itext component of a given type
     */
    public ITextComponent findITextParent(UIComponent parent, Class c) {
        if (parent == null) {
            return null;
        }
        
        if (parent instanceof ITextComponent) {
            if (c==null || parent.getClass().equals(c)) {
                return (ITextComponent) parent;
            }
        }

        return findITextParent(parent.getParent(),c);
    }


    /**
     * add a component (usually the current itext object) to the itext parent's itext object
     */
    public void addToITextParent(Object obj) {
        ITextComponent parent = findITextParent(getParent());
        if (parent != null) {
            parent.add(obj);
        } else {
            throw new RuntimeException("Couldn't find ITextComponent parent for component " + 
                                       this.getClass().getName());
        }
        
    }

    // ------------------------------------------------------

    @Override
    public String getFamily()
    {
        return COMPONENT_FAMILY;
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeBegin(FacesContext context) 
        throws IOException
    {
        createITextObject();
    }

    @Override
    public void encodeEnd(FacesContext context) 
        throws IOException
    {
        Object obj = getITextObject();
        if (obj != null) {
            addToITextParent(getITextObject());
        }
    }

    @Override
    public void encodeChildren(FacesContext context)
        throws IOException
    {
        for (UIComponent child: (List<UIComponent>) this.getChildren()) {
            // ugly hack to be able to capture facelets text
            if (child.getFamily().equals("facelets.LiteralText")) {
                ResponseWriter response = context.getResponseWriter();
                StringWriter stringWriter = new StringWriter();
                ResponseWriter cachingResponseWriter = response.cloneWithWriter(stringWriter);
                context.setResponseWriter(cachingResponseWriter);

                JSF.renderChild(context, child);

                context.setResponseWriter(response);

                String text = stringWriter.getBuffer().toString();
                Font   font = getFont();
                if (font == null) {
                    Chunk chunk = new Chunk(text);
                    //chunk.setBackground(new Color(140,50,50));
                    add(chunk);
                } else {
                    add(new Chunk(text, getFont()));
                }
            } else {
                encode(context, child);
            }
        }
    }


    public void encode(FacesContext context,
                       UIComponent component) 
        throws IOException, 
               FacesException 
    {
        if (!component.isRendered()) {
            return;
        }

        component.encodeBegin(context);

        if (component.getChildCount() > 0) {
            if (component.getRendersChildren()) {
                component.encodeChildren(context);
            } else {
                for (UIComponent child: (List<UIComponent>) this.getChildren()) {
                    encode(context, child);
                }
            }
        }

        component.encodeEnd(context);
    }
}
