package org.jboss.seam.test.integration.faces;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

import org.jboss.seam.annotations.Create;

public class SequenceAction implements Serializable {
   private static final long serialVersionUID = 1L;
   
   private Deque<Long> sequence;
   private Long input;
   
   @Create
   public void create() {
      System.out.println("XXX: create");
      sequence = new LinkedList<Long>();
   }
   
   public String getOutput() {
      StringBuilder sb = new StringBuilder();
      for (Long n : sequence) {
         sb.append(n);
         sb.append(", ");
      }
      
      return sb.toString();
   }
   
   public void append() {
      sequence.add(input);
   }

   public Long getInput()
   {
      return input;
   }

   public void setInput(Long input)
   {
      this.input = input;
   }
   
   public Long getMinimum() {
      if (sequence.isEmpty()) {
         return 0L;
      }
      
      return sequence.getLast();
   }
}