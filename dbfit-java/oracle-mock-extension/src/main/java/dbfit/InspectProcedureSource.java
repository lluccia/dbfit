package dbfit;

import java.sql.SQLException;

import dbfit.api.DBEnvironment;
import dbfit.api.DbStoredProcedure;
import dbfit.fixture.StatementExecution;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessors;
import fit.Fixture;
import fit.Parse;

public class InspectProcedureSource extends Fixture {

	private DBEnvironment dbEnvironment;
	private String procName;
    
	public InspectProcedureSource(DBEnvironment dbEnvironment, String procName) {
		this.dbEnvironment = dbEnvironment;
		this.procName = procName;
	}

	@Override
	public void doTable(Parse table) {
		DbStoredProcedure dbStoredProcedure = new DbStoredProcedure(dbEnvironment, procName);
		DbParameterAccessors accessors = new DbParameterAccessors();
		try {
			StatementExecution statement = dbStoredProcedure.buildPreparedStatement(accessors.toArray());
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.doTable(table);
	}
}
