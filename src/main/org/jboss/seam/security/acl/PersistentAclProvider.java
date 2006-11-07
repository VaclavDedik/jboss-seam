package org.jboss.seam.security.acl;

import java.security.Principal;
import java.security.acl.Permission;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.ManagedHibernateSession;
import org.jboss.seam.core.ManagedPersistenceContext;
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
  private enum PersistenceType {
    managedPersistenceContext,
    managedHibernateSession,
    entityManagerFactory };

  private PersistenceType persistenceType;
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

  protected void bindQueryParams(Object query, Principal principal)
  {

  }

  protected Object executeQuery(Object query)
  {
    return null;
  }

  protected void closeQuery(Object query)
  {

  }

  public Set<Permission> getPermissions(Object obj, Principal principal)
  {
    try
    {
      Object q = createAclQuery(principal);

      bindQueryParams(q, principal);

      executeQuery(q);

      closeQuery(q);
    }
    catch (Exception ex) { }

    return null;
  }

  public Map<Principal,Set<Permission>> getPermissions(Object obj)
  {
    return null;
  }
}
