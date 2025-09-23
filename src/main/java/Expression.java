import java.util.Optional;
import java.util.function.Predicate;

public class Expression {
    private final String value;
    private int index = 0;

    // Construtor que inicializa a expressão e adiciona quebra de linha
    public Expression(String value) {
        this.value = value != null ? value+"\n" : "\n";
    }

    // Retorna o próximo caractere e avança o índice
    public Optional<Character> next() {
        var next = getNext();
        index ++;
        return next;
    }

    // Obtém o próximo caractere sem avançar o índice
    public Optional<Character> getNext() {
        if (!hasNext()) {
            return Optional.empty();
        }
        return Optional.of(value.charAt(index + 1));
    }

    // Obtém uma substring de tamanho específico a partir da posição atual
    public String getNext(int count) {
        if (isOutOfBounds(index + count)) {
            return "";
        }
        return value.substring(index, index + count);
    }

    // Verifica se há mais caracteres para processar
    public boolean hasNext() {
        return !isOutOfBounds(index+1);
    }

    // Verifica se o índice está fora dos limites da string
    public boolean isOutOfBounds(int index) {
        return index >= value.length();
    }

    // Retorna o caractere na posição atual
    public char getCurrentChar() {
        return value.charAt(index);
    }

    // Acumula caracteres enquanto a condição for verdadeira
    public String accumulateWhile(Predicate<Character> condition) {
        StringBuilder sb = new StringBuilder();
        while (hasNext() && condition.test(getCurrentChar())) {
            sb.append(getCurrentChar());
            index++;
        }
        return sb.toString();
    }

    // Acumula caracteres baseado em uma janela de caracteres
    public String accumulateWhileWindow(int windowSize, Predicate<String> condition) {
        StringBuilder sb = new StringBuilder();
        while (hasNext() && condition.test(getNext(windowSize))) {
            sb.append(getCurrentChar());
            index++;
        }
        return sb.toString();
    }

    // Avança o índice por um número específico de posições
    public void advance(int steps) {
        if (!isOutOfBounds(index + steps)) {
            index += steps;
        }
    }

    // Avança o índice em uma posição
    public void advance() {
        advance(1);
    }

    // Verifica se o caractere atual é igual a qualquer um dos caracteres fornecidos
    public boolean currentCharIsAnyOf(char... chars) {
        char current = getCurrentChar();
        for (char c : chars) {
            if (current == c) {
                return true;
            }
        }
        return false;
    }
}