package org.jboss.seam.test;

import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.component.UIOutput;
import javax.faces.event.ValueChangeEvent;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.TimeZoneSelector;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class TimeZoneTest extends SeamTest
{

   @Override
   protected void startJbossEmbeddedIfNecessary() throws DeploymentException, IOException {}
   
   @Test
   public void timeZoneTest() throws Exception
   {
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            assert org.jboss.seam.international.TimeZone.instance().equals(java.util.TimeZone.getDefault());
            
            TimeZone cet = TimeZone.getTimeZone("CET");
            TimeZoneSelector.instance().setTimeZone(cet);
            
            assert org.jboss.seam.international.TimeZone.instance().equals(cet);
          
            TimeZoneSelector.instance().setTimeZoneId("CET");
            
            assert org.jboss.seam.international.TimeZone.instance().equals(cet);
            
            TimeZoneSelector.instance().selectTimeZone("GMT");
            assert org.jboss.seam.international.TimeZone.instance().getID().equals("GMT");
            
            ValueChangeEvent valueChangeEvent = new ValueChangeEvent(new UIOutput(), "GMT", "PST");
            TimeZoneSelector.instance().select(valueChangeEvent);
            assert org.jboss.seam.international.TimeZone.instance().getID().equals("PST");
            
            // TODO Test cookie stuff (need to extend Mocks for this)
            
         }
      }.run();
   }
}
