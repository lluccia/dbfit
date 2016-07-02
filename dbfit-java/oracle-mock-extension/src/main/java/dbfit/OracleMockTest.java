package dbfit;

import java.sql.SQLException;
import java.util.Map;

import dbfit.environment.OracleMockEnvironment;
import dbfit.mock.StoredProcedureDummy;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DdlStatementExecution;
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

    public Fixture createDummy(String procName) throws SQLException {
        String originalDDL = ((OracleMockEnvironment) environment).getProcedureSource(procName);
        ((OracleMockEnvironment) environment).addMockedObject(procName, originalDDL);
        
        
        Map<String, DbParameterAccessor> allParams = environment.getAllProcedureParameters(procName);
        
        if (allParams.isEmpty()){
            throw new SQLException(
                    "Cannot retrieve list of parameters for procedure " +
                        procName + " - check spelling and access rights");
        }
        
        StoredProcedureDummy spDummy = new StoredProcedureDummy(procName);
        spDummy.setAllParams(allParams);
        String dummyDDL = spDummy.buildDDL();
        
        DdlStatementExecution ddl = environment.createDdlStatementExecution(dummyDDL);
        ddl.run();
        
        return new InspectProcedureSource((OracleMockEnvironment) environment, procName);
    }
}
