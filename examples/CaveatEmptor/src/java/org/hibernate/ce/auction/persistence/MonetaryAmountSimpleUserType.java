package org.hibernate.ce.auction.persistence;

import org.hibernate.*;
import org.hibernate.usertype.UserType;
import org.hibernate.ce.auction.model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Currency;
import java.io.Serializable;

/**
 * This is a simple Hibernate custom mapping type for MonetaryAmount value types.
 * <p>
 * Note that this mapping type is for legacy databases that only have a
 * single numeric column to hold monetary amounts. Every <tt>MonetaryAmount</tt>
 * will be converted to USD (using some conversion magic of the class itself)
 * and saved to the database.
 *
 * @author Christian Bauer <christian@hibernate.org>
 */
public class MonetaryAmountSimpleUserType
		implements UserType {

	private static final int[] SQL_TYPES = {Types.NUMERIC};

	public int[] sqlTypes() { return SQL_TYPES; }

	public Class returnedClass() { return MonetaryAmount.class; }

	public boolean isMutable() { return false; }

	public Object deepCopy(Object value) {
		return value; // MonetaryAmount is immutable
	}

	public boolean equals(Object x, Object y) {
		if (x == y) return true;
		if (x == null || y == null) return false;
		return x.equals(y);
	}

	public int hashCode(Object o) throws HibernateException { return o.hashCode();  }
	public Serializable disassemble(Object o) throws HibernateException { return (Serializable)o; }
	public Object assemble(Serializable cached, Object owner) throws HibernateException { return cached; }
	// TODO: Whats this?
	public Object replace(Object o, Object o1, Object o2) throws HibernateException { return null; }

	public Object nullSafeGet(ResultSet resultSet,
							  String[] names,
							  Object owner)
			throws HibernateException, SQLException {

		if (resultSet.wasNull()) return null;
		BigDecimal valueInUSD = resultSet.getBigDecimal(names[0]);
		Currency userCurrency = Currency.getInstance("USD");
		return new MonetaryAmount(valueInUSD, userCurrency);
	}

	public void nullSafeSet(PreparedStatement statement,
							Object value,
							int index)
			throws HibernateException, SQLException {

		if (value == null) {
			statement.setNull(index, Types.NUMERIC);
		} else {
			MonetaryAmount anyCurrency = (MonetaryAmount)value;
			MonetaryAmount amountInUSD =
			  MonetaryAmount.convert( anyCurrency,
									  Currency.getInstance("USD") );
			statement.setBigDecimal(index, amountInUSD.getValue());
		}
	}
}
