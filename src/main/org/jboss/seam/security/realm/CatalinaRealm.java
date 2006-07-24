package org.jboss.seam.security.realm;

import java.lang.reflect.Method;
import java.security.Principal;

/**
 * Wrapper for Tomcat realm authentication
 *
 * @author Shane Bryzak
 */
public class CatalinaRealm implements Realm
{
  private Object realm;
  private Method authenticateMethod1;  // authenticate(String, String)
  private Method authenticateMethod2;  // authenticate(String, byte[])

  /**
   * Constructor - does some reflection gymnastics to find the Tomcat realm.
   * This is so we don't have a dependency on the catalina libs.
   */
  public CatalinaRealm(String localContext)
  {
    try
    {
      // First get the Server object
      Class serverFactoryClass = Class.forName("org.apache.catalina.ServerFactory");
      Method getServerMethod = serverFactoryClass.getMethod("getServer");
      Object server = getServerMethod.invoke(null);

      // Now try to find the service
      Method findServiceMethod = server.getClass().getMethod("findService", String.class);
      Object service = findServiceMethod.invoke(server, "Catalina");

      // Are we running under jboss-web ?
      if (service == null)
        service = findServiceMethod.invoke(server, "jboss.web");

      // Ok, as a last resort take the first service we find...
      if (service == null)
      {
        Method findServicesMethod = server.getClass().getMethod("findServices");
        Object[] services = (Object[]) findServicesMethod.invoke(server);
        if (services.length > 0)
          service = services[0];
        else
          throw new RuntimeException("Cannot find Tomcat service");
      }

      // Now get the Engine...
      Method getContainerMethod = service.getClass().getMethod("getContainer");
      Object engine = getContainerMethod.invoke(service);

      // Then the Host...
      Method getDefaultHostMethod = engine.getClass().getMethod("getDefaultHost");
      Method engineFindChildMethod = engine.getClass().getMethod("findChild", String.class);
      Object defaultHost = getDefaultHostMethod.invoke(engine);
      Object host = engineFindChildMethod.invoke(engine, defaultHost);

      // Then the Context...
      Method hostFindChildMethod = host.getClass().getMethod("findChild", String.class);
      Object context = hostFindChildMethod.invoke(host, localContext);

      // We can now get the realm... phew
      Method getRealmMethod = context.getClass().getMethod("getRealm");
      realm = getRealmMethod.invoke(context);

      authenticateMethod1 = realm.getClass().getMethod("authenticate", String.class, String.class);
      authenticateMethod2 = realm.getClass().getMethod("authenticate", String.class, byte[].class);
    }
    catch (ClassNotFoundException ex)
    {
      /** @todo log error and throw exception */
      ex.printStackTrace();
    }
    catch (Exception ex)
    {
      /** @todo log error and throw exception */
      ex.printStackTrace();
    }
  }

  /**
   * Authenticate the user using their username and credentials
   *
   * @param username String
   * @param credentials String
   * @return Principal
   */
  public Principal authenticate(String username, String credentials)
  {
    try
    {
      return (Principal) authenticateMethod1.invoke(realm, username, credentials);
    }
    catch (Exception ex)
    {
      /** @todo Log the error and work out what to do here  (throw an exception??) */
      return null;
    }
  }

  /**
   * Authenticate the user using their username and credentials
   *
   * @param username String
   * @param credentials byte[]
   * @return Principal
   */
  public Principal authenticate(String username, byte[] credentials)
  {
    try
    {
      return (Principal) authenticateMethod2.invoke(realm, username, credentials);
    }
    catch (Exception ex)
    {
      /** @todo Log the error and work out what to do here  (throw an exception??) */
      return null;
    }
  }
}
