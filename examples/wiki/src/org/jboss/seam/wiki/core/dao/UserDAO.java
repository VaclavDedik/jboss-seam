package org.jboss.seam.wiki.core.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.wiki.core.users.Role;
import org.jboss.seam.wiki.core.users.User;

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

}
