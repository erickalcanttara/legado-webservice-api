package clienteService;


import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static utils.CompareFiles.filesCompareByLine;
import static utils.CompareFiles.filesCompareByLineForBuscarDadosCliente;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BuscarDadosClienteTest {
    final String PROXY_URL_CLIENTE = "http://internal-alb-renner-proxy-511841837.us-east-2.elb.amazonaws.com:21634/Services/ClienteService.svc";
    final String LEGADO_URL_CLIENTE = "http://10.75.30.52:21634/Services/ClienteService.svc";

    @Order(3)
    @DisplayName("Testes Regras de Negócio - StatusCode 200")
    @ParameterizedTest
    @CsvFileSource(files = {"C:\\Users\\erick.alcantara\\IdeaProjects\\MigracaoWSsRenner\\src\\test\\resources\\massaDeTestes\\buscarDadosCliente\\buscarDadosClienteMassaDeTestes-1000-Accounts-1.csv",
            "C:\\Users\\erick.alcantara\\IdeaProjects\\MigracaoWSsRenner\\src\\test\\resources\\massaDeTestes\\buscarDadosCliente\\buscarDadosClienteMassaDeTestes-1000-Accounts-2.csv",
            "C:\\Users\\erick.alcantara\\IdeaProjects\\MigracaoWSsRenner\\src\\test\\resources\\massaDeTestes\\buscarDadosCliente\\buscarDadosClienteMassaDeTestes-1000-Accounts-3.csv",
            "C:\\Users\\erick.alcantara\\IdeaProjects\\MigracaoWSsRenner\\src\\test\\resources\\massaDeTestes\\buscarDadosCliente\\buscarDadosClienteMassaDeTestes-1000-Accounts-4.csv",
            "C:\\Users\\erick.alcantara\\IdeaProjects\\MigracaoWSsRenner\\src\\test\\resources\\massaDeTestes\\buscarDadosCliente\\buscarDadosClienteMassaDeTestes-1000-Accounts-5.csv"},
            numLinesToSkip = 1, delimiter = ';')
    public void BuscarDadosClienteTest_SC_OK(String ReferenceTest, String CPF, String Chapa, String id_conta, String descricaoStatus) throws IOException, ParserConfigurationException, SAXException {

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.ClienteService\" xmlns:br1=\"br.com.conductor.RealizeWs.Cliente.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:BuscarDadosCliente>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>"+ CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>" + Chapa + "</br1:Chapa>\n" +
                "         </br:request>\n" +
                "      </br:BuscarDadosCliente>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.ClienteService/ClienteService/BuscarDadosCliente\"").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                when().
                        post(PROXY_URL_CLIENTE).
                then().
                        log().status().
                        assertThat().
                        statusCode(HttpStatus.SC_OK).
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();
        long timeResponsesProxy = proxyResponse.getTime();

        File testDirList = new File("src/test/resources/" + "/buscarDadosCliente/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.ClienteService/ClienteService/BuscarDadosCliente").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                when().
                        post(LEGADO_URL_CLIENTE).
                then().
                        log().status().
                        statusCode(HttpStatus.SC_OK).
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();
        long timeResponsesLegado = legadoResponse.getTime();

        FileWriter file2 = new FileWriter("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLineForBuscarDadosCliente(pathFileLegado, pathFileProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);

        assertEquals(statusCodeLegado, statusCodeProxy);
        assertEquals(-1, result);
    }

    @Order(1)
    @DisplayName("Testes para CPF Nulo e Vazio")
    @ParameterizedTest
    @NullAndEmptySource
    public void ListarContratosTest_SC_INT_ERR_TESTE_CPF_VAZIO_NULL(String CPF) throws IOException {

        String ReferenceTest = "ER_CPF_VAZIO";
        if (CPF == null ){
            ReferenceTest = "ER_CPF_NULL";
        }

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.ClienteService\" xmlns:br1=\"br.com.conductor.RealizeWs.Cliente.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:BuscarDadosCliente>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>"+ CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa></br1:Chapa>\n" +
                "         </br:request>\n" +
                "      </br:BuscarDadosCliente>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.ClienteService/ClienteService/BuscarDadosCliente\"").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                when().
                        post(PROXY_URL_CLIENTE).
                then().
                        log().status().
                        assertThat().
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();
        long timeResponsesProxy = proxyResponse.getTime();

        File testDirList = new File("src/test/resources/" + "/buscarDadosCliente/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.ClienteService/ClienteService/BuscarDadosCliente").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                when().
                        post(LEGADO_URL_CLIENTE).
                then().
                        log().status().
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();
        long timeResponsesLegado = legadoResponse.getTime();

        FileWriter file2 = new FileWriter("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLineForBuscarDadosCliente(pathFileLegado, pathFileProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);

        assertEquals(statusCodeLegado, statusCodeProxy);
        assertEquals(-1, result);
    }

    @Order(2)
    @DisplayName("Testes para CPF com Formato Inválido")
    @ParameterizedTest
    @CsvFileSource(files = "src/test/resources/massaDeTestes/buscarDadosCliente/buscarDadosCliente_ERR.csv", numLinesToSkip = 1, delimiter = ';')
    public void ListarContratosTest_SC_INT_ERR_TESTE_CPF_FORMATO_INVALIDO(String ReferenceTest, String CPF, String Chapa, String id_conta, String descricaoStatus) throws IOException {

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.ClienteService\" xmlns:br1=\"br.com.conductor.RealizeWs.Cliente.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:BuscarDadosCliente>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>"+ CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa></br1:Chapa>\n" +
                "         </br:request>\n" +
                "      </br:BuscarDadosCliente>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.ClienteService/ClienteService/BuscarDadosCliente\"").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                when().
                        post(PROXY_URL_CLIENTE).
                then().
                        log().status().
                        assertThat().
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();
        long timeResponsesProxy = proxyResponse.getTime();

        File testDirList = new File("src/test/resources/" + "/buscarDadosCliente/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.ClienteService/ClienteService/BuscarDadosCliente").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                when().
                        post(LEGADO_URL_CLIENTE).
                then().
                        log().status().
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();
        long timeResponsesLegado = legadoResponse.getTime();

        FileWriter file2 = new FileWriter("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/buscarDadosCliente/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLineForBuscarDadosCliente(pathFileLegado, pathFileProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);

        assertEquals(statusCodeLegado, statusCodeProxy);
        assertEquals(-1, result);
    }
}
