package org.jboss.seam.example.mail.test;


import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jboss.seam.mail.MailSession;
import org.jboss.seam.mail.MockTransport;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

/**
 * @author Pete Muir
 *
 */
public class MailTest extends SeamTest
{
    
    @Test
    public void testSimpleMessage() throws Exception
    {
        
        new FacesRequest()
        {

            @Override
            protected void updateModelValues() throws Exception
            {
                setValue("#{person.firstname}", "Pete");
                setValue("#{person.lastname}", "Muir");
                setValue("#{person.address}", "test@example.com");
            }
            
            @Override
            protected void invokeApplication() throws Exception
            {
                MimeMessage renderedMessage = getRenderedMailMessage("/simple.xhtml");
             
                assert MailSession.instance().getTransport() instanceof MockTransport;
                
                assert renderedMessage != null;
                assert renderedMessage.getAllRecipients().length == 1;
                assert renderedMessage.getAllRecipients()[0] instanceof InternetAddress;
                InternetAddress to = (InternetAddress) renderedMessage.getAllRecipients()[0];
                assert to.getAddress().equals("test@example.com");
                assert to.getPersonal().equals("Pete Muir");
                assert renderedMessage.getFrom().length == 1;
                assert renderedMessage.getFrom()[0] instanceof InternetAddress;
                InternetAddress from = (InternetAddress) renderedMessage.getFrom()[0];
                assert from.getAddress().equals("peter@example.com");
                assert from.getPersonal().equals("Peter");
                assert "Try out Seam!".equals(renderedMessage.getSubject());
                assert renderedMessage.getContent() != null;
                assert renderedMessage.getContent() instanceof MimeMultipart;
                MimeMultipart body = (MimeMultipart) renderedMessage.getContent();
                assert body.getCount() == 1;
                assert body.getBodyPart(0) != null;
                assert body.getBodyPart(0) instanceof MimeBodyPart;
                MimeBodyPart bodyPart = (MimeBodyPart) body.getBodyPart(0);
                assert bodyPart.getContent() != null;
                assert "text/html; charset=us-ascii".equals(bodyPart.getContentType());
                assert "inline".equals(bodyPart.getDisposition());
                assert bodyPart.isMimeType("text/html");

            }            
        }.run();
       
    }
    
}
