package org.jboss.seam.test.unit;

import org.jboss.seam.security.management.PasswordHash;
import org.jboss.seam.security.management.PasswordHash.Algorithm;
import org.testng.annotations.Test;

public class PasswordHashTest
{   
   @Test
   public void testMd5Hash()
   {
      String hash = PasswordHash.generateHash("secret", Algorithm.MD5);
      assert hash.equals("Xr4ilOzQ4PCOq3aQ0qbuaQ==");
   }
   
   @Test
   public void testShaHash()
   {
      String hash = PasswordHash.generateHash("secret", Algorithm.SHA);
      assert hash.equals("5en6G6MezRroT3XKqkdPOmY/BfQ=");
   }
}
