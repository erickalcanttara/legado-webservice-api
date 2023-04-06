package utils;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CompareFiles {

    /*
        Se todas as linhas forem idênticas para ambos os arquivos, retorna-se -1L,
        mas se houver discrepância, retorna-se o número da linha onde foi encontrada a primeira incompatibilidade.
        Se os arquivos forem de tamanhos diferentes,
        mas o arquivo menor corresponder às linhas correspondentes do arquivo maior, ele retornará o número de linhas do arquivo menor.
     */
    // Métodos para todos os outros serviços
    public static long filesCompareByLine(Path path1, Path path2) throws IOException {
        try (BufferedReader bf1 = Files.newBufferedReader(path1);
             BufferedReader bf2 = Files.newBufferedReader(path2)) {

            long lineNumber = 1;
            String linhaLegado = "", linhaProxy = "";
            while ((linhaLegado = bf1.readLine()) != null) {
                linhaProxy = bf2.readLine();

                if (linhaLegado.equals(linhaProxy)) {
                    lineNumber++;
                } else {
                    if (linhaProxy == null || !linhaLegado.equals(linhaProxy)) {
                        if ((linhaLegado.contains("CPF incorreto.") && linhaProxy.contains("Formato CPF inválido."))
                                || (linhaLegado.contains("CPF obrigatório") && linhaProxy.contains("Formato CPF inválido."))
                                || (linhaLegado.contains("CPF não encontrado.") && linhaProxy.contains("Formato CPF inválido."))
                                || (linhaLegado.contains("CPF não encontrado.") && linhaProxy.contains("Conta não encontrada parao CPF informado."))
                                || (linhaLegado.contains("Cartao/Conta não encontrado.") && linhaProxy.contains("Conta não encontrada para o CPF informado."))
                        ) {
                            lineNumber++;
                        } else {
                            System.out.println("Os arquivos NÃO têm o mesmo conteúdo");
                            System.out.println("A linha com a primeira diferença é: " + lineNumber + "\n");

                            System.out.println("\nA linha que representa o Legado mostra: " + linhaLegado);
                            System.out.println("A linha que representa o Proxy mostra: " + linhaProxy);
                            return lineNumber;
                        }
                    }
                }
            }
            if (bf2.readLine() == null) {
                System.out.println("Os arquivos têm o mesmo conteúdo.");
                return -1;
            } else {
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

                if (linhaLegadoWithoutWhiteSpaces.equals(linhaProxyWithoutWhiteSpaces)) {
                    lineNumber++;
                } else {
                    if (linhaProxyWithoutWhiteSpaces == null || !linhaLegadoWithoutWhiteSpaces.equals(linhaProxyWithoutWhiteSpaces)) {
                        if ((linhaLegadoWithoutWhiteSpaces.contains("<a:Afinidade/>") && linhaProxyWithoutWhiteSpaces.contains("<a:Afinidadei:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:Sexo/>") && linhaProxyWithoutWhiteSpaces.contains("<a:Sexoi:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:NomeEmpresa/>") && linhaProxyWithoutWhiteSpaces.contains("<a:NomeEmpresai:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:CPF/>") && linhaProxyWithoutWhiteSpaces.contains("<a:CPFi:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:CNPJ/>") && linhaProxyWithoutWhiteSpaces.contains("<a:CNPJi:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:Naturalidade/>") && linhaProxyWithoutWhiteSpaces.contains("<a:Naturalidadei:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:Pais/>") && linhaProxyWithoutWhiteSpaces.contains("<a:Paisi:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:UF/>") && linhaProxyWithoutWhiteSpaces.contains("<a:UFi:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:UFNaturalidade/>") && linhaProxyWithoutWhiteSpaces.contains("<a:UFNaturalidadei:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("<a:CEP/>") && linhaProxyWithoutWhiteSpaces.contains("<a:CEPi:nil=\"true\"/>"))
                                || (linhaLegadoWithoutWhiteSpaces.contains("CPFincorreto.") && linhaProxyWithoutWhiteSpaces.contains("FormatoCPFinválido."))
                                || (linhaLegadoWithoutWhiteSpaces.contains("CPFobrigatório") && linhaProxyWithoutWhiteSpaces.contains("FormatoCPFinválido."))
                                || (linhaLegadoWithoutWhiteSpaces.contains("CPFnãoencontrado.") && linhaProxyWithoutWhiteSpaces.contains("FormatoCPFinválido."))
                        ) {
                            lineNumber++;
                        } else {
                            System.out.println("Os arquivos NÃO têm o mesmo conteúdo");
                            System.out.println("A linha com a primeira diferença é: " + lineNumber + "\n");

                            System.out.println("\nA linha que representa o Legado mostra: " + linhaLegadoWithoutWhiteSpaces);
                            System.out.println("A linha que representa o Proxy mostra: " + linhaProxyWithoutWhiteSpaces);
                            return lineNumber;
                        }
                    }
                }
            }
            if (bf2.readLine() == null) {
                System.out.println("Os arquivos têm o mesmo conteúdo.");
                return -1;
            } else {
                return lineNumber;
            }
        }
    }

    public static void replicaTestBuscarDadosMotorCredito(Path pathOrigem, Path PathDestine) throws IOException {
        try {
            FileReader file = new FileReader(String.valueOf(pathOrigem));
            BufferedReader bufferedReader = new BufferedReader(file);

            FileWriter fileWriter = new FileWriter(String.valueOf(PathDestine), true);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                String[] lineSplited = line.split(";");
                for (int i = 0; i < 3; i++) {
                    fileWriter.write(lineSplited[0] + getRandomNumber(0, 3003) + ";" + lineSplited[1] + ";" + lineSplited[2] + ";" + (i + 1) + ";" +
                            lineSplited[4] + ";" + lineSplited[5] + "\n");
                    fileWriter.flush();
                }

            }
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }


    // Esse método foi utilizado apenas para gerar massa para o serviço BuscarDadosMotorCredito
    public static void main(String[] args) throws IOException {

        /*Path pathOrigem = Paths.get("C:\\Users\\erick.alcantara\\IdeaProjects\\MigracaoWSsRenner\\src\\test\\resources\\massaDeTestes\\buscarDadosMotorCredito\\massaTeste2.csv");
        Path pathDestiny = Paths.get("C:\\Users\\erick.alcantara\\IdeaProjects\\MigracaoWSsRenner\\src\\test\\resources\\massaDeTestes\\buscarDadosMotorCredito\\massaDeTestesBuscarDadosMotorCredito-3000lines.csv");

        replicaTestBuscarDadosMotorCredito(pathOrigem, pathDestiny);*/

        Path pathOrigem = Paths.get("C:\\Users\\erick.alcantara\\IdeaProjects\\MigracaoWSsRenner\\src\\test\\resources\\consultarFaturasCPFV3\\T119\\T119-ws.xml");
        Path pathDestiny = Paths.get("C:\\Users\\erick.alcantara\\IdeaProjects\\MigracaoWSsRenner\\src\\test\\resources\\consultarFaturasCPFV3\\T119\\T119-in.xml");

        filesCompareByLine_for_FaturaService(pathOrigem, pathDestiny);
        //filesCompareByLineTEST_CPF(pathOrigem, pathDestiny);

    }
    public static long filesCompareByLine_for_FaturaService(Path path1, Path path2) throws IOException {
        try (BufferedReader bf1 = Files.newBufferedReader(path1);
             BufferedReader bf2 = Files.newBufferedReader(path2);
             BufferedReader bf3 = Files.newBufferedReader(path2)) {

            long lineNumber = 1;
            String linhaLegado = "", linhaProxy = "";

            List<String> arrayForResponseProxy = new ArrayList<>();
            List<String> arrayForLinesLegado = new ArrayList<>();
            while ((linhaProxy = bf2.readLine()) != null) {
                arrayForResponseProxy.add(linhaProxy);
            }
            System.out.println(arrayForResponseProxy.size());

            String linhaProxyIterator = "";
            while ((linhaLegado = bf1.readLine()) != null) {
                linhaProxyIterator = bf3.readLine();

                if (linhaProxyIterator == null || !linhaLegado.equals(linhaProxyIterator)) {
                    if ((linhaLegado.contains("<a:Descricao>") && linhaProxyIterator.contains("<a:Descricao>"))
                            || (linhaLegado.contains("<a:DescricaoLancamento>") && linhaProxyIterator.contains("<a:DescricaoLancamento>"))){
                        arrayForResponseProxy.remove(linhaProxyIterator);
                        lineNumber++;
                    }
                    String elementOnList = findElementOnList(linhaLegado, arrayForResponseProxy, linhaProxyIterator);
                    if(elementOnList != null){
                        lineNumber++;
                    } else {
                        arrayForLinesLegado.add(elementOnList);
                        return lineNumber;
                    }
                } else {
                    arrayForResponseProxy.remove(linhaProxyIterator);
                    lineNumber++;
                }
            }

            System.out.println(arrayForLinesLegado.size());
            for (int i = 0; i < arrayForLinesLegado.size(); i++) {
                System.out.println(arrayForLinesLegado.get(i));
            }
            if (bf3.readLine() == null) {
                System.out.println("Os arquivos têm o mesmo conteúdo.");
                return -1;
            } else {
                return lineNumber;
            }
        }
    }

    private static String findElementOnList(String linhaLegado, List<String> arrayForResponseProxy, String linhaProxyIterator) {
        String element = "";
        for (int i = 0; i < arrayForResponseProxy.size(); i++) {
            if (linhaLegado.equals(arrayForResponseProxy.get(i))){
                element = arrayForResponseProxy.get(i);
                arrayForResponseProxy.remove(linhaProxyIterator);
            }
        }
        System.out.println(element);
        return element;
    }
}
