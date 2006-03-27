package org.jboss.seam.example.remoting;

import javax.ejb.Interceptors;
import javax.ejb.Stateless;

import static org.jboss.seam.ScopeType.SESSION;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.ScopeType;
import java.util.Random;

@Stateless
@Name("progressBarAction")
@Scope(SESSION)
@Interceptors(SeamInterceptor.class)
public class ProgressBarAction implements ProgressBarLocal {

  @In(create = true) @Out(scope = ScopeType.SESSION)
  Progress progress;

  public String doSomething() {
    Random r = new Random(System.currentTimeMillis());
    try {
      for (int i = 1; i <= 100;)
      {
        Thread.currentThread().sleep(r.nextInt(200));
        progress.setPercentComplete(i);
        i++;
      }
    }
    catch (InterruptedException ex) {
    }

    return "complete";
  }

  public Progress getProgress()
  {
    return progress;
  }
}
