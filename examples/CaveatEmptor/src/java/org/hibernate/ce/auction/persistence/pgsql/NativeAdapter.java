package org.hibernate.ce.auction.persistence.pgsql;

import org.postgresql.PGConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class NativeAdapter
 extends org.postgresql.Driver {

    public Connection connect(String s, Properties properties) throws SQLException {
        Thread.dumpStack();
        Connection con = super.connect(s, properties);;
/*
        // Add PG type conversion adapters
        ((PGConnection)con).addDataType(MonetaryAmountNativeType.PGSQL_TYPE_NAME,
                                        MonetaryAmountNativeType.class);
*/
        return (Connection)con;
    }

}
