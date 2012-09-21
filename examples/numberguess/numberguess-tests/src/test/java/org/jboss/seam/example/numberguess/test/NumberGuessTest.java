//$Id: NumberGuessTest.java 5335 2007-06-20 09:07:34Z gavin $
package org.jboss.seam.example.numberguess.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.core.Manager;
import org.jboss.seam.example.numberguess.NumberGuess;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.pageflow.Pageflow;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class NumberGuessTest extends JUnitSeamTest {

    private int guess;

    @Deployment(name = "NumberGuessTest")
    @OverProtocol("Servlet 3.0")
    public static Archive<?> createDeployment() {
        EnterpriseArchive er = Deployments.numberGuessDeployment();
        WebArchive web = er.getAsType
                (WebArchive.class, "numberguess-web.war");

        web.addClasses(NumberGuessTest.class);

        return er;
    }

    @Test
    public void testNumberGuessWin() throws Exception {
        String id = new NonFacesRequest("/numberGuess.xhtml") {

            @Override
            protected void renderResponse() throws Exception {
                NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
                assert ng.getMaxGuesses() == 10;
                assert ng.getBiggest() == 100;
                assert ng.getSmallest() == 1;
                assert ng.getCurrentGuess() == null;
                assert ng.getGuessCount() == 0;
                assert Manager.instance().isLongRunningConversation();
                assert Pageflow.instance().getProcessInstance().getRootToken()
                        .getNode().getName().equals("displayGuess");
            }

        }.run();

        String id2 = new FacesRequest("/numberGuess.xhtml", id) {

            @Override
            protected void applyRequestValues() throws Exception {
                NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
                guess = ng.getRandomNumber() > 50 ? 25 : 75;
                ng.setCurrentGuess(guess);
            }

            @Override
            protected void invokeApplication() throws Exception {
                setOutcome("guess");
                //ng.guess();
            }

            @Override
            protected void afterRequest() {
                assert !isRenderResponseBegun();
                assert getViewId().equals("/numberGuess.xhtml");
            }

        }.run();

        assert id2.equals(id);

        new NonFacesRequest("/numberGuess.xhtml", id) {

            @Override
            protected void renderResponse() throws Exception {
                NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
                assert ng.getMaxGuesses() == 10;
                assert (guess > ng.getRandomNumber() && ng.getBiggest() == guess - 1)
                        || (guess < ng.getRandomNumber() && ng.getSmallest() == guess + 1);
                assert !ng.isCorrectGuess();
                assert !ng.isLastGuess();
                assert ng.getCurrentGuess() == guess;
                assert ng.getGuessCount() == 1;
                assert ng.getRemainingGuesses() == 9;
                assert Manager.instance().isLongRunningConversation();
                assert Pageflow.instance().getProcessInstance().getRootToken()
                        .getNode().getName().equals("displayGuess");
            }

        }.run();

        id2 = new FacesRequest("/numberGuess.xhtml", id) {

            @Override
            protected void applyRequestValues() throws Exception {
                NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
                ng.setCurrentGuess(ng.getRandomNumber());
            }

            @Override
            protected void invokeApplication() throws Exception {
                setOutcome("guess");
                //ng.guess();
            }

            @Override
            protected void afterRequest() {
                assert !isRenderResponseBegun();
                assert getViewId().equals("/win.xhtml");
            }

        }.run();

        assert id2.equals(id);

        new NonFacesRequest("/win.xhtml", id) {
            @Override
            protected void renderResponse() throws Exception {
                NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
                assert ng.getMaxGuesses() == 10;
                assert ng.isCorrectGuess();
                assert ng.getCurrentGuess() == ng.getRandomNumber();
                assert ng.getGuessCount() == 2;
                assert !Manager.instance().isLongRunningConversation();
                assert Pageflow.instance().getProcessInstance().getRootToken()
                        .getNode().getName().equals("win");
            }

        }.run();

    }

    @Test
    public void testNumberGuessLose() throws Exception {
        String id = new NonFacesRequest("/numberGuess.xhtml") {

            @Override
            protected void renderResponse() throws Exception {
                NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
                assert ng.getMaxGuesses() == 10;
                assert ng.getBiggest() == 100;
                assert ng.getSmallest() == 1;
                assert ng.getCurrentGuess() == null;
                assert ng.getGuessCount() == 0;
                assert Manager.instance().isLongRunningConversation();
            }

        }.run();

        for (int i = 1; i <= 9; i++) {

            final int count = i;

            new FacesRequest("/numberGuess.xhtml", id) {

                @Override
                protected void applyRequestValues() throws Exception {
                    NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
                    guess = ng.getRandomNumber() > 50 ? 25 + count : 75 - count;
                    ng.setCurrentGuess(guess);
                }

                @Override
                protected void invokeApplication() throws Exception {
                    setOutcome("guess");
                    //ng.guess();
                    //assert Pageflow.instance().getProcessInstance().getRootToken()
//                     .getNode().getName().equals("displayGuess");
                }

                @Override
                protected void afterRequest() {
                    assert !isRenderResponseBegun();
                    assert getViewId().equals("/numberGuess.xhtml");
                }

            }.run();

            new NonFacesRequest("/numberGuess.xhtml", id) {

                @Override
                protected void renderResponse() throws Exception {
                    NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
                    assert ng.getMaxGuesses() == 10;
                    assert (guess > ng.getRandomNumber() && ng.getBiggest() == guess - 1)
                            || (guess < ng.getRandomNumber() && ng.getSmallest() == guess + 1);
                    assert !ng.isCorrectGuess();
                    assert !ng.isLastGuess();
                    assert ng.getCurrentGuess() == guess;
                    assert ng.getGuessCount() == count;
                    assert ng.getRemainingGuesses() == 10 - count;
                    assert Manager.instance().isLongRunningConversation();
                    assert Pageflow.instance().getProcessInstance().getRootToken()
                            .getNode().getName().equals("displayGuess");
                }

            }.run();

        }

        new FacesRequest("/numberGuess.xhtml", id) {

            @Override
            protected void applyRequestValues() throws Exception {
                NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
                guess = ng.getRandomNumber() > 50 ? 49 : 51;
                ng.setCurrentGuess(guess);
            }

            @Override
            protected void invokeApplication() throws Exception {
                setOutcome("guess");
                //ng.guess();
                assert Pageflow.instance().getProcessInstance().getRootToken()
                        .getNode().getName().equals("displayGuess");
            }

            @Override
            protected void afterRequest() {
                assert !isRenderResponseBegun();
                assert getViewId().equals("/lose.xhtml");
            }

        }.run();

        new NonFacesRequest("/lose.xhtml", id) {

            @Override
            protected void renderResponse() throws Exception {
                NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
                assert ng.getMaxGuesses() == 10;
                assert (guess > ng.getRandomNumber() && ng.getBiggest() == guess - 1)
                        || (guess < ng.getRandomNumber() && ng.getSmallest() == guess + 1);
                assert !ng.isCorrectGuess();
                assert ng.isLastGuess();
                assert ng.getCurrentGuess() == guess;
                assert ng.getGuessCount() == 10;
                assert ng.getRemainingGuesses() == 0;
                assert !Manager.instance().isLongRunningConversation();
                assert Pageflow.instance().getProcessInstance().getRootToken()
                        .getNode().getName().equals("lose");
            }

        }.run();

    }

}
