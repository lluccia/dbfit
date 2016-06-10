package dbfit.util.oracle.mockextensions;

import fit.Fixture;
import fit.Parse;

public class HelloMock extends Fixture {
	
	@Override
	public void doTable(Parse table) {
		Parse newRow = new Parse("tr", null, null, null);
        table.parts.more = newRow;

        Parse cell = new Parse("td", Fixture.gray("HelloFromOracleMock"), null, null);
        newRow.parts = cell;
	}

}
