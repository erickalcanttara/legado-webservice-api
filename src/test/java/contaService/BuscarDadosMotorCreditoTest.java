package contaService;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
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
import static utils.CompareFiles.filesCompareByLine;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BuscarDadosMotorCreditoTest {

    final String PROXY_URL_CONTA = "http://internal-alb-renner-proxy-511841837.us-east-2.elb.amazonaws.com:21634/Services/ContaService.svc";
    final String LEGADO_URL_CONTA = "http://10.75.30.52:21634/Services/ContaService.svc";

    @Order(2)
    @DisplayName("Testes Regras de Negócio - StatusCode 200")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/buscarDadosMotorCredito/massaDeTestesBuscarDadosMotorCredito-3000lines.csv", delimiter = ';')
    public void BuscarDadosAlteracaoLimitesTest_SC_OK(String ReferenceTest, String CPF, String Chapa, int TipoLimite) throws IOException {

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.ContaService\" xmlns:br1=\"br.com.conductor.RealizeWs.Conta.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:BuscarDadosMotorCredito>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>" + CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>" + Chapa + "</br1:Chapa>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:TipoLimite>" + TipoLimite + "</br1:TipoLimite>\n" +
                "         </br:request>\n" +
                "      </br:BuscarDadosMotorCredito>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.ContaService/ContaService/BuscarDadosMotorCredito\"").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(PROXY_URL_CONTA).
                then().
                        log().status().
                        assertThat().
                        statusCode(HttpStatus.SC_OK).
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();
        long timeResponsesProxy = proxyResponse.getTime();

        File testDirList = new File("src/test/resources/" + "/buscarDadosMotorCredito/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/buscarDadosMotorCredito/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/buscarDadosMotorCredito/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/buscarDadosMotorCredito/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/buscarDadosMotorCredito/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.ContaService/ContaService/BuscarDadosMotorCredito").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(LEGADO_URL_CONTA).
                then().
                        log().status().
                        statusCode(HttpStatus.SC_OK).
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();
        long timeResponsesLegado = legadoResponse.getTime();

        FileWriter file2 = new FileWriter("src/test/resources/buscarDadosMotorCredito/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/buscarDadosMotorCredito/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLine(pathFileLegado, pathFileProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);

        assertEquals(statusCodeLegado, statusCodeProxy);
        assertEquals(-1, result);
    }

    @Order(1)
    @DisplayName("Testes Regras de Negócio - StatusCode 500")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/buscarDadosMotorCredito/buscarDadosMotorCreditoMassaDeTestes_ERR.csv", numLinesToSkip = 1, delimiter = ';')
    public void BuscarDadosAlteracaoLimites_SC_INT_ERR(String ReferenceTest, String CPF, String Chapa, int TipoLimite) throws IOException {

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.ContaService\" xmlns:br1=\"br.com.conductor.RealizeWs.Conta.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:BuscarDadosMotorCredito>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>" + CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>" + Chapa + "</br1:Chapa>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:TipoLimite>" + TipoLimite + "</br1:TipoLimite>\n" +
                "         </br:request>\n" +
                "      </br:BuscarDadosMotorCredito>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.ContaService/ContaService/BuscarDadosMotorCredito\"").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(PROXY_URL_CONTA).
                then().
                        log().status().
                        assertThat().
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();
        long timeResponsesProxy = proxyResponse.getTime();

        File testDirList = new File("src/test/resources/" + "/buscarDadosMotorCredito/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/buscarDadosMotorCredito/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/buscarDadosMotorCredito/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/buscarDadosMotorCredito/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/buscarDadosMotorCredito/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.ContaService/ContaService/BuscarDadosMotorCredito").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
               when().
                        post(LEGADO_URL_CONTA).
               then().
                        log().status().
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();
        long timeResponsesLegado = legadoResponse.getTime();

        FileWriter file2 = new FileWriter("src/test/resources/buscarDadosMotorCredito/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/buscarDadosMotorCredito/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLine(pathFileLegado, pathFileProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);

        assertEquals(statusCodeLegado, statusCodeProxy);
        assertEquals(-1, result);
    }


}
