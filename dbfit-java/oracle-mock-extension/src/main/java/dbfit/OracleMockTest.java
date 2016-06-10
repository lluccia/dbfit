package dbfit;

import dbfit.util.oracle.mockextensions.HelloMock;
import fit.Fixture;

public class OracleMockTest extends OracleTest {
	
	public Fixture helloMock() {
		return new HelloMock();
	}
	
	public Fixture inspectProcedureSource(String procName) {
		return new InspectProcedureSource(environment, procName);
	}
}
