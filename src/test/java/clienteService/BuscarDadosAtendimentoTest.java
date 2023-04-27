package clienteService;


import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
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

public class BuscarDadosAtendimentoTest {

    final String PROXY_URL_CLIENTE = "http://internal-alb-renner-proxy-511841837.us-east-2.elb.amazonaws.com:21634/Services/ClienteService.svc";
    final String LEGADO_URL_CLIENTE = "http://10.75.30.52:21634/Services/ClienteService.svc";

    @DisplayName("Testes Regras de Negócio - StatusCode 200")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/buscarDadosAtendimento/buscarDadosAtendimentoMassaDeTestes.csv", numLinesToSkip = 1, delimiter = ';')
    public void BuscarDadosAtendimentoTest_SC_OK(String ReferenceTest, String CPF, String id_conta, String id_cartao, String statusCartao, String estagioCartao) throws IOException, ParserConfigurationException, SAXException {

        System.out.println("ReferenceTest = " + ReferenceTest +
                "\nCPF = " + CPF + "\nid_conta = " + id_conta + "\nid_cartao = " + id_cartao
                + "\nstatusCartao = " + statusCartao + "\nestagioCartao = " + estagioCartao);

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.ClienteService\" xmlns:br1=\"br.com.conductor.RealizeWs.Cliente.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:BuscarDadosAtendimento>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>" + CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>123</br1:Chapa>\n" +
                "         </br:request>\n" +
                "      </br:BuscarDadosAtendimento>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.ClienteService/ClienteService/BuscarDadosAtendimento\"").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(PROXY_URL_CLIENTE).
                then().
                        log().status().
                        assertThat().
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();
        long timeResponsesProxy = proxyResponse.getTime();
        String contentTypeProxy = proxyResponse.contentType();

        File testDirList = new File("src/test/resources/" + "/buscarDadosAtendimento/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/buscarDadosAtendimento/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/buscarDadosAtendimento/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/buscarDadosAtendimento/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/buscarDadosAtendimento/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.ClienteService/ClienteService/BuscarDadosAtendimento").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(LEGADO_URL_CLIENTE).
                then().
                        log().status().
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();
        long timeResponsesLegado = legadoResponse.getTime();
        String contentTypeLegado = legadoResponse.contentType();

        FileWriter file2 = new FileWriter("src/test/resources/buscarDadosAtendimento/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/buscarDadosAtendimento/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLine(pathFileLegado, pathFileProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);

        assertEquals(contentTypeLegado, contentTypeProxy, "As chamadas não têm o mesmo Content-Type");
        assertEquals(statusCodeLegado, statusCodeProxy, "As chamadas não têm o mesmo StatusCode");
        assertEquals(-1, result);
    }

    @DisplayName("Testes Regras de Negócio - StatusCode 500")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/consultaContaCliente/consultaContaClienteMassaDeTestesSC_500.csv", numLinesToSkip = 1, delimiter = ';')
    public void ConsultaContaCliente_SC_INT_ERR(String ReferenceTest, String CPF, String Chapa, String id_conta, String descricaoStatus) throws IOException, ParserConfigurationException, SAXException {

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.ClienteService\" xmlns:br1=\"br.com.conductor.RealizeWs.Cliente.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:ConsultaContaCliente>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>" + CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>" + Chapa + "</br1:Chapa>\n" +
                "         </br:request>\n" +
                "      </br:ConsultaContaCliente>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.ClienteService/ClienteService/ConsultaContaCliente\"").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
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

        File testDirList = new File("src/test/resources/" + "/consultaContaCliente/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/consultaContaCliente/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/consultaContaCliente/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/consultaContaCliente/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/consultaContaCliente/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.ClienteService/ClienteService/ConsultaContaCliente").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
               when().
                        post(LEGADO_URL_CLIENTE).
               then().
                        log().status().
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();
        long timeResponsesLegado = legadoResponse.getTime();

        FileWriter file2 = new FileWriter("src/test/resources/consultaContaCliente/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/consultaContaCliente/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLine(pathFileLegado, pathFileProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);

        assertEquals(statusCodeLegado, statusCodeProxy);
        assertEquals(-1, result);
    }
}
