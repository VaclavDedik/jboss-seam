package org.hibernate.ce.auction.dao;

import org.hibernate.*;
import org.hibernate.criterion.Example;
import org.hibernate.ce.auction.exceptions.InfrastructureException;
import org.hibernate.ce.auction.model.*;
import org.hibernate.ce.auction.persistence.HibernateUtil;

import java.util.Collection;

/**
 * A typical DAO for auction items using Hibernate.
 *
 * @author Christian Bauer <christian@hibernate.org>
 */
public class ItemDAO {

	public ItemDAO() {
		HibernateUtil.beginTransaction();
	}

	// ********************************************************** //

	public Item getItemById(Long itemId, boolean lock)
			throws InfrastructureException {

		Session session = HibernateUtil.getSession();
		Item item = null;
		try {
			if (lock) {
				item = (Item) session.load(Item.class, itemId, LockMode.UPGRADE);
			} else {
				item = (Item) session.load(Item.class, itemId);
			}
		}  catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return item;
	}

	// ********************************************************** //

	public Bid getMaxBid(Long itemId)
			throws InfrastructureException {

		Bid maxBidAmount = null;
		try {
			// Note the creative where-clause subselect expression...
			Query q = HibernateUtil.getSession().getNamedQuery("maxBid");
			q.setLong("itemid", itemId.longValue());
			maxBidAmount = (Bid) q.uniqueResult();
		}
		catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return maxBidAmount;
	}

	// ********************************************************** //

	public Bid getMinBid(Long itemId)
			throws InfrastructureException {

		Bid maxBidAmount = null;
		try {
			// Note the creative where-clause subselect expression..
			Query q = HibernateUtil.getSession().getNamedQuery("minBid");
			q.setLong("itemid", itemId.longValue());
			maxBidAmount = (Bid) q.uniqueResult();
		}
		catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return maxBidAmount;
	}

	// ********************************************************** //

	public Collection findAll()
			throws InfrastructureException {

		Collection items;
		try {
			items = HibernateUtil.getSession().createCriteria(Item.class).list();
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return items;
	}

	// ********************************************************** //

	public Collection findByExample(Item exampleItem)
			throws InfrastructureException {

		Collection items;
		try {
			Criteria crit = HibernateUtil.getSession().createCriteria(Item.class);
			items = crit.add(Example.create(exampleItem)).list();
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return items;
	}

	// ********************************************************** //

	public void makePersistent(Item item)
			throws InfrastructureException {

		try {
			HibernateUtil.getSession().saveOrUpdate(item);
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
	}

	// ********************************************************** //

	public void makeTransient(Item item)
			throws InfrastructureException {

		try {
			HibernateUtil.getSession().delete(item);
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
	}

}
