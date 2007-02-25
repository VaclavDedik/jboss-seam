package org.jboss.seam.example.ui;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.ui.Person.Honorific;

@Name("factories")
public class Factories
{
   @Factory("honorifics")
   public Honorific[] getHonorifics() {
      return Honorific.values();
   }

}
