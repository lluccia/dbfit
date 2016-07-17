package dbfit.fixture;

import java.sql.SQLException;
import java.util.Map;

import dbfit.environment.OracleMockEnvironment;
import dbfit.mock.ProcedureDummy;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DdlStatementExecution;
import fit.Fixture;
import fit.Parse;

public class CreateDummy extends Fixture {

    private OracleMockEnvironment dbEnvironment;
    
    private String procName;

    public CreateDummy(OracleMockEnvironment dbEnvironment, String procName) {
        this.dbEnvironment = dbEnvironment;
        this.procName = procName;
    }

    @Override
    public void doRows(Parse rows) {
        try {
            String originalDDL = dbEnvironment.getProcedureSource(procName);
            dbEnvironment.addMockedObject(procName, originalDDL);
            
            Map<String, DbParameterAccessor> allParams = dbEnvironment.getAllProcedureParameters(procName);
            
            ProcedureDummy procedureDummy = new ProcedureDummy(procName);

            procedureDummy.setAllParams(allParams);
            String dummyDDL = procedureDummy.buildDDL();
            
            DdlStatementExecution ddl = dbEnvironment.createDdlStatementExecution(dummyDDL);
            ddl.run();
        } catch (SQLException e) {
             exception(rows.parts, e);
        }
    }
}

