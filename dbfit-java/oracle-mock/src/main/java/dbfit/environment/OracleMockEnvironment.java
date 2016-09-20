package dbfit.environment;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.util.DdlStatementExecution;
import dbfit.util.Log;

@DatabaseEnvironment(name = "OracleMock", driver = "oracle.jdbc.OracleDriver")
public class OracleMockEnvironment extends OracleEnvironment {

    private static final int MAX_CLOB_LENGTH = 50000;

    private Map<String, String> mockedObjects = new HashMap<>();
    private Map<String, String> spyTables = new HashMap<>();

    public OracleMockEnvironment(String driverClassName) {
        super(driverClassName);
    }

    public String getProcedureSource(String procName) throws SQLException {
        String query = "SELECT DBMS_METADATA.GET_DDL(OBJECT_TYPE, OBJECT_NAME) SOURCE "
                + "FROM                                        "
                + "    ALL_OBJECTS                             "
                + "WHERE                                       "
                + "    OBJECT_TYPE IN ('FUNCTION','PROCEDURE') "
                + "    AND OBJECT_NAME = ?                     ";
        String[] queryParameters = { procName };

        Object source = null;
        try (CallableStatement dc = openDbCallWithParameters(query, queryParameters)) {
            Log.log("executing query");
            ResultSet rs = dc.executeQuery();

            if (rs.next()) {
                source = rs.getObject(1);
            } else {
                throw new IllegalArgumentException(String.format("Procedure or function not found: %s", procName));
            }
        }

        return clobToString(source);
    }

    private CallableStatement openDbCallWithParameters(String query, String[] queryParameters) throws SQLException {
        Log.log("preparing call " + query, (Object[]) queryParameters);
        CallableStatement dc = currentConnection.prepareCall(query);
        Log.log("setting parameters");
        for (int i = 0; i < queryParameters.length; i++) {
            dc.setString(i + 1, queryParameters[i].toUpperCase());
        }

        return dc;
    }

    public String clobToString(Object o) throws SQLException {
        if (o == null)
            return null;
        if (!(o instanceof oracle.sql.CLOB)) {
            throw new UnsupportedOperationException("OracleClobNormaliser cannot work with " + o.getClass());
        }
        oracle.sql.CLOB clob = (oracle.sql.CLOB) o;
        if (clob.length() > MAX_CLOB_LENGTH)
            throw new UnsupportedOperationException(
                    "Clobs larger than " + MAX_CLOB_LENGTH + " bytes are not supported by DBFIT");
        char[] buffer = new char[MAX_CLOB_LENGTH];
        int total = clob.getChars(1, MAX_CLOB_LENGTH, buffer);
        return String.valueOf(buffer, 0, total);
    }

    @Override
    public void closeConnection() throws SQLException {
        restoreMockedObjects();
        removeSpyTables();
        super.closeConnection();
    }
    
    private void restoreMockedObjects() throws SQLException {
        for (String mockedDDL : mockedObjects.values()) {
            DdlStatementExecution ddl = createDdlStatementExecution(mockedDDL);
            ddl.run();
        }
    }
    
    private void removeSpyTables() throws SQLException {
        for(String spyTableName: spyTables.values()) {
            DdlStatementExecution ddl = createDdlStatementExecution("DROP TABLE " + spyTableName);
            ddl.run();
        }
    }

    public void addMockedObject(String procName, String originalDdl) {
        if (mockedObjects.get(procName) == null)
            this.mockedObjects.put(procName, originalDdl);
    }
    
    public void addSpyTable(String procName, String spyTableName) {
        this.spyTables.put(procName, spyTableName);
    }
    
    public String getSpyTable(String procName) {
        return this.spyTables.get(procName);
    }
}
