package carneService;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListarContratosTest {

    final String PROXY_URL_CARNE = "http://internal-alb-renner-proxy-1897409209.us-east-2.elb.amazonaws.com:21634/Services/CarneService.svc";
    final String LEGADO_URL_CARNE = "http://10.75.30.52:21634/Services/CarneService.svc";

    @DisplayName("Testes Regras de Negócio - StatusCode 200")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/listarcontratos/listarContratosMassaDeTestes.csv", numLinesToSkip = 1, delimiter = ';')
    public void ListarContratosTest_SC_OK(String ReferenceTest, String CPF, String Chapa, String Contrato, String FiltroDataFim, String FiltroDataInicio,
                                    String Produto, String StatusContrato) throws IOException {

        String requestBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.CarneService\" xmlns:br1=\"br.com.conductor.RealizeWs.Carne.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:ListarContratos>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>" + CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>"+ Chapa+ "</br1:Chapa>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Contrato>" + Contrato + "</br1:Contrato>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:FiltroDataFim>" + FiltroDataFim + "</br1:FiltroDataFim>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:FiltroDataInicio>" + FiltroDataInicio + "</br1:FiltroDataInicio>\n" +
                "            <!--Optional:-->\n" +
                "             <br1:Produto>" + Produto + "</br1:Produto>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:StatusContrato>" + StatusContrato + "</br1:StatusContrato>\n" +
                "         </br:request>\n" +
                "      </br:ListarContratos>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
        given().
                header("Content-Type", "text/xml; charset=utf-8").
                header("SOAPAction", "\"br.com.conductor.RealizeWs.CarneService/CarneService/ListarContratos\"").
                accept("*/*").
                body(requestBody).
                log().method().
                log().uri().
                log().headers().
                log().body().
        when().
                post(PROXY_URL_CARNE).
        then().
                log().status().
                assertThat().
                statusCode(HttpStatus.SC_OK).
                extract().
                response();

        int statusCodeProxy = proxyResponse.statusCode();

        File testDirList = new File("src/test/resources/" + "/listarContratos/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/listarContratos/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
        given().
                header("Content-Type", "text/xml; charset=utf-8").
                header("SOAPAction", "br.com.conductor.RealizeWs.CarneService/CarneService/ListarContratos").
                accept("*/*").
                body(requestBody).
                log().method().
                log().uri().
                log().headers().
                log().body().
        when().
                post(LEGADO_URL_CARNE).
        then().
                log().status().
                statusCode(HttpStatus.SC_OK).
                extract().
                response();

        int statusCodeLegado = legadoResponse.statusCode();

        FileWriter file2 = new FileWriter("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLine(pathFileLegado, pathFileProxy);

        System.out.println("O result é: " + result);
        if (result == -1){
            System.out.println("Os arquivos têm o mesmo conteúdo.");
        } else {
            System.out.println("Os arquivos NÃO têm o mesmo conteúdo");
            System.out.println("A linha com a primeira diferença é: " + result);
        }

        assertEquals(statusCodeLegado, statusCodeProxy);
        assertEquals(-1, result);

    }

    @DisplayName("Testes Regras de Negócio - StatusCode 500")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/listarcontratos/listarContratosMassaDeTestes_SC_ERR.csv", numLinesToSkip = 1, delimiter = ';')
    public void ListarContratosTest_SC_INT_ERR(String ReferenceTest, String CPF, String Chapa, String Contrato, String FiltroDataFim, String FiltroDataInicio,
                                          String Produto, String StatusContrato) throws IOException {

        String requestBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.CarneService\" xmlns:br1=\"br.com.conductor.RealizeWs.Carne.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:ListarContratos>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>" + CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>"+ Chapa+ "</br1:Chapa>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Contrato>" + Contrato + "</br1:Contrato>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:FiltroDataFim>" + FiltroDataFim + "</br1:FiltroDataFim>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:FiltroDataInicio>" + FiltroDataInicio + "</br1:FiltroDataInicio>\n" +
                "            <!--Optional:-->\n" +
                "             <br1:Produto>" + Produto + "</br1:Produto>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:StatusContrato>" + StatusContrato + "</br1:StatusContrato>\n" +
                "         </br:request>\n" +
                "      </br:ListarContratos>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.CarneService/CarneService/ListarContratos\"").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(PROXY_URL_CARNE).
                then().
                        log().status().
                        assertThat().
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();

        File testDirList = new File("src/test/resources/" + "/listarContratos/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/listarContratos/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.CarneService/CarneService/ListarContratos").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(LEGADO_URL_CARNE).
                then().
                        log().status().
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();

        FileWriter file2 = new FileWriter("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLine(pathFileLegado, pathFileProxy);

        System.out.println("O result é: " + result);
        if (result == -1){
            System.out.println("Os arquivos têm o mesmo conteúdo.");
        } else {
            System.out.println("Os arquivos NÃO têm o mesmo conteúdo");
            System.out.println("A linha com a primeira diferença é: " + result);
        }

        assertEquals(statusCodeLegado, statusCodeProxy);
        assertEquals(-1, result);

    }

    /*
        Se todas as linhas forem idênticas para ambos os arquivos, retorna-se -1L,
        mas se houver discrepância, retorna-se o número da linha onde foi encontrada a primeira incompatibilidade.
        Se os arquivos forem de tamanhos diferentes,
        mas o arquivo menor corresponder às linhas correspondentes do arquivo maior, ele retornará o número de linhas do arquivo menor.
     */
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
