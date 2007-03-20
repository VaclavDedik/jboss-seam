package org.jboss.seam.example.spring;


import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.springframework.orm.jpa.support.JpaDaoSupport;

/**
 * Example of using the JpaDaoSupport.
 *
 * @author youngm
 */
public class BookingService 
    extends JpaDaoSupport 
{
    @SuppressWarnings("unchecked")
    public List<Hotel> findHotels(String searchPattern, int firstResult, int maxResults) {
        return getJpaTemplate().getEntityManager()
                               .createQuery("select h from Hotel h where lower(h.name) like :search or lower(h.city) like :search or lower(h.zip) like :search or lower(h.address) like :search")
                               .setParameter("search", searchPattern)
                               .setMaxResults(maxResults)
                               .setFirstResult(firstResult)
                               .getResultList();
    }



    @SuppressWarnings("unchecked")
    public List<Booking> findBookingsByUsername(String username) {
        return getJpaTemplate().findByNamedParams("select b from Booking b where b.user.username = :username order by b.checkinDate",
                                                  Collections.singletonMap("username", username));

    }

    public void cancelBooking(Long bookingId) {
        if (bookingId == null) {
            throw new IllegalArgumentException("BookingId cannot be null");
        }

        Booking cancelled = getJpaTemplate().find(Booking.class, bookingId);
        if (cancelled != null) {
            getJpaTemplate().remove(cancelled);
        }
    }

    public void validateBooking(Booking booking) 
        throws ValidationException 
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        if (booking.getCheckinDate().before(calendar.getTime())) {
            throw new ValidationException("Check in date must be a future date");
        } else if (!booking.getCheckinDate().before(booking.getCheckoutDate())) {
            throw new ValidationException("Check out date must be later than check in date");
        }
    }



    public void bookHotel(Booking booking) 
        throws ValidationException 
    {
        validateBooking(booking);

        getJpaTemplate().persist(booking);
        getJpaTemplate().flush();
    }
    
    public void testNonWebRequest() {
    	System.out.print("Yup. successfully called the method");
    }

    public Hotel findHotelById(Long hotelId) {
        if (hotelId == null) {
            throw new IllegalArgumentException("hotelId cannot be null");
        }

        return getJpaTemplate().find(Hotel.class, hotelId);
    }
}
