//$Id$
package org.jboss.seam.example.numberguess.test;

import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Jbpm;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pageflow;
import org.jboss.seam.example.numberguess.NumberGuess;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class NumberGuessTest extends SeamTest
{
   
   @Test
   public void testNumberGuessWin() throws Exception
   {
      String id = new Script()
      {

         @Override
         protected void renderResponse() throws Exception {
            NumberGuess ng = (NumberGuess) Component.getInstance(NumberGuess.class, true);
            assert ng.getMaxGuesses()==10;
            assert ng.getBiggest()==100;
            assert ng.getSmallest()==1;
            assert ng.getCurrentGuess()==null;
            assert ng.getGuessCount()==0;
            assert Manager.instance().isLongRunningConversation();
            assert Pageflow.instance().getProcessInstance().getRootToken()
                  .getNode().getName().equals("displayGuess");
         }
         
      }.run();

      new Script(id)
      {
         
         NumberGuess ng;
         int guess;

         @Override
         protected void applyRequestValues() throws Exception {
            ng = (NumberGuess) Component.getInstance(NumberGuess.class, true);
            guess = ng.getRandomNumber() > 50 ? 25 : 75;
            ng.setCurrentGuess(guess);
         }

         @Override
         protected void invokeApplication() throws Exception {
            ng.guess();
         }

         @Override
         protected void renderResponse() throws Exception {
            assert ng.getMaxGuesses()==10;
            assert ( guess > ng.getRandomNumber() && ng.getBiggest()==guess-1 ) 
                  || ( guess < ng.getRandomNumber() && ng.getSmallest()==guess+1 );
            assert !ng.isCorrectGuess();
            assert !ng.isLastGuess();
            assert ng.getCurrentGuess()==guess;
            assert ng.getGuessCount()==1;
            assert ng.getRemainingGuesses()==9;
            assert Manager.instance().isLongRunningConversation();
            assert Pageflow.instance().getProcessInstance().getRootToken()
                  .getNode().getName().equals("displayGuess");
         }
         
      }.run();

      new Script(id)
      {
         
         NumberGuess ng;

         @Override
         protected void applyRequestValues() throws Exception {
            ng = (NumberGuess) Component.getInstance(NumberGuess.class, true);
            ng.setCurrentGuess( ng.getRandomNumber() );
         }

         @Override
         protected void invokeApplication() throws Exception {
            ng.guess();
         }

         @Override
         protected void renderResponse() throws Exception {
            assert ng.getMaxGuesses()==10;
            assert ng.isCorrectGuess();
            assert ng.getCurrentGuess()==ng.getRandomNumber();
            assert ng.getGuessCount()==2;
            /*assert Pageflow.instance().getProcessInstance().getRootToken()
                  .getNode().getName().equals("win");*/
            //assert !Manager.instance().isLongRunningConversation();
         }
         
      }.run();

   }
   
   @Test
   public void testNumberGuessLose() throws Exception
   {
      String id = new Script()
      {

         @Override
         protected void renderResponse() throws Exception {
            NumberGuess ng = (NumberGuess) Component.getInstance(NumberGuess.class, true);
            assert ng.getMaxGuesses()==10;
            assert ng.getBiggest()==100;
            assert ng.getSmallest()==1;
            assert ng.getCurrentGuess()==null;
            assert ng.getGuessCount()==0;
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      for (int i=1; i<=9; i++)
      {
         
         final int count = i;

         new Script(id)
         {
            
            NumberGuess ng;
            int guess;
   
            @Override
            protected void applyRequestValues() throws Exception {
               ng = (NumberGuess) Component.getInstance(NumberGuess.class, true);
               guess = ng.getRandomNumber() > 50 ? 25+count : 75-count;
               ng.setCurrentGuess(guess);
            }
   
            @Override
            protected void invokeApplication() throws Exception {
               ng.guess();
               assert Pageflow.instance().getProcessInstance().getRootToken()
                     .getNode().getName().equals("displayGuess");
            }
   
            @Override
            protected void renderResponse() throws Exception {
               assert ng.getMaxGuesses()==10;
               assert ( guess > ng.getRandomNumber() && ng.getBiggest()==guess-1 ) 
                     || ( guess < ng.getRandomNumber() && ng.getSmallest()==guess+1 );
               assert !ng.isCorrectGuess();
               assert !ng.isLastGuess();
               assert ng.getCurrentGuess()==guess;
               assert ng.getGuessCount()==count;
               assert ng.getRemainingGuesses()==10-count;
               assert Manager.instance().isLongRunningConversation();
               assert Pageflow.instance().getProcessInstance().getRootToken()
                     .getNode().getName().equals("displayGuess");
            }
            
         }.run();
      
      }

      new Script(id)
      {
         
         NumberGuess ng;
         int guess;

         @Override
         protected void applyRequestValues() throws Exception {
            ng = (NumberGuess) Component.getInstance(NumberGuess.class, true);
            guess = ng.getRandomNumber() > 50 ? 49 : 51;
            ng.setCurrentGuess(guess);
         }

         @Override
         protected void invokeApplication() throws Exception {
            ng.guess();
            assert Pageflow.instance().getProcessInstance().getRootToken()
                  .getNode().getName().equals("displayGuess");
         }

         @Override
         protected void renderResponse() throws Exception {
            assert ng.getMaxGuesses()==10;
            assert ( guess > ng.getRandomNumber() && ng.getBiggest()==guess-1 ) 
                  || ( guess < ng.getRandomNumber() && ng.getSmallest()==guess+1 );
            assert !ng.isCorrectGuess();
            assert ng.isLastGuess();
            assert ng.getCurrentGuess()==guess;
            assert ng.getGuessCount()==10;
            assert ng.getRemainingGuesses()==0;
            /*assert Pageflow.instance().getProcessInstance().getRootToken()
                  .getNode().getName().equals("lose");*/
            //assert !Manager.instance().isLongRunningConversation();
         }
         
      }.run();


   }
   

   @Override
   public void initServletContext(Map initParams)
   {
      initParams.put(Init.COMPONENT_CLASSES, "org.jboss.seam.core.Jbpm");
      initParams.put(Jbpm.PAGEFLOW_DEFINITIONS, "jbpm-pageflow.xml");
   }
   
}
