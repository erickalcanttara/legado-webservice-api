package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompareFiles {

    public static void main(String[] args) throws IOException {

        Path pathFileLegado = Paths.get("C:\\Users\\erick.alcantara\\IdeaProjects\\MigracaoWSsRenner\\src\\test\\resources\\consultarFaturasCPFV3\\T400\\T400-ws.xml");
        Path pathFileProxy = Paths.get("C:\\Users\\erick.alcantara\\IdeaProjects\\MigracaoWSsRenner\\src\\test\\resources\\consultarFaturasCPFV3\\T400\\T400-in.xml");
        long result = filesCompareByLine(pathFileLegado, pathFileProxy);

        System.out.println("O result é: " + result);
        if (result == -1){
            System.out.println("Os arquivos têm o mesmo conteúdo.");
        } else {
            System.out.println("Os arquivos NÃO têm o mesmo conteúdo");
            System.out.println("A linha com a primeira diferença é: " + result);
        }

        assertEquals(-1, result);


    }

    public static long filesCompareByLine(Path path1, Path path2) throws IOException {
        try (BufferedReader bf1 = Files.newBufferedReader(path1);
             BufferedReader bf2 = Files.newBufferedReader(path2)) {

            long lineNumber = 1;
            String line1 = "", line2 = "";
            while ((line1 = bf1.readLine()) != null) {
                line2 = bf2.readLine();
                if (line2 == null || !line1.equals(line2)) {
                    return lineNumber;
                }
                lineNumber++;
            }
            if (bf2.readLine() == null) {
                return -1;
            }
            else {
                return lineNumber;
            }
        }
    }

}
