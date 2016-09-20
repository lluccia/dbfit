package dbfit.fixture;

import dbfit.environment.OracleMockEnvironment;

public class AssertSpy extends Query {

    public AssertSpy(OracleMockEnvironment environment, String procName) {
        super(environment, getQueryOrSymbol(environment, procName));
    }

    private static String getQueryOrSymbol(OracleMockEnvironment environment, String procName) {
        return "SELECT * FROM " + environment.getSpyTable(procName.toUpperCase());
    }

}
