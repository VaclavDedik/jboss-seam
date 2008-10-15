package com.sun.facelets;

import java.io.Writer;

import javax.faces.context.ResponseWriter;

/**
 * This is a hack to instantiate a thread-local object that Facelets uses to
 * write the STATE_KEY into the response when directed by JSF. The STATE_KEY is
 * written in the case when there is a form on the page. This hack is necessary
 * since we are not calling Facelets in the normal way (and hence it is not
 * completely initialized).
 */
public class StateWriterControl
{
   public static void initialize(Writer writer)
   {
      new StateWriter(writer, 1024);
   }
   
   public static ResponseWriter createClone(ResponseWriter writer) {
      return writer.cloneWithWriter(StateWriter.getCurrentInstance());
   }
   
   public static boolean isStateWritten() {
      return StateWriter.getCurrentInstance().isStateWritten();
   }
   
   public static String getAndResetBuffer() {
      return StateWriter.getCurrentInstance().getAndResetBuffer();
   }
   
   public static void release() {
      StateWriter.getCurrentInstance().release();
   }
}
