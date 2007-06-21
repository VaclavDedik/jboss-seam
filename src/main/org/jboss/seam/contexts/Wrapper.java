package org.jboss.seam.contexts;


interface Wrapper
{
   public Object getInstance();
   public void activate();
   public boolean passivate();
}
