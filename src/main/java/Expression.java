import java.util.Optional;
import java.util.function.Predicate;

public class Expression {
    private final String value;
    private int index = 0;

    public Expression(String value) {
        this.value = value != null ? value+"\n" : "\n";
    }

    public Optional<Character> next() {
        var next = getNext();
        index ++;
        return next;
    }
    
    public Optional<Character> getNext() {
        if (!hasNext()) {
            return Optional.empty();
        }
        return Optional.of(value.charAt(index + 1));
    }

    public String getNext(int count) {
        if (isOutOfBounds(index + count)) {
            return "";
        }
        return value.substring(index, index + count);
    }

    public boolean hasNext() {
        return !isOutOfBounds(index+1);
    }
    
    public boolean isOutOfBounds(int index) {
        return index >= value.length();
    }
    
    public char getCurrentChar() {
        return value.charAt(index);
    }
    
    public String accumulateWhile(Predicate<Character> condition) {
        StringBuilder sb = new StringBuilder();
        while (hasNext() && condition.test(getCurrentChar())) {
            sb.append(getCurrentChar());
            index++;
        }
        return sb.toString();
    }
    public String accumulateWhileWindow(int windowSize, Predicate<String> condition) {
        StringBuilder sb = new StringBuilder();
        while (hasNext() && condition.test(getNext(windowSize))) {
            sb.append(getCurrentChar());
            index++;
        }
        return sb.toString();
    }

    public void advance(int steps) {
        if (!isOutOfBounds(index + steps)) {
            index += steps;
        }
    }
    
    public void advance() {
        advance(1);
    }
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
