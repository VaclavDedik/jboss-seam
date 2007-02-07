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

public class PDFRenderKit 
    extends RenderKit 
{
    Map<String,Renderer> renderers = new HashMap<String,Renderer>();

    public ResponseStream createResponseStream(OutputStream stream) {
        System.out.println("RESPONSE STREAM!");
        return null;
    }

    
    public ResponseWriter createResponseWriter(Writer writer, 
                                               String contentTypeList, 
                                               String characterEncoding) 
    {
        return new PDFResponseWriter(FacesContext.getCurrentInstance(),
                                     writer);
    }

    public ResponseStateManager getResponseStateManager() {
        System.out.println("GET RESPONSE STATE MANAGER");
        return null;
    }

    public Renderer getRenderer(String family, String rendererType) {
        System.out.println("GET RENDERER: " + family + "/" + rendererType);
        return renderers.get(key(family,rendererType));
    }

    public void addRenderer(String family, String rendererType, Renderer renderer) {
        System.out.println("ADD RENDER " + family + "/" + rendererType + ":" + renderer);
        renderers.put(key(family,rendererType), renderer);
    }
                                                                  
    private String key(String family, String rendererType) {
        return family + "#" + rendererType;
    }
}
