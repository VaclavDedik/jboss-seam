package org.jboss.seam.remoting;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.PhaseId;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.remoting.messaging.PollRequest;
import org.jboss.seam.remoting.wrapper.Wrapper;

/**
 *
 * @author Shane Bryzak
 */
public class PollHandler extends BaseRequestHandler implements RequestHandler
{
  private static final Log log = LogFactory.getLog(SubscriptionHandler.class);

  private ServletContext servletContext;

  public void setServletContext(ServletContext ctx)
  {
    this.servletContext = ctx;
  }

  public void handle(HttpServletRequest request, HttpServletResponse response)
      throws Exception
  {
    // We're sending an XML response, so set the response content type to text/xml
    response.setContentType("text/xml");

    // Parse the incoming request as XML
    SAXReader xmlReader = new SAXReader();
    Document doc = xmlReader.read(request.getInputStream());
    Element env = doc.getRootElement();

    List<PollRequest> polls = unmarshalRequests(env);

    try
    {
      HttpSession session = ((HttpServletRequest) request).getSession(true);
      Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
      Lifecycle.setServletRequest(request);
      Lifecycle.beginRequest(servletContext, session);

      for (PollRequest req : polls)
        req.poll();
    }
    finally
    {
      Lifecycle.endRequest();
      Lifecycle.setServletRequest(null);
      Lifecycle.setPhaseId(null);
    }


    // Package up the response
    marshalResponse(polls, response.getOutputStream());
  }


  private List<PollRequest> unmarshalRequests(Element env)
      throws Exception
  {
    try
    {
      List<PollRequest> requests = new ArrayList<PollRequest>();

      List<Element> requestElements = env.element("body").elements("poll");
      for (Element e : requestElements)
      {
        requests.add(new PollRequest(e.attributeValue("token"),
                                     Integer.parseInt(e.attributeValue("timeout"))));
      }

      return requests;
    }
    catch (Exception ex)
    {
      log.error("Error unmarshalling subscriptions from request", ex);
      throw ex;
    }
  }

  private void marshalResponse(List<PollRequest> reqs, OutputStream out)
      throws IOException
  {
    out.write(ENVELOPE_TAG_OPEN);
    out.write(BODY_TAG_OPEN);

    for (PollRequest req : reqs)
    {
      if (req.getMessages() != null && req.getMessages().size() > 0)
      {
        out.write("<messages token=\"".getBytes());
        out.write(req.getToken().getBytes());
        out.write("\">".getBytes());
        for (Message m : req.getMessages()) {
          try {
            writeMessage(m, out);
          }
          catch (JMSException ex) {
          }
          catch (IOException ex) {
          }
        }
        out.write("</messages>".getBytes());
      }
    }

    out.write(BODY_TAG_CLOSE);
    out.write(ENVELOPE_TAG_CLOSE);
    out.flush();
  }

  private void writeMessage(Message m, OutputStream out)
      throws IOException, JMSException
  {
    out.write("<message type=\"".getBytes());

    // We need one of these to maintain a list of outbound references
    CallContext ctx = new CallContext();
    Object value = null;

    if (m instanceof TextMessage)
    {
      out.write("text".getBytes());
      value = ((TextMessage) m).getText();
    }
    else if (m instanceof ObjectMessage)
    {
      out.write("object".getBytes());
      value = ((ObjectMessage) m).getObject();
    }

    out.write("\">".getBytes());

    out.write("<value>".getBytes());
    ctx.createWrapperFromObject(value).marshal(out);
    out.write("</value>".getBytes());

    out.write(REFS_TAG_OPEN);

    // Using a for-loop, because stuff can get added to outRefs as we recurse the object graph
    for (int i = 0; i < ctx.getOutRefs().size(); i++)
    {
      Wrapper wrapper = ctx.getOutRefs().get(i);

      out.write(REF_TAG_OPEN_START);
      out.write(Integer.toString(i).getBytes());
      out.write(REF_TAG_OPEN_END);

      wrapper.serialize(out);

      out.write(REF_TAG_CLOSE);
    }

    out.write(REFS_TAG_CLOSE);

    out.write("</message>".getBytes());
  }
}
