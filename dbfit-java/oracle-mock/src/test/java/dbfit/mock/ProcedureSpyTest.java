package dbfit.mock;

import static dbfit.mock.TestUtils.assertNormalized;
import static dbfit.mock.TestUtils.createParameter;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;

public class ProcedureSpyTest {
    
    @Test(expected=IllegalArgumentException.class)
    public void procedureWithNoInputParametersCannotBeSpied() {
        ProcedureSpy procedureStub = new ProcedureSpy("procname");
        
        procedureStub.validate();
    }
    
    @Test
    public void canCreateSpyTableDDLOneParameter() {
        ProcedureSpy procedureStub = new ProcedureSpy("procname");
        
        Map<String, DbParameterAccessor> paramMap = new HashMap<String, DbParameterAccessor>();
        paramMap.put("param1", createParameter(0, "PARAM1", Direction.INPUT, "VARCHAR2"));
        
        procedureStub.setAllParams(paramMap);
        
        assertNormalized(
                procedureStub.buildSpyTableDDL(),
                "CREATE TABLE SPY_PROCNAME ("
                + " PARAM1 VARCHAR2(4000)"
                + " )");
    }
    
    @Test
    public void canCreateSpyTableDDLMultipleParameters() {
        ProcedureSpy procedureStub = new ProcedureSpy("procname");
        
        Map<String, DbParameterAccessor> paramMap = new HashMap<String, DbParameterAccessor>();
        paramMap.put("param1", createParameter(0, "PARAM1", Direction.INPUT, "VARCHAR2"));
        paramMap.put("param2", createParameter(1, "PARAM2", Direction.INPUT, "NUMBER"));
        paramMap.put("param3", createParameter(2, "PARAM3", Direction.INPUT_OUTPUT, "VARCHAR2"));
        paramMap.put("param4", createParameter(3, "PARAM4", Direction.OUTPUT, "VARCHAR2"));
        
        procedureStub.setAllParams(paramMap);
        
        assertNormalized(
                procedureStub.buildSpyTableDDL(),
                "CREATE TABLE SPY_PROCNAME ("
                + " PARAM1 VARCHAR2(4000),"
                + " PARAM2 NUMBER,"
                + " PARAM3 VARCHAR2(4000)"
                + " )");
    }
    
    @Test
    public void includesInsertCommandInSpyBody() {
        ProcedureSpy procedureStub = new ProcedureSpy("procname");
        
        Map<String, DbParameterAccessor> paramMap = new HashMap<String, DbParameterAccessor>();
        paramMap.put("param1", createParameter(0, "PARAM1", Direction.INPUT, "VARCHAR2"));
        paramMap.put("param2", createParameter(1, "PARAM2", Direction.INPUT, "NUMBER"));
        paramMap.put("param3", createParameter(2, "PARAM3", Direction.INPUT_OUTPUT, "VARCHAR2"));
        paramMap.put("param4", createParameter(3, "PARAM4", Direction.OUTPUT, "VARCHAR2"));
        
        procedureStub.setAllParams(paramMap);
        
        assertNormalized(
                procedureStub.buildBody(),
                "INSERT INTO SPY_PROCNAME VALUES ( PARAM1, PARAM2, PARAM3 );"
            );
    }
}
