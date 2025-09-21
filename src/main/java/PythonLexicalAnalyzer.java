import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

import static java.util.Objects.nonNull;

public class PythonLexicalAnalyzer {

    private static final Set<String> RESERVED_WORDS = Set.of(
            "if", "else", "for", "while", "def", "class", "return",
            "import", "from", "try", "except", "with", "and", "or",
            "not", "True", "False", "None", "in", "is"
    );

    private static final Set<String> RELATIONAL_OPS = Set.of("==", "!=", "<=", ">=");
    private static final Set<String> ASSIGNMENT_OPS = Set.of("+=", "-=", "*=", "/=");
    private static final Set<String> ARITHMETIC_OPS_DOUBLE = Set.of("//", "**");
    private static final Set<Character> ARITHMETIC_OPS_SINGLE = Set.of('*', '/', '+', '-', '%');
    
    private static final Set<Character> SIMPLE_OPERATORS = Set.of(
            '+', '-', '*', '/', '%', '=', '<', '>', '!'
    );

    private static final Set<Character> DELIMITERS = Set.of(
            '(', ')', '[', ']', '{', '}', ',', ':', '.', ';'
    );

    private final Expression expr;
    private int tabDepth = 0;

    public PythonLexicalAnalyzer(File file) throws IOException {
        this.expr = new Expression(readFile(file));
    }
    
    public PythonLexicalAnalyzer(String code) {
        this.expr = new Expression(code);
    }

    private String readFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }

    public void analyzeCode() {
        while (expr.hasNext()) {
            Token token = getToken();
            if (nonNull(token)) {
                System.out.println(token);
            }
        }
    }

    private Token getToken() {
        setIndentationDepth();
        skipSpaces();

        if (!expr.hasNext()) {
            return null;
        }

        if (expr.getCurrentChar() == '#') {
            return readComment();
        } else if (expr.currentCharIsAnyOf('"', '\'')) {
            return readString(expr.getCurrentChar());
        } else if (Character.isDigit(expr.getCurrentChar())) {
            return readNumber();
        } else if (Character.isLetter(expr.getCurrentChar()) || expr.getCurrentChar() == '_') {
            return readIdentifier();
        } else if (SIMPLE_OPERATORS.contains(expr.getCurrentChar())) {
            return readOperator();
        } else if (DELIMITERS.contains(expr.getCurrentChar())) {
            return readDelimiter();
        }

        char invalidSymbol = expr.getCurrentChar();
        expr.advance();
        throw new RuntimeException(
                "Erro: símbolo inválido '" + invalidSymbol + "'"
        );
    }

    private Token readDelimiter() {
        char c = expr.getCurrentChar();
        expr.advance();
        TokenType delimiterType = switch (c) {
            case '(' -> TokenType.LEFT_PARENTHESIS;
            case ')' -> TokenType.RIGHT_PARENTHESIS;
            case '[' -> TokenType.LEFT_BRACKET;
            case ']' -> TokenType.RIGHT_BRACKET;
            case '{' -> TokenType.LEFT_BRACE;
            case '}' -> TokenType.RIGHT_BRACE;
            case ',' -> TokenType.COMMA;
            case ':' -> TokenType.COLON;
            case '.' -> TokenType.DOT;
            case ';' -> TokenType.SEMICOLON;
            default -> TokenType.ERROR;
        };

        return new Token(delimiterType, String.valueOf(c));
    }

    private void setIndentationDepth() {
        if (expr.getCurrentChar() == '\n') {
            expr.next();
            String startLineWhitespaces = expr.accumulateWhile(c -> c == '\t' || c == ' ');
            tabDepth = startLineWhitespaces.length();
        }
    }
    
    private void skipSpaces() {
        expr.accumulateWhile(c -> c == ' ' || c == '\t' || c == '\r' || c == '\n');
    }

    private Token readComment() {
        String result = expr.accumulateWhile(c -> c != '\n' && c != '\r');
        
        return new Token(TokenType.COMMENT, result);
    }

    private Token readString(char quoteType) {
        assert quoteType == '"' || quoteType == '\'';
        
        final boolean multiline;
        
        String quotes = expr.accumulateWhile(c -> c == quoteType);
        
        if (quotes.length() == 2 || quotes.length() == 6)
            return new Token(TokenType.STRING, quotes);
        
        multiline = quotes.length() >= 3;
        int windowSize = multiline ? 4 : 2;

        String tokenString = quotes;

        tokenString += expr.accumulateWhileWindow(windowSize, s ->  s.equals("\\"+quotes) || !s.endsWith(quotes) || (s.endsWith("\n") && !multiline));
        tokenString += expr.getNext(windowSize);
        expr.advance(windowSize);
        
        if (tokenString.contains("\n") && !multiline) {
            throw new RuntimeException("Erro: string não fechada");
        }
        
        return new Token(TokenType.STRING, tokenString);
    }

    private Token readNumber() {
        boolean isFloat = false;
        boolean isScientific = false;
        String numberStr = expr.accumulateWhile(Character::isDigit);
        
        if (expr.hasNext() && expr.getCurrentChar() == '.') {
            isFloat = true;
            numberStr += expr.getCurrentChar(); // Append the '.'
            expr.advance(); // Consume the '.'

            String fractionPart = expr.accumulateWhile(Character::isDigit);
            if (fractionPart.isEmpty()) {
                // Error for numbers ending in a dot, like "123."
                throw new RuntimeException("Erro: número decimal inválido '" + numberStr + "'");
            }
            numberStr += fractionPart;
        }

        if (expr.hasNext() && expr.currentCharIsAnyOf('e', 'E')) {
            isFloat = true;
            numberStr += expr.getCurrentChar(); // Append the 'e' or 'E'
            expr.advance(); // Consume the 'e' or 'E'

            // Check for an optional '+' or '-' sign for the exponent.
            if (expr.hasNext() && (expr.currentCharIsAnyOf('+', '-'))) {
                numberStr += expr.getCurrentChar(); // Append the sign
                expr.advance(); // Consume the sign
            }

            String exponentPart = expr.accumulateWhile(Character::isDigit);
            if (exponentPart.isEmpty()) {
                // Error for incomplete scientific notation like "123e" or "123e-"
                throw new RuntimeException("Erro: número científico inválido '" + numberStr + "'");
            }
            else {
                //If scientific notation is complete the flag isScientific is set to true
                isFloat = false;
                isScientific = true;
            }
            numberStr += exponentPart;
        }

        if (expr.hasNext() && Character.isLetter(expr.getCurrentChar())) {
            throw new RuntimeException("Erro: número inválido '" + numberStr + expr.getCurrentChar() + "'");
        }

        // 5. Determine the token type.
        TokenType tokenType;
        if (isScientific) {
            tokenType = TokenType.SCIENTIFIC;
        } else if (isFloat) {
            tokenType = TokenType.FLOAT;
        } else {
            tokenType = TokenType.INTEGER;
        }

        return new Token(tokenType, numberStr);
    }

    private Token readIdentifier() {
        String text = expr.accumulateWhile(c -> Character.isLetterOrDigit(c) || c == '_');

        TokenType tokenType;

        if (RESERVED_WORDS.contains(text)) {
            if (text.equals("True") || text.equals("False")) {
                tokenType = TokenType.BOOLEAN;
            } else if (text.equals("None")) {
                tokenType = TokenType.NONE;
            } else if (text.equals("and") || text.equals("or") || text.equals("not")) {
                tokenType = TokenType.LOGICAL_OP;
            } else {
                tokenType = TokenType.RESERVED_WORD;
            }
        } else {
            tokenType = TokenType.IDENTIFIER;
        }

        return new Token(tokenType, text);
    }

    private Token readOperator() {
        char firstOpChar = expr.getCurrentChar();
        char secondOpChar = expr.getNext().orElse('\0');

        String doubleOp = "" + firstOpChar + secondOpChar;

        TokenType doubleType = switch (doubleOp) {
            case String op when RELATIONAL_OPS.contains(op) -> TokenType.RELATIONAL_OP;
            case String op when ASSIGNMENT_OPS.contains(op) -> TokenType.ASSIGNMENT_OP;
            case String op when ARITHMETIC_OPS_DOUBLE.contains(op) -> TokenType.ARITHMETIC_OP;
            default -> null;
        };
        
        if (doubleType != null) {
            expr.advance(); // Consume firstOpChar
            expr.advance(); // Consume secondOpChar
            return new Token(doubleType, doubleOp);
        }
        TokenType simpleType = switch (Character.valueOf(firstOpChar)) {
            case Character op when ARITHMETIC_OPS_SINGLE.contains(op) -> TokenType.ARITHMETIC_OP;
            case Character op when Set.of('<', '>').contains(op) -> TokenType.RELATIONAL_OP;
            case Character op when op.equals('=') -> TokenType.ASSIGNMENT_OP;
            case Character op when op.equals('!') -> TokenType.LOGICAL_OP;
            default -> TokenType.ERROR;
        };
        
        expr.advance(); // Consume firstOpChar
        assert simpleType != TokenType.ERROR : "Operador inválido: " + firstOpChar;
        return new Token(simpleType, String.valueOf(firstOpChar));
    }
}