package org.jboss.seam.pdf.ui;

import javax.faces.*;
import javax.faces.context.*;
import javax.faces.component.*;
import javax.faces.el.ValueBinding;

import java.io.*;
import java.util.List;
import org.jboss.seam.ui.JSF;

import com.lowagie.text.*;

public abstract class ITextComponent
    extends UIComponentBase
{
    public static final String COMPONENT_FAMILY = "org.jboss.seam.pdf";
	
    protected String inFacet;
    protected Object currentFacet;
	//protected Map<String,Object> facets = new HashMap<String,Object>();   
  
    /**
     * get the current Itext object
     */
    abstract public Object getITextObject();

    /**
     * signal that the component should create it's managed object
     */
    abstract public void createITextObject(FacesContext context);

    /**
     * remove the itext objext
     */
    abstract public void removeITextObject();
 
    /** 
     * subcomponents should implement this to add child components
     * to themselves
     */ 
    abstract public void handleAdd(Object other);
    
    final public void add(Object other) {
        if (inFacet != null) {
            handleFacet(inFacet, other);
        } else {
            handleAdd(other);
        }
    }
    
    public void handleFacet(String facetName, Object obj) {
       currentFacet = obj;
       // facets.put(facetName,obj);
	}

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
    public ITextComponent findITextParent(UIComponent parent, Class<?> c) {
        if (parent == null) {
            return null;
        }
        
        if (parent instanceof ITextComponent) {
            if (c==null || c.isAssignableFrom(parent.getClass())) {
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

    public Object processFacet(String facetName) {
        if (inFacet!=null && inFacet.equals(facetName)) {
            return null;
        }
        
        UIComponent facet = this.getFacet(facetName);
        Object result = null;
        if (facet != null) {
            currentFacet = null;
            inFacet = facetName;
            try {
                encode(FacesContext.getCurrentInstance(), facet);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                inFacet = null;
                result = currentFacet;
                currentFacet = null;
            }
        }        
        return result;              
    }
   
    public Object valueBinding(FacesContext context, 
                               String property, 
                               Object defaultValue) {
        Object value = defaultValue; 
        ValueBinding binding = getValueBinding(property);
        if (binding != null) {
            value = binding.getValue(context);
        }
        return value;
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
        createITextObject(context);
    }

    @Override
    public void encodeEnd(FacesContext context) 
        throws IOException
    {
        Object obj = getITextObject();
        if (obj != null) {
            addToITextParent(getITextObject());
        }
        removeITextObject();
    }

    @SuppressWarnings("unchecked")
	@Override
    public void encodeChildren(FacesContext context)
        throws IOException
    {
        for (UIComponent child: (List<UIComponent>) this.getChildren()) {
            // ugly hack to be able to capture facelets text
            if (child.getFamily().equals("facelets.LiteralText")) {
                String text = replaceEntities(extractText(context, child));
                Font   font = getFont();
                Chunk chunk = null;
                if (font == null) {
                    chunk = new Chunk(text);
                } else {
                    chunk = new Chunk(text, getFont());
                }
                add(chunk);
            } else {
                encode(context, child);
            }
        }
    }

    public String extractText(FacesContext context, UIComponent child) 
        throws IOException
    {
        ResponseWriter response = context.getResponseWriter();
        StringWriter stringWriter = new StringWriter();
        ResponseWriter cachingResponseWriter = response.cloneWithWriter(stringWriter);
        context.setResponseWriter(cachingResponseWriter);
        
        JSF.renderChild(context, child);

        context.setResponseWriter(response);
        
        return stringWriter.getBuffer().toString();
    }

    /**
     * facelets automatically escapes text, so we have to undo the
     * the damage here.  This is just a placeholder for something
     * more intelligent.  The replacement strategy here is not
     * sufficient.
     */
    private String replaceEntities(String text) {
        StringBuffer buffer = new StringBuffer(text);

        replaceAll(buffer, "&quot;", "\"");
        // XXX - etc....

        return buffer.toString();
    }

    private void replaceAll(StringBuffer buffer, String original, String changeTo) {
        int pos;
        while ((pos = buffer.indexOf(original)) != -1) {
            buffer.replace(pos,pos+original.length(), changeTo);
        }
    }


    @SuppressWarnings("unchecked")
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
