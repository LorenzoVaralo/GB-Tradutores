import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) throws IOException {
        //        final var sourceCode = "src/test/data/test.txt";
        //        final var lex = new SimpleScanner(new FileReader(sourceCode));
        final var code = 
                "pi = 3.14\n" + 
                "if (pi > 3):\n" +
                "\tprint(pi)";
        final var lex = new PythonLexer(new StringReader(code));

        System.out.println("-".repeat(40));
        System.out.println("| Token | Lexeme |");
        System.out.println("-".repeat(40));
        
        do {
            final var token = lex.yylex();
            if (token == null) break;
            System.out.printf("| %-5s | %-6s |\n", token.m_index, token.m_text);
            
        } while (!lex.yyatEOF());
    }
}
