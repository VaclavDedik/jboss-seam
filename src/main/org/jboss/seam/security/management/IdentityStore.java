package org.jboss.seam.security.management;

import java.security.MessageDigest;

import org.jboss.seam.util.Hex;

/**
 * The identity store does the actual work of persisting user accounts in a
 * database, LDAP directory, etc.  
 * 
 * @author Shane Bryzak
 */
public abstract class IdentityStore
{      
   private String hashFunction = "MD5";
   private String hashCharset = "UTF-8";

   protected abstract UserAccount createAccount(String username, String password);
   
   protected void hashAccountPassword(UserAccount account, String password)
   {
      try {
         MessageDigest md = MessageDigest.getInstance(hashFunction);
         md.update(password.getBytes(hashCharset));         
         byte[] raw = md.digest();
         // TODO - salt the hash, possibly using the user name? 
         account.setPasswordHash(new String(Hex.encodeHex(raw)));
     } 
     catch (Exception e) {
         throw new RuntimeException(e);        
     }      
   }
}
