package org.jboss.seam.ui.pdf;

import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.component.*;
import javax.faces.render.*;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import org.jboss.seam.ui.facelet.*;

public class TextRenderer
    extends Renderer
{
    public void encodeBegin(FacesContext context,
                            UIComponent component)
            throws IOException
    {
        System.out.println("BEGIN RENDER " + component);
    }

    public void encodeEnd(FacesContext context,
                          UIComponent component)
            throws IOException
    {
        System.out.println("END RENDER " + component);
        if (component instanceof ValueHolder) {
            ValueHolder holder = (ValueHolder) component;
            Object value = holder.getValue();
            System.out.println("*VALUE IS " + value);
            if (value != null) {
                Chunk c = new Chunk(value.toString());
                
                UIComponent parent = component.getParent();
                if (parent instanceof ITextComponent) {
                    ((ITextComponent) parent).add(c);
                }
            }
        }
    }

}
