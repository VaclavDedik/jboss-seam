package org.jboss.seam.test.mail.unit;

import org.jboss.seam.mail.ui.Header;
import org.testng.annotations.Test;

public class HeaderTest
{
   @Test
   public void testHeader()
   {
      String name = "foo";
      String value = "bar";
      
      Header header = new Header(name, value);
      
      assert header.getSanitizedName().equals(name);
      assert header.getSanitizedValue().equals(value);
   }
   
   @Test
   public void testHeaderWithLineFeed()
   {
      String name = "foo\nnewline";
      String value = "bar\nnewline";
      
      Header header = new Header(name, value);
      
      assert !header.getSanitizedName().equals(name);
      assert !header.getSanitizedValue().equals(value);
      
      assert "foo".equals(header.getSanitizedName());
      assert "bar".equals(header.getSanitizedValue());
   }
   
   @Test
   public void testHeaderWithCarrigeReturnLineBreak()
   {
      String name = "foo\r\nnewline";
      String value = "bar\r\nnewline";
      
      Header header = new Header(name, value);
      
      assert !header.getSanitizedName().equals(name);
      assert !header.getSanitizedValue().equals(value);
      
      assert "foo".equals(header.getSanitizedName());
      assert "bar".equals(header.getSanitizedValue());
   }
   
   @Test
   public void testHeaderWithCarriageReturn()
   {
      String name = "foo\rnewline";
      String value = "bar\rnewline";
      
      Header header = new Header(name, value);
      
      assert !header.getSanitizedName().equals(name);
      assert !header.getSanitizedValue().equals(value);
      
      assert "foo".equals(header.getSanitizedName());
      assert "bar".equals(header.getSanitizedValue());
   }
}
