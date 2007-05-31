package org.jboss.seam.core;

import java.util.concurrent.Callable;

import javax.ejb.Local;
import javax.ejb.Timer;

/**
 * Local interface for TimerServiceDispatcher.
 * 
 * @author Gavin King
 *
 */
@Local
public interface LocalTimerServiceDispatcher extends Dispatcher<Timer, TimerServiceSchedule>
{   
   public Object call(Callable task);
}
