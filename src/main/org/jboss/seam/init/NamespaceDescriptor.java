package org.jboss.seam.init;

import org.jboss.seam.annotations.Namespace;

public class NamespaceDescriptor
{
   private Namespace namespace;
   private Package pkg;

   NamespaceDescriptor(Namespace namespace, Package pkg)
   {
      this.namespace = namespace;
      this.pkg = pkg;
   }

   public Namespace getNamespace()
   {
      return namespace;
   }

   public Package getPackage()
   {
      return pkg;
   }

   @Override
   public String toString()
   {
      return "EventListenerDescriptor(" + namespace + ')';
   }
}