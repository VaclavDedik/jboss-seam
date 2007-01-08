package org.jboss.seam.mail;

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
import org.jboss.seam.InterceptionType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;

/**
 * Class represents an email message.
 * The email can be sent using an SMTP session via the send() method
 * 
 * @author Peter Muir
 *
 */
@Scope(CONVERSATION)
@Install(precedence = BUILT_IN)
@Intercept(InterceptionType.NEVER)
public class MimeMessage implements Serializable
{
	public static final String HTML = "HTML";
	public static final String PLAIN = "PLAIN";

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
	public javax.mail.internet.MimeMessage getMimeMessage() throws MessagingException, UnsupportedEncodingException
	{
		javax.mail.internet.MimeMessage mimeMessage = new javax.mail.internet.MimeMessage(session);
		for (String bcc : getBcc())
		{
				mimeMessage.addRecipient(RecipientType.BCC, getInternetAddress(bcc));
		}
		for (String cc : getCc())
		{
				mimeMessage.addRecipient(RecipientType.CC, getInternetAddress(cc));
		}
		for (String to: getTo())
		{
				mimeMessage.addRecipient(RecipientType.TO, getInternetAddress(to));
		}
		if ( getBody()!=null )
		{
			String body = getBody();
				if ( MimeMessage.PLAIN.equals( getType() ) )
				{
					mimeMessage.setText(body + getTextSignature());
				} 
            else if ( MimeMessage.HTML.equals( getType() ) )
				{
					mimeMessage.setContent( getHtmlBody() );
				}
		}
		if ( getFrom()!=null )
		{
				mimeMessage.setFrom( getInternetAddress( getFrom() ) );
		}
		if ( getSubject()!=null )
		{
				mimeMessage.setSubject( getSubject() );
		}
		for (String header : getHeaders())
		{
			if (header.indexOf(":") > 0)
			{
					String headerName = header.substring(0, header.indexOf(":") - 1);
					String headerValue = header.substring(header.indexOf(":"));
					mimeMessage.setHeader(headerName, headerValue);
			}
		}
		if ( getReplyTo()!=null )
		{
				mimeMessage.setReplyTo(new Address[] { getInternetAddress( getReplyTo() ) });
		}
		return mimeMessage;
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
	public void send() throws MessagingException, UnsupportedEncodingException
	{
         javax.mail.internet.MimeMessage message = getMimeMessage();
         message.saveChanges();
         Transport.send(message);
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

	public String getSignatureSeparator()
	{
		return signatureSeparator;
	}

	public void setSignatureSeparator(String signatureSeparator)
	{
		this.signatureSeparator = signatureSeparator;
	}
	

}
