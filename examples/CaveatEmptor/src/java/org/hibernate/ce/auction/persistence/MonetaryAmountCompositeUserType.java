package org.hibernate.ce.auction.persistence;

import org.hibernate.*;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.ce.auction.model.MonetaryAmount;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Currency;

/**
 * This is a simple Hibernate custom mapping type for MonetaryAmount value types.
 * <p>
 * Basically the same as the simple <tt>MonetaryAmountSimpleUserType</tt>, but
 * implementing the Hibernate <tt>CompositeUserType</tt> interface. This interface
 * has some additional methods that allow Hibernate to analyze the value type you
 * are mapping. This is mostly useful for HQL queries: with this custom mapping
 * type, you can use the "amount" and "currency" sub-components in HQL queries.
 *
 * @see MonetaryAmountSimpleUserType
 * @author Christian Bauer <christian@hibernate.org>
 */
public class MonetaryAmountCompositeUserType
		implements CompositeUserType {

	public Class returnedClass() { return MonetaryAmount.class; }

	public boolean equals(Object x, Object y) {
		if (x == y) return true;
		if (x == null || y == null) return false;
		return x.equals(y);
	}
	public int hashCode(Object o) throws HibernateException { return o.hashCode();  }
	public Serializable disassemble(Object o, SessionImplementor s) throws HibernateException { return (Serializable)o; }
	public Object assemble(Serializable cached, SessionImplementor s, Object owner) throws HibernateException { return cached; }
	// TODO: Whats this?
	public Object replace(Object o, Object o1, SessionImplementor s, Object o2) throws HibernateException { return null; }

	public Object deepCopy(Object value) {
		return value; // MonetaryAmount is immutable
	}

	public boolean isMutable() { return false; }

	public Object nullSafeGet(ResultSet resultSet,
							  String[] names,
							  SessionImplementor session,
							  Object owner)
			throws HibernateException, SQLException {

		if (resultSet.wasNull()) return null;
		BigDecimal value = resultSet.getBigDecimal( names[0] );
		Currency currency =
			Currency.getInstance(resultSet.getString( names[1] ) );
		return new MonetaryAmount(value, currency);
	}

	public void nullSafeSet(PreparedStatement statement,
							Object value,
							int index,
							SessionImplementor session)
			throws HibernateException, SQLException {

		if (value==null) {
		    statement.setNull(index, Types.NUMERIC);
		    statement.setNull(index+1, Types.VARCHAR);
		} else {
		    MonetaryAmount amount = (MonetaryAmount) value;
		    String currencyCode =
		                amount.getCurrency().getCurrencyCode();
		    statement.setBigDecimal( index, amount.getValue() );
		    statement.setString( index+1, currencyCode );
		}
	}

	public String[] getPropertyNames() {
		return new String[] { "value", "currency" };
	}

	public Type[] getPropertyTypes() {
		return new Type[] { Hibernate.BIG_DECIMAL, Hibernate.CURRENCY };
	}

	public Object getPropertyValue(Object component,
								   int property)
			throws HibernateException {
        MonetaryAmount monetaryAmount = (MonetaryAmount) component;
        if (property == 0)
            return monetaryAmount.getValue();
        else
            return monetaryAmount.getCurrency();
	}

	public void setPropertyValue(Object component,
								 int property,
								 Object value) throws HibernateException {
       throw new UnsupportedOperationException("MonetaryAmount is immutable");
	}

}