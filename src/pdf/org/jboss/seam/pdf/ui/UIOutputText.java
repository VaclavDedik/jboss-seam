package org.jboss.seam.pdf.ui;

import java.io.IOException;

import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.lowagie.text.Chunk;

public class UIOutputText extends ITextComponent implements ValueHolder

{
    Chunk chunk;

    Converter converter;
    Object value;
    Object localValue;

    
    // -- ITextComponent methods
    @Override
    public void encodeEnd(FacesContext context) 
        throws IOException
    {
        String stringValue = "";
        
        Object myValue = getValue();
        
        if (converter != null) {
            stringValue = converter.getAsString(context, this, myValue);
        } else {
            if (myValue != null) {
                stringValue = myValue.toString();
            } 
        }
        
        chunk.append(stringValue);
        System.out.println("String value:" + stringValue);
                
        super.encodeEnd(context);
    }
    
    @Override
    public void createITextObject(FacesContext context) {
        com.lowagie.text.Font font = getFont();
        
        if (font == null) {
            chunk = new Chunk("");
        } else {
            chunk = new Chunk("", getFont());
        }
    }

    @Override
    public Object getITextObject() {
        return chunk;
    }

    @Override
    public void handleAdd(Object other) {
        throw new RuntimeException("illegal child element");
    }

    @Override
    public void removeITextObject() {
        chunk = null;
    }

    // -- ValueHolder methods

    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    public Object getValue() {
         return valueBinding(FacesContext.getCurrentInstance(), "value", localValue);
    }

    public void setValue(Object value) {
        this.localValue = value;
    }

    public Object getLocalValue() {
        return localValue;
    }

}
