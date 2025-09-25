public enum TokenType {
    INTEGER("Número Inteiro"),
    FLOAT("Número Float"),
    SCIENTIFIC("Número Científico"),
    STRING("String"),
    BOOLEAN("Literal Booleano"),
    NONE("Literal None"),
    IDENTIFIER("Identificador"),
    RESERVED_WORD("Palavra Reservada"),
    BUILT_IN_FUNCTION("Função Embutida"),
    ARITHMETIC_OP("Operador Aritmético"),
    RELATIONAL_OP("Operador Relacional"),
    LOGICAL_OP("Operador Lógico"),
    ASSIGNMENT_OP("Operador de Atribuição"),
    LEFT_PARENTHESIS("Parêntese Esquerdo"),
    RIGHT_PARENTHESIS("Parêntese Direito"),
    LEFT_BRACKET("Colchete Esquerdo"),
    RIGHT_BRACKET("Colchete Direito"),
    LEFT_BRACE("Chave Esquerda"),
    RIGHT_BRACE("Chave Direita"),
    COMMA("Vírgula"),
    SEMICOLON("Ponto e Vírgula"),
    COLON("Dois Pontos"),
    DOT("Ponto"),
    COMMENT("Comentário"),
    ERROR("Token inválido");

    private final String desc;

    TokenType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}