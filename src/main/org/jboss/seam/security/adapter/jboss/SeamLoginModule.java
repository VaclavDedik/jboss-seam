package org.jboss.seam.security.adapter.jboss;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.jboss.seam.security.Authentication;
import org.jboss.seam.util.Reflections;

/**
 *
 *
 * @author Shane Bryzak
 */
public class SeamLoginModule implements LoginModule
{
  private static final String SIMPLE_PRINCIPAL_CLASS =
      "org.jboss.security.SimplePrincipal";
  private static final String SIMPLE_GROUP_CLASS =
      "org.jboss.security.SimpleGroup";

  private Constructor simplePrincipalConstructor = null;
  private Constructor simpleGroupConstructor = null;

  private Subject subject;

  private CallbackHandler callbackHandler;

  private Authentication authentication;
  private Group roles;

  public boolean abort()
  {
    return true;
  }

  /**
   *
   * @return boolean
   */
  public boolean commit()
  {
    Set<Principal> principals = subject.getPrincipals();

    principals.add(authentication);

    try
    {
      for (Group group : getRoleSets())
      {
        Group subjectGroup = null;
        subjectGroup = createGroup(group.getName(), principals);

        // Copy the group members to the Subject group
        Enumeration members = group.members();
        while (members.hasMoreElements())
        {
          Principal role = (Principal) members.nextElement();
          subjectGroup.addMember(role);
        }
      }

      return true;
    }
    catch (Exception ex)
    {
      return false;
    }
  }

  /**
   *
   * @param name String
   * @param principals Set
   * @return Group
   */
  protected Group createGroup(String name, Set<Principal> principals)
      throws Exception
  {
    roles = null;
    for (Principal principal : principals)
    {
      if (! (principal instanceof Group))
        continue;

      if ( ( (Group) principal).getName().equals(name))
      {
        roles = (Group) principal;
        break;
      }
    }

    if (roles == null)
    {
      roles = createSimpleGroup(name);
      principals.add(roles);
    }
    return roles;
  }

  /**
   *
   * @param name String
   * @return Principal
   * @throws Exception
   */
  private Principal createSimplePrincipal(String name)
      throws Exception
  {
    if (simplePrincipalConstructor == null)
    {
      Class cls = Reflections.classForName(SIMPLE_PRINCIPAL_CLASS);
      simplePrincipalConstructor = cls.getConstructor(String.class);
    }
    return (Principal) simplePrincipalConstructor.newInstance(name);
  }

  /**
   *
   * @param name String
   * @return Group
   */
  private Group createSimpleGroup(String name)
      throws Exception
  {
    if (simpleGroupConstructor == null)
    {
      Class cls = Class.forName(SIMPLE_GROUP_CLASS);
      simpleGroupConstructor = cls.getConstructor(String.class);
    }

    return (Group) simpleGroupConstructor.newInstance(name);
  }

  /**
   *
   * @return Group[]
   * @throws LoginException
   */
  protected Group[] getRoleSets()
      throws Exception
  {
    Group rolesGroup = createSimpleGroup("Roles");

    ArrayList groups = new ArrayList();
    groups.add(rolesGroup);

    for (String role : authentication.getRoles())
    {
      rolesGroup.addMember(createIdentity(role));
    }

    Group[] roleSets = new Group[groups.size()];
    groups.toArray(roleSets);
    return roleSets;
  }

  /**
   *
   * @param username String
   * @return Principal
   * @throws Exception
   */
  protected Principal createIdentity(String username)
      throws Exception
  {
    return createSimplePrincipal(username);
  }

  /**
   *
   * @param subject Subject
   * @param handler CallbackHandler
   * @param sharedState Map
   * @param options Map
   */
  public void initialize(Subject subject, CallbackHandler handler,
                         Map sharedState, Map options)
  {
    this.subject = subject;
    this.callbackHandler = handler;
  }

  /**
   *
   * @return boolean
   */
  public boolean login()
      throws LoginException
  {
    authentication = Authentication.instance();

    if (authentication == null || !authentication.isAuthenticated())
    {
      NameCallback nameCallback = new NameCallback("Username");
      PasswordCallback pwCallback = new PasswordCallback("Password", false);
      try
      {
        callbackHandler.handle(new Callback[]
                               {nameCallback, pwCallback});
      }
      catch (UnsupportedCallbackException ex)
      {
      }
      catch (IOException ex)
      {
      }

      /** @todo Authenticate here if not already authenticated */
    }

    return true;
  }

  /**
   *
   * @return boolean
   * @throws LoginException
   */
  public boolean logout()
      throws LoginException
  {
    Set principals = subject.getPrincipals();
    principals.remove(authentication);
    if (roles != null)
      principals.remove(roles);
    return true;
  }
}
