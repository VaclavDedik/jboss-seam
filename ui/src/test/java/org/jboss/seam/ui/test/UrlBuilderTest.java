package org.jboss.seam.ui.test;

import java.io.UnsupportedEncodingException;

import org.jboss.seam.ui.util.UrlBuilder;
import org.testng.annotations.Test;

public class UrlBuilderTest
{

   @Test
   public void testBaseUrlAlreadyHasParams() throws UnsupportedEncodingException
   {
      UrlBuilder url = new UrlBuilder("/someurl?arg1=a", "", "UTF8");
      url.addParameter("foo", "bar");

      String encodedUrl = url.getEncodedUrl();
      
      assert "/someurl?arg1=a&foo=bar".equals(encodedUrl);
   } 
   
}
