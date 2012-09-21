package org.jboss.seam.example.contactlist.test;

import static org.junit.Assert.*;

import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.example.contactlist.Contact;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ContactListTest
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
      Lifecycle.beginCall();

      EntityQuery<Contact> contacts = (EntityQuery<Contact>)Component.getInstance("contacts");
      List<Contact> contactsList = (List<Contact>) (contacts.getResultList());
      assertEquals(5, contactsList.size());

      Lifecycle.endCall();
   }

   @Test
   public void testSearch() throws Exception
   {
      Lifecycle.beginCall();
      
      Contact exampleContact = (Contact)Component.getInstance("exampleContact");
      exampleContact.setFirstName("Norman");

      EntityQuery<Contact> contacts = (EntityQuery<Contact>)Component.getInstance("contacts");
      List<Contact> contactsList = (List<Contact>) (contacts.getResultList());
      assertEquals(1, contactsList.size());
      
      Lifecycle.endCall();

      Lifecycle.beginCall();

      exampleContact = (Contact)Component.getInstance("exampleContact");
      exampleContact.setLastName("King");
      
      contacts = (EntityQuery<Contact>)Component.getInstance("contacts");
      contactsList = (List<Contact>) (contacts.getResultList());
      assertEquals(1, contactsList.size());
      
      Lifecycle.endCall();
   }

   Long contactId;

   @Test
   public void testCreateDeleteContact() throws Exception
   {
      Lifecycle.beginCall();

      Contact contact = (Contact) Component.getInstance("contact");
      contact.setFirstName("Emmanuel");
      contact.setLastName("Bernard");
      contact.setCity("Paris");

      EntityHome<Contact> contactHome = (EntityHome<Contact>)Component.getInstance("contactHome");
      assertEquals("persisted", contactHome.persist());
      contactId = (Long)contactHome.getId();

      Lifecycle.endCall();
      Lifecycle.beginCall();

      contactHome = (EntityHome<Contact>)Component.getInstance("contactHome");
      contactHome.setId(contactId);
      contact = (Contact) Component.getInstance("contact");
      assertEquals("Emmanuel", contact.getFirstName());
      assertEquals("Bernard", contact.getLastName());
      assertEquals("Paris", contact.getCity());

      Lifecycle.endCall();
      Lifecycle.beginCall();

      contactHome = (EntityHome<Contact>)Component.getInstance("contactHome");
      contactHome.setId(contactId);
      assertEquals("removed", contactHome.remove());

      Lifecycle.endCall();
   }
}
