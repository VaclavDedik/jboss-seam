package org.jboss.seam.mail;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.core.AbstractMutable;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

@Name("org.jboss.seam.mail.mailSession")
@Install(precedence=BUILT_IN)
@Scope(APPLICATION)
@Intercept(InterceptionType.NEVER)
public class MailSession extends AbstractMutable implements Serializable
{
   
   private static final LogProvider log = Logging.getLogProvider(MailSession.class);

	private Session session;

	private String host = "localhost";
   private Integer port = 25;
	private String username;
	private String password;
	private boolean debug = false;

   @Unwrap
   public Session getSession()
   {
      return session;
   }

   @Create
	public void create()
	{
      log.info("connecting to mail server: " + getHost() + ':' + getPort());
      
		Properties properties = new Properties();

		// Enable debugging if set
		properties.put("mail.debug", isDebug());

		if ( getHost()!=null )
		{
			properties.put("mail.host", getHost());
		}
		if ( getPort()!=null ) {
			properties.put("mail.smtp.port", getPort());
			properties.put("mail.imap.port", getPort());
			properties.put("mail.pop3.port", getPort());
		}

		if ( getUsername()!=null && getPassword()==null )
		{
			log.warn("username supplied without a password (if an empty password is required supply an empty string)");
		}
		if ( getUsername()==null && getPassword()!=null )
		{
			log.warn("password supplied without a username (if no authentication required supply neither)");
		}

		// Authentication if required
		Authenticator authenticator = null;
		if ( getUsername()!=null && getPassword()!=null )
		{
			properties.put("mail.smtp.auth", "true");
			authenticator = new Authenticator()
			{
				@Override
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(getUsername(), getPassword());
				}
			};
		}
      
      
      // Use TLS (if supported) by default.
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.imap.starttls.enable", "true");

		session = javax.mail.Session.getInstance(properties, authenticator);
		session.setDebug( isDebug() );
      
      log.info("connected to mail server");
	}

	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password
	 *            The password to use to authenticate to the sending server. If
	 *            no authentication is required it should be left empty. Must be
	 *            supplied in conjunction with username.
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getUsername()
	{
		return username;
	}

	/**
	 * @param username
	 *            The username to use to authenticate to the server. If not set
	 *            then no authentication is used. Must be set in conjunction
	 *            with password.
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	public boolean isDebug()
	{
		return debug;
	}

	/**
	 * @param debug
	 *            Whether to display debug message logging. Warning, very
	 *            verbose.
	 */
	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public String getHost()
	{
		return host;
	}

	/**
	 * @param host
	 *            The host to connect to. Used unless overriden by a protocol
	 *            specific host
	 */
	public void setHost(String host)
	{
		this.host = host;
	}

	public void setPort(Integer port)
	{
		this.port = port;
	}
	
	public Integer getPort()
	{
		return port;
	}
   
   public static Session instance() {
      return (Session) Component.getInstance(MailSession.class);
   }

}
