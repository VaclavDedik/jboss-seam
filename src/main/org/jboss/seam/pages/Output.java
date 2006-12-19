package org.jboss.seam.pages;


public class Output extends Put
{
   public void out()
   {
      getScope().getContext().set( getName(), getValue().getValue() );
   }
   
}
