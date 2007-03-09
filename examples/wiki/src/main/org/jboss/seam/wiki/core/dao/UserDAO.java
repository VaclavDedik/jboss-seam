package org.jboss.seam.wiki.core.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.wiki.core.model.User;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.transform.DistinctRootEntityResultTransformer;

import java.util.List;

@Name("userDAO")
@AutoCreate
@Transactional
public class UserDAO {

    @In
    protected EntityManager entityManager;

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

    public Role findRole(int accessLevel) {
        entityManager.joinTransaction();

        try {
            return (Role) entityManager
                    .createQuery("select r from Role r where r.accessLevel = :accessLevel")
                    .setParameter("accessLevel", accessLevel)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Role findRole(Long roleId) {
        entityManager.joinTransaction();
        return entityManager.find(Role.class, roleId);
    }

    public List<User> findByExample(User exampleUser, String orderByProperty, boolean orderDescending,
                                    int firstResult, int maxResults, String... ignoreProperty) {
        Criteria crit = prepareExampleCriteria(exampleUser, orderByProperty, orderDescending, ignoreProperty);
        crit.setFirstResult(firstResult).setMaxResults(maxResults);
        return (List<User>)crit.list();
    }

    public int getRowCountByExample(User exampleUser, String... ignoreProperty) {

        Criteria crit = prepareExampleCriteria(exampleUser, null, false, ignoreProperty);
        ScrollableResults cursor = crit.scroll();
        cursor.last();
        int count = cursor.getRowNumber() + 1;
        cursor.close();
        return count;
    }

    private Criteria prepareExampleCriteria(User exampleUser, String orderByProperty, boolean orderDescending, String... ignoreProperty) {
        entityManager.joinTransaction();

        Example example =  Example.create(exampleUser).enableLike(MatchMode.ANYWHERE).ignoreCase();

        for (String s : ignoreProperty) example.excludeProperty(s);

        Session session = (Session)entityManager.getDelegate();

        Criteria crit = session.createCriteria(User.class).add(example);
        if (orderByProperty != null)
                crit.addOrder( orderDescending ? Order.desc(orderByProperty) : Order.asc(orderByProperty) );

        return crit.setResultTransformer(new DistinctRootEntityResultTransformer());
    }

}
