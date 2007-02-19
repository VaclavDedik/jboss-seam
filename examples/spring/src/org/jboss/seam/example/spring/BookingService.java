/**
 *
 */
package org.jboss.seam.example.spring;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;

/**
 * @author youngm
 *
 */
public class BookingService {
	private Session session;

	public List<Hotel> findHotels(String searchPattern, int firstResult, int maxResults) {
		return session.createQuery("select h from Hotel h where lower(h.name) like :search or lower(h.city) like :search or lower(h.zip) like :search or lower(h.address) like :search")
	            .setParameter("search", searchPattern)
	            .setMaxResults(maxResults)
	            .setFirstResult( firstResult )
	            .list();
	}

	public List findBookingsByUsername(String username) {
		return session.createQuery("select b from Booking b where b.user.username = :username order by b.checkinDate")
				.setParameter("username", username).list();
	}

	public void cancelBooking(Long bookingId) {
		if (bookingId == null) {
			throw new IllegalArgumentException("BookingId cannot be null");
		}
		Booking cancelled = (Booking) session.get(Booking.class, bookingId);
		if (cancelled != null) {
			session.delete(cancelled);
		}
	}

	public void validateBooking(Booking booking) throws ValidationException {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		if (booking.getCheckinDate().before(calendar.getTime())) {
			throw new ValidationException("Check in date must be a future date");
		} else if (!booking.getCheckinDate().before(booking.getCheckoutDate())) {
			throw new ValidationException("Check out date must be later than check in date");
		}
	}

	public void bookHotel(Booking booking) throws ValidationException {
		validateBooking(booking);
		session.persist(booking);
	}

	public Hotel findHotelById(Long hotelId) {
		if (hotelId == null) {
			throw new IllegalArgumentException("hotelId cannot be null");
		}
		return (Hotel) session.get(Hotel.class, hotelId);
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}
}
