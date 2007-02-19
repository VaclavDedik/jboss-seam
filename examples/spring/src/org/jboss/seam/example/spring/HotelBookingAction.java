package org.jboss.seam.example.spring;

import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

//import org.jboss.seam.core.Events;
import org.jboss.seam.core.FacesMessages;
//import org.jboss.seam.log.Log;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

public class HotelBookingAction
{   
    private JpaTemplate jpaTemplate;

    private User user;
    private Hotel hotel;
    private Booking booking;
    private FacesMessages facesMessages;
//    private Events events;
//    private Log log;

    private boolean bookingValid;

    //@Begin
    public void selectHotel(final Hotel selectedHotel)
    {
        hotel = (Hotel) jpaTemplate.execute(new JpaCallback() {        
            public Object doInJpa(EntityManager em) 
                throws PersistenceException 
            {
                return em.merge(selectedHotel);
            }
        });
    }

    public void bookHotel()
    {      
        booking = new Booking(hotel, user);
        Calendar calendar = Calendar.getInstance();
        booking.setCheckinDate(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        booking.setCheckoutDate(calendar.getTime());
    }

    public void setBookingDetails()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (booking.getCheckinDate().before(calendar.getTime())) {
            facesMessages.add("Check in date must be a future date");
            bookingValid=false;
        } else if (!booking.getCheckinDate().before(booking.getCheckoutDate())) {
            facesMessages.add("Check out date must be later than check in date");
            bookingValid=false;
        } else {
            bookingValid=true;
        }
    }
    public boolean isBookingValid()
    {
        return bookingValid;
    }

    //@End
    public void confirm()
    {
        jpaTemplate.execute(new JpaCallback() {        
            public Object doInJpa(EntityManager em) 
                throws PersistenceException 
            {
                System.out.println("PERSIST: " + booking);
                em.persist(booking);
                return null;
            }
        });
       
        //facesMessages.add("Thank you, #{user.name}, your confimation number for #{hotel.name} is #{booking.id}");
        //log.info("New booking: #{booking.id} for #{user.username}");
        //events.raiseTransactionSuccessEvent("bookingConfirmed");
    }

    //@End
    public void cancel() {}
    
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        jpaTemplate = new JpaTemplate(emf);
    }
}