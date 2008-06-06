package org.jboss.seam.security.management;

import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.security.MessageDigest;

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
   
   public String generateHash(String password)
   {
      return generateHash(password, DEFAULT_ALGORITHM);
   }
   
   public String generateHash(String password, String algorithm)
   {
      return generateSaltedHash(password, null, algorithm);
   }
   
   public String generateSaltedHash(String password, String saltPhrase)
   {
      return generateSaltedHash(password, saltPhrase, DEFAULT_ALGORITHM);
   }
   
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
   
   public static PasswordHash instance()
   {
      return (PasswordHash) Component.getInstance(PasswordHash.class, ScopeType.STATELESS);
   }
}
