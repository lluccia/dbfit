package dbfit;

import dbfit.environment.OracleMockEnvironment;
import dbfit.util.oracle.mockextensions.HelloMock;
import fit.Fixture;

public class OracleMockTest extends OracleTest {
	
	public OracleMockTest() {
		super();
		environment = dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("OracleMock"); 
	}

	public Fixture helloMock() {
		return new HelloMock();
	}

	public Fixture inspectProcedureSource(String procName) {
		return new InspectProcedureSource((OracleMockEnvironment) environment, procName);
	}
}
