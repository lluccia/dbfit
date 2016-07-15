package dbfit.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.OracleDbParameterAccessor;

public class ProcedureStub extends ProcedureDummy {

    private Map<String, Object> stubValues = new HashMap<>();

    public ProcedureStub(String name) {
        super(name);
    }

    public void setStubValue(String name, Object value) {
        this.stubValues.put(name, value);
    }
    
    public void validate() {
        List<DbParameterAccessor> outputParameters = getOutputParameters();
        
        if (outputParameters.isEmpty())
            throw new IllegalArgumentException("Procedure must have at least one output parameter (OUT, IN_OUT) to be stubbed");
        
        for (DbParameterAccessor parameterAccessor: outputParameters)
            if (!stubValues.containsKey(parameterAccessor.getName()))
                throw new IllegalArgumentException("All output parameters must be set");
    }

    @Override
    protected String buildBody() {
        String body = "";
        
        String returnValue = "";
        
        for (Entry<String, Object> stubValue : stubValues.entrySet()) {
            String parameterName = stubValue.getKey();
            Object value = stubValue.getValue();
            
            if ("".equals(parameterName))
                returnValue = "RETURN " + 
                        buildParameterValue(parameterName, value) +
                        ";\n";
            else
                body += parameterName + " := " +
                    buildParameterValue(parameterName, value) +
                    ";\n";
        }
        
        return body + returnValue;
    }

    private String buildParameterValue(String parameterName, Object value) {
        return (isVarchar(parameterName) ? "'" : "" ) +
                                String.valueOf(value) + 
                                (isVarchar(parameterName) ? "'" : "" );
    }
    
    private boolean isVarchar(String parameterName) {
        OracleDbParameterAccessor dbParameterAccessor = (OracleDbParameterAccessor) allParams.get(parameterName);
        
        return dbParameterAccessor.getOriginalTypeName().equals("VARCHAR2");
    }

    private List<DbParameterAccessor> getOutputParameters() {
        List<DbParameterAccessor> outputParameters = new ArrayList<>();
        
        for (DbParameterAccessor paramAccessor: allParams.values())
            if(Arrays.asList(Direction.RETURN_VALUE, Direction.INPUT_OUTPUT, Direction.OUTPUT)
                    .contains(paramAccessor.getDirection()))
                outputParameters.add(paramAccessor);
                
        return outputParameters;
    }
    
}
