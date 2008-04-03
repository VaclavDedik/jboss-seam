package org.jboss.seam.test.unit;

import org.jboss.seam.security.management.PasswordHash;
import org.testng.annotations.Test;

public class PasswordHashTest
{   
   @Test
   public void testMd5Hash()
   {
      String hash = PasswordHash.instance().generateHash("secret", "MD5");
      assert hash.equals("Xr4ilOzQ4PCOq3aQ0qbuaQ==");
   }
   
   @Test
   public void testShaHash()
   {
      String hash = PasswordHash.instance().generateHash("secret", "SHA");
      assert hash.equals("5en6G6MezRroT3XKqkdPOmY/BfQ=");
   }
}
