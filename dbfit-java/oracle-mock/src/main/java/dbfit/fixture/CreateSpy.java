package dbfit.fixture;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dbfit.environment.OracleMockEnvironment;
import dbfit.mock.ProcedureSpy;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DdlStatementExecution;
import fit.Fixture;
import fit.Parse;

public class CreateSpy extends Fixture {

    private OracleMockEnvironment dbEnvironment;
    
    private String procName;
    private List<String> columnNames;
    private List<String> columnValues;

    public CreateSpy(OracleMockEnvironment dbEnvironment, String procName) {
        this.dbEnvironment = dbEnvironment;
        this.procName = procName;
    }

    @Override
    public void doRows(Parse rows) {
        columnNames = getColumnDataFrom(rows.parts);
        columnValues = getColumnDataFrom(rows.more.parts);
        
        try {
            String originalDDL = dbEnvironment.getProcedureSource(procName);
            
            Map<String, DbParameterAccessor> allParams = dbEnvironment.getAllProcedureParameters(procName);
            
            ProcedureSpy procedureSpy = new ProcedureSpy(procName);
            
            procedureSpy.setAllParams(allParams);
            for (int i = 0; i < columnNames.size(); i++)
                procedureSpy.setStubValue(columnNames.get(i).toUpperCase(), columnValues.get(i));
            
            procedureSpy.validate();
            
            DdlStatementExecution spyTableDdl = dbEnvironment.createDdlStatementExecution(procedureSpy.buildSpyTableDDL());
            spyTableDdl.run();
            dbEnvironment.addSpyTable(procName, procedureSpy.getSpyTableName());
            
            DdlStatementExecution spyProcDdl = dbEnvironment.createDdlStatementExecution(procedureSpy.buildDDL());
            spyProcDdl.run();
            dbEnvironment.addMockedObject(procName, originalDDL);
            
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

