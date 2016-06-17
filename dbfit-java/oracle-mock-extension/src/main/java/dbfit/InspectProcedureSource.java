package dbfit;

import dbfit.api.DbStoredProcedure;
import dbfit.environment.OracleMockEnvironment;
import fit.Fixture;
import fit.Parse;

public class InspectProcedureSource extends Fixture {

	private DbStoredProcedure dbStoredProcedure;
	private OracleMockEnvironment dbEnvironment;
	private String procName;
    
	public InspectProcedureSource(OracleMockEnvironment dbEnvironment, String procName) {
		this.dbEnvironment = dbEnvironment;
		this.procName = procName;
	}

	@Override
	public void doTable(Parse table) {
		super.doTable(table);
		try {
			String source = dbEnvironment.getProcedureSource(procName);
			
			createRows(table, source);
		} catch (Exception e) {
			exception(table.parts.parts, e);
        }
	}
	
	private void createRows(Parse table, String source) {
		Parse pre = new Parse("pre", Fixture.gray(source), null, null);
		Parse nameCell = new Parse("td", null, pre, null);
        Parse newRow = new Parse("tr", null, nameCell, null);
    	table.parts.more = newRow;
	}
}
