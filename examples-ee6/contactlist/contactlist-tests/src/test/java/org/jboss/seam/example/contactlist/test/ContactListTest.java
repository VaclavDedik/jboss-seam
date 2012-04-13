package org.jboss.seam.example.contactlist.test;

import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.contactlist.Contact;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ContactListTest extends JUnitSeamTest
{

   @Deployment(name="ContactListTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive er = Deployments.contactListDeployment();
      WebArchive web = er.getAsType(WebArchive.class, "contactlist-web.war");

      web.addClasses(ContactListTest.class);

      return er;
   }

   @Test
   public void testList() throws Exception
   {
      new NonFacesRequest("/search.xhtml")
      {
         @Override
         protected void renderResponse() throws Exception
         {
            List<Contact> contacts = (List<Contact>) getValue("#{contacts.resultList}");
            assert contacts.size()==5;
         }
      }.run();
   }

   @Test
   public void testSearch() throws Exception
   {
      new FacesRequest("/search.xhtml")
      {
         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{exampleContact.firstName}", "Norman");
         }
         @Override
         protected void invokeApplication() throws Exception
         {
            setOutcome("/search.xhtml");
         }
         @Override
         protected void afterRequest()
         {
            assert !isRenderResponseBegun();
         }
      }.run();
      
      new NonFacesRequest("/search.xhtml")
      {         
         @Override
         protected void beforeRequest()
         {
            setParameter("firstName", "Norman");
         }
         @Override
         protected void renderResponse() throws Exception
         {
            List<Contact> contacts = (List<Contact>) getValue("#{contacts.resultList}");
            System.out.println(contacts.size());
            assert contacts.size()==1;
         }
      }.run();

      new FacesRequest("/search.xhtml")
      {
         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{exampleContact.lastName}", "King");
         }
         @Override
         protected void invokeApplication() throws Exception
         {
            setOutcome("/search.xhtml");
         }
         @Override
         protected void afterRequest()
         {
            assert !isRenderResponseBegun();
         }
      }.run();
      
      new NonFacesRequest("/search.xhtml")
      {         
         @Override
         protected void beforeRequest()
         {
            setParameter("lastName", "King");
         }
         @Override
         protected void renderResponse() throws Exception
         {
            List<Contact> contacts = (List<Contact>) getValue("#{contacts.resultList}");
            assert contacts.size()==1;
         }
      }.run();
   }
   
   String contactId;
   
   @Test
   public void testCreateDeleteContact() throws Exception
   {
      new FacesRequest("/editContact.xhtml")
      {
         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{contact.firstName}", "Emmanuel");
            setValue("#{contact.lastName}", "Bernard");
            setValue("#{contact.city}", "Paris");
         }
         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeMethod("#{contactHome.persist}").equals("persisted");
            contactId = getValue("#{contactHome.id}").toString();
         }
      }.run();
      
      new NonFacesRequest("/viewContact.xhtml")
      {
         @Override
         protected void beforeRequest()
         {
            setParameter("contactId", contactId);
         }
         @Override
         protected void renderResponse() throws Exception
         {
            assert getValue("#{contact.firstName}").equals("Emmanuel");
            assert getValue("#{contact.lastName}").equals("Bernard");
            assert getValue("#{contact.city}").equals("Paris");
         }
      }.run();
      
      new FacesRequest("/viewContact.xhtml")
      {
         @Override
         protected void beforeRequest()
         {
            setPageParameter( "contactId", new Long(contactId) );
         }
         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeMethod("#{contactHome.remove}").equals("removed");
         }
      }.run();
   }

}
