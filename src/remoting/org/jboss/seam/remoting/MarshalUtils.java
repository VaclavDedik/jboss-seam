package org.jboss.seam.remoting;

import java.io.OutputStream;
import java.io.IOException;
import org.jboss.seam.remoting.wrapper.BeanWrapper;
import org.jboss.seam.remoting.wrapper.Wrapper;
import java.util.List;

/**
 *
 *
 * @author Shane Bryzak
 */
public class MarshalUtils
{
  private static final byte[] RESULT_TAG_OPEN_START = "<result id=\"".getBytes();
  private static final byte[] RESULT_TAG_OPEN_END = "\">".getBytes();
  private static final byte[] RESULT_TAG_OPEN = "<result>".getBytes();
  private static final byte[] RESULT_TAG_CLOSE = "</result>".getBytes();

  private static final byte[] VALUE_TAG_OPEN = "<value>".getBytes();
  private static final byte[] VALUE_TAG_CLOSE = "</value>".getBytes();

  public static void marshalResult(String callId, CallContext ctx, OutputStream out,
                                   Object result, List<String> constraints)
      throws IOException
  {
    if (callId != null)
    {
      out.write(RESULT_TAG_OPEN_START);
      out.write(callId.getBytes());
      out.write(RESULT_TAG_OPEN_END);
    }
    else
      out.write(RESULT_TAG_OPEN);

    out.write(VALUE_TAG_OPEN);

    ctx.createWrapperFromObject(result, "").marshal(out);

    out.write(VALUE_TAG_CLOSE);

    out.write(RequestHandler.REFS_TAG_OPEN);

    // Using a for-loop, because stuff can get added to outRefs as we recurse the object graph
    for (int i = 0; i < ctx.getOutRefs().size(); i++)
    {
      Wrapper wrapper = ctx.getOutRefs().get(i);

      out.write(RequestHandler.REF_TAG_OPEN_START);
      out.write(Integer.toString(i).getBytes());
      out.write(RequestHandler.REF_TAG_OPEN_END);

      if (wrapper instanceof BeanWrapper && constraints != null)
        ((BeanWrapper) wrapper).serialize(out, constraints);
      else
        wrapper.serialize(out);

      out.write(RequestHandler.REF_TAG_CLOSE);
    }

    out.write(RequestHandler.REFS_TAG_CLOSE);
    out.write(RESULT_TAG_CLOSE);
  }
}
