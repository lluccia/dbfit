package dbfit.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.OracleDbParameterAccessor;

public class StoredProcedureDummy {

    private String name;
    private Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();;

    public StoredProcedureDummy(String name) {
        this.name = name;
    }

    public void setAllParams(Map<String, DbParameterAccessor> allParams) {
        this.allParams = allParams;
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
        List<DbParameterAccessor> orderedParameters = getOrderedParameters();
        if (orderedParameters.isEmpty())
            return "";
        else {
        	StringBuilder parametersDeclaration = new StringBuilder("(");
        	
        	int paramCount = 0;
        	
        	for (DbParameterAccessor paramAccessor: orderedParameters) {
        	    parametersDeclaration.append(paramAccessor.getName());
        	    parametersDeclaration.append(" ");
        	    
        	    if (paramAccessor.hasDirection(Direction.OUTPUT))
        	        parametersDeclaration.append("OUT ");
        	    else if (paramAccessor.hasDirection(Direction.INPUT_OUTPUT))
        	        parametersDeclaration.append("IN OUT ");
        	    
        	    parametersDeclaration.append(((OracleDbParameterAccessor) paramAccessor).getOriginalTypeName());
        	    
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
            return ((OracleDbParameterAccessor) getReturnValue()).getOriginalTypeName();
        else
            return null;
    }

    private List<DbParameterAccessor> getOrderedParameters() {
        List<DbParameterAccessor> parameters = getParameters();
        List<DbParameterAccessor> orderedParameters = new ArrayList<DbParameterAccessor>(parameters.size());
        
        orderedParameters.addAll(parameters);
        
        for (DbParameterAccessor paramAccessor: parameters)
            orderedParameters.set(paramAccessor.getPosition(), paramAccessor);
        
        return orderedParameters;
    }
    
    private List<DbParameterAccessor> getParameters() {
        List<DbParameterAccessor> parameters = new ArrayList<DbParameterAccessor>(allParams.size());
        
        for (DbParameterAccessor paramAccessor: allParams.values())
            if(paramAccessor.doesNotHaveDirection(Direction.RETURN_VALUE))
                parameters.add(paramAccessor);
        
        return parameters;
    }
    
    private boolean isFunction() {
        return getReturnValue() != null;
    }
    
    private DbParameterAccessor getReturnValue() {
        for (DbParameterAccessor paramAccessor: allParams.values())
            if(paramAccessor.hasDirection(Direction.RETURN_VALUE))
                return paramAccessor;
        
        return null;
    }
    
}
