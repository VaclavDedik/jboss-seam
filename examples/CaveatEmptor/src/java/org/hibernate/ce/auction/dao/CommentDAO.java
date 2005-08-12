package org.hibernate.ce.auction.dao;

import org.hibernate.*;
import org.hibernate.criterion.Example;
import org.hibernate.ce.auction.exceptions.InfrastructureException;
import org.hibernate.ce.auction.model.*;
import org.hibernate.ce.auction.persistence.HibernateUtil;

import java.util.Collection;

/**
 * A typical DAO for comments using Hibernate
 *
 * @author Christian Bauer <christian@hibernate.org>
 */
public class CommentDAO {

	public CommentDAO() {
		HibernateUtil.beginTransaction();
	}

	// ********************************************************** //

	public Comment getCommentById(Long commentId, boolean lock)
			throws InfrastructureException {

		Session session = HibernateUtil.getSession();
		Comment comment = null;
		try {
			if (lock) {
				comment = (Comment) session.load(Comment.class, commentId, LockMode.UPGRADE);
			} else {
				comment = (Comment) session.load(Comment.class, commentId);
			}
		}  catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return comment;
	}

	// ********************************************************** //

	public Collection findAll()
			throws InfrastructureException {

		Collection comments;
		try {
			comments = HibernateUtil.getSession().createCriteria(Comment.class).list();
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return comments;
	}

	// ********************************************************** //

	public Collection findByExample(Comment exampleComment)
			throws InfrastructureException {

		Collection comments;
		try {
			Criteria crit = HibernateUtil.getSession().createCriteria(Comment.class);
			comments = crit.add(Example.create(exampleComment)).list();
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return comments;
	}

	// ********************************************************** //

	public void makePersistent(Comment comment)
			throws InfrastructureException {

		try {
			HibernateUtil.getSession().saveOrUpdate(comment);
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
	}

	// ********************************************************** //

	public void makeTransient(Comment comment)
			throws InfrastructureException {

		try {
			HibernateUtil.getSession().delete(comment);
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
	}


}
