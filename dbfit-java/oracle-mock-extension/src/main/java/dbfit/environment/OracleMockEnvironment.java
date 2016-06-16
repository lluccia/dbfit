package dbfit.environment;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.util.Log;

@DatabaseEnvironment(name="OracleMock", driver="oracle.jdbc.OracleDriver")
public class OracleMockEnvironment extends OracleEnvironment {

	public OracleMockEnvironment(String driverClassName) {
		super(driverClassName);
	}

	public String getProcedureSource(String procName) throws SQLException {
		String query = "SELECT TEXT FROM USER_SOURCE WHERE NAME = ? ORDER BY LINE";
		String[] queryParameters = { procName };
		
		StringBuilder source = new StringBuilder();
		try (CallableStatement dc = openDbCallWithParameters(query, queryParameters)) {
            Log.log("executing query");
            ResultSet rs = dc.executeQuery();

            while (rs.next()) {
            	source.append(rs.getString(1));
            }
        }
		
		return "CREATE OR REPLACE " + source.toString();
	}
	
	private CallableStatement openDbCallWithParameters(String query,
            String[] queryParameters) throws SQLException {
        Log.log("preparing call " + query, (Object[]) queryParameters);
        CallableStatement dc = currentConnection.prepareCall(query);
        Log.log("setting parameters");
        for (int i = 0; i < queryParameters.length; i++) {
            dc.setString(i + 1, queryParameters[i].toUpperCase());
        }

        return dc;
    }

}
