/**
 *
 */
package org.jboss.seam.example.spring;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

/**
 * @author youngm
 *
 */
public class UserService {	
    private EntityManager entityManager;

	public boolean changePassword(String username, String oldPassword, String newPassword) {
		if (newPassword == null || "".equals(newPassword)) {
			throw new IllegalArgumentException("newPassword cannot be null.");
		}
        
		User user = findUser(username);
		if (user.getPassword().equals(oldPassword)) {
			user.setPassword(newPassword);
			return true;
		} else {
			return false;
		}
	}

	public User findUser(String username) {
		if (username == null || "".equals(username)) {
			throw new IllegalArgumentException("Username cannot be null");
		}
		return (User) entityManager.find(User.class, username);
	}

    public User findUser(String username, String password) {
        try {
	    return (User) 
            entityManager.createQuery("select u from User u where u.username=:username and u.password=:password")
                         .setParameter("username", username)
                         .setParameter("password", password)
                         .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

    public void createUser(User user) throws ValidationException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        User existingUser = findUser(user.getUsername());
        if (existingUser != null) {
            throw new ValidationException("Username "+user.getUsername()+" already exists");
        }
        
        entityManager.persist(user);
    }

	/**
	 * @param session the session to set
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
