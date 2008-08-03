package org.jboss.seam.excel;

public interface Template
{
   public enum TemplateType
   {
      cell, worksheet
   }

   public abstract TemplateType getType();

   public abstract String getName();

}
