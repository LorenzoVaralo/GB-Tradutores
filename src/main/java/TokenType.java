public enum TokenType {
    INTEGER("Número Inteiro"),
    FLOAT("Número Float"),
    SCIENTIFIC("Número científico"),
    STRING("String"),
    BOOLEAN("Literal booleano"),
    NONE("Literal None"),
    IDENTIFIER("Identificador"),
    RESERVED_WORD("Palavra reservada"),
    ARITHMETIC_OP("Operador aritmético"),
    RELATIONAL_OP("Operador relacional"),
    LOGICAL_OP("Operador lógico"),
    ASSIGNMENT_OP("Operador de atribuição"),
    LEFT_PARENTHESIS("Parêntese esquerdo"),
    RIGHT_PARENTHESIS("Parêntese direito"),
    LEFT_BRACKET("Colchete esquerdo"),
    RIGHT_BRACKET("Colchete direito"),
    LEFT_BRACE("Chave esquerda"),
    RIGHT_BRACE("Chave direita"),
    COMMA("Vírgula"),
    SEMICOLON("Ponto e vírgula"),
    COLON("Dois pontos"),
    DOT("Ponto"),
    COMMENT("Comentário"),
    ERROR("Token inválido"),
    INDENT("Indentação"),
    DEDENT("Desindentação"),
    NEWLINE("Nova linha");

    private final String desc;

    TokenType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}