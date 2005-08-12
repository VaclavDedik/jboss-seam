package org.hibernate.ce.auction.persistence.pgsql;

import org.hibernate.ce.auction.persistence.MonetaryAmountCompositeUserType;
import org.hibernate.ce.auction.model.MonetaryAmount;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.hibernate.type.Type;
import org.postgresql.util.*;
import org.postgresql.PGConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

public class MonetaryAmountNativeType extends PGobject implements UserType {

    public static final String PGSQL_TYPE_NAME = "monetary_amount";

    MonetaryAmount monAm = null;

    public MonetaryAmountNativeType(MonetaryAmount monAm) {
        setType(PGSQL_TYPE_NAME);
        this.monAm = monAm;
    }

    /**
     * Required by the driver
     */
    public MonetaryAmountNativeType() {
        setType(PGSQL_TYPE_NAME);
    }


    /**
     * Nesting doesn't work for me, found no examples anywhere using
     * string literals, don't know how to quote this:
     * (1.99, (US Dollar,USD) )
     */
    public String getValue() {
        // MonetaryAmount conversion to postgres
        StringBuffer monetaryAmount = new StringBuffer(35);
        monetaryAmount.append("(");
        monetaryAmount.append(monAm.getValue().toPlainString());
        monetaryAmount.append(",");
        monetaryAmount.append(monAm.getCurrency().getCurrencyCode());
        monetaryAmount.append(")");

        return monetaryAmount.toString();
    }

    public void setValue(String s) throws SQLException {
        // MonetaryAmount conversion from postgres
        PGtokenizer t = new PGtokenizer(PGtokenizer.removePara(s), ',');
        try {
            BigDecimal value = new BigDecimal(t.getToken(0));
            Currency currency = Currency.getInstance(t.getToken(1));
            monAm = new MonetaryAmount(value, currency);
        } catch (NumberFormatException e) {
            throw new PSQLException(GT.tr("Conversion to type {0} failed: {1}.", new Object[]{type, s}), PSQLState.DATA_TYPE_MISMATCH, e);
        }
    }

    public MonetaryAmount getMonetaryAmount() {
        return monAm;
    }

    public int[] sqlTypes() {
        return new int[]{java.sql.Types.OTHER};
    }

    public Class returnedClass() {
        return MonetaryAmount.class;
    }

    public boolean equals(Object x, Object y) {
        if (x == y) return true;
        if (x == null || y == null) return false;
        return x.equals(y);
    }

    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    // TODO: Whats this?
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object deepCopy(Object value) {
        return value; // MonetaryAmount is immutable
    }

    public boolean isMutable() {
        return false;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        if (rs.wasNull()) return null;
        PGobject o = ((PGobject)rs.getObject(names[0]));
        System.out.println("### o = " + o.getClass().getName());
        if (o instanceof MonetaryAmountNativeType) {
            System.out.println("### TRUE");
        }
        return (MonetaryAmountNativeType)o;
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            MonetaryAmountNativeType adapter =
                    new MonetaryAmountNativeType((MonetaryAmount) value);
            st.setObject(index, adapter);
        }
    }

}