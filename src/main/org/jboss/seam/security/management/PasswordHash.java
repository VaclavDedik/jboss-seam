package org.jboss.seam.security.management;

import java.security.MessageDigest;

import org.jboss.seam.util.Base64;

/**
 * Password hashing utility functions
 *  
 * @author Shane Bryzak
 */
public class PasswordHash
{
   public enum Algorithm {SHA, MD5}
   
   private static final Algorithm DEFAULT_ALGORITHM = Algorithm.MD5;
   
   public static String generateHash(String password)
   {
      return generateHash(password, DEFAULT_ALGORITHM);
   }
   
   public static String generateHash(String password, Algorithm algorithm)
   {
      return generateHash(password, algorithm, null);
   }
   
   public static String generateHash(String password, String saltPhrase)
   {
      return generateHash(password, DEFAULT_ALGORITHM, saltPhrase);
   }
   
   public static String generateHash(String password, Algorithm algorithm, String saltPhrase)
   {
      try {        
         MessageDigest md = MessageDigest.getInstance(algorithm.name());
                  
         if (saltPhrase != null)
         {
            md.update(saltPhrase.getBytes());
            byte[] salt = md.digest();
            
            md.reset();
            md.update(password.getBytes());
            md.update(salt);
         }
         else
         {
            md.update(password.getBytes());
         }
         
         byte[] raw = md.digest();
         return Base64.encodeBytes(raw);
     } 
     catch (Exception e) {
         throw new RuntimeException(e);        
     } 
   }
}
