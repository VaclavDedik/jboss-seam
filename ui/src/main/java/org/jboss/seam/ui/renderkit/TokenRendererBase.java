package org.jboss.seam.ui.renderkit;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpSession;

import org.jboss.seam.ui.UnauthorizedCommandException;
import org.jboss.seam.ui.component.UIToken;
import org.jboss.seam.ui.util.HTML;
import org.jboss.seam.ui.util.cdk.RendererBase;
import org.jboss.seam.util.Base64;
import org.jboss.seam.util.RandomStringUtils;

/**
 * <p>
 * The <strong>TokenRendererBase</strong> renders the form's signature as a
 * hidden form field for the UIToken component.
 * </p>
 * 
 * <p>
 * The form signature is calculated as follows:
 * </p>
 * 
 * <pre>
 * sha1(signature = contextPath + viewId + &quot;,&quot; + formClientId + random alphanum, salt = clientUid)
 * </pre>
 * 
 * <p>
 * The developer can also choose to incorporate the session id into this hash to
 * generate a more secure token (at the cost of binding it to the session) by
 * setting the requireSession attribute to true. Then the calculation becomes:
 * </p>
 * 
 * <pre>
 * sha1(signature = contextPath + viewId + &quot;,&quot; + formClientId + &quot;,&quot; + random alphanum + sessionId, salt = clientUid)
 * </pre>
 * 
 * <p>The decode method performs the following steps:</p>
 * <ol>
 * <li>check if this is a postback, otherwise skip the check</li>
 * <li>check that this form was the one that was submitted, otherwise skip the check</li>
 * <li>get the unique client identifier (from cookie), otherwise throw an exception that the browser must have unique identifier</li>
 * <li>get the javax.faces.FormSignature request parameter, otherwise throw an exception that the form signature is missing</li>
 * <li>generate the hash as before and verify that it equals the value of the javax.faces.FormSignature request parameter, otherwise throw an exception</li>
 * </ol>
 * 
 * <p>If all of that passes, we are okay to process the form (advance to validate phase as decode() is called in apply request values).</p>
 * 
 * @author Dan Allen
 * @see UnauthorizedCommandException
 */
public class TokenRendererBase extends RendererBase
{
   public static final String FORM_SIGNATURE_PARAM = "javax.faces.FormSignature";

   public static final String RENDER_STAMP_ATTR = "javax.faces.RenderStamp";
   
   private static final String COOKIE_CHECK_SCRIPT_KEY = "org.jboss.seam.ui.COOKIE_CHECK_SCRIPT";

   @Override
   protected Class getComponentClass()
   {
      return UIToken.class;
   }

   @Override
   protected void doDecode(FacesContext context, UIComponent component)
   {
      UIToken token = (UIToken) component;
      UIForm form = token.getParentForm();
      if (context.getRenderKit().getResponseStateManager().isPostback(context) && form.isSubmitted())
      {
         String clientToken = token.getClientUid();
         String viewId = context.getViewRoot().getViewId();
         if (clientToken == null)
         {
            throw new UnauthorizedCommandException(viewId, "No client identifier provided");
         }

         String requestedViewSig = context.getExternalContext().getRequestParameterMap().get(FORM_SIGNATURE_PARAM);
         if (requestedViewSig == null)
         {
            throw new UnauthorizedCommandException(viewId, "No form signature provided");
         }

         if (!requestedViewSig.equals(generateViewSignature(context, form, token.isRequireSession(), clientToken)))
         {
            throw new UnauthorizedCommandException(viewId, "Form signature invalid");
         }

         form.getAttributes().remove(RENDER_STAMP_ATTR);
      }
   }

   @Override
   protected void doEncodeBegin(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
   {
      UIToken token = (UIToken) component;
      UIForm form = token.getParentForm();
      if (form == null)
      {
         throw new IllegalStateException("UIToken must be inside a UIForm.");
      }
      
      writeCookieCheckScript(context, writer, token);

      token.getClientUidSelector().seed();
      form.getAttributes().put(RENDER_STAMP_ATTR, RandomStringUtils.randomAlphanumeric(50));
      writer.startElement(HTML.INPUT_ELEM, component);
      writer.writeAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_HIDDEN, HTML.TYPE_ATTR);
      writer.writeAttribute(HTML.NAME_ATTR, FORM_SIGNATURE_PARAM, HTML.NAME_ATTR);
      writer.writeAttribute(HTML.VALUE_ATTR, generateViewSignature(context, form, token.isRequireSession(), token.getClientUidSelector().getClientUid()), HTML.VALUE_ATTR);
      writer.endElement(HTML.INPUT_ELEM);
   }

   /**
    * If the client has not already delivered us a cookie and the cookie notice is enabled, write out JavaScript that will show the user
    * an alert if cookies are not enabled.
    */
   private void writeCookieCheckScript(FacesContext context, ResponseWriter writer, UIToken token) throws IOException
   {
      if (!token.getClientUidSelector().isSet() && token.isEnableCookieNotice() && !context.getExternalContext().getRequestMap().containsKey(COOKIE_CHECK_SCRIPT_KEY)) {
         writer.startElement(HTML.SCRIPT_ELEM, token);
         writer.writeAttribute(HTML.TYPE_ATTR, "text/javascript", HTML.TYPE_ATTR);
         writer.write("if (!document.cookie) {" +
            " alert('This website uses a security measure that requires cookies to be enabled in your browser. Since you have cookies disabled, you will not be permitted to submit a form.');" +
            " }");
         writer.endElement(HTML.SCRIPT_ELEM);
         context.getExternalContext().getRequestMap().put(COOKIE_CHECK_SCRIPT_KEY, true);
      }
   }

   private String generateViewSignature(FacesContext context, UIForm form, boolean useSessionId, String saltPhrase)
   {
      String rawViewSignature = context.getExternalContext().getRequestContextPath() + "," + context.getViewRoot().getViewId() + "," + form.getClientId(context) + "," + form.getAttributes().get(RENDER_STAMP_ATTR);
      if (useSessionId)
      {
         rawViewSignature += "," + ((HttpSession) context.getExternalContext().getSession(true)).getId();
      }
      try
      {
         MessageDigest digest = MessageDigest.getInstance("SHA-1");
         digest.update(saltPhrase.getBytes());
         byte[] salt = digest.digest();
         digest.reset();
         digest.update(rawViewSignature.getBytes());
         digest.update(salt);
         byte[] raw = digest.digest();
         return Base64.encodeBytes(raw);
      }
      catch (NoSuchAlgorithmException ex)
      {
         ex.printStackTrace();
         return null;
      }
   }
   
}
