package org.hibernate.ce.auction.persistence.audit;

import org.hibernate.*;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.*;

import org.hibernate.ce.auction.model.*;
import org.apache.commons.logging.*;

public class AuditLogInterceptor implements Interceptor {

	private static Log log = LogFactory.getLog(AuditLogInterceptor.class);

	private Session session;
	private Long userId;

	private Set inserts = new HashSet();
	private Set updates = new HashSet();

	public void setSession(Session session) {
		this.session=session;
	}
	public void setUserId(Long userId) {
		this.userId=userId;
	}

	public boolean onSave(Object entity,
						 Serializable id,
						 Object[] state,
						 String[] propertyNames,
						 Type[] types)
			throws CallbackException {

		if (entity instanceof Auditable)
			inserts.add(entity);

		return false;
	}

	public boolean onFlushDirty(Object entity,
								Serializable id,
								Object[] currentState,
								Object[] previousState,
								String[] propertyNames,
								Type[] types)
			throws CallbackException {

		if (entity instanceof Auditable)
			updates.add(entity);

		return false;
	}

	public boolean onLoad(Object o, Serializable serializable, Object[] objects, String[] strings, Type[] types) throws CallbackException {
		return false;
	}

	public void onDelete(Object o, Serializable serializable, Object[] objects, String[] strings, Type[] types) throws CallbackException {
	}

	public void preFlush(Iterator iterator) throws CallbackException {
	}

	public void postFlush(Iterator iterator) throws CallbackException {
		try {
			for (Iterator it = inserts.iterator(); it.hasNext();) {
				Auditable entity = (Auditable) it.next();
				log.debug("Intercepted creation of : " + entity);
				AuditLog.logEvent("create",
								  entity,
								  userId,
								  session.connection());
			}
			for (Iterator it = updates.iterator(); it.hasNext();) {
				Auditable entity = (Auditable) it.next();
				log.debug("Intercepted modification of : " + entity);
				AuditLog.logEvent("update",
								  entity,
								  userId,
								  session.connection());
			}
		} catch (HibernateException ex) {
			throw new CallbackException(ex);
		} finally {
			inserts.clear();
			updates.clear();
		}
	}

	public int[] findDirty(Object o, Serializable serializable, Object[] objects, Object[] objects1, String[] strings, Type[] types) {
		return null;
	}

	public Object instantiate(Class aClass, Serializable serializable) throws CallbackException {
		return null;
	}

	public Boolean isTransient(Object o) {
		return null;
	}

	public Object instantiate(String s, Serializable serializable) throws CallbackException {
		return null;
	}

	public String getEntityName(Object o) throws CallbackException {
		return null;
	}

	public Object getEntity(String s, Serializable serializable) throws CallbackException {
		return null;
	}

    public Object instantiate(String entityName, EntityMode entityMode, Serializable id) throws CallbackException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void afterTransactionBegin(Transaction tx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void beforeTransactionCompletion(Transaction tx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void afterTransactionCompletion(Transaction tx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
