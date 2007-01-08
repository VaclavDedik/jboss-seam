package org.jboss.seam.mail;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.log.Log;

/**
 * Class represents an email message.
 * The email can be sent using an SMTP session via the send() method
 * 
 * @author Peter Muir
 *
 */
@Scope(CONVERSATION)
@Install(precedence = BUILT_IN)
public class MimeMessage implements Serializable
{
	public static final String HTML = "HTML";
	public static final String PLAIN = "PLAIN";

   private String failedSendMessage = "Email sending failed";
	private String successfulSendMessage = "Email successfully sent";
	private String errorProcessingSubjectMessage = "Error processing subject";
	private String errorProcessingBodyMessage = "Error processing body";
	private String errorProcessingReplyToMessage = "Error processing reply-to address";
	private String errorProcessingFromMessage = "Error processing from address";
	private String errorProcessingBccMessage = "Error processing bcc address";
	private String errorProcessingCcMessage = "Error processing cc address";
	private String errorProcessingToMessage = "Error processing to address";
	private String errorProcessingHeaderMessage = "Error processing header";
	
	@Logger
	private Log log;

	@In(create = true)
	private FacesMessages facesMessages;

	private Session session;

	private Pattern htmlTagPattern = Pattern.compile("</?(\\w)(\\s\\w=(\\w|\"[^\"]*\"|'[^']*'))*>");

   private List<String> to = new ArrayList<String>();
   private List<String> cc = new ArrayList<String>();
   private List<String> bcc = new ArrayList<String>();
   private String from;
   private String replyTo;

   private List<String> headers = new ArrayList<String>();

   private String subject;
   private String body;
   private String signatureSeparator = "--";
	private String signature;
	private String type = HTML;

	public List<String> getHeaders()
	{
		return headers;
	}

	public void setHeaders(List<String> headers)
	{
		this.headers = headers;
	}

	public String getReplyTo()
	{
		return replyTo;
	}

	public void setReplyTo(String replyTo)
	{
		this.replyTo = replyTo;
	}

	private InternetAddress getInternetAddress(String address)
			throws AddressException, UnsupportedEncodingException
	{
		InternetAddress internetAddress;
		if (address.indexOf("<") > 0
				&& address.indexOf("<") + 2 < address.length())
		{
			String name = address.substring(0, address.indexOf("<") - 1);
			String a = address.substring(address.indexOf("<") + 1, address
					.length() - 1);
			internetAddress = new InternetAddress(a, name);
		} 
      else
		{
			internetAddress = new InternetAddress(address);
		}
		internetAddress.validate();
		return internetAddress;
	}

	protected void errorProcessingBodyMessage(Exception e)
	{
		facesMessages.addFromResourceBundle(SEVERITY_ERROR,
				getErrorProcessingBodyMessageKey(),
				getErrorProcessisngBodyMessage());
		log.debug("Error processing subject", e);

	}

	protected String getErrorProcessingBodyMessageKey()
	{
		return "org.jboss.seam.mail.errorProcessingBody";
	}

	protected String getErrorProcessisngBodyMessage()
	{
		return errorProcessingBodyMessage;
	}

	protected void errorProcessingSubjectMessage(Exception e)
	{
		facesMessages.addFromResourceBundle(SEVERITY_ERROR,
				getErrorProcessingSubjectMessageKey(),
				getErrorProcessingSubjectMessage());
		log.debug("Error processing subject", e);

	}

	protected String getErrorProcessingSubjectMessageKey()
	{
		return "org.jboss.seam.mail.errorProcessingSubject";
	}

	protected void sendingFailedMessage(Exception e)
	{
		facesMessages.addFromResourceBundle(SEVERITY_ERROR,
				getSendingFailedMessageKey(), getSendingFailedMessage());
		log.debug("Error sending email", e);
	}

	public List<String> getBcc()
	{
		return bcc;
	}

	public String getBody()
	{
		return body;
	}

	public List<String> getCc()
	{
		return cc;
	}

	public String getSendingFailedMessage()
	{
		return failedSendMessage;
	}

	protected String getSendingFailedMessageKey()
	{
		return "org.jboss.seam.mail.sendingFailed";
	}

	public String getFrom()
	{
		return from;
	}
	private MimeBodyPart wrapHtml(String content) throws MessagingException {
		MimeBodyPart html = new MimeBodyPart();
		html.setContent("<html><title></title><body>"  + content + "</body></html>", "text/html");
		return html;
	}
	
	private String getHtmlSignature() throws MessagingException
	{
		return getSignature() == null ? "" : "<br />" + getSignatureSeparator() + "<br />" + getSignature();
	}
	
	private String getTextSignature() throws MessagingException
	{
		return getSignature() == null ? "" :"\n" + getSignatureSeparator() + "\n" + getSignature();
	}

	private Multipart getHtmlBody() throws MessagingException
	{
		if ( !htmlTagPattern.matcher( getBody() ).find() )
		{
			BodyPart text = new MimeBodyPart();
			text.setText(getBody() + getTextSignature());
			Multipart multipart = new MimeMultipart("alternative");
			multipart.addBodyPart(wrapHtml(getBody() + getHtmlSignature()));
			multipart.addBodyPart(text);
			return multipart;
		} 
		else
		{
			Multipart multipart = new MimeMultipart("related");
			if (getBody().indexOf("<html>") <= 0
					&& getBody().indexOf("<body>") <= 0)
			{
				multipart.addBodyPart(wrapHtml(getBody() + getHtmlSignature()));
			} 
			else
			{
				MimeBodyPart html = new MimeBodyPart();
				html.setContent(getBody(), "text/html");
				html.setDisposition(Part.INLINE);
				multipart.addBodyPart(html);
			}
			if ( getSignature()!=null ) 
			{
				MimeBodyPart signature = wrapHtml(getHtmlSignature());
				signature.setDisposition(Part.INLINE);
				signature.setContentID("signature");
				multipart.addBodyPart(signature);
			}	
			return multipart;
		}
	}

	/**
	 * Get the underlying JavaMail Message 
	 * 
	 * @throws MessagingException 
	 */
	public javax.mail.internet.MimeMessage getMimeMessage() throws MessagingException
	{
		javax.mail.internet.MimeMessage mimeMessage = new javax.mail.internet.MimeMessage(session);
		boolean ok = true;
		for (String bcc : getBcc())
		{
			try
			{
				mimeMessage.addRecipient(RecipientType.BCC, getInternetAddress(bcc));
			} 
         catch (Exception e)
			{
				ok = false;
				errorProcessingBccMessage(bcc, e);
			}
		}
		for (String cc : getCc())
		{
			try
			{
				mimeMessage.addRecipient(RecipientType.CC, getInternetAddress(cc));
			} 
			catch (Exception e)
			{
				ok = false;
				errorProcessingCcMessage(cc, e);
			}
		}
		for (String to: getTo())
		{
			try
			{
				mimeMessage.addRecipient(RecipientType.TO, getInternetAddress(to));
			} 
         catch (Exception e)
			{
				ok = false;
				errorProcessingToMessage(to, e);
			}
		}
		if ( getBody()!=null )
		{
			String body = getBody();
			try
			{
				if ( MimeMessage.PLAIN.equals( getType() ) )
				{
					mimeMessage.setText(body + getTextSignature());
				} 
            else if ( MimeMessage.HTML.equals( getType() ) )
				{
					mimeMessage.setContent( getHtmlBody() );
				}
			} 
         catch (Exception e)
			{
				ok = false;
				errorProcessingBodyMessage(e);
			}
		}
		if ( getFrom()!=null )
		{
			try
			{
				mimeMessage.setFrom( getInternetAddress( getFrom() ) );
			} 
         catch (Exception e)
			{
				ok = false;
				errorProcessingFromMessage(e);
			}
		}
		if ( getSubject()!=null )
		{
			try
			{
				mimeMessage.setSubject( getSubject() );
			} 
         catch (Exception e)
			{
				ok = false;
				errorProcessingSubjectMessage(e);
			}
		}
		for (String header : getHeaders())
		{
			if (header.indexOf(":") > 0)
			{
				try
				{
					String headerName = header.substring(0, header.indexOf(":") - 1);
					String headerValue = header.substring(header.indexOf(":"));
					mimeMessage.setHeader(headerName, headerValue);
				} 
            catch (Exception e)
				{
					ok = false;
					errorProcessingHeaderMessage(header, e);
				}
			}
		}
		if ( getReplyTo()!=null )
		{
			try
			{
				mimeMessage.setReplyTo(new Address[] { getInternetAddress(getReplyTo()) });
			} 
         catch (Exception e)
			{
				ok = false;
				errorProcessingReplyToMessage(e);
			}
		}
		if (!ok) 
      {
			throw new MessagingException();
		}
		return mimeMessage;
	}

	protected void errorProcessingReplyToMessage(Exception e)
	{
		facesMessages.addFromResourceBundle(SEVERITY_ERROR,
				getErrorProcessingReplyToMessageKey(),
				getErrorProcessisngReplyToMessage());
		log.debug("Error processing Reply-to address", e);

	}

	protected String getErrorProcessisngReplyToMessage()
	{
		return errorProcessingReplyToMessage;
	}

	protected String getErrorProcessingReplyToMessageKey()
	{
		return "org.jboss.seam.mail.errorProcessingReplyTo";
	}

	private void errorProcessingFromMessage(Exception e)
	{
		facesMessages.addFromResourceBundle(SEVERITY_ERROR,
				getErrorProcessingFromMessageKey(),
				getErrorProcessisngFromMessage());
		log.debug("Error processing From address", e);

	}

	protected String getErrorProcessingFromMessageKey()
	{
		return "org.jboss.seam.mail.errorProcessingFrom";
	}

	protected String getErrorProcessisngFromMessage()
	{
		return errorProcessingFromMessage;
	}

	protected void errorProcessingBccMessage(String bcc, Exception e)
	{
		facesMessages.addFromResourceBundle(SEVERITY_ERROR,
				getErrorProcessingBccMessageKey() + " (" + bcc + ")",
				getErrorProcessisngBccMessage() + " (" + bcc + ")");
		log.debug("Error processing Bcc address: " + bcc, e);

	}

	protected String getErrorProcessingBccMessageKey()
	{
		return "org.jboss.seam.mail.errorProcessingBcc";
	}

	protected String getErrorProcessisngBccMessage()
	{
		return errorProcessingBccMessage;
	}

	private void errorProcessingCcMessage(String cc, Exception e)
	{
		facesMessages.addFromResourceBundle(SEVERITY_ERROR,
				getErrorProcessingCcMessageKey() + " (" + cc + ")",
				getErrorProcessisngCcMessage() + " (" + cc + ")");
		log.debug("Error processing Cc address: " + cc, e);

	}

	private String getErrorProcessingCcMessageKey()
	{
		return "org.jboss.seam.mail.errorProcessingCc";
	}

	private String getErrorProcessisngCcMessage()
	{
		return errorProcessingCcMessage;
	}

	private void errorProcessingToMessage(String to, Exception e)
	{
		facesMessages.addFromResourceBundle(SEVERITY_ERROR,
				getErrorProcessingToMessageKey() + " (" + to + ")",
				getErrorProcessisngToMessage() + " (" + to + ")");
		log.debug("Error processing To address: " + to, e);
	}

	private String getErrorProcessingToMessageKey()
	{
		return "org.jboss.seam.mail.errorProcessingTo";
	}

	private String getErrorProcessisngToMessage()
	{
		return errorProcessingToMessage;
	}

	private void errorProcessingHeaderMessage(String header, Exception e)
	{
		facesMessages.addFromResourceBundle(SEVERITY_ERROR,
				getErrorProcessingHeaderMessageKey() + " (" + header + ")",
				getErrorProcessisngHeaderMessage() + " (" + header + ")");
		log.debug("Error processing header: " + to, e);
	}

	private String getErrorProcessingHeaderMessageKey()
	{
		return "org.jboss.seam.mail.errorProcessingHeader";
	}

	private String getErrorProcessisngHeaderMessage()
	{
		return errorProcessingHeaderMessage;
	}

	/**
	 * Get the underlying JavaMail session which will be used to send this message
	 */
	public Session getSession()
	{
      //TODO: JNDI lookup of the mail session
		if (session == null)
		{
			session = (Session) Component.getInstance( Seam.getComponentName(MailSession.class) );
		}
		return session;
	}
   
   public void setSession(Session session)
   {
      this.session = session;
   }

	/**
	 * The signature is appended to the end of the email
	 */
	public String getSignature()
	{
		return signature;
	}

	/**
	 * The subject of the email
	 * 
	 */
	public String getSubject()
	{
		return subject;
	}

	public String getSuccessfulSendMessage()
	{
		return successfulSendMessage;
	}

	protected String getSuccessfulSendMessageKey()
	{
		return "org.jboss.seam.mail.sucessfulSend";
	}

	/**
	 * A list of recipients of the message
	 * 
	 */
	public List<String> getTo()
	{
		return to;
	}

	/**
	 * The type of the message - HTML or PLAIN
	 * 
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Send the message
	 *
	 */
	public void send()
	{
		try
		{
         javax.mail.internet.MimeMessage message = getMimeMessage();
         message.saveChanges();
         Transport.send(message);
			successfulSendMessage();
		} 
      catch (Exception e)
		{
			sendingFailedMessage(e);
		}
	}

	public void setBcc(List<String> bcc)
	{
		this.bcc = bcc;
	}

	public void setBody(String body)
	{
		this.body = body;
	}

	public void setCc(List<String> cc)
	{
		this.cc = cc;
	}

	public void setFailedSendMessage(String failedSendMessage)
	{
		this.failedSendMessage = failedSendMessage;
	}

	public void setFrom(String fromAddress)
	{
		this.from = fromAddress;
	}

	public void setSignature(String signature)
	{
		this.signature = signature;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public void setSuccessfulSendMessage(String successfulSendMessage)
	{
		this.successfulSendMessage = successfulSendMessage;
	}

	public void setTo(List<String> to)
	{
		this.to = to;
	}

	public void setType(String type)
	{
		type = type.toUpperCase();
		if (PLAIN.equals(type) || HTML.equals(type))
		{
			this.type = type;
		}
	}

	protected void successfulSendMessage()
	{
		facesMessages.addFromResourceBundle(SEVERITY_INFO,
				getSuccessfulSendMessageKey(), getSuccessfulSendMessage());
	}

	public String getErrorProcessingBccMessage()
	{
		return errorProcessingBccMessage;
	}

	public void setErrorProcessingBccMessage(String errorProcessingBccMessage)
	{
		this.errorProcessingBccMessage = errorProcessingBccMessage;
	}

	public String getErrorProcessingBodyMessage()
	{
		return errorProcessingBodyMessage;
	}

	public void setErrorProcessingBodyMessage(String errorProcessingBodyMessage)
	{
		this.errorProcessingBodyMessage = errorProcessingBodyMessage;
	}

	public String getErrorProcessingCcMessage()
	{
		return errorProcessingCcMessage;
	}

	public void setErrorProcessingCcMessage(String errorProcessingCcMessage)
	{
		this.errorProcessingCcMessage = errorProcessingCcMessage;
	}

	public String getErrorProcessingFromMessage()
	{
		return errorProcessingFromMessage;
	}

	public void setErrorProcessingFromMessage(String errorProcessingFromMessage)
	{
		this.errorProcessingFromMessage = errorProcessingFromMessage;
	}

	public String getErrorProcessingHeaderMessage()
	{
		return errorProcessingHeaderMessage;
	}

	public void setErrorProcessingHeaderMessage(String errorProcessingHeaderMessage)
	{
		this.errorProcessingHeaderMessage = errorProcessingHeaderMessage;
	}

	public String getErrorProcessingReplyToMessage()
	{
		return errorProcessingReplyToMessage;
	}

	public void setErrorProcessingReplyToMessage(
			String errorProcessingReplyToMessage)
	{
		this.errorProcessingReplyToMessage = errorProcessingReplyToMessage;
	}

	public String getErrorProcessingSubjectMessage()
	{
		return errorProcessingSubjectMessage;
	}

	public void setErrorProcessingSubjectMessage(
			String errorProcessingSubjectMessage)
	{
		this.errorProcessingSubjectMessage = errorProcessingSubjectMessage;
	}

	public String getErrorProcessingToMessage()
	{
		return errorProcessingToMessage;
	}

	public void setErrorProcessingToMessage(String errorProcessingToMessage)
	{
		this.errorProcessingToMessage = errorProcessingToMessage;
	}

	public String getSignatureSeparator()
	{
		return signatureSeparator;
	}

	public void setSignatureSeparator(String signatureSeparator)
	{
		this.signatureSeparator = signatureSeparator;
	}
	

}
