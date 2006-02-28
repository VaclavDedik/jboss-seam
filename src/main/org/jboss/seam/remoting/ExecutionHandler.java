package org.jboss.seam.remoting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.logging.Logger;
import org.jboss.seam.remoting.wrapper.Wrapper;

/**
 * Unmarshals the calls from an HttpServletRequest, executes them in order and
 * marshals the responses.
 *
 * @author Shane Bryzak
 */
public class ExecutionHandler
{
  private static Logger log = Logger.getLogger(ExecutionHandler.class);

  private static final byte[] CONTEXT_TAG_OPEN = "<context>".getBytes();
  private static final byte[] CONTEXT_TAG_CLOSE = "</context>".getBytes();
  private static final byte[] CONVERSATION_ID_TAG_OPEN = "<conversationId>".getBytes();
  private static final byte[] CONVERSATION_ID_TAG_CLOSE = "</conversationId>".getBytes();
  private static final byte[] VALUE_TAG_OPEN = "<value>".getBytes();
  private static final byte[] VALUE_TAG_CLOSE = "</value>".getBytes();
  private static final byte[] ENVELOPE_TAG_OPEN = "<envelope>".getBytes();
  private static final byte[] ENVELOPE_TAG_CLOSE = "</envelope>".getBytes();
  private static final byte[] BODY_TAG_OPEN = "<body>".getBytes();
  private static final byte[] BODY_TAG_CLOSE = "</body>".getBytes();
  private static final byte[] REFS_TAG_OPEN = "<refs>".getBytes();
  private static final byte[] REFS_TAG_CLOSE = "</refs>".getBytes();

  private static final byte[] REF_TAG_OPEN_START = "<ref id=\"".getBytes();
  private static final byte[] REF_TAG_OPEN_END = "\">".getBytes();
  private static final byte[] REF_TAG_CLOSE = "</ref>".getBytes();

  private static final byte[] RESULT_TAG_OPEN_START = "<result id=\"".getBytes();
  private static final byte[] RESULT_TAG_OPEN_END = "\">".getBytes();
  private static final byte[] RESULT_TAG_CLOSE = "</result>".getBytes();

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
    // Extract the calls from the request
    List<Call> calls = unmarshalRequest(request.getInputStream());

    // Execute each of the calls
    for (Call call : calls) {
      call.execute();
    }

    response.setContentType("text/xml");

    // Package up the response
    marshalResponse(calls, response.getOutputStream());
  }

  /**
   * Unmarshal the request into a list of Calls.
   *
   * @param in InputStream
   * @throws Exception
   */
  private List<Call> unmarshalRequest(InputStream in) throws Exception
  {
    try {
      List<Call> calls = new ArrayList<Call>();

      SAXReader xmlReader = new SAXReader();
      Document doc = xmlReader.read(in);

      Element env = doc.getRootElement();

      List<Element> callElements = env.element("body").elements("call");

      for (Element e : callElements) {
        Call call = new Call(e.attributeValue("id"),
                             e.attributeValue("component"),
                             e.attributeValue("method"));

        // First reconstruct all the references
        Element refsNode = e.element("refs");

        Iterator iter = refsNode.elementIterator("ref");
        while (iter.hasNext())
        {
          call.getContext().createWrapperFromElement((Element) iter.next());
        }

        // Now unmarshal the ref values
        for (Wrapper w : call.getContext().getInRefs().values())
          w.unmarshal();

        Element paramsNode = e.element("params");

        // Then process the param values
        iter = paramsNode.elementIterator("param");
        while (iter.hasNext()) {
          Element param = (Element) iter.next();

          call.addParameter(call.getContext().createWrapperFromElement(
            (Element) param.elementIterator().next()));
        }

        calls.add(call);
      }

      return calls;
    }
    catch (Exception ex) {
      log.error("Error unmarshalling request", ex);
      throw ex;
    }
  }

  /**
   * Write the results to the output stream.
   *
   * @param calls List The list of calls to write
   * @param out OutputStream The stream to write to
   * @throws IOException
   */
  private void marshalResponse(List<Call> calls, OutputStream out)
      throws IOException
  {
    out.write(ENVELOPE_TAG_OPEN);
    out.write(BODY_TAG_OPEN);

    for (Call call : calls)
    {
      out.write(RESULT_TAG_OPEN_START);
      out.write(call.getId().getBytes());
      out.write(RESULT_TAG_OPEN_END);

      out.write(CONTEXT_TAG_OPEN);

      if (call.getContext().getConversationId() != null)
      {
        out.write(CONVERSATION_ID_TAG_OPEN);
        out.write(call.getContext().getConversationId().getBytes());
        out.write(CONVERSATION_ID_TAG_CLOSE);
      }
      out.write(CONTEXT_TAG_CLOSE);

      out.write(VALUE_TAG_OPEN);
      call.getContext().createWrapperFromObject(call.getResult()).marshal(out);
      out.write(VALUE_TAG_CLOSE);

      out.write(REFS_TAG_OPEN);

      // Using a for-loop, because stuff can get added to outRefs as we recurse the object graph
      for (int i = 0; i < call.getContext().getOutRefs().size(); i++)
      {
        Wrapper wrapper = call.getContext().getOutRefs().get(i);

        out.write(REF_TAG_OPEN_START);
        out.write(Integer.toString(i).getBytes());
        out.write(REF_TAG_OPEN_END);

        wrapper.serialize(out);

        out.write(REF_TAG_CLOSE);
      }

      out.write(REFS_TAG_CLOSE);
      out.write(RESULT_TAG_CLOSE);
    }

    out.write(BODY_TAG_CLOSE);
    out.write(ENVELOPE_TAG_CLOSE);
    out.flush();
  }
}
