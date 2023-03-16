package utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
public class CompareFiles {

    // Métodos para todos os outros serviços
    public static long filesCompareByLine(Path path1, Path path2) throws IOException {
        try (BufferedReader bf1 = Files.newBufferedReader(path1);
             BufferedReader bf2 = Files.newBufferedReader(path2)) {

            long lineNumber = 1;
            String linhaLegado = "", linhaProxy = "";
            while ((linhaLegado = bf1.readLine()) != null) {
                linhaProxy = bf2.readLine();

                String linhaLegadoWithoutWhiteSpaces = StringUtils.deleteWhitespace(linhaLegado);
                String linhaProxyWithoutWhiteSpaces = StringUtils.deleteWhitespace(linhaProxy);

                if (linhaProxyWithoutWhiteSpaces == null || !linhaLegadoWithoutWhiteSpaces.equals(linhaProxyWithoutWhiteSpaces)) {
                    System.out.println("\nA linha que representa o Legado mostra: " + linhaLegadoWithoutWhiteSpaces);
                    System.out.println("A linha que representa o Proxy mostra: " + linhaProxyWithoutWhiteSpaces);
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

    // Método dedicado ao BuscarDadosCliente
    public static long filesCompareByLineForBuscarDadosCliente(Path path1, Path path2) throws IOException {
        try (BufferedReader bf1 = Files.newBufferedReader(path1);
             BufferedReader bf2 = Files.newBufferedReader(path2)) {

            long lineNumber = 1;
            String linhaLegado = "", linhaProxy = "";
            while ((linhaLegado = bf1.readLine()) != null) {
                linhaProxy = bf2.readLine();

                String linhaLegadoWithoutWhiteSpaces = StringUtils.deleteWhitespace(linhaLegado);
                String linhaProxyWithoutWhiteSpaces = StringUtils.deleteWhitespace(linhaProxy);

                if(linhaLegadoWithoutWhiteSpaces.equals(linhaProxyWithoutWhiteSpaces)) {
                    lineNumber++;
                }else {
                    if (linhaProxyWithoutWhiteSpaces == null || !linhaLegadoWithoutWhiteSpaces.equals(linhaProxyWithoutWhiteSpaces)) {
                        if((linhaLegadoWithoutWhiteSpaces.contains("<a:Afinidade/>") && linhaProxyWithoutWhiteSpaces.contains("<a:Afinidadei:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:Sexo/>") && linhaProxyWithoutWhiteSpaces.contains("<a:Sexoi:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:NomeEmpresa/>") && linhaProxyWithoutWhiteSpaces.contains("<a:NomeEmpresai:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:CPF/>") && linhaProxyWithoutWhiteSpaces.contains("<a:CPFi:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:CNPJ/>") && linhaProxyWithoutWhiteSpaces.contains("<a:CNPJi:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:Naturalidade/>") && linhaProxyWithoutWhiteSpaces.contains("<a:Naturalidadei:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:Pais/>") && linhaProxyWithoutWhiteSpaces.contains("<a:Paisi:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:UF/>") && linhaProxyWithoutWhiteSpaces.contains("<a:UFi:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:UFNaturalidade/>") && linhaProxyWithoutWhiteSpaces.contains("<a:UFNaturalidadei:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:CEP/>") && linhaProxyWithoutWhiteSpaces.contains("<a:CEPi:nil=\"true\"/>"))
                        ){
                            lineNumber++;
                        }else{
                            System.out.println("\nA linha que representa o Legado mostra: " + linhaLegadoWithoutWhiteSpaces);
                            System.out.println("A linha que representa o Proxy mostra: " + linhaProxyWithoutWhiteSpaces);
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

}
