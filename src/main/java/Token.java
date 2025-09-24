public class Token {

    private final TokenType type;
    private final String lexeme;

    public Token(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    @Override
    public String toString() {
        
        if (Constants.contains(lexeme)) {
            return this.getLexeme() + " - " + getType().getDesc() + " " + this.getLexeme();
        }
        
        return this.getLexeme() + " - " + getType().getDesc();
    }
}