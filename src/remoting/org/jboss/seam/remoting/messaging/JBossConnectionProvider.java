package org.jboss.seam.remoting.messaging;

import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;

/**
 *
 * @author Shane Bryzak
 */
public class JBossConnectionProvider implements JMSConnectionProvider
{
  private static final String FACTORY_JNDI_NAME = "UIL2ConnectionFactory";

  public TopicConnection createConnection()
    throws Exception
  {
    InitialContext ctx = new InitialContext();
    TopicConnectionFactory f = (TopicConnectionFactory) ctx.lookup(FACTORY_JNDI_NAME);
    return f.createTopicConnection();
  }
}
