package dbfit.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.OracleDbParameterAccessor;

public class ProcedureSpy extends ProcedureStub {

    public ProcedureSpy(String procName) {
        super(procName);
    }
    
    @Override
    public void validate() {
        if (!getOutputParameters().isEmpty())
            super.validate();
        
        if (getInputParameters().isEmpty())
            throw new IllegalArgumentException("Procedure must have at least one input parameter (IN, IN_OUT) to be spied");
        
    }
    
    protected List<DbParameterAccessor> getInputParameters() {
        List<DbParameterAccessor> inputParameters = new ArrayList<>();
        
        for (DbParameterAccessor paramAccessor: getOrderedParameters())
            if(Arrays.asList(Direction.INPUT_OUTPUT, Direction.INPUT)
                    .contains(paramAccessor.getDirection()))
                inputParameters.add(paramAccessor);
                
        return inputParameters;
    }

    public String buildSpyTableDDL() {
        return "CREATE OR REPLACE TABLE SPY_PROCNAME (\n" + buildSpyColumns() + ")";
    }

    private String buildSpyColumns() {
        StringBuilder spyColumns = new StringBuilder();
        List<DbParameterAccessor> inputParameters = getInputParameters();
        
        int paramCount = 0;
        for (DbParameterAccessor inputParameter : inputParameters) {
            spyColumns.append(inputParameter.getName());
            spyColumns.append(" ");
            spyColumns.append(((OracleDbParameterAccessor) inputParameter).getOriginalTypeName());
            spyColumns.append(maxTypeSize(((OracleDbParameterAccessor) inputParameter).getOriginalTypeName()));

            if (++paramCount < inputParameters.size())
                spyColumns.append(",");

            spyColumns.append("\n");
        }
        return spyColumns.toString();
    }

    private String maxTypeSize(String originalTypeName) {
        String maxTypeSize = "";
        if ("VARCHAR2".equals(originalTypeName))
            maxTypeSize = "(4000)";
            
        return maxTypeSize;
    }

}
