package org.jboss.seam.example.remoting;

import java.util.Random;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
@Name("progressBarAction")
@Interceptors(SeamInterceptor.class)
public class ProgressBarAction implements ProgressBarLocal {

  @In(create = true)
  Progress progress;

  public String doSomething() {
    Random r = new Random(System.currentTimeMillis());
    try {
      for (int i = 1; i <= 100;)
      {
        Thread.sleep(r.nextInt(200));
        progress.setPercentComplete(i);
        i++;
      }
    }
    catch (InterruptedException ex) {
    }

    return "/complete.xhtml";
  }

  public Progress getProgress()
  {
    return progress;
  }
}
