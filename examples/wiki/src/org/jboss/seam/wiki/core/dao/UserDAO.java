package org.jboss.seam.wiki.core.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.wiki.core.users.Role;
import org.jboss.seam.wiki.core.users.User;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.Session;
import org.hibernate.transform.DistinctRootEntityResultTransformer;

import java.util.List;

@Name("userDAO")
public class UserDAO {

    @In
    protected EntityManager entityManager;

    @Transactional
    public User findUser(String username, boolean onlyActivated) {
        entityManager.joinTransaction();

        StringBuffer query = new StringBuffer("select u from User u where u.username = :username");
        if (onlyActivated) query.append(" and u.activated = true");

        try {
            return (User) entityManager
                    .createQuery(query.toString())
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    @Transactional
    public User findUserWithActivationCode(String activationCode) {
        entityManager.joinTransaction();

        StringBuffer query = new StringBuffer("select u from User u where u.activationCode = :activationCode");
        try {
            return (User) entityManager
                    .createQuery(query.toString())
                    .setParameter("activationCode", activationCode)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    @Transactional
    public Role findRole(String rolename) {
        entityManager.joinTransaction();

        try {
            return (Role) entityManager
                    .createQuery("select r from Role r where r.name = :name")
                    .setParameter("name", rolename)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    @Transactional
    public List<User> findByExample(User exampleUser, String orderByProperty, boolean orderDescending,
                                    int firstResult, int maxResults, String... ignoreProperty) {

        Example example =  Example.create(exampleUser).enableLike(MatchMode.ANYWHERE).ignoreCase();

        for (String s : ignoreProperty) example.excludeProperty(s);

        Session session = (Session)entityManager.getDelegate();

        List result = session.createCriteria(User.class).add(example)
                .addOrder( orderDescending ? Order.desc(orderByProperty) : Order.asc(orderByProperty))
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .setResultTransformer(new DistinctRootEntityResultTransformer())
                .list();

        return (List<User>)result;
    }

}
