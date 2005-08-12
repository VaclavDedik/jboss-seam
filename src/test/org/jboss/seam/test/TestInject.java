/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.test;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
public class TestInject extends TestCase
{

   public void testInject01()
   {
      System.out.println("Test");
      Mock mock = new Mock();
      mock.getProcessInstance();
   }
}


