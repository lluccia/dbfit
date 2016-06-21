package dbfit.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dbfit.util.Direction;
import dbfit.util.OracleDbParameterAccessor;

public class StoredProcedureDummy {

    private String name;
    private Map<String, OracleDbParameterAccessor> allParams = new HashMap<String, OracleDbParameterAccessor>();;

    public StoredProcedureDummy(String name) {
        this.name = name;
    }

    public void setAllParams(Map<String, OracleDbParameterAccessor> paramMap) {
        this.allParams = paramMap;
    }

    public String buildDDL() {
        return "CREATE OR REPLACE " +
                    (isFunction() ? "FUNCTION" : "PROCEDURE") +
                    " " + name.toUpperCase() +
                    buildParameters() +
                    " " + buildReturnTypeDeclaration() +
                    "IS BEGIN " + 
                    (isFunction() ? "RETURN " : "") +
                    "NULL; " +
                    "END;";
    }

    private String buildParameters() {
        List<OracleDbParameterAccessor> orderedParameters = getOrderedParameters();
        if (orderedParameters.isEmpty())
            return "";
        else {
        	StringBuilder parametersDeclaration = new StringBuilder("(");
        	
        	int paramCount = 0;
        	
        	for (OracleDbParameterAccessor paramAccessor: orderedParameters) {
        	    parametersDeclaration.append(paramAccessor.getName());
        	    parametersDeclaration.append(" ");
        	    
        	    if (paramAccessor.hasDirection(Direction.OUTPUT))
        	        parametersDeclaration.append("OUT ");
        	    else if (paramAccessor.hasDirection(Direction.INPUT_OUTPUT))
        	        parametersDeclaration.append("IN OUT ");
        	    
        	    parametersDeclaration.append(paramAccessor.getOriginalTypeName());
        	    
        	    if (++paramCount < orderedParameters.size())
        	        parametersDeclaration.append(", ");
        	}
        	
        	parametersDeclaration.append(")");
        	
            return parametersDeclaration.toString();
        }
    }
    
    private String buildReturnTypeDeclaration() {
        if (isFunction())
            return "RETURN " + getReturnValueType() + " ";
        else
            return "";
    }
    
    private String getReturnValueType() {
        if (isFunction())
            return getReturnValue().getOriginalTypeName();
        else
            return null;
    }

    private List<OracleDbParameterAccessor> getOrderedParameters() {
        List<OracleDbParameterAccessor> parameters = getParameters();
        List<OracleDbParameterAccessor> orderedParameters = new ArrayList<OracleDbParameterAccessor>(parameters.size());
        
        orderedParameters.addAll(parameters);
        
        for (OracleDbParameterAccessor paramAccessor: parameters)
            orderedParameters.set(paramAccessor.getPosition(), paramAccessor);
        
        return orderedParameters;
    }
    
    private List<OracleDbParameterAccessor> getParameters() {
        List<OracleDbParameterAccessor> parameters = new ArrayList<OracleDbParameterAccessor>(allParams.size());
        
        for (OracleDbParameterAccessor paramAccessor: allParams.values())
            if(paramAccessor.doesNotHaveDirection(Direction.RETURN_VALUE))
                parameters.add(paramAccessor);
        
        return parameters;
    }
    
    private boolean isFunction() {
        return getReturnValue() != null;
    }
    
    private OracleDbParameterAccessor getReturnValue() {
        for (OracleDbParameterAccessor paramAccessor: allParams.values())
            if(paramAccessor.hasDirection(Direction.RETURN_VALUE))
                return paramAccessor;
        
        return null;
    }
    
}
