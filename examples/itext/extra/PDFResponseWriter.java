package org.jboss.seam.ui.pdf;

import javax.faces.*;
import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.component.*;
import javax.faces.render.*;
import javax.servlet.http.*;

import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.html.*;

public class PDFResponseWriter
    extends ResponseWriter 
{
    FacesContext context;
    Writer writer;
    Document document;

    public PDFResponseWriter(FacesContext context, Writer writer)
    {
        System.out.println("Context is " + context);
        this.writer = writer;
        this.context = context;
    }


    public Document getDocument() {
        return document;
    }
    
    public String getContentType() {
        // return "application/pdf";
        return "text/html";
    }


    public String getCharacterEncoding() {
        return "ISO-8859-1";
    }


    public void startDocument() 
        throws IOException 
    {
//         System.out.println("-- START");

//         try {
//             document = new Document();
//             System.out.println("stream: " + context.getResponseStream());
//             HtmlWriter.getInstance(document, context.getResponseStream());
            
//             document.open();
//             document.add(new Paragraph("Hello World: " + new java.util.Date()));
//         } catch (DocumentException e) {
//             throw new RuntimeException(e);
//         }
    }


    public void endDocument() 
        throws IOException 
    {  
//         System.out.println("-- END");
//         document.close();
    }


    public ResponseWriter cloneWithWriter(Writer writer) {
        return new PDFResponseWriter(context, writer);
    }

    public void flush() throws IOException { 
        writer.flush();
    }

    public void startElement(String name, UIComponent componentForElement)
        throws IOException 
    {
        System.out.println("XXX-Writer:startElement");
    }

    public void endElement(String name) 
        throws IOException 
    {
        System.out.println("XXX-Writer:endElement");
    }

    public void writeAttribute(String name, 
                               Object value,
                               String componentPropertyName)
          throws IOException 
    {
        System.out.println("XXX-Writer:writeAttribute");
    }


    public void writeURIAttribute(String name, 
                                  Object value,
                                  String componentPropertyName)
        throws IOException 
    {
        System.out.println("XXX-Writer:writeURIAttribute");
    }

    public void writeComment(Object comment) 
        throws IOException 
               
    {
        System.out.println("XXX-Writer:writeComment");
    }


    public void writeText(Object text, String componentPropertyName)
        throws IOException 
    {
        System.out.println("YYY-Writer:writeText prop=" + componentPropertyName + " text=" + text);
        writer.write(text.toString());
    }

    public void writeText(char text) 
        throws IOException 
    {
        System.out.println("YYY-Writer:writeText");
        writer.write(text);
    }

    public void writeText(char[] text) 
        throws IOException 
    {
        System.out.println("YYY-Writer:writeText");
        writer.write(text);
    }


    public void writeText(char text[], int off, int len)
          throws IOException 
    {
        System.out.println("YYY-Writer:writeText");
        writer.write(text,off,len);
    }


    public void close() throws IOException {
        writer.close();
    }


    public void write(char cbuf) throws IOException {
        writer.write(cbuf);
    }


    public void write(char[] cbuf, int off, int len) throws IOException {
        writer.write(cbuf, off, len);
    }


    public void write(int c) throws IOException {
        writer.write(c);
    }


    public void write(String str) throws IOException {
        writer.write(str);
    }


    public void write(String str, int off, int len) throws IOException {
        writer.write(str, off, len);
    }
}

