package org.jboss.seam.remoting;

import java.util.Map;
import java.util.HashMap;

/**
 * Provides request handlers for different request paths.
 *
 * @author Shane Bryzak
 */
public class RequestHandlerFactory
{
  private static final String REQUEST_PATH_EXECUTE = "/execute";
  private static final String REQUEST_PATH_SUBSCRIPTION = "/subscription";
  private static final String REQUEST_PATH_POLL = "/poll";
  private static final String REQUEST_PATH_INTERFACE = "/interface.js";

  private static RequestHandlerFactory instance = new RequestHandlerFactory();

  private Map<String,RequestHandler> handlers = new HashMap<String,RequestHandler>();

  private RequestHandlerFactory()
  {
    registerHandler(REQUEST_PATH_EXECUTE, new ExecutionHandler());
    registerHandler(REQUEST_PATH_SUBSCRIPTION, new SubscriptionHandler());
    registerHandler(REQUEST_PATH_POLL, new PollHandler());
    registerHandler(REQUEST_PATH_INTERFACE, new InterfaceGenerator());
  }

  public void registerHandler(String path, RequestHandler handler)
  {
    handlers.put(path, handler);
  }

  public RequestHandler getRequestHandler(String path)
  {
    return handlers.get(path);
  }

  public static RequestHandlerFactory getInstance()
  {
    return instance;
  }
}
