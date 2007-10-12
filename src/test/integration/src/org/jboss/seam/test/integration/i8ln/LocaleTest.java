package org.jboss.seam.test.integration.i8ln;

import java.util.Locale;

import javax.faces.component.UIOutput;
import javax.faces.event.ValueChangeEvent;

import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class LocaleTest extends SeamTest
{
   
   @Test
   public void localeTest() throws Exception
   {
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            assert org.jboss.seam.international.Locale.instance().equals(Locale.getDefault());
            
            LocaleSelector.instance().setLocale(Locale.UK);
            
            assert org.jboss.seam.international.Locale.instance().equals(Locale.UK);
          
            LocaleSelector.instance().setLocaleString(Locale.FRANCE.toString());
            
            LocaleSelector.instance().getLanguage().equals(Locale.FRANCE.getLanguage());
            LocaleSelector.instance().getCountry().equals(Locale.FRANCE.getCountry());
            LocaleSelector.instance().getVariant().equals(Locale.FRANCE.getVariant());
            
            assert org.jboss.seam.international.Locale.instance().equals(Locale.FRANCE);
            assert LocaleSelector.instance().getLocaleString().equals(Locale.FRANCE.toString());
            
            LocaleSelector.instance().select();
            assert org.jboss.seam.international.Locale.instance().equals(Locale.FRANCE);
            
            LocaleSelector.instance().selectLanguage(Locale.JAPANESE.getLanguage());
            assert org.jboss.seam.international.Locale.instance().getLanguage().equals(Locale.JAPANESE.getLanguage());
            
            ValueChangeEvent valueChangeEvent = new ValueChangeEvent(new UIOutput(), Locale.JAPANESE.toString(), Locale.TAIWAN.toString());
            LocaleSelector.instance().select(valueChangeEvent);
            assert org.jboss.seam.international.Locale.instance().equals(Locale.TAIWAN);
            
            Locale uk_posix = new Locale(Locale.UK.getLanguage(), Locale.UK.getCountry(), "POSIX");
            LocaleSelector.instance().setLocale(uk_posix);
            
            assert org.jboss.seam.international.Locale.instance().equals(uk_posix);
            assert LocaleSelector.instance().getLanguage().equals(uk_posix.getLanguage());
            assert LocaleSelector.instance().getCountry().equals(uk_posix.getCountry());
            assert LocaleSelector.instance().getVariant().equals(uk_posix.getVariant());
            
            LocaleSelector.instance().setLanguage(Locale.CHINA.getLanguage());
            LocaleSelector.instance().setCountry(Locale.CHINA.getCountry()); 
            LocaleSelector.instance().setVariant(null);
            
            assert org.jboss.seam.international.Locale.instance().equals(Locale.CHINA);
            
            LocaleSelector.instance().setLanguage(Locale.ITALIAN.getLanguage());
            LocaleSelector.instance().setCountry(null);            
            LocaleSelector.instance().setVariant(null);
            
            assert org.jboss.seam.international.Locale.instance().equals(Locale.ITALIAN);
            
            assert LocaleSelector.instance().getSupportedLocales().size() == 1;
            assert LocaleSelector.instance().getSupportedLocales().get(0).getValue().equals(Locale.ENGLISH.toString());
            assert LocaleSelector.instance().getSupportedLocales().get(0).getLabel().equals(Locale.ENGLISH.getDisplayName());

            boolean failed = false;
            try
            {
               LocaleSelector.instance().setLocale(null);
            }
            catch (NullPointerException e) 
            {
               failed = true;
            }
            assert failed;
            
            // TODO Test cookie stuff (need to extend Mocks for this)
            
         }
      }.run();
   }
}
