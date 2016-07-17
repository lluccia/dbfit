package dbfit.mock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.PrintWriter;

import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.Test;

import fit.Parse;
import fit.exception.FitParseException;

public class ParseTest {

    @Test
    public void testStringConstructor() throws FitParseException {

        Parse table = new Parse(
                "<table><tbody>" + 
                    "<tr><td>fixture poc</td><td colspan=\"2\">fixture parameter</td></tr>" + 
                    "<tr><td>col1</td><td>col2</td><td>col3</td></tr>" + 
                    "<tr><td>val1</td><td>val2</td><td>val3</td></tr>" + 
                "</tbody></table>");
        
        assertTable(table);
    }

    private void assertTable(Parse table) {
        assertThat(table.size(), is(1));

        assertThat(table.leader, is(""));
        assertThat(table.tag, is("<table>"));
        assertThat(table.body, is(nullValue()));
        assertThat(table.end, is("</table>"));
        assertThat(table.trailer, is(""));
        
        assertThat(table.more, is(nullValue()));
        assertThat(table.parts, is(notNullValue()));
        
        assertThat(parsePrint(table), 
                is("<table><tbody>"
                        + "<tr><td>fixture poc</td><td colspan=\"2\">fixture parameter</td></tr>"
                        + "<tr><td>col1</td><td>col2</td><td>col3</td></tr>"
                        + "<tr><td>val1</td><td>val2</td><td>val3</td></tr>"
                + "</tbody></table>"));
        
        assertFirstRow(table.parts);
        
    }

    private void assertFirstRow(Parse firstRow) {
        assertThat(firstRow.size(), is(3));

        assertThat(firstRow.leader, is("<tbody>"));
        assertThat(firstRow.tag, is("<tr>"));
        assertThat(firstRow.body, is(nullValue()));
        assertThat(firstRow.end, is("</tr>"));
        assertThat(firstRow.trailer, is(nullValue()));
        
        assertThat(firstRow.more, is(notNullValue()));
        assertThat(firstRow.parts, is(notNullValue()));
        
        assertThat(parsePrint(firstRow), 
                is("<tbody>"
                        + "<tr><td>fixture poc</td><td colspan=\"2\">fixture parameter</td></tr>"
                        + "<tr><td>col1</td><td>col2</td><td>col3</td></tr>"
                        + "<tr><td>val1</td><td>val2</td><td>val3</td></tr>"
                + "</tbody>"));
        
        assertFirstRowFirstColumn(firstRow.parts);
        
        assertSecondRow(firstRow.more);
    }
    
    private void assertFirstRowFirstColumn(Parse firstRowFirstColumn) {
        assertThat(firstRowFirstColumn.size(), is(2));

        assertThat(firstRowFirstColumn.leader, is(""));
        assertThat(firstRowFirstColumn.tag, is("<td>"));
        assertThat(firstRowFirstColumn.body, is("fixture poc"));
        assertThat(firstRowFirstColumn.end, is("</td>"));
        assertThat(firstRowFirstColumn.trailer, is(nullValue()));
        
        assertThat(firstRowFirstColumn.more, is(notNullValue()));
        assertThat(firstRowFirstColumn.parts, is(nullValue()));
        
        assertFirstRowSecondColumn(firstRowFirstColumn.more);
    }

    private void assertFirstRowSecondColumn(Parse firstRowSecondColumn) {
        assertThat(firstRowSecondColumn.size(), is(1));
        
        assertThat(firstRowSecondColumn.leader, is(""));
        assertThat(firstRowSecondColumn.tag, is("<td colspan=\"2\">"));
        assertThat(firstRowSecondColumn.body, is("fixture parameter"));
        assertThat(firstRowSecondColumn.end, is("</td>"));
        assertThat(firstRowSecondColumn.trailer, is(""));
        
        assertThat(firstRowSecondColumn.more, is(nullValue()));
        assertThat(firstRowSecondColumn.parts, is(nullValue()));
    }

    private void assertSecondRow(Parse secondRow) {
        assertThat(secondRow.size(), is(2));

        assertThat(secondRow.leader, is(""));
        assertThat(secondRow.tag, is("<tr>"));
        assertThat(secondRow.body, is(nullValue()));
        assertThat(secondRow.end, is("</tr>"));
        assertThat(secondRow.trailer, is(nullValue()));
        
        assertThat(secondRow.more, is(notNullValue()));
        assertThat(secondRow.parts, is(notNullValue()));
        
        assertThirdRow(secondRow.more);
    }

    private void assertThirdRow(Parse thirdRow) {
        assertThat(thirdRow.size(), is(1));

        assertThat(thirdRow.leader, is(""));
        assertThat(thirdRow.tag, is("<tr>"));
        assertThat(thirdRow.body, is(nullValue()));
        assertThat(thirdRow.end, is("</tr>"));
        assertThat(thirdRow.trailer, is("</tbody>"));
        
        assertThat(thirdRow.more, is(nullValue()));
        assertThat(thirdRow.parts, is(notNullValue()));
    }

    protected String parsePrint(Parse parse) {
        StringBuilderWriter stringWriter = new StringBuilderWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        parse.print(printWriter);
        String parsePrint = stringWriter.toString();
        return parsePrint;
    }

}
