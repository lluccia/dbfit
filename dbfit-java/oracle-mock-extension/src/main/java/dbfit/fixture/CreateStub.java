package dbfit.fixture;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dbfit.environment.OracleMockEnvironment;
import dbfit.mock.ProcedureStub;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DdlStatementExecution;
import fit.Fixture;
import fit.Parse;

public class CreateStub extends Fixture {

    private OracleMockEnvironment dbEnvironment;
    
    private String procName;
    private List<String> columnNames;
    private List<String> columnValues;

    public CreateStub(OracleMockEnvironment dbEnvironment, String procName) {
        this.dbEnvironment = dbEnvironment;
        this.procName = procName;
    }

    @Override
    public void doRows(Parse rows) {
        columnNames = getColumnDataFrom(rows.parts);
        columnValues = getColumnDataFrom(rows.more.parts);
        
        try {
            String originalDDL = dbEnvironment.getProcedureSource(procName);
            dbEnvironment.addMockedObject(procName, originalDDL);
            
            Map<String, DbParameterAccessor> allParams = dbEnvironment.getAllProcedureParameters(procName);
            
            ProcedureStub procedureStub = new ProcedureStub(procName);
            
            procedureStub.setAllParams(allParams);
            for (int i = 0; i < columnNames.size(); i++)
                procedureStub.setStubValue(columnNames.get(i).toUpperCase(), columnValues.get(i));
            
            procedureStub.validate();
            
            String stubDDL = procedureStub.buildDDL();
            
            DdlStatementExecution ddl = dbEnvironment.createDdlStatementExecution(stubDDL);
            ddl.run();
            
            
            
        } catch (SQLException e) {
             exception(rows.parts, e);
        }
    }
    
    private List<String> getColumnDataFrom(Parse parse) {
        List<String> columnData = new ArrayList<>();
        for (; parse != null; parse = parse.more) {
            columnData.add(parse.text());
        }
        return columnData;
    }
    
}

