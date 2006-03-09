package org.jboss.seam.remoting;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.logging.Logger;
import org.jboss.seam.remoting.messaging.SubscriptionRequest;

/**
 *
 * @author Shane Bryzak
 */
public class SubscriptionHandler extends BaseRequestHandler implements RequestHandler
{
  private static Logger log = Logger.getLogger(SubscriptionHandler.class);

  /**
   * The entry point for handling a request.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @throws Exception
   */
  public void handle(HttpServletRequest request, HttpServletResponse response)
      throws Exception
  {
    // We're sending an XML response, so set the response content type to text/xml
    response.setContentType("text/xml");

    // Parse the incoming request as XML
    SAXReader xmlReader = new SAXReader();
    Document doc = xmlReader.read(request.getInputStream());
    Element env = doc.getRootElement();

    // Extract the subscriptions from the request
    List<SubscriptionRequest> subscriptions = unmarshalRequest(env);
    for (SubscriptionRequest req : subscriptions)
      req.subscribe();

    // Package up the response
    marshalResponse(subscriptions, response.getOutputStream());
  }

  /**
   *
   * @param env Element
   * @return List
   */
  private List<SubscriptionRequest> unmarshalRequest(Element env)
      throws Exception
  {
    try
    {
      List<SubscriptionRequest> requests = new ArrayList<SubscriptionRequest>();

      List<Element> requestElements = env.element("body").elements("subscribe");
      for (Element e : requestElements)
      {
        requests.add(new SubscriptionRequest(e.attributeValue("topic")));
      }

      return requests;
    }
    catch (Exception ex)
    {
      log.error("Error unmarshalling subscriptions from request", ex);
      throw ex;
    }
  }

  private void marshalResponse(List<SubscriptionRequest> requests, OutputStream out)
      throws IOException
  {
    out.write(ENVELOPE_TAG_OPEN);
    out.write(BODY_TAG_OPEN);

    for (SubscriptionRequest req : requests)
    {
      req.marshal(out);
    }

    out.write(BODY_TAG_CLOSE);
    out.write(ENVELOPE_TAG_CLOSE);
    out.flush();
  }

}
