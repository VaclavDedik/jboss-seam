package org.jboss.seam.security.management;

import java.security.MessageDigest;
import java.util.List;

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

   protected abstract boolean createAccount(String username, String password);
   protected abstract boolean deleteAccount(String name);
   
   protected abstract boolean grantRole(String name, String role);
   protected abstract boolean revokeRole(String name, String role);
   
   protected abstract boolean enableAccount(String name);
   protected abstract boolean disableAccount(String name);   
   
   protected abstract List<String> listUsers();
   protected abstract List<String> listUsers(String filter);
   protected abstract List<String> listRoles();
   
   protected abstract List<String> getGrantedRoles(String name);
   protected abstract List<String> getImpliedRoles(String name);
   
   protected abstract boolean authenticate(String username, String password);
   
   protected String hashPassword(String password)
   {
      try {
         MessageDigest md = MessageDigest.getInstance(hashFunction);
         md.update(password.getBytes(hashCharset));         
         byte[] raw = md.digest();
         
         // TODO - salt the hash, possibly using the user name? 
         return new String(Hex.encodeHex(raw));
     } 
     catch (Exception e) {
         throw new RuntimeException(e);        
     }      
   }
}
