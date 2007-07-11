package org.jboss.seam.wiki.test.util;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.*;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for integration testing with Seam and DBUnit datasets.
 * <p>
 * Subclass this class instead of <tt>SeamTest</tt> if you need to insert or clean data in
 * your database before and after a test. You need to implement <tt>prepareDBUnitOperations()</tt> and
 * add instances of <tt>DataSetOperation</tt>s to the * <tt>beforeTestOperations</tt> and
 * <tt>afterTestOperations</tt> lists. An example:
 * <pre>
 * public class MyTest extends DBUnitSeamTest {
 *
 *   protected void prepareDBUnitOperations() {
 *       beforeTestOperations.add(
 *          new DataSetOperation("my/datasets/BaseData.xml")
 *       );
 *       beforeTestOperations.add(
 *           new DataSetOperation("my/datasets/AdditionalData.xml", DatabaseOperation.INSERT)
 *       );
 *   }
 * ... // Various test methods with @Test annotation
 * }
 * </pre>
 * <p>
 * Note that <tt>DataSetOperation</tt> defaults to <tt>DatabaseOperation.CLEAN_INSERT</tt> if no
 * other operation is specified as a constructor argument. The above example cleans all tables defined
 * in <tt>BaseData.xml</tt>, then inserts all rows declared in <tt>BaseData.xml</tt>, then inserts
 * all the rows declared in <tt>AdditionalData.xml</tt>. This executes before every each test method
 * is invoked. If you require extra cleanup after a test method executes, add operations to the
 * <tt>afterTestOperations</tt> list.
 * </p>
 * <p>
 * A test class obtains the database connection for loading and cleaning of datasets in one of the following ways:
 * </p>
 * <dl>
 * <li>A TestNG test parameter named <tt>datasourceJndiName</tt> is provided by the TestNG test runner, which
 * automatically calls <tt>setDatasourceJndiName()</tt> on the test class before a logical test runs.</li
 * >
 * <li>An instance of a test class is created manually and the <tt>datasourceJndiName</tt> is passed as a
 * constructor argument.</li>
 *
 * <li>An instance of a test class is created manually and the <tt>setDatasourceJndiName()</tt> method is
 * called after creation and before a test runs.</li>
 *
 * <li>A subclass overrides the <tt>getConnection()</tt> method and returns a custom database connection.</li>
 *
 * </dl>
 * <p>
 * Referential integrity checks (foreign keys) will be or have to be disabled on the database connection
 * used for DBUnit operations. This makes adding circular references in datasets easier. Referential integrity checks
 * are enabled again after the connection has been used.
 * </p>
 * <p>
 * <b>Note that the methods <tt>disableReferentialIntegrity()</tt>,
 * <tt>enableReferentialIntegrity()</tt>, and <tt>editConfig()</tt> are implemented for HSQL DB. If you want to run 
 * unit tests on any other DBMS, you need to override these methods and implement them for your DBMS.</b>
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class DBUnitSeamTest extends SeamTest {

    private Log log = Logging.getLog(DBUnitSeamTest.class);

    protected String datasourceJndiName;
    protected List<DataSetOperation> beforeTestOperations = new ArrayList<DataSetOperation>();
    protected List<DataSetOperation> afterTestOperations = new ArrayList<DataSetOperation>();

    protected DBUnitSeamTest() {}

    protected DBUnitSeamTest(String datasourceJndiName) {
        this.datasourceJndiName = datasourceJndiName;
    }

    @BeforeTest
    @Parameters("datasourceJndiName")
    public void setDatasourceJndiName(String datasourceJndiName) {
        this.datasourceJndiName = datasourceJndiName;
    }

    @BeforeClass
    @Override
    public void init() throws Exception {
        super.init();
        prepareDBUnitOperations();
    }

    @BeforeMethod
    @Override
    public void begin() {
        super.begin();
        executeOperations(beforeTestOperations);
    }

    @AfterMethod
    @Override
    public void end() {
        super.end();
        executeOperations(afterTestOperations);
    }

    private void executeOperations(List<DataSetOperation> list) {
        IDatabaseConnection con = getConnection();
        disableReferentialIntegrity(con);
        for (DataSetOperation op : list) {
            log.info("executing DBUnit operation: " + op);
            op.execute(con);
        }
        enableReferentialIntegrity(con);
    }

    protected class DataSetOperation {
        String dataSetLocation;
        ReplacementDataSet dataSet;
        DatabaseOperation operation;

        /**
         * Defaults to <tt>DatabaseOperation.CLEAN_INSERT</tt>
         */
        public DataSetOperation(String dataSetLocation){
            this(dataSetLocation, DatabaseOperation.CLEAN_INSERT);
        }

        public DataSetOperation(String dataSetLocation, DatabaseOperation operation) {
            log.info("preparing dataset: " + dataSetLocation);

            // Load the base dataset file
            InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(dataSetLocation);
            try {
                this.dataSet = new ReplacementDataSet( new FlatXmlDataSet(input) );
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            this.dataSet.addReplacementObject("[NULL]", null);
            this.operation = operation;
            this.dataSetLocation = dataSetLocation;
        }

        public IDataSet getDataSet() {
            return dataSet;
        }

        public DatabaseOperation getOperation() {
            return operation;
        }

        public void execute(IDatabaseConnection connection) {
            try {
                this.operation.execute(connection, dataSet);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        public String toString() {
            // TODO: This is not pretty because DBUnit's DatabaseOperation doesn't implement toString() properly
            return operation.getClass() + " with dataset: " + dataSetLocation;
        }
    }

    // Subclasses can/have to override the following methods

    /**
     * Override this method if you want to provide your own DBUnit <tt>IDatabaseConnection</tt> instance.
     * <p/>
     * If you do not override this, default behavior is to use the * configured datasource name and
     * to obtain a connection with a JNDI lookup.
     *
     * @return a DBUnit database connection (wrapped)
     */
    protected IDatabaseConnection getConnection() {
        try {
            DataSource datasource = ((DataSource)getInitialContext().lookup(datasourceJndiName));

            // Get a JDBC connection from JNDI datasource
            Connection con = datasource.getConnection();
            IDatabaseConnection dbUnitCon = new DatabaseConnection(con);
            editConfig(dbUnitCon.getConfig());
            return dbUnitCon;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Override this method if you aren't using HSQL DB.
     * <p/>
     * Execute whatever statement is necessary to either defer or disable foreign
     * key constraint checking on the given database connection, which is used by
     * DBUnit to import datasets.
     *
     * @param con A DBUnit connection wrapper, which is used afterwards for dataset operations
     */
    protected void disableReferentialIntegrity(IDatabaseConnection con) {
        try {
            con.getConnection().prepareStatement("set referential_integrity FALSE").execute(); // HSQL DB
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Override this method if you aren't using HSQL DB.
     * <p/>
     * Execute whatever statement is necessary to enable integrity constraint checks after
     * dataset operations.
     *
     * @param con A DBUnit connection wrapper, before it is used by the application again
     */
    protected void enableReferentialIntegrity(IDatabaseConnection con) {
        try {
            con.getConnection().prepareStatement("set referential_integrity TRUE").execute();  // HSQL DB
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Override this method if you require DBUnit configuration features or additional properties.
     * <p>
     * Called after a connection has been obtaind and before the connection is used. Can be a
     * NOOP method if no additional settings are necessary for your DBUnit/DBMS setup.
     *
     * @param config A DBUnit <tt>DatabaseConfig</tt> object for setting properties and features
     */
    protected void editConfig(DatabaseConfig config) {

        // TODO: DBUnit/HSQL bugfix
        // http://www.carbonfive.com/community/archives/2005/07/dbunit_hsql_and.html
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new DefaultDataTypeFactory() {
            public DataType createDataType(int sqlType, String sqlTypeName)
              throws DataTypeException {
               if (sqlType == Types.BOOLEAN) {
                  return DataType.BOOLEAN;
                }
               return super.createDataType(sqlType, sqlTypeName);
             }
        });
    }

    /**
     * Implement this in a subclass.
     * <p>
     * Use it to stack DBUnit <tt>DataSetOperation</tt>'s with
     * the <tt>beforeTestOperations</tt> and <tt>afterTestOperations</tt> lists.
     */
    protected abstract void prepareDBUnitOperations();

}
