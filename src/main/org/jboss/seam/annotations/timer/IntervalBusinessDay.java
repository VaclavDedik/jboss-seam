package org.jboss.seam.annotations.timer;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation occurs on a parameter of type NthBusinessDay
 * of a method marked @Asynchronous. It schedules the asynchronous
 * call to be repeated on the Nth business day based on a calendar
 * specified in NthBusinessDay. It is only available in the Quartz dispatcher.
 * 
 * @author Michael Yuan
 *
 */
@Target(PARAMETER)
@Retention(RUNTIME)
@Documented
public @interface IntervalBusinessDay {}
