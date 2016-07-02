package dbfit.mock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.OracleDbParameterAccessor;

public class StoredProcedureDummyTest {
	
    @Test
    public void canBuildProcedureDDL() {
        StoredProcedureDummy spDummyProcName = new StoredProcedureDummy("procname");

        assertThat(spDummyProcName.buildDDL(),
                is("CREATE OR REPLACE PROCEDURE PROCNAME IS BEGIN NULL; END;"));

        StoredProcedureDummy spDummyAnotherProcName = new StoredProcedureDummy("anotherprocname");
        assertThat(spDummyAnotherProcName.buildDDL(),
                is("CREATE OR REPLACE PROCEDURE ANOTHERPROCNAME IS BEGIN NULL; END;"));
    }
    
    @Test
    public void canBuildProcedureDDLWithParameters() {
        StoredProcedureDummy spDummy = new StoredProcedureDummy("procname");

        Map<String, DbParameterAccessor> paramMap = new HashMap<String, DbParameterAccessor>();
        paramMap.put("param1", createParameter(0, "PARAM1", Direction.INPUT, "VARCHAR2"));
        paramMap.put("param2", createParameter(1, "PARAM2", Direction.INPUT, "NUMBER"));
        paramMap.put("param3", createParameter(2, "PARAM3", Direction.INPUT, "INTEGER"));
        paramMap.put("param4", createParameter(3, "PARAM4", Direction.OUTPUT, "INTEGER"));
        paramMap.put("param5", createParameter(4, "PARAM5", Direction.INPUT_OUTPUT, "INTEGER"));
        
        spDummy.setAllParams(paramMap);
        
        assertThat(spDummy.buildDDL(),
                is("CREATE OR REPLACE PROCEDURE PROCNAME(PARAM1 VARCHAR2, PARAM2 NUMBER, PARAM3 INTEGER, PARAM4 OUT INTEGER, PARAM5 IN OUT INTEGER) IS BEGIN NULL; END;"));

    }
    
    @Test
    public void canBuildFunctionDDL() {
        StoredProcedureDummy spDummy = new StoredProcedureDummy("functionname");

        Map<String, DbParameterAccessor> paramMap = new HashMap<String, DbParameterAccessor>();
        paramMap.put("", createParameter(-1, "", Direction.RETURN_VALUE, "VARCHAR2"));
        
        spDummy.setAllParams(paramMap);
        
        assertThat(spDummy.buildDDL(),
                is("CREATE OR REPLACE FUNCTION FUNCTIONNAME RETURN VARCHAR2 IS BEGIN RETURN NULL; END;"));

    }
    
    @Test
    public void canBuildFunctionDDLWithParameters() {
        StoredProcedureDummy spDummy = new StoredProcedureDummy("functionname");

        Map<String, DbParameterAccessor> paramMap = new HashMap<String, DbParameterAccessor>();
        paramMap.put("", createParameter(-1, "", Direction.RETURN_VALUE, "VARCHAR2"));
        paramMap.put("param1", createParameter(0, "PARAM1", Direction.INPUT, "VARCHAR2"));
        paramMap.put("param2", createParameter(1, "PARAM2", Direction.INPUT, "NUMBER"));
        paramMap.put("param3", createParameter(2, "PARAM3", Direction.INPUT, "INTEGER"));
        paramMap.put("param4", createParameter(3, "PARAM4", Direction.OUTPUT, "INTEGER"));
        paramMap.put("param5", createParameter(4, "PARAM5", Direction.INPUT_OUTPUT, "INTEGER"));
        
        spDummy.setAllParams(paramMap);
        
        assertThat(spDummy.buildDDL(),
                is("CREATE OR REPLACE FUNCTION FUNCTIONNAME(PARAM1 VARCHAR2, PARAM2 NUMBER, PARAM3 INTEGER, PARAM4 OUT INTEGER, PARAM5 IN OUT INTEGER) RETURN VARCHAR2 IS BEGIN RETURN NULL; END;"));

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
