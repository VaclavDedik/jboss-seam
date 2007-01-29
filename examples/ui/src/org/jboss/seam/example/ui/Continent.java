package org.jboss.seam.example.ui;

public enum Continent
{
   ANTARTICA("Antarctica"),
   SOUTH_AMERICA("South America"),
   NORTH_AMERICA("North America"),
   EUROPE("Europe"), 
   ASIA("Asia"),
   AFRICA("Africa"),
   AUSTRALASIA("Australasia");
   
   private String name;

   Continent(String name) {
      this.name = name;
   }
   
   public String getName()
   {
      return name;
   }

}
