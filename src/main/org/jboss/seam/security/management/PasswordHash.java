package org.jboss.seam.security.management;

import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.util.Base64;

/**
 * Password hashing utility functions
 *  
 * @author Shane Bryzak
 */
@Scope(STATELESS)
@Name("org.jboss.seam.security.passwordHash")
@Install(precedence = BUILT_IN)
@BypassInterceptors
public class PasswordHash
{
   public static final String ALGORITHM_MD5 = "MD5";
   public static final String ALGORITHM_SHA = "SHA";
        
   private static final String DEFAULT_ALGORITHM = ALGORITHM_MD5;
   
   private int saltLength = 8; // default password salt length, in bytes
      
   @Deprecated
   public String generateHash(String password)
   {
      return generateHash(password, DEFAULT_ALGORITHM);
   }
   
   @Deprecated
   public String generateHash(String password, String algorithm)
   {
      return generateSaltedHash(password, null, algorithm);
   }
   
   @Deprecated
   public String generateSaltedHash(String password, String saltPhrase)
   {
      return generateSaltedHash(password, saltPhrase, DEFAULT_ALGORITHM);
   }
   
   /**
    * @deprecated Use PasswordHash.createPasswordKey() instead
    */
   @Deprecated
   public String generateSaltedHash(String password, String saltPhrase, String algorithm)
   {
      try {        
         MessageDigest md = MessageDigest.getInstance(algorithm);
                  
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
   
   public byte[] generateRandomSalt()
   {      
      byte[] salt = new byte[saltLength];
      new SecureRandom().nextBytes(salt);
      return salt;      
   }
   
   /**
    * 
    */
   public String createPasswordKey(char[] password, byte[] salt, int iterations) 
      throws GeneralSecurityException 
   {
      PBEKeySpec passwordKeySpec = new PBEKeySpec(password, salt, iterations, 256);
      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      SecretKey passwordKey = secretKeyFactory.generateSecret(passwordKeySpec);
      passwordKeySpec.clearPassword();
      byte[] encoded = passwordKey.getEncoded();
      return Base64.encodeBytes(new SecretKeySpec(encoded, "AES").getEncoded());
   }
   
   public static PasswordHash instance()
   {
      return (PasswordHash) Component.getInstance(PasswordHash.class, ScopeType.STATELESS);
   }
   
   public int getSaltLength()
   {
      return saltLength;
   }
   
   public void setSaltLength(int saltLength)
   {
      this.saltLength = saltLength;
   }
}
