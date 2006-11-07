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
import javax.transaction.SystemException;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.ManagedHibernateSession;
import org.jboss.seam.core.ManagedPersistenceContext;
import org.jboss.seam.persistence.PersistenceProvider;
import org.jboss.seam.security.Authentication;
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
  protected enum PersistenceType {
    managedPersistenceContext,
    managedHibernateSession,
    entityManagerFactory };

  protected PersistenceType persistenceType;

  private Object pcm;
  private String aclUserQuery;
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
      this.pcm = value;
    }
    else if (ManagedHibernateSession.class.isAssignableFrom(value.getClass()))
    {
      persistenceType = PersistenceType.managedHibernateSession;
      this.pcm = value;
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
          this.pcm = obj;
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

  public String getAclUserQuery()
  {
    return aclUserQuery;
  }

  public void setAclUserQuery(String aclUserQuery)
  {
    this.aclUserQuery = aclUserQuery;
  }

  public String getAclQuery()
  {
    return aclQuery;
  }

  public void setAclQuery(String aclQuery)
  {
    this.aclQuery = aclQuery;
  }

  protected Object createAclQuery(Principal principal)
      throws Exception
  {
    switch (persistenceType)
    {
      case managedPersistenceContext:
        return ((ManagedPersistenceContext) pcm).getEntityManager().createQuery(aclUserQuery);
      case managedHibernateSession:
        return ((ManagedHibernateSession) pcm).getSession().createQuery(aclUserQuery);
      case entityManagerFactory:
        EntityManager em = ((EntityManagerFactory) pcm).createEntityManager();
        if ( !Lifecycle.isDestroying() && Transactions.isTransactionActive() )
           em.joinTransaction();
        return em.createQuery(aclUserQuery);
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
        ((Query) query).setParameter("recipient", principal.getName())
          .setParameter("roles", roles)
          .setParameter("identity", getObjectIdentity(target));
        break;
      case managedHibernateSession:
        /** @todo implement */
        break;
      case entityManagerFactory:
        /** @todo implement */
        break;
    }
  }

  protected Object getObjectIdentity(Object obj)
  {
    switch (persistenceType)
    {
      case managedPersistenceContext:
        try
        {
          return PersistenceProvider.instance().getId(obj,
              ( (ManagedPersistenceContext) pcm).getEntityManager());
        }
        catch (SystemException ex) {  /** @todo  */
        }
        catch (NamingException ex) { /** @todo  */
        }

      /** @todo Implement hibernate and emf support */
    }

    return null;
  }

  protected Object executeQuery(Object query)
  {
    switch (persistenceType)
    {
      case managedPersistenceContext:
        return ((Query) query).getResultList();
      /** @todo Implement hibernate and emf support */
    }

    return null;
  }

  protected Set<Permission> convertToPermissions(Object target, Object perms)
  {
    /** @todo use the @AclProvider specified on the target object to convert
     * the specified permissions param to a set of actual permissions */
    return null;
  }

  public Set<Permission> getPermissions(Object obj, Principal principal)
  {
    try
    {
      Object q = createAclQuery(principal);

      bindQueryParams(q, obj, principal);

      Object result = executeQuery(q);

      return convertToPermissions(obj, result);
    }
    catch (Exception ex) { }

    return null;
  }

  public Map<Principal,Set<Permission>> getPermissions(Object obj)
  {
    /** @todo implement this */
    return null;
  }
}
