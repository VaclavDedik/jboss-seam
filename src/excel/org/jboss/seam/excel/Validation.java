package org.jboss.seam.excel;

public interface Validation
{
   public enum ValidationType {
      numeric, range, list
   }
   
   public abstract ValidationType getType();
}
