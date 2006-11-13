package org.jboss.seam.security.acl;

import java.security.Principal;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.ManagedHibernateSession;
import org.jboss.seam.core.ManagedPersistenceContext;
import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.SeamSecurityManager;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Transactions;

/**
 * Persistent Acl provider.
 *
 * @author Shane Bryzak
 */
@Intercept(NEVER)
@Scope(APPLICATION)
public class PersistentAclProvider extends AbstractAclProvider
{
  protected enum PersistenceType
  {
    managedPersistenceContext,
    managedHibernateSession,
    entityManagerFactory
  }

  protected PersistenceType persistenceType;

  private Object pcm;
  private String aclQuery;

  public Object getPersistenceContextManager()
  {
    return pcm;
  }

  public void setPersistenceContextManager(Object value)
  {
    if (ManagedPersistenceContext.class.isAssignableFrom(value.getClass()))
    {
      persistenceType = PersistenceType.managedPersistenceContext;
      pcm = value;
    }
    else if (ManagedHibernateSession.class.isAssignableFrom(value.getClass()))
    {
      persistenceType = PersistenceType.managedHibernateSession;
      pcm = value;
    }
    else if (EntityManagerFactory.class.isAssignableFrom(value.getClass()))
    {
      persistenceType = PersistenceType.entityManagerFactory;
      pcm = value;
    }
    else if (value instanceof String)
    {
      Object obj = null;
      try
      {
        obj = Naming.getInitialContext().lookup( (String) value);
        if (EntityManagerFactory.class.isAssignableFrom(obj.getClass()))
        {
          persistenceType = PersistenceType.entityManagerFactory;
          pcm = obj;
          return;
        }
      }
      catch (NamingException ex) { }

      throw new IllegalArgumentException("Invalid JNDI name specified for EntityManagerFactory");
    }
    else
      throw new IllegalArgumentException(
        "Parameter must be instance of ManagedPersistenceContext, " +
        "ManagedHibernateSession or String value specifying the JNDI name of an EntityManagerFactory");
  }

  public String getAclQuery()
  {
    return aclQuery;
  }

  public void setAclQuery(String aclQuery)
  {
    this.aclQuery = aclQuery;
  }

  protected Object createAclQuery()
      throws Exception
  {
    switch (persistenceType)
    {
      case managedPersistenceContext:
        return ((ManagedPersistenceContext) pcm).getEntityManager().createQuery(aclQuery);
      case managedHibernateSession:
        return ((ManagedHibernateSession) pcm).getSession().createQuery(aclQuery);
      case entityManagerFactory:
        EntityManager em = ((EntityManagerFactory) pcm).createEntityManager();
        if ( !Lifecycle.isDestroying() && Transactions.isTransactionActive() )
           em.joinTransaction();
        return em.createQuery(aclQuery);
    }

    throw new IllegalStateException("Unknown persistence type");
  }

  protected void bindQueryParams(Object query, Object target, Principal principal)
  {
    List<String> roles = new ArrayList<String>();

    if (Authentication.class.isAssignableFrom(principal.getClass()))
    {
      for (String role : ((Authentication) principal).getRoles())
      {
        roles.add(role);
      }
    }

    switch (persistenceType)
    {
      case managedPersistenceContext:
      case entityManagerFactory:
        ((Query) query).setParameter("identity",
            SeamSecurityManager.instance().getObjectIdentity(target));
        break;
      case managedHibernateSession:
        ((org.hibernate.Query) query).setParameter("identity",
            SeamSecurityManager.instance().getObjectIdentity(target));
        break;
    }
  }

  protected Object executeQuery(Object query)
  {
    switch (persistenceType)
    {
      case managedPersistenceContext:
      case entityManagerFactory:
        return ((Query) query).getResultList();
      case managedHibernateSession:
        return ((org.hibernate.Query) query).list();
    }

    return null;
  }

  protected Set<Permission> convertToPermissions(Object target, Object perms)
  {


    /** @todo use the @AclProvider specified on the target object to convert
     * the specified permissions param to a set of actual permissions */
    return null;
  }

  @Override
  public Set<Permission> getPermissions(Object obj, Principal principal)
  {
    try
    {
      Object q = createAclQuery();

      bindQueryParams(q, obj, principal);

      Object result = executeQuery(q);

      return convertToPermissions(obj, result);
    }
    catch (Exception ex) { }

    return null;
  }

  @Override
  public Map<Principal,Set<Permission>> getPermissions(Object obj)
  {
    /** @todo implement this */
    return null;
  }
}
