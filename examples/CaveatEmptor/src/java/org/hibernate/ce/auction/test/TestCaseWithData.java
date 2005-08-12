package org.hibernate.ce.auction.test;

import org.hibernate.ce.auction.model.*;
import org.hibernate.ce.auction.persistence.HibernateUtil;
import org.hibernate.ce.auction.dao.*;

import java.math.BigDecimal;
import java.util.*;
/**
 * No actual test, but only test data initialization.
 *
 * @author Christian Bauer <christian@hibernate.org>
 */
public abstract class TestCaseWithData extends TestCase {

	// Keep references to domain objects
	Category cars;
	Category carsLuxury;
	Category carsSUV;

	User u1;
	User u2;
	User u3;

	Item auctionOne;
	Item auctionTwo;
	Item auctionThree;
	Item auctionFour;

	// ********************************************************** //

	/**
	 * Create test data for our domain model.
	 *
	 * @throws Exception
	 */
	protected void initData() throws Exception {

		// Prepare DAOS
		CategoryDAO catDAO = new CategoryDAO();
		UserDAO userDAO = new UserDAO();
		ItemDAO itemDAO = new ItemDAO();
		CommentDAO commentDAO = new CommentDAO();

		// Categories
		cars = new Category("Cars");
		carsLuxury = new Category("Luxury Cars");
		cars.addChildCategory(carsLuxury);
		carsSUV = new Category("SUVs");
		cars.addChildCategory(carsSUV);
		catDAO.makePersistent(cars);

		// Users
		u1 = new User("Christian", "Bauer", "turin", "abc123", "christian@hibernate.org");
		u1.setAddress(new Address("Foo", "12345", "Bar"));
		u1.setAdmin(true);
		u2= new User("Gavin", "King", "gavin", "abc123", "gavin@hibernate.org");
		u2.setAddress(new Address("Foo", "12345", "Bar"));
		u3= new User("Max", "Andersen", "max", "abc123", "max@hibernate.org");
		u3.setAddress(new Address("Foo", "12345", "Bar"));
		userDAO.makePersistent(u1);
		userDAO.makePersistent(u2);
		userDAO.makePersistent(u3);

		// BillingDetails
		BillingDetails ccOne = new CreditCard("Christian  Bauer", u1, "1234567890",
		                                        CreditCardType.MASTERCARD, "10", "2005");
		BillingDetails accOne = new BankAccount("Christian Bauer", u1, "234234234234",
		                                        "FooBar Rich Bank", "foobar123foobaz");
		u1.addBillingDetails(ccOne);
		u1.addBillingDetails(accOne);

		// Items
		Calendar inThreeDays = GregorianCalendar.getInstance();
		inThreeDays.roll(Calendar.DAY_OF_YEAR, 3);
		Calendar inFiveDays = GregorianCalendar.getInstance();
		inFiveDays.roll(Calendar.DAY_OF_YEAR, 5);
		Calendar nextWeek = GregorianCalendar.getInstance();
		nextWeek.roll(Calendar.WEEK_OF_YEAR, true);

		auctionOne = new Item("Item One", "An item in the carsLuxury category.",
		        u2,
		        new MonetaryAmount(new BigDecimal("1.99"), Currency.getInstance(Locale.US)),
		        new MonetaryAmount(new BigDecimal("50.33"), Currency.getInstance(Locale.US)),
		        new Date(), inThreeDays.getTime());
		auctionOne.getImages().add("imagefiledupe1.jpg");
		auctionOne.getImages().add("imagefiledupe1.jpg");
		auctionOne.getImages().add("imagefile2.jpg");
		auctionOne.setPendingForApproval();
		auctionOne.approve(u1);
		itemDAO.makePersistent(auctionOne);
		new CategorizedItem(u1.getUsername(), carsLuxury, auctionOne);

		auctionTwo = new Item("Item Two", "Another item in the carsLuxury category.",
				u2,
		        new MonetaryAmount(new BigDecimal("2.22"), Currency.getInstance(Locale.US)),
		        new MonetaryAmount(new BigDecimal("100.88"), Currency.getInstance(Locale.US)),
		        new Date(), inFiveDays.getTime());
		itemDAO.makePersistent(auctionTwo);
		new CategorizedItem(u1.getUsername(), carsLuxury, auctionTwo);

		auctionThree = new Item("Item Three", "Don't drive SUVs.",
				u2,
		        new MonetaryAmount(new BigDecimal("3.11"), Currency.getInstance(Locale.US)),
		        new MonetaryAmount(new BigDecimal("300.55"), Currency.getInstance(Locale.US)),
		        new Date(), inThreeDays.getTime());
		itemDAO.makePersistent(auctionThree);
		new CategorizedItem(u1.getUsername(), carsSUV, auctionThree);

		auctionFour = new Item("Item Four", "Really, not even luxury SUVs.",
				u1,
		        new MonetaryAmount(new BigDecimal("4.55"), Currency.getInstance(Locale.US)),
		        new MonetaryAmount(new BigDecimal("40.99"), Currency.getInstance(Locale.US)),
		        new Date(), nextWeek.getTime());
		itemDAO.makePersistent(auctionFour);
		new CategorizedItem(u1.getUsername(), carsLuxury, auctionFour);
		new CategorizedItem(u1.getUsername(), carsSUV, auctionFour);

		// Bids
		Bid bidOne1 = new Bid(new MonetaryAmount(new BigDecimal("12.12"), Currency.getInstance(Locale.US)),
		        auctionOne, u3);
		org.hibernate.ce.auction.model.Bid bidOne2 = new Bid(new MonetaryAmount(new BigDecimal("13.13"), Currency.getInstance(Locale.US)),
		        auctionOne, u1);
		Bid bidOne3 = new Bid(new MonetaryAmount(new BigDecimal("14.14"), Currency.getInstance(Locale.US)),
		        auctionOne, u3);

		auctionOne.addBid(bidOne1);
		auctionOne.addBid(bidOne2);
		auctionOne.addBid(bidOne3);

		// Successful Bid
		auctionOne.setSuccessfulBid(bidOne3);

		// Comments
		Comment commentOne = new Comment(Rating.EXCELLENT, "This is Excellent.", u3, auctionOne);
		Comment commentTwo = new Comment(Rating.LOW, "This is very Low.", u1, auctionThree);
		commentDAO.makePersistent(commentOne);
		commentDAO.makePersistent(commentTwo);

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

	}

	// ********************************************************** //

	public TestCaseWithData(String x) {
		super(x);
	}

}
