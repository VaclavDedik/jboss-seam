package org.hibernate.ce.auction.dao;

import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.ce.auction.exceptions.InfrastructureException;
import org.hibernate.ce.auction.model.Category;
import org.hibernate.ce.auction.persistence.HibernateUtil;

import java.util.Collection;

/**
 * A typical DAO for categories using Hibernate.
 * 
 * @author Christian Bauer <christian@hibernate.org>
 */ 
public class CategoryDAO {

	public CategoryDAO() {
		HibernateUtil.beginTransaction();
	}

	// ********************************************************** //

	public Category getCategoryById(Long categoryId, boolean lock)
			throws InfrastructureException {

		Session session = HibernateUtil.getSession();
		Category cat = null;
		try {
			if (lock) {
				cat = (Category) session.load(Category.class, categoryId, LockMode.UPGRADE);
			} else {
				cat = (Category) session.load(Category.class, categoryId);
			}
		}  catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return cat;
	}

	// ********************************************************** //

	public Collection findAll(boolean onlyRootCategories)
			throws InfrastructureException {

		Collection categories;
		try {
			if (onlyRootCategories) {
				Criteria crit = HibernateUtil.getSession().createCriteria(Category.class);
				categories = crit.add(Expression.isNull("parentCategory")).list();
			} else {
				categories = HibernateUtil.getSession().createCriteria(Category.class).list();
			}
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return categories;
	}

	// ********************************************************** //

	public Collection findByExample(Category exampleCategory)
			throws InfrastructureException {

		Collection categories;
		try {
			Criteria crit = HibernateUtil.getSession().createCriteria(Category.class);
			categories = crit.add(Example.create(exampleCategory)).list();
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return categories;
	}

	// ********************************************************** //

	public void makePersistent(Category category)
			throws InfrastructureException {

		try {
			HibernateUtil.getSession().saveOrUpdate(category);
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
	}

	// ********************************************************** //

	public void makeTransient(Category category)
			throws InfrastructureException {

		try {
			HibernateUtil.getSession().delete(category);
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
	}

}
