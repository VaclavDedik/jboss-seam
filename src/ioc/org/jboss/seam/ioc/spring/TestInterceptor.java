/**
 *
 */
package org.jboss.seam.ioc.spring;

import java.io.Serializable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author youngm
 *
 */
public class TestInterceptor implements MethodInterceptor, Serializable {

	/**
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	public Object invoke(MethodInvocation arg0) throws Throwable {
		System.out.println("Hit interceptor");
		return arg0.proceed();
	}

}
