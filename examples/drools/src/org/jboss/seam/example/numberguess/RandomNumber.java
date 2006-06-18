package org.jboss.seam.example.numberguess;

import java.util.Random;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("randomNumber")
@Scope(ScopeType.CONVERSATION)
public class RandomNumber
{
   private int randomNumber;
   
   @Create 
   public void begin()
   {
      randomNumber = new Random().nextInt(100);
   }
   
   public int getValue() 
   {
      return randomNumber;
   }
   
}
