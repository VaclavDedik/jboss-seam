package org.hibernate.ce.auction.test;

import junit.framework.*;
import junit.textui.TestRunner;
import org.hibernate.ce.auction.dao.*;
import org.hibernate.ce.auction.exceptions.*;
import org.hibernate.ce.auction.model.*;
import org.hibernate.ce.auction.persistence.HibernateUtil;

import java.math.BigDecimal;
import java.util.*;

public class ItemTest extends TestCaseWithData {

	// ********************************************************** //

	public void testItemData() throws Exception {
		initData();

		ItemDAO itemDAO = new ItemDAO();

		Item a1 = itemDAO.getItemById(auctionOne.getId(), false);
		assertEquals(a1.getInitialPrice(),
		             new MonetaryAmount(new BigDecimal("1.99"), Currency.getInstance(Locale.US)));

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
	}

	// ********************************************************** //

	public void testPlaceBid() throws Exception {
		initData();

		// TODO: Test doesn't consider currency properly!

		ItemDAO itemDAO = new ItemDAO();
		UserDAO userDAO = new UserDAO();

		Bid currentMaxBid = itemDAO.getMaxBid(auctionTwo.getId());
		Bid currentMinBid = itemDAO.getMinBid(auctionTwo.getId());
		Item a2 = itemDAO.getItemById(auctionTwo.getId(), true);

		// Fail, auction is not active yet
		try {
			BigDecimal bidAmount = new BigDecimal("99.99");
			MonetaryAmount newAmount = new MonetaryAmount(bidAmount, Currency.getInstance("USD"));
			a2.placeBid(userDAO.getUserById(u3.getId(), false),
									newAmount,
									currentMaxBid,
									currentMinBid);
		} catch (BusinessException success) {}

		// Fail, user isn't an admin
		try {
			a2.approve(u3);
		} catch (PermissionException success) {}

		// Success, set active
		a2.setPendingForApproval();
		a2.approve(u1);

		// Success, place a bid
		try {
			BigDecimal bidAmount = new BigDecimal("100.00");
			MonetaryAmount newAmount = new MonetaryAmount(bidAmount, Currency.getInstance("USD"));
			a2.placeBid(userDAO.getUserById(u3.getId(), false),
									newAmount,
									currentMaxBid,
									currentMinBid);

		} catch (BusinessException failure) {
			throw failure;
		}

		// Fail, bid amount is too low
		try {
			BigDecimal bidAmount = new BigDecimal("99.99");
			MonetaryAmount newAmount = new MonetaryAmount(bidAmount, Currency.getInstance("USD"));
			a2.placeBid(userDAO.getUserById(u3.getId(), false),
									newAmount,
									currentMaxBid,
									currentMinBid);
		} catch (BusinessException success) {}

		// TODO: Implement test for auction dates...

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

	}

	// ********************************************************** //

	public ItemTest(String x) {
		super(x);
	}

	public static Test suite() {
		return new TestSuite(ItemTest.class);
	}

	public static void main(String[] args) throws Exception {
		TestRunner.run( suite() );
	}

}
