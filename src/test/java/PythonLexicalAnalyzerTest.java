import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive unit tests for PythonLexicalAnalyzer
 */
public class PythonLexicalAnalyzerTest {
    
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @Before
    public void setUp() {
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Helper method to capture tokens from analyzer output
     */
    private List<String> captureTokens(String code) {
        outputStream.reset();
        PythonLexicalAnalyzer analyzer = new PythonLexicalAnalyzer(code);
        analyzer.analyzeCode();
        String output = outputStream.toString();
        List<String> tokens = new ArrayList<>();
        for (String line : output.split("\n")) {
            if (!line.trim().isEmpty()) {
                tokens.add(line.trim());
            }
        }
        return tokens;
    }

    public void assertToken(String actual, String expectedValue, TokenType expectedType) {
        final var expectedTypeDesc = expectedType.getDesc();
        String expected = String.format("%s - %s", expectedValue, expectedTypeDesc);
        assertEquals("Should identify '" + expectedValue + "' as " + expectedTypeDesc, expected, actual);
    }

    // Test Reserved Words
    @Test
    public void testReservedWords() {
        String code = "if else for while def class";
        List<String> tokens = captureTokens(code);
        assertEquals(6, tokens.size());
        assertToken(tokens.get(0), "if", TokenType.RESERVED_WORD);
        assertToken(tokens.get(1), "else", TokenType.RESERVED_WORD);
        assertToken(tokens.get(2), "for", TokenType.RESERVED_WORD);
        assertToken(tokens.get(3), "while", TokenType.RESERVED_WORD);
        assertToken(tokens.get(4), "def", TokenType.RESERVED_WORD);
        assertToken(tokens.get(5), "class", TokenType.RESERVED_WORD);
    }

    @Test
    public void testBooleanLiterals() {
        String code = "True False";
        List<String> tokens = captureTokens(code);
        assertEquals(2, tokens.size());
        assertToken(tokens.get(0), "True", TokenType.BOOLEAN);
        assertToken(tokens.get(1), "False", TokenType.BOOLEAN);
    }

    @Test
    public void testNoneLiteral() {
        String code = "None";
        List<String> tokens = captureTokens(code);
        assertEquals(1, tokens.size());
        assertToken(tokens.get(0), "None", TokenType.NONE);
    }

    @Test
    public void testLogicalOperators() {
        String code = "and or not";
        List<String> tokens = captureTokens(code);
        assertEquals(3, tokens.size());
        assertToken(tokens.get(0), "and", TokenType.LOGICAL_OP);
        assertToken(tokens.get(1), "or", TokenType.LOGICAL_OP);
        assertToken(tokens.get(2), "not", TokenType.LOGICAL_OP);
    }

    // Test Numbers
    @Test
    public void testIntegers() {
        String code = "123 0 999";
        List<String> tokens = captureTokens(code);
        assertEquals(3, tokens.size());
        assertToken(tokens.get(0), "123", TokenType.INTEGER);
        assertToken(tokens.get(1), "0", TokenType.INTEGER);
        assertToken(tokens.get(2), "999", TokenType.INTEGER);
    }

    @Test
    public void testFloats() {
        String code = "123.456 0.5 999.0";
        List<String> tokens = captureTokens(code);
        assertEquals(3, tokens.size());
        assertToken(tokens.get(0), "123.456", TokenType.FLOAT);
        assertToken(tokens.get(1), "0.5", TokenType.FLOAT);
        assertToken(tokens.get(2), "999.0", TokenType.FLOAT);
    }

    @Test
    public void testScientificNotation() {
        String code = "1e5 2.5E-3 1.23e+10";
        List<String> tokens = captureTokens(code);
        assertEquals(3, tokens.size());
        assertToken(tokens.get(0), "1e5", TokenType.SCIENTIFIC);
        assertToken(tokens.get(1), "2.5E-3", TokenType.SCIENTIFIC);
        assertToken(tokens.get(2), "1.23e+10", TokenType.SCIENTIFIC);
    }

    @Test
    public void testInvalidNumberWithLetter() {
        try {
            String code = "123abc";
            captureTokens(code);
            fail("Should throw exception for invalid number");
        } catch (RuntimeException e) {
            assertTrue("Should contain error message", e.getMessage().contains("número inválido"));
        }
    }

    @Test
    public void testInvalidDecimalNumber() {
        try {
            String code = "123.";
            captureTokens(code);
            fail("Should throw exception for incomplete decimal");
        } catch (RuntimeException e) {
            assertTrue("Should contain error message", e.getMessage().contains("número decimal inválido"));
        }
    }

    @Test
    public void testInvalidScientificNotation() {
        try {
            String code = "123e";
            captureTokens(code);
            fail("Should throw exception for incomplete scientific notation");
        } catch (RuntimeException e) {
            assertTrue("Should contain error message", e.getMessage().contains("número científico inválido"));
        }
    }

    // Test Strings
    @Test
    public void testSingleQuotedStrings() {
        String code = "'hello' 'world'";
        List<String> tokens = captureTokens(code);
        assertEquals(2, tokens.size());
        assertToken(tokens.get(0), "'hello'", TokenType.STRING);
        assertToken(tokens.get(1), "'world'", TokenType.STRING);
    }

    @Test
    public void testDoubleQuotedStrings() {
        String code = "\"hello\" \"world\"";
        List<String> tokens = captureTokens(code);
        assertEquals(2, tokens.size());
        assertToken(tokens.get(0), "\"hello\"", TokenType.STRING);
        assertToken(tokens.get(1), "\"world\"", TokenType.STRING);
    }

    @Test
    public void testEmptyStrings() {
        String code = "'' \"\"";
        List<String> tokens = captureTokens(code);
        assertEquals(2, tokens.size());
        assertToken(tokens.get(0), "''", TokenType.STRING);
        assertToken(tokens.get(1), "\"\"", TokenType.STRING);
    }

    @Test
    public void testUnterminatedString() {
        try {
            String code = "'unterminated\nstring";
            captureTokens(code);
            fail("Should throw exception for unterminated string");
        } catch (RuntimeException e) {
            assertTrue("Should contain error message", e.getMessage().contains("string não fechada"));
        }
    }

    // Test Identifiers
    @Test
    public void testValidIdentifiers() {
        String code = "variable _private __dunder__";
        List<String> tokens = captureTokens(code);
        assertEquals(3, tokens.size());
        assertToken(tokens.get(0), "variable", TokenType.IDENTIFIER);
        assertToken(tokens.get(1), "_private", TokenType.IDENTIFIER);
        assertToken(tokens.get(2), "__dunder__", TokenType.IDENTIFIER);
    }

    @Test
    public void testIdentifiersWithNumbers() {
        String code = "var1 test123 name2";
        List<String> tokens = captureTokens(code);
        assertEquals(3, tokens.size());
        assertToken(tokens.get(0), "var1", TokenType.IDENTIFIER);
        assertToken(tokens.get(1), "test123", TokenType.IDENTIFIER);
        assertToken(tokens.get(2), "name2", TokenType.IDENTIFIER);
    }

    // Test Operators
    @Test
    public void testArithmeticOperators() {
        String code = "+ - * / % // **";
        List<String> tokens = captureTokens(code);
        assertEquals(7, tokens.size());
        assertToken(tokens.get(0), "+", TokenType.ARITHMETIC_OP);
        assertToken(tokens.get(1), "-", TokenType.ARITHMETIC_OP);
        assertToken(tokens.get(2), "*", TokenType.ARITHMETIC_OP);
        assertToken(tokens.get(3), "/", TokenType.ARITHMETIC_OP);
        assertToken(tokens.get(4), "%", TokenType.ARITHMETIC_OP);
        assertToken(tokens.get(5), "//", TokenType.ARITHMETIC_OP);
        assertToken(tokens.get(6), "**", TokenType.ARITHMETIC_OP);
    }

    @Test
    public void testRelationalOperators() {
        String code = "== != <= >= < >";
        List<String> tokens = captureTokens(code);
        assertEquals(6, tokens.size());
        assertToken(tokens.get(0), "==", TokenType.RELATIONAL_OP);
        assertToken(tokens.get(1), "!=", TokenType.RELATIONAL_OP);
        assertToken(tokens.get(2), "<=", TokenType.RELATIONAL_OP);
        assertToken(tokens.get(3), ">=", TokenType.RELATIONAL_OP);
        assertToken(tokens.get(4), "<", TokenType.RELATIONAL_OP);
        assertToken(tokens.get(5), ">", TokenType.RELATIONAL_OP);
    }

    @Test
    public void testAssignmentOperators() {
        String code = "= += -= *= /=";
        List<String> tokens = captureTokens(code);
        assertEquals(5, tokens.size());
        assertToken(tokens.get(0), "=", TokenType.ASSIGNMENT_OP);
        assertToken(tokens.get(1), "+=", TokenType.ASSIGNMENT_OP);
        assertToken(tokens.get(2), "-=", TokenType.ASSIGNMENT_OP);
        assertToken(tokens.get(3), "*=", TokenType.ASSIGNMENT_OP);
        assertToken(tokens.get(4), "/=", TokenType.ASSIGNMENT_OP);
    }

    // Test Delimiters
    @Test
    public void testDelimiters() {
        String code = "( ) [ ] { } , . ;";
        List<String> tokens = captureTokens(code);
        assertEquals(9, tokens.size());
        assertToken(tokens.get(0), "(", TokenType.LEFT_PARENTHESIS);
        assertToken(tokens.get(1), ")", TokenType.RIGHT_PARENTHESIS);
        assertToken(tokens.get(2), "[", TokenType.LEFT_BRACKET);
        assertToken(tokens.get(3), "]", TokenType.RIGHT_BRACKET);
        assertToken(tokens.get(4), "{", TokenType.LEFT_BRACE);
        assertToken(tokens.get(5), "}", TokenType.RIGHT_BRACE);
        assertToken(tokens.get(6), ",", TokenType.COMMA);
        assertToken(tokens.get(7), ".", TokenType.DOT);
        assertToken(tokens.get(8), ";", TokenType.SEMICOLON);
    }
    
    @Test
    public void testColon() {
        String code = "while x > 0:\n" +
                "    x -= 1";
        List<String> tokens = captureTokens(code);
        assertToken(tokens.get(4), ":", TokenType.COLON);
    }
    
    @Test(expected = RuntimeException.class)
    public void testInvalidIndentation() {
        String code = "while x > 0:\n" +
                "x -= 1";
        List<String> tokens = captureTokens(code);
    }

    @Test(expected = RuntimeException.class)
    public void testAnotherInvalidIndentation() {
        String code = "while x > 0: x -= 1";
        List<String> tokens = captureTokens(code);
    }

    // Test Comments
    @Test
    public void testComments() {
        String code = "# This is a comment";
        List<String> tokens = captureTokens(code);
        assertEquals(1, tokens.size());
        assertToken(tokens.get(0), "# This is a comment", TokenType.COMMENT);
    }

    @Test
    public void testInlineComment() {
        String code = "x = 5  # inline comment";
        List<String> tokens = captureTokens(code);
        assertEquals(4, tokens.size());
        assertToken(tokens.get(0), "x", TokenType.IDENTIFIER);
        assertToken(tokens.get(1), "=", TokenType.ASSIGNMENT_OP);
        assertToken(tokens.get(2), "5", TokenType.INTEGER);
        assertToken(tokens.get(3), "# inline comment", TokenType.COMMENT);
    }

    // Test Complex Expressions
    @Test
    public void testSimpleAssignment() {
        String code = "x = 42";
        List<String> tokens = captureTokens(code);
        assertEquals(3, tokens.size());
        assertToken(tokens.get(0), "x", TokenType.IDENTIFIER);
        assertToken(tokens.get(1), "=", TokenType.ASSIGNMENT_OP);
        assertToken(tokens.get(2), "42", TokenType.INTEGER);
    }

    @Test
    public void testFunctionCall() {
        String code = "func(arg1, arg2)";
        List<String> tokens = captureTokens(code);
        assertEquals(6, tokens.size());
        assertToken(tokens.get(0), "func", TokenType.IDENTIFIER);
        assertToken(tokens.get(1), "(", TokenType.LEFT_PARENTHESIS);
        assertToken(tokens.get(2), "arg1", TokenType.IDENTIFIER);
        assertToken(tokens.get(3), ",", TokenType.COMMA);
        assertToken(tokens.get(4), "arg2", TokenType.IDENTIFIER);
        assertToken(tokens.get(5), ")", TokenType.RIGHT_PARENTHESIS);
    }

    @Test
    public void testListLiteral() {
        String code = "[1, 2, 3]";
        List<String> tokens = captureTokens(code);
        assertEquals(7, tokens.size());
        assertToken(tokens.get(0), "[", TokenType.LEFT_BRACKET);
        assertToken(tokens.get(1), "1", TokenType.INTEGER);
        assertToken(tokens.get(2), ",", TokenType.COMMA);
        assertToken(tokens.get(3), "2", TokenType.INTEGER);
        assertToken(tokens.get(4), ",", TokenType.COMMA);
        assertToken(tokens.get(5), "3", TokenType.INTEGER);
        assertToken(tokens.get(6), "]", TokenType.RIGHT_BRACKET);
    }

    @Test
    public void testIfStatement() {
        String code = "if x == 5:";
        List<String> tokens = captureTokens(code);
        assertEquals(5, tokens.size());
        assertToken(tokens.get(0), "if", TokenType.RESERVED_WORD);
        assertToken(tokens.get(1), "x", TokenType.IDENTIFIER);
        assertToken(tokens.get(2), "==", TokenType.RELATIONAL_OP);
        assertToken(tokens.get(3), "5", TokenType.INTEGER);
        assertToken(tokens.get(4), ":", TokenType.COLON);
    }

    @Test
    public void testForLoop() {
        String code = "for i in range(10):";
        List<String> tokens = captureTokens(code);
        assertEquals(8, tokens.size());
        assertToken(tokens.get(0), "for", TokenType.RESERVED_WORD);
        assertToken(tokens.get(1), "i", TokenType.IDENTIFIER);
        assertToken(tokens.get(2), "in", TokenType.RESERVED_WORD);
        assertToken(tokens.get(3), "range", TokenType.IDENTIFIER);
        assertToken(tokens.get(4), "(", TokenType.LEFT_PARENTHESIS);
        assertToken(tokens.get(5), "10", TokenType.INTEGER);
        assertToken(tokens.get(6), ")", TokenType.RIGHT_PARENTHESIS);
        assertToken(tokens.get(7), ":", TokenType.COLON);
    }

    // Test Error Cases
    @Test
    public void testInvalidSymbol() {
        try {
            String code = "x @ y";
            captureTokens(code);
            fail("Should throw exception for invalid symbol");
        } catch (RuntimeException e) {
            assertTrue("Should contain error message", e.getMessage().contains("símbolo inválido"));
        }
    }

    // Test Edge Cases
    @Test
    public void testEmptyCode() {
        String code = "";
        List<String> tokens = captureTokens(code);
        assertEquals("Empty code should produce no tokens", 0, tokens.size());
    }

    @Test
    public void testOnlyWhitespace() {
        String code = "   \t\n  \r  ";
        List<String> tokens = captureTokens(code);
        assertEquals("Only whitespace should produce no tokens", 0, tokens.size());
    }

    @Test
    public void testOnlyComment() {
        String code = "# Just a comment";
        List<String> tokens = captureTokens(code);
        assertEquals(1, tokens.size());
        assertToken(tokens.get(0), "# Just a comment", TokenType.COMMENT);
    }

    @Test
    public void testWhitespaceHandling() {
        String code = "   x   =   5   ";
        List<String> tokens = captureTokens(code);
        assertEquals(3, tokens.size());
        assertToken(tokens.get(0), "x", TokenType.IDENTIFIER);
        assertToken(tokens.get(1), "=", TokenType.ASSIGNMENT_OP);
        assertToken(tokens.get(2), "5", TokenType.INTEGER);
    }

    @Test
    public void testMultipleLines() {
        String code = "x = 5\ny = 10";
        List<String> tokens = captureTokens(code);
        assertEquals(6, tokens.size());
        assertToken(tokens.get(0), "x", TokenType.IDENTIFIER);
        assertToken(tokens.get(1), "=", TokenType.ASSIGNMENT_OP);
        assertToken(tokens.get(2), "5", TokenType.INTEGER);
        assertToken(tokens.get(3), "y", TokenType.IDENTIFIER);
        assertToken(tokens.get(4), "=", TokenType.ASSIGNMENT_OP);
        assertToken(tokens.get(5), "10", TokenType.INTEGER);
    }

}
