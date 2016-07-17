package dbfit.mock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.OracleDbParameterAccessor;

public class ProcedureStubTest {
    
    private String normalizeSpaces(String input) {
        return input.replaceAll("\\s+", " ").trim();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void procedureWithNoOutputParametersCannotBeStubbed() {
        ProcedureStub procedureStub = new ProcedureStub("procname");
        
        procedureStub.validate();
    }

    @Test(expected=IllegalArgumentException.class)
    public void stubMustDefineAllOutputParameters() {
        ProcedureStub procedureStub = new ProcedureStub("procname");
        
        Map<String, DbParameterAccessor> paramMap = new HashMap<String, DbParameterAccessor>();
        paramMap.put("param1", createParameter(1, "PARAM1", Direction.OUTPUT, "VARCHAR2"));
        
        procedureStub.setAllParams(paramMap);
        
        procedureStub.validate();
    }
    
    @Test
    public void canStubOutputParameters() {
        ProcedureStub procedureStub = new ProcedureStub("procname");
        
        Map<String, DbParameterAccessor> paramMap = new HashMap<String, DbParameterAccessor>();
        paramMap.put("param1", createParameter(1, "PARAM1", Direction.OUTPUT, "VARCHAR2"));
        paramMap.put("param2", createParameter(2, "PARAM2", Direction.OUTPUT, "NUMBER"));
        
        procedureStub.setAllParams(paramMap);
        
        procedureStub.setStubValue("PARAM1", "STUBBED_VALUE");
        procedureStub.setStubValue("PARAM2", 1);
        
        procedureStub.validate();
        
        assertThat(normalizeSpaces(procedureStub.buildBody()), is(
                "PARAM2 := 1; " +
                "PARAM1 := 'STUBBED_VALUE';"
            ));
    }
    
    @Test
    public void canStubFunctionReturnValue() {
        ProcedureStub procedureStub = new ProcedureStub("funcname");
        
        Map<String, DbParameterAccessor> paramMap = new HashMap<String, DbParameterAccessor>();
        paramMap.put("", createParameter(-1, "", Direction.RETURN_VALUE, "VARCHAR2"));
        
        procedureStub.setAllParams(paramMap);
        
        procedureStub.setStubValue("", "STUBBED_VALUE");
        
        procedureStub.validate();
        
        assertThat(normalizeSpaces(procedureStub.buildBody()), is(
                "RETURN 'STUBBED_VALUE';"
            ));
    }
    
    @Test
    public void canStubFunctionReturnValueAndOutParameters() {
        ProcedureStub procedureStub = new ProcedureStub("funcname");
        
        Map<String, DbParameterAccessor> paramMap = new HashMap<String, DbParameterAccessor>();
        paramMap.put("", createParameter(-1, "", Direction.RETURN_VALUE, "VARCHAR2"));
        paramMap.put("param1", createParameter(1, "PARAM1", Direction.OUTPUT, "VARCHAR2"));
        paramMap.put("param2", createParameter(2, "PARAM2", Direction.OUTPUT, "NUMBER"));
        
        procedureStub.setAllParams(paramMap);
        
        procedureStub.setStubValue("", "STUBBED_VALUE");
        procedureStub.setStubValue("PARAM1", "STUBBED_VALUE_PARAM1");
        procedureStub.setStubValue("PARAM2", 1);
        
        procedureStub.validate();
        
        assertThat(normalizeSpaces(procedureStub.buildBody()), is(
                "PARAM2 := 1; " +
                "PARAM1 := 'STUBBED_VALUE_PARAM1'; " +
                "RETURN 'STUBBED_VALUE';"
            ));
    }
    
    private OracleDbParameterAccessor createParameter(int position, String name, Direction direction, String type) {
        return new OracleDbParameterAccessor(
                name, 
                direction,
                0, 
                null,
                position,
                null,
                type, 
                null);
    }
}
