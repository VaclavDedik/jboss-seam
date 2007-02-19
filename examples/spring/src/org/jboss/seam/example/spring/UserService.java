/**
 *
 */
package org.jboss.seam.example.spring;

import java.util.List;

import org.hibernate.Session;
import org.jboss.seam.core.FacesMessages;

/**
 * @author youngm
 *
 */
public class UserService {
	private Session session;

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
		return (User) session.get(User.class, username);
	}

	public User findUser(String username, String password) {
		List<User> results = session.createQuery(
				"select u from User u where u.username=:username and u.password=:password").setParameter("username",
				username).setParameter("password", password).list();
		if (results.size() == 0) {
			return null;
		} else {
			return results.get(0);
		}
	}

	public void createUser(User user) throws ValidationException {
		if(user == null) {
			throw new IllegalArgumentException("User cannot be null");
		}
		User existingUser = findUser(user.getUsername());
		if(existingUser != null) {
			throw new ValidationException("Username "+user.getUsername()+" already exists");
		}
		session.persist(user);
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}
}
