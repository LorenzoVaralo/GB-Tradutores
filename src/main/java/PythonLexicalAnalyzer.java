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

    private static final Set<Character> SIMPLE_OPERATORS = Set.of(
            '+', '-', '*', '/', '%', '=', '<', '>', '!'
    );

    private static final Set<Character> DELIMITERS = Set.of(
            '(', ')', '[', ']', '{', '}', ',', ':', '.', ';'
    );

    private String code;
    private int position = 0;

    public PythonLexicalAnalyzer(File file) throws IOException {
        this.code = readFile(file);
    }

    private String readFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }

    public void analyzeCode() {
        while (position < code.length()) {
            Token token = getToken();
            if (nonNull(token)) {
                System.out.println(token);
            }
        }
    }

    private Token getToken() {
        skipSpaces();

        if (position >= code.length()) {
            return null;
        }

        char c = code.charAt(position);

        if (c == '#') {
            return readComment();
        } else if (c == '"' || c == '\'') {
            return readString();
        } else if (Character.isDigit(c)) {
            return readNumber();
        } else if (Character.isLetter(c) || c == '_') {
            return readIdentifier();
        } else if (SIMPLE_OPERATORS.contains(c)) {
            return readOperator();
        } else if (DELIMITERS.contains(c)) {
            return readDelimiter();
        }

        char invalidSymbol = code.charAt(position++);
        throw new RuntimeException(
                "Erro: símbolo inválido '" + invalidSymbol + "'"
        );
    }

    private Token readDelimiter() {
        char c = code.charAt(position++);
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

    private void skipSpaces() {
        while (position < code.length()) {
            char c = code.charAt(position);
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                position++;
            } else {
                break;
            }
        }
    }

    private Token readComment() {
        String result = "";
        while (position < code.length() && code.charAt(position) != '\n' && code.charAt(position) != '\r') {
            result += code.charAt(position++);
        }
        return new Token(TokenType.COMMENT, result);
    }

    private Token readString() {
        char quotes = code.charAt(position);
        String tokenString = "" + quotes;
        position++;

        boolean multiline = false;
        if (code.charAt(position) == quotes && code.charAt(position + 1) == quotes) {
            multiline = true;
            tokenString += "" + quotes + quotes;
            position += 2;
        }

        while (position < code.length()) {
            char c = code.charAt(position);

            if (multiline) {
                if (c == quotes && code.charAt(position + 1) == quotes && code.charAt(position + 2) == quotes) {
                    tokenString += "" + quotes + quotes + quotes;
                    position += 3;
                    break;
                }
            } else {
                if (c == quotes) {
                    tokenString += c;
                    position++;
                    break;
                }
                if (c == '\n') break;
            }

            tokenString += c;
            position++;
        }

        return new Token(TokenType.STRING, tokenString);
    }

    private Token readNumber() {
        String number = "";
        boolean hasDecimalPoint = false;
        boolean hasScientificNotation = false;

        while (position < code.length()) {
            char c = code.charAt(position);
            if (Character.isDigit(c)) {
                number += c;
                position++;
            } else if (c == '.' && !hasDecimalPoint && !hasScientificNotation) {
                hasDecimalPoint = true;
                number += c;
                position++;
            } else {
                break;
            }
        }

        if (position < code.length()) {
            char c = code.charAt(position);
            if (c == 'e' || c == 'E') {
                hasScientificNotation = true;
                number += c;
                position++;

                if (position < code.length()) {
                    char nextChar = code.charAt(position);
                    if (nextChar == '+' || nextChar == '-') {
                        number += nextChar;
                        position++;
                    }
                }

                boolean hasExponentDigits = false;
                while (position < code.length()) {
                    char exponentChar = code.charAt(position);
                    if (Character.isDigit(exponentChar)) {
                        number += exponentChar;
                        position++;
                        hasExponentDigits = true;
                    } else {
                        break;
                    }
                }

                if (!hasExponentDigits) {
                    throw new RuntimeException(
                            "Erro: número científico inválido '" + number + "'"
                    );
                }
            }
        }

        TokenType tokenType;
        if (hasScientificNotation || hasDecimalPoint) {
            tokenType = TokenType.FLOAT;
        } else {
            tokenType = TokenType.INTEGER;
        }

        return new Token(tokenType, number);
    }

    private Token readIdentifier() {
        String text = "";

        while (position < code.length()) {
            char c = code.charAt(position);
            if (Character.isLetterOrDigit(c) || c == '_') {
                text += c;
                position++;
            } else {
                break;
            }
        }

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
        char c = code.charAt(position);

        if (SIMPLE_OPERATORS.contains(code.charAt(position + 1))) {
            char next = code.charAt(position + 1);
            String doubleOp = "" + c + next;

            TokenType doubleType = switch (doubleOp) {
                case "==" -> TokenType.RELATIONAL_OP;
                case "!=" -> TokenType.RELATIONAL_OP;
                case "<=" -> TokenType.RELATIONAL_OP;
                case ">=" -> TokenType.RELATIONAL_OP;
                case "+=" -> TokenType.ASSIGNMENT_OP;
                case "-=" -> TokenType.ASSIGNMENT_OP;
                case "*=" -> TokenType.ASSIGNMENT_OP;
                case "/=" -> TokenType.ASSIGNMENT_OP;
                case "//" -> TokenType.ARITHMETIC_OP;
                case "**" -> TokenType.ARITHMETIC_OP;
                default -> null;
            };

            if (nonNull(doubleType)) {
                position += 2;
                return new Token(doubleType, doubleOp);
            }
        }

        position++;
        TokenType simpleType = switch (c) {
            case '+' -> TokenType.ARITHMETIC_OP;
            case '-' -> TokenType.ARITHMETIC_OP;
            case '*' -> TokenType.ARITHMETIC_OP;
            case '/' -> TokenType.ARITHMETIC_OP;
            case '%' -> TokenType.ARITHMETIC_OP;
            case '<' -> TokenType.RELATIONAL_OP;
            case '>' -> TokenType.RELATIONAL_OP;
            case '=' -> TokenType.ASSIGNMENT_OP;
            case '!' -> TokenType.LOGICAL_OP;
            default -> TokenType.ERROR;
        };

        return new Token(simpleType, String.valueOf(c));
    }
}