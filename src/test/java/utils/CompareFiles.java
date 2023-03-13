package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
public class CompareFiles {

    public static long filesCompareByLineForBuscarDadosCliente(Path path1, Path path2) throws IOException {
        try (BufferedReader bf1 = Files.newBufferedReader(path1);
             BufferedReader bf2 = Files.newBufferedReader(path2)) {

            long lineNumber = 1;
            String linhaLegado = "", linhaProxy = "";
            while ((linhaLegado = bf1.readLine()) != null) {
                linhaProxy = bf2.readLine();

                if(linhaLegado.equals(linhaProxy)) {
                    lineNumber++;
                }else {
                    if (linhaProxy == null || !linhaLegado.equals(linhaProxy)) {
                        if((linhaLegado.contains("<a:Afinidade/>") && linhaProxy.contains("<a:Afinidade i:nil=\"true\"/>"))
                                || (linhaLegado.contains("<a:Sexo/>") && linhaProxy.contains("<a:Sexo i:nil=\"true\"/>"))
                                || (linhaLegado.contains("<a:NomeEmpresa/>") && linhaProxy.contains("<a:NomeEmpresa i:nil=\"true\"/>"))
                                || (linhaLegado.contains("<a:CPF/>") && linhaProxy.contains("<a:CPF i:nil=\"true\"/>"))
                                || (linhaLegado.contains("<a:CNPJ/>") && linhaProxy.contains("<a:CNPJ i:nil=\"true\"/>"))
                                || (linhaLegado.contains("<a:Naturalidade/>") && linhaProxy.contains("<a:Naturalidade i:nil=\"true\"/>"))
                                || (linhaLegado.contains("<a:Pais/>") && linhaProxy.contains("<a:Pais i:nil=\"true\"/>"))
                                || (linhaLegado.contains("<a:UF/>") && linhaProxy.contains("<a:UF i:nil=\"true\"/>"))){
                            lineNumber++;
                        }else{
                            System.out.println("A linha que representa o Legado mostra: " + linhaLegado);
                            System.out.println("A linha que representa o Proxy mostra: " + linhaProxy);
                            return lineNumber;
                        }
                    }
                }
            }
            if (bf2.readLine() == null) {
                return -1;
            }
            else {
                return lineNumber;
            }
        }
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
