package org.jboss.seam.remoting.messaging;

import javax.jms.TopicConnection;

/**
 *
 * @author Shane Bryzak
 */
public interface JMSConnectionProvider {
  public TopicConnection createConnection() throws Exception;
}
