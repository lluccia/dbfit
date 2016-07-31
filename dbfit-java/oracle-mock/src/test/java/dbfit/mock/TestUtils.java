package dbfit.mock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import dbfit.util.Direction;
import dbfit.util.OracleDbParameterAccessor;

public class TestUtils {
    
    public static void assertNormalized(String text, String expectedNormalized) {
        assertThat(normalizeSpaces(text), is(expectedNormalized));
    }
    
    /**
     * Replaces repeating consecutive whitespaces (including line breaks) by only one space and trim
     */
    public static String normalizeSpaces(String input) {
        return input.replaceAll("\\s+", " ").trim();
    }
    
    public static OracleDbParameterAccessor createParameter(int position, String name, Direction direction, String type) {
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
