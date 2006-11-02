package org.jboss.seam.example.test;

import org.jboss.seam.example.registration.Register;
import org.jboss.seam.example.registration.User;
import org.testng.annotations.Test;
import org.jboss.seam.mock.SeamTest;

public class RegisterTest extends SeamTest {

	@Test
	public void testRegistration() throws Exception {
		new Script() {
			@Override
			protected void updateModelValues() throws Exception {				
				User user = (User) getInstance("user");				
				assert user != null;
				user.setUsername("1ovthafew");
				user.setPassword("secret");
				user.setName("Gavin King");
			}
			@Override
			protected void invokeApplication() {
				Register register = (Register) getInstance(
						"register");
				String outcome = register.register();
				assert "success".equals(outcome);
			}
			@Override
			protected void renderResponse() {
				User user = (User) getInstance("user");
				assert user != null;
				assert user.getName().equals("Gavin King");
				assert user.getUsername().equals("1ovthafew");
				assert user.getPassword().equals("secret");
			}
		}.run();
	}	
}
