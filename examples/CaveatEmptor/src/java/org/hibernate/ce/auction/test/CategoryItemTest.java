package org.hibernate.ce.auction.test;

import junit.framework.*;
import junit.textui.TestRunner;
import org.hibernate.ce.auction.model.*;
import org.hibernate.ce.auction.persistence.HibernateUtil;

import java.util.*;

import org.hibernate.*;

public class CategoryItemTest extends TestCaseWithData {

	// ********************************************************** //

	public void testCompositeQuery() throws Exception {
		initData();

		// Query for Category and all categorized Items (three tables joined)
		HibernateUtil.beginTransaction();
		Session s = HibernateUtil.getSession();

		Query q = s.createQuery("select c from Category as c left join fetch c.categorizedItems as ci join fetch ci.item as i");
		Collection result = new HashSet(q.list());
		assertTrue(result.size() == 2);

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		// Check initialization (should be eager fetched)
		for (Iterator it = result.iterator(); it.hasNext();) {
			Category cat = (Category) it.next();
			for (Iterator it2 = cat.getCategorizedItems().iterator(); it2.hasNext();) {
				assertTrue(it2.next() != null);
			}
		}
	}

	public void testDeletionFromItem() throws Exception {
		initData();

		// Delete all links for auctionFour by clearing collection
		HibernateUtil.beginTransaction();
		Session s = HibernateUtil.getSession();
		Item i = (Item)s.get(Item.class, auctionFour.getId());
		i.getCategorizedItems().clear();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		// Check deletion
		HibernateUtil.beginTransaction();
		s = HibernateUtil.getSession();
		CategorizedItem catItem = (CategorizedItem)s.get(CategorizedItem.class,
					new CategorizedItem.Id(carsLuxury.getId(), auctionFour.getId()));
		assertTrue(catItem == null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
	}

	public void testDeletionFromCategory() throws Exception {
		initData();

		// Delete all links for auctionFour by clearing collection
		HibernateUtil.beginTransaction();
		Session s = HibernateUtil.getSession();
		Category c = (Category)s.get(Category.class, carsSUV.getId());
		c.getCategorizedItems().clear();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		// Check deletion
		HibernateUtil.beginTransaction();
		s = HibernateUtil.getSession();
		CategorizedItem catItem = (CategorizedItem)s.get(CategorizedItem.class,
					new CategorizedItem.Id(carsSUV.getId(), auctionThree.getId()));
		assertTrue(catItem == null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

	}

	// ********************************************************** //

	public CategoryItemTest(String x) {
		super(x);
	}

	public static Test suite() {
		return new TestSuite(CategoryItemTest.class);
	}

	public static void main(String[] args) throws Exception {
		TestRunner.run( suite() );
	}

}
