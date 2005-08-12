package org.hibernate.ce.auction.dao;

import org.hibernate.*;
import org.hibernate.criterion.Example;
import org.hibernate.ce.auction.exceptions.InfrastructureException;
import org.hibernate.ce.auction.model.User;
import org.hibernate.ce.auction.persistence.HibernateUtil;

import java.util.Collection;

/**
 * A typical DAO for users using Hibernate.
 *
 * @author Christian Bauer <christian@hibernate.org>
 */
public class UserDAO {

	public UserDAO() {
		HibernateUtil.beginTransaction();
	}

	// ********************************************************** //

	public User getUserById(Long userId, boolean lock)
			throws InfrastructureException {

		Session session = HibernateUtil.getSession();
		User user = null;
		try {
			if (lock) {
				user = (User) session.load(User.class, userId, LockMode.UPGRADE);
			} else {
				user = (User) session.load(User.class, userId);
			}
		}  catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return user;
	}

	// ********************************************************** //

	public Collection findAll()
			throws InfrastructureException {

		Collection users;
		try {
			users = HibernateUtil.getSession().createCriteria(User.class).list();
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return users;
	}

	// ********************************************************** //

	public Collection findByExample(User exampleUser)
			throws InfrastructureException {

		Collection users;
		try {
			Criteria crit = HibernateUtil.getSession().createCriteria(User.class);
			users = crit.add(Example.create(exampleUser)).list();
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return users;
	}

	// ********************************************************** //

	public void makePersistent(User user)
			throws InfrastructureException {

		try {
			HibernateUtil.getSession().saveOrUpdate(user);
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
	}

	// ********************************************************** //

	public void makeTransient(User user)
			throws InfrastructureException {

		try {
			HibernateUtil.getSession().delete(user);
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
	}


}
