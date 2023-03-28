package cartaoService;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
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
import java.sql.Ref;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.CompareFiles.filesCompareByLine;

public class ConsultarCartoesTest {

    final String PROXY_URL_CARTAO = "http://internal-alb-renner-proxy-511841837.us-east-2.elb.amazonaws.com:21634/Services/CartaoService.svc";
    final String LEGADO_URL_CARTAO = "http://10.75.30.52:21634/Services/CartaoService.svc";

    @DisplayName("Testes Regras de Negócio - StatusCode 200")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/consultarCartoes/consultarCartoesMassaDeTestes.csv", numLinesToSkip = 1, delimiter = ';')
    public void ConsultaContaClienteTest_SC_OK(String ReferenceTest, String CPF, String id_conta, String id_cartao, int statusCartao) throws IOException, ParserConfigurationException, SAXException {

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.CartaoService\" xmlns:br1=\"br.com.conductor.RealizeWs.Cartao.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:ConsultarCartoes>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>"+ CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>R2D2</br1:Chapa>\n" +
                "         </br:request>\n" +
                "      </br:ConsultarCartoes>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.CartaoService/CartaoService/ConsultarCartoes\"").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(PROXY_URL_CARTAO).
                then().
                        log().status().
                        assertThat().
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();
        long timeResponsesProxy = proxyResponse.getTime();

        File testDirList = new File("src/test/resources/" + "/consultarCartoes/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/consultarCartoes/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.CartaoService/CartaoService/ConsultarCartoes").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(LEGADO_URL_CARTAO).
                then().
                        log().status().
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();
        long timeResponsesLegado = legadoResponse.getTime();

        FileWriter file2 = new FileWriter("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLine(pathFileLegado, pathFileProxy);

        assertEquals(statusCodeLegado, statusCodeProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);
        assertEquals(-1, result);
        assertEquals(statusCodeLegado, statusCodeProxy);
    }

    @DisplayName("Testes CPF com formato inválido")
    @ParameterizedTest
    @NullAndEmptySource
    public void ConsultaContaCliente_CPF_VAZIO_E_NULO(String CPF) throws IOException {

        String ReferenceTest = "CPF_VAZIO";
        if (CPF == null){
            ReferenceTest = "CPF_NULO";
        }

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.CartaoService\" xmlns:br1=\"br.com.conductor.RealizeWs.Cartao.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:ConsultarCartoes>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>"+ CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>R2D2</br1:Chapa>\n" +
                "         </br:request>\n" +
                "      </br:ConsultarCartoes>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.CartaoService/CartaoService/ConsultarCartoes\"").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(PROXY_URL_CARTAO).
                then().
                        log().status().
                        assertThat().
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();
        long timeResponsesProxy = proxyResponse.getTime();

        File testDirList = new File("src/test/resources/" + "/consultarCartoes/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/consultarCartoes/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.CartaoService/CartaoService/ConsultarCartoes").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(LEGADO_URL_CARTAO).
                then().
                        log().status().
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();
        long timeResponsesLegado = legadoResponse.getTime();

        FileWriter file2 = new FileWriter("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLine(pathFileLegado, pathFileProxy);

        assertEquals(statusCodeLegado, statusCodeProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);
        assertEquals(-1, result);
    }

    @DisplayName("Testes para CPF com formato inválido")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/consultarCartoes/consultarCartoesMassaDeTestes_CPF_INVALIDO.csv", numLinesToSkip = 1, delimiter = ';')
    public void ConsultaContaClienteTest_FORMATO_CPF_INVALIDO(String ReferenceTest, String CPF, String id_conta, String id_cartao, int statusCartao) throws IOException, ParserConfigurationException, SAXException {

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.CartaoService\" xmlns:br1=\"br.com.conductor.RealizeWs.Cartao.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:ConsultarCartoes>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>"+ CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>R2D2</br1:Chapa>\n" +
                "         </br:request>\n" +
                "      </br:ConsultarCartoes>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.CartaoService/CartaoService/ConsultarCartoes\"").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(PROXY_URL_CARTAO).
                then().
                        log().status().
                        assertThat().
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();
        long timeResponsesProxy = proxyResponse.getTime();

        File testDirList = new File("src/test/resources/" + "/consultarCartoes/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/consultarCartoes/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.CartaoService/CartaoService/ConsultarCartoes").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(LEGADO_URL_CARTAO).
                then().
                        log().status().
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();
        long timeResponsesLegado = legadoResponse.getTime();

        FileWriter file2 = new FileWriter("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/consultarCartoes/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLine(pathFileLegado, pathFileProxy);

        assertEquals(statusCodeLegado, statusCodeProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);
        assertEquals(-1, result);
        assertEquals(statusCodeLegado, statusCodeProxy);
    }
}
