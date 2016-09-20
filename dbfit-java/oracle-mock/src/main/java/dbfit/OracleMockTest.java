package dbfit;

import java.sql.SQLException;

import dbfit.environment.OracleMockEnvironment;
import dbfit.fixture.CreateDummy;
import dbfit.fixture.CreateSpy;
import dbfit.fixture.CreateStub;
import dbfit.fixture.InspectProcedureSource;
import fit.Fixture;

public class OracleMockTest extends OracleTest {

    public OracleMockTest() {
        super();
        environment = dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("OracleMock");
    }

    public Fixture inspectProcedureSource(String procName) {
        return new InspectProcedureSource((OracleMockEnvironment) environment, procName);
    }

    public Fixture createDummy(String procName) throws SQLException {
        return new CreateDummy((OracleMockEnvironment) environment, procName);
    }
    
    public Fixture createStub(String procName) {
        return new CreateStub((OracleMockEnvironment) environment, procName);
    }
    
    public Fixture createSpy(String procName) {
        return new CreateSpy((OracleMockEnvironment) environment, procName);
    }
    
    
    
}
