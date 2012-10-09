/**
 * 
 */
package org.jboss.seam.example.spring;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * A Service to demonstrate/test some different methods of accessing Seam Managed Hibernate Session in Spring.
 * @author Mike Youngstrom
 */
public class HibernateTestService
{
   public static final String HIBERNATE_HOTEL_NAME = "This is the Hibernate Hotel";

   public static final String HIBERNATE_HOTEL_ADDRESS = "Hibernate Address";

   private TransactionTemplate hibernateTransactionTemplate;

   private SessionFactory sessionFactory;

   public void testHibernateIntegration()
   {
      hibernateTransactionTemplate.execute(new TransactionCallback()
      {
         public Object doInTransaction(TransactionStatus status)
         {
         // Testing access through SessionFactory.getCurrentSession()
            Hotel hotel = getFirstHotel(sessionFactory.getCurrentSession());
            hotel.setName(HIBERNATE_HOTEL_NAME);
            return null;
         }
      });
      hibernateTransactionTemplate.execute(new TransactionCallback()
      {
         public Object doInTransaction(TransactionStatus status)
         {
            // Testing access through SessionFactory.getCurrentSession()
            Hotel hotel = getFirstHotel(sessionFactory.getCurrentSession());
            if (!HIBERNATE_HOTEL_NAME.equals(hotel.getName()))
            {
               throw new RuntimeException("Hotel name not set.  Hibernate integration not working.");
            }
            hotel.setAddress(HIBERNATE_HOTEL_ADDRESS);
            return null;
         }
      });
      hibernateTransactionTemplate.execute(new TransactionCallback()
      {
         public Object doInTransaction(TransactionStatus status)
         {
            // Testing access through SessionFactory.getCurrentSession()
            Hotel hotel = getFirstHotel(sessionFactory.getCurrentSession());
            if (!HIBERNATE_HOTEL_ADDRESS.equals(hotel.getAddress()))
            {
               throw new RuntimeException("Hotel address not set.  Hibernate integration not working.");
            }
            return null;
         }
      });
   }

   /**
    * @return
    */
   private Hotel getFirstHotel(Session session)
   {
      List<Hotel> results = session.createQuery("from Hotel").list();
      if (results.size() <= 0)
      {
         throw new RuntimeException("Hibernate integration is broken");
      }
      Hotel hotel = results.get(0);
      return hotel;
   }

   /**
    * @param transactionTemplate the transactionTemplate to set
    */
   public void setTransactionTemplate(TransactionTemplate transactionTemplate)
   {
      this.hibernateTransactionTemplate = transactionTemplate;
   }

   public void setSessionFactory(SessionFactory sessionFactory)
   {
      this.sessionFactory = sessionFactory;
   }
}
