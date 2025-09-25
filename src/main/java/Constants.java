import java.util.Set;

public class Constants {
    
    public static final Set<String> BUILT_IN_FUNCTION = Set.of("print", "range");
    
    public static final Set<String> RESERVED_WORDS = Set.of(
            "if", "else", "for", "while", "def", "class", "return",
            "import", "from", "try", "except", "with", "and", "or",
            "not", "True", "False", "None", "in", "is"
    );

    public static final Set<String> RELATIONAL_OPS = Set.of("==", "!=", "<=", ">=");
    public static final Set<String> ASSIGNMENT_OPS = Set.of("+=", "-=", "*=", "/=");
    public static final Set<String> ARITHMETIC_OPS_DOUBLE = Set.of("//", "**");
    public static final Set<Character> ARITHMETIC_OPS_SINGLE = Set.of('*', '/', '+', '-', '%');

    public static final Set<Character> SIMPLE_OPERATORS = Set.of(
            '+', '-', '*', '/', '%', '=', '<', '>', '!'
    );

    public static final Set<Character> DELIMITERS = Set.of(
            '(', ')', '[', ']', '{', '}', ',', ':', '.', ';'
    );

    public static boolean contains(String lexeme) {
        if (BUILT_IN_FUNCTION.contains(lexeme) ||
                RESERVED_WORDS.contains(lexeme) ||
                RELATIONAL_OPS.contains(lexeme) ||
                ARITHMETIC_OPS_DOUBLE.contains(lexeme)) {
            return true;
        }
        
        if (lexeme.length() == 1) {
            char ch = lexeme.charAt(0);
            return ARITHMETIC_OPS_SINGLE.contains(ch);
        }

        return false;
    }
}
