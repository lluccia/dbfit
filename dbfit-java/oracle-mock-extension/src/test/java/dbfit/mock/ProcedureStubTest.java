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
        return input.replaceAll("\\s+", " ");
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
        paramMap.put("PARAM1", createParameter(1, "PARAM1", Direction.OUTPUT, "VARCHAR2"));
        
        procedureStub.setAllParams(paramMap);
        
        procedureStub.validate();
    }
    
    @Test
    public void canStubOutputParameters() {
        ProcedureStub procedureStub = new ProcedureStub("procname");
        
        Map<String, DbParameterAccessor> paramMap = new HashMap<String, DbParameterAccessor>();
        paramMap.put("PARAM1", createParameter(1, "PARAM1", Direction.OUTPUT, "VARCHAR2"));
        paramMap.put("PARAM2", createParameter(2, "PARAM2", Direction.OUTPUT, "NUMBER"));
        
        procedureStub.setAllParams(paramMap);
        
        Map<String, Object> stubMap = new HashMap<String, Object>();
        stubMap.put("PARAM1", "STUBBED_VALUE");
        stubMap.put("PARAM2", 1);
        
        procedureStub.setStubValues(stubMap);
        
        procedureStub.validate();
        
        assertThat(procedureStub.buildBody(), is(
                "PARAM2 := 1;\n" +
                "PARAM1 := 'STUBBED_VALUE';\n"
            ));
    }
    
    @Test
    public void canStubFunctionReturnValue() {
        ProcedureStub procedureStub = new ProcedureStub("funcname");
        
        Map<String, DbParameterAccessor> paramMap = new HashMap<String, DbParameterAccessor>();
        paramMap.put("", createParameter(-1, "", Direction.RETURN_VALUE, "VARCHAR2"));
        
        procedureStub.setAllParams(paramMap);
        
        Map<String, Object> stubMap = new HashMap<String, Object>();
        stubMap.put("", "STUBBED_VALUE");
        
        procedureStub.setStubValues(stubMap);
        
        procedureStub.validate();
        
        assertThat(procedureStub.buildBody(), is(
                "RETURN 'STUBBED_VALUE';\n"
            ));
    }
    
    @Test
    public void canStubFunctionReturnValueAndOutParameters() {
        ProcedureStub procedureStub = new ProcedureStub("funcname");
        
        Map<String, DbParameterAccessor> paramMap = new HashMap<String, DbParameterAccessor>();
        paramMap.put("", createParameter(-1, "", Direction.RETURN_VALUE, "VARCHAR2"));
        paramMap.put("PARAM1", createParameter(1, "PARAM1", Direction.OUTPUT, "VARCHAR2"));
        paramMap.put("PARAM2", createParameter(2, "PARAM2", Direction.OUTPUT, "NUMBER"));
        
        procedureStub.setAllParams(paramMap);
        
        Map<String, Object> stubMap = new HashMap<String, Object>();
        stubMap.put("", "STUBBED_VALUE");
        stubMap.put("PARAM1", "STUBBED_VALUE_PARAM1");
        stubMap.put("PARAM2", 1);
        
        procedureStub.setStubValues(stubMap);
        
        procedureStub.validate();
        
        assertThat(procedureStub.buildBody(), is(
                "PARAM2 := 1;\n" +
                "PARAM1 := 'STUBBED_VALUE_PARAM1';\n" +
                "RETURN 'STUBBED_VALUE';\n"
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
