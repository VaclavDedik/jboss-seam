package org.jboss.seam.example.mail.test;


import java.io.InputStream;

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
    public void testSimple() throws Exception
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
                
                // Test the headers
                
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
                assert renderedMessage.getHeader("Precedence") == null;
                assert renderedMessage.getHeader("X-Priority") == null;
                assert renderedMessage.getHeader("Priority") == null;
                assert renderedMessage.getHeader("Importance") == null;
                assert renderedMessage.getHeader("Disposition-Notification-To") == null;

                
                // Check the body
                
                assert renderedMessage.getContent() != null;
                assert renderedMessage.getContent() instanceof MimeMultipart;
                MimeMultipart body = (MimeMultipart) renderedMessage.getContent();
                assert body.getCount() == 1;
                assert body.getBodyPart(0) != null;
                assert body.getBodyPart(0) instanceof MimeBodyPart;
                MimeBodyPart bodyPart = (MimeBodyPart) body.getBodyPart(0);
                assert bodyPart.getContent() != null;
                assert "inline".equals(bodyPart.getDisposition());
                assert bodyPart.isMimeType("text/html");

            }            
        }.run();
       
    }
    
    @Test
    public void testAttachment() throws Exception
    {
        
        new FacesRequest()
        {

            @Override
            protected void updateModelValues() throws Exception
            {
                setValue("#{person.firstname}", "Gavin");
                setValue("#{person.lastname}", "King");
                setValue("#{person.address}", "gavin@king.com");
            }
            
            @Override
            protected void invokeApplication() throws Exception
            {
                
                /*<m:from name="Peter" address="peter@email.tld" />
                <m:to name="#{person.firstname} #{person.lastname}">#{person.address}</m:to>
                <m:subject>Try out Seam!</m:subject>
                <m:attachment value="/jboss.jpg" /> 
                <m:attachment value="#{numbers}" />
                <m:attachment value="#{person.photo}" contentType="image/png" fileName="#{person.firstname}_#{person.lastname}.jpg" status="personPhoto" />
                <m:attachment fileName="whyseam.pdf">
                      <ui:include src="/whyseam.xhtml" />
                </m:attachment>
                <ui:repeat value="#{people}" var="person">
                     <m:attachment value="#{person.photo}" contentType="image/jpeg" fileName="#{person.firstname}_#{person.lastname}.jpg" />
                </ui:repeat>
                <m:body>
                    <p><h:outputText value="Dear #{person.firstname}" />,</p>
                    <p>This example email demonstrates how easy it is to add attachments to an email!</p>
                    <p>We can even display attached images inline:</p>
                    <img src="cid:#{personPhoto.contentId}" />
                    <p>The attached pdf was generated by Seam's PDF library</p>
                </m:body>*/
                
                MimeMessage renderedMessage = getRenderedMailMessage("/attachment.xhtml");
                
                // Test the headers
                
                InternetAddress to = (InternetAddress) renderedMessage.getAllRecipients()[0];
                assert to.getAddress().equals("gavin@king.com");
                assert to.getPersonal().equals("Gavin King");
                InternetAddress from = (InternetAddress) renderedMessage.getFrom()[0];
                assert from.getAddress().equals("peter@email.tld");
                assert from.getPersonal().equals("Peter");
                assert "Try out Seam!".equals(renderedMessage.getSubject());
                MimeMultipart body = (MimeMultipart) renderedMessage.getContent();
                
                // Test the attachments (no ui:repeat atm, so only 6)
                assert body.getCount() == 6;
                
                // Attachment 1
                assert body.getBodyPart(0) != null;                
                assert body.getBodyPart(0) instanceof MimeBodyPart;
                MimeBodyPart bodyPart = (MimeBodyPart) body.getBodyPart(0);
                assert bodyPart.getContent() != null;
                assert bodyPart.getContent() instanceof InputStream;
                assert "jboss.jpg".equals(bodyPart.getFileName());
                assert bodyPart.isMimeType("image/jpeg");
                assert "attachment".equals(bodyPart.getDisposition());
                
                // Attachment 2
                assert body.getBodyPart(1) != null;                
                assert body.getBodyPart(1) instanceof MimeBodyPart;
                bodyPart = (MimeBodyPart) body.getBodyPart(1);
                assert bodyPart.getContent() != null;
                assert bodyPart.getContent() instanceof InputStream;
                assert "numbers.csv".equals(bodyPart.getFileName());
                assert bodyPart.isMimeType("content/unknown");
                assert "attachment".equals(bodyPart.getDisposition());
                
                // Attachment 3
                assert body.getBodyPart(2) != null;                
                assert body.getBodyPart(2) instanceof MimeBodyPart;
                bodyPart = (MimeBodyPart) body.getBodyPart(2);
                assert bodyPart.getContent() != null;
                assert bodyPart.getContent() != null;
                assert bodyPart.getContent() instanceof InputStream;
                assert "Gavin_King.jpg".equals(bodyPart.getFileName());
                assert bodyPart.isMimeType("image/png");
                assert "attachment".equals(bodyPart.getDisposition());
                
                // Attachment 4
                assert body.getBodyPart(3) != null;                
                assert body.getBodyPart(3) instanceof MimeBodyPart;
                bodyPart = (MimeBodyPart) body.getBodyPart(3);
                assert bodyPart.getContent() != null;
                // No PDF rendering here :(
                assert bodyPart.getContent() instanceof String;
                assert "whyseam.pdf".equals(bodyPart.getFileName());
                assert "attachment".equals(bodyPart.getDisposition());
                
                // Attachment 5
                assert body.getBodyPart(4) != null;                
                assert body.getBodyPart(4) instanceof MimeBodyPart;
                bodyPart = (MimeBodyPart) body.getBodyPart(4);
                assert bodyPart.getContent() != null;
                assert "Gavin_King.jpg".equals(bodyPart.getFileName());
                assert bodyPart.isMimeType("image/jpeg");
                assert "attachment".equals(bodyPart.getDisposition());
                
                // Attachment 6 (the actual message)
                assert body.getBodyPart(5) != null;                
                assert body.getBodyPart(5) instanceof MimeBodyPart;
                bodyPart = (MimeBodyPart) body.getBodyPart(5);
                assert bodyPart.getContent() != null;
                assert bodyPart.getContent() != null;
                assert "inline".equals(bodyPart.getDisposition());
                assert bodyPart.isMimeType("text/html");
            }            
        }.run();
       
    }
    
    @Test
    public void testHtml() throws Exception
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
                MimeMessage renderedMessage = getRenderedMailMessage("/html.xhtml");
             
                // Test the standard headers
                
                InternetAddress to = (InternetAddress) renderedMessage.getAllRecipients()[0];
                assert to.getAddress().equals("test@example.com");
                assert to.getPersonal().equals("Pete Muir");
                InternetAddress from = (InternetAddress) renderedMessage.getFrom()[0];
                assert from.getAddress().equals("do-not-reply@jboss.com");
                assert from.getPersonal().equals("Seam");
                assert "Seam Mail".equals(renderedMessage.getSubject());
                
                // Test the extra headers
                
                // Importance
                assert renderedMessage.getHeader("X-Priority") != null;
                assert renderedMessage.getHeader("Priority") != null;
                assert renderedMessage.getHeader("Importance") != null;
                assert renderedMessage.getHeader("X-Priority").length == 1;
                assert renderedMessage.getHeader("Priority").length == 1;
                assert renderedMessage.getHeader("Importance").length == 1;
                assert "5".equals(renderedMessage.getHeader("X-Priority")[0]);
                assert "Non-urgent".equals(renderedMessage.getHeader("Priority")[0]);
                assert "low".equals(renderedMessage.getHeader("Importance")[0]);
                
                // read receipt
                assert renderedMessage.getHeader("Disposition-Notification-To") != null;
                assert renderedMessage.getHeader("Disposition-Notification-To").length == 1;
                assert "Seam <do-not-reply@jboss.com>".equals(renderedMessage.getHeader("Disposition-Notification-To")[0]);
                
                // m:header
                assert renderedMessage.getHeader("X-Sent-From") != null;
                assert renderedMessage.getHeader("X-Sent-From").length == 1;
                assert "JBoss Seam".equals(renderedMessage.getHeader("X-Sent-From")[0]);
                
                MimeMultipart body = (MimeMultipart) renderedMessage.getContent();
                
                // Check the alternative facet
                assert renderedMessage.getContentType().startsWith("multipart/mixed");
                assert body.getCount() == 1;
                MimeBodyPart bodyPart = (MimeBodyPart) body.getBodyPart(0);
                assert bodyPart.getContentType().startsWith("multipart/alternative");
                assert bodyPart.getContent() instanceof MimeMultipart;
                MimeMultipart bodyParts = (MimeMultipart) bodyPart.getContent();
                assert bodyParts.getCount() == 2;
                assert bodyParts.getBodyPart(0) instanceof MimeBodyPart;
                assert bodyParts.getBodyPart(1) instanceof MimeBodyPart;
                MimeBodyPart alternative = (MimeBodyPart) bodyParts.getBodyPart(0);
                MimeBodyPart html = (MimeBodyPart) bodyParts.getBodyPart(1);
                assert alternative.isMimeType("text/plain");
                assert "inline".equals(alternative.getDisposition());
                assert html.isMimeType("text/html");
                assert "inline".equals(html.getDisposition());
            }            
        }.run();
       
    }
    
    
    @Test
    public void testPlain() throws Exception
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
                MimeMessage renderedMessage = getRenderedMailMessage("/plain.xhtml");
             
                // Test the standard headers
                
                InternetAddress to = (InternetAddress) renderedMessage.getAllRecipients()[0];
                assert to.getAddress().equals("test@example.com");
                assert to.getPersonal().equals("Pete Muir");
                InternetAddress from = (InternetAddress) renderedMessage.getFrom()[0];
                assert from.getAddress().equals("do-not-reply@jboss.com");
                assert from.getPersonal().equals("Seam");
                assert "Plain text email sent by Seam".equals(renderedMessage.getSubject());
                
                // Check the body
                
                assert renderedMessage.getContent() != null;
                MimeMultipart body = (MimeMultipart) renderedMessage.getContent();
                assert body.getCount() == 1;
                MimeBodyPart bodyPart = (MimeBodyPart) body.getBodyPart(0);
                assert bodyPart.getContent() != null;
                assert "inline".equals(bodyPart.getDisposition());
                assert bodyPart.isMimeType("text/plain");
            }
        }.run();
    }
    
    @Test
    public void testTemplating() throws Exception
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
                MimeMessage renderedMessage = getRenderedMailMessage("/templating.xhtml");
             
                // Test the standard headers
                
                InternetAddress to = (InternetAddress) renderedMessage.getAllRecipients()[0];
                assert to.getAddress().equals("test@example.com");
                assert to.getPersonal().equals("Pete Muir");
                InternetAddress from = (InternetAddress) renderedMessage.getFrom()[0];
                assert from.getAddress().equals("do-not-reply@jboss.com");
                assert from.getPersonal().equals("Seam");
                assert "Templating with Seam Mail".equals(renderedMessage.getSubject());
                
                // Check the body
                
                MimeMultipart body = (MimeMultipart) renderedMessage.getContent();
                
                // Check the alternative facet
                assert renderedMessage.getContentType().startsWith("multipart/mixed");
                assert body.getCount() == 1;
                MimeBodyPart bodyPart = (MimeBodyPart) body.getBodyPart(0);
                assert bodyPart.getContentType().startsWith("multipart/alternative");
                assert bodyPart.getContent() instanceof MimeMultipart;
                MimeMultipart bodyParts = (MimeMultipart) bodyPart.getContent();
                assert bodyParts.getCount() == 2;
                assert bodyParts.getBodyPart(0) instanceof MimeBodyPart;
                assert bodyParts.getBodyPart(1) instanceof MimeBodyPart;
                MimeBodyPart alternative = (MimeBodyPart) bodyParts.getBodyPart(0);
                MimeBodyPart html = (MimeBodyPart) bodyParts.getBodyPart(1);
                assert alternative.isMimeType("text/plain");
                assert "inline".equals(alternative.getDisposition());
                assert html.isMimeType("text/html");
                assert "inline".equals(html.getDisposition());       
            }
        }.run();
    }
}
