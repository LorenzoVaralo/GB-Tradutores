import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        String rootPath = Paths.get("").toAbsolutePath().toString();
        String subPath  = "/src/main/python/codes";
        String sourceCode = rootPath + subPath + "/program2.txt";

        System.out.println("Analisando arquivo: " + sourceCode);
        System.out.println("Tokens encontrados:");
        System.out.println("-".repeat(45));

        try {
            File codigoTeste = new File(sourceCode);

            PythonLexicalAnalyzer lexical = new PythonLexicalAnalyzer(codigoTeste);

            lexical.analyzeCode();

            System.out.println("-".repeat(45));
            System.out.println("Análise concluída!");
        } catch (Exception e) {
            System.err.println("Erro durante análise: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
