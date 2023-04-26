package contaService;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.CompareFiles.filesCompareByLine;

public class ConsultaSituacaoTest {

    final String PROXY_URL_CONTA = "http://internal-alb-renner-proxy-511841837.us-east-2.elb.amazonaws.com:21634/Services/ContaService.svc";
    final String LEGADO_URL_CONTA = "http://10.75.30.52:21634/Services/ContaService.svc";

    @DisplayName("Testes Regras de Negócio - StatusCode 200")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/consultaSituacao/consultaSituacaoMassaDeTestes.csv", numLinesToSkip = 1, delimiter = ';')
    public void ConsultaSituacao_SC_OK(String ReferenceTest, String CPF, String id_conta, String statusConta) throws IOException {

        System.out.println("ReferenceTest = " + ReferenceTest + "\nCPF = " + CPF + "\nid_conta = " + id_conta + "\ntatusConta = " + statusConta);

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.ContaService\" xmlns:br1=\"br.com.conductor.RealizeWs.Conta.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:ConsultaSituacao>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>" + CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>123</br1:Chapa>\n" +
                "         </br:request>\n" +
                "      </br:ConsultaSituacao>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.ContaService/ContaService/ConsultaSituacao\"").
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
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();
        long timeResponsesProxy = proxyResponse.getTime();
        String contentTypeProxy = proxyResponse.contentType();

        File testDirList = new File("src/test/resources/" + "/consultaSituacao/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/consultaSituacao/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/consultaSituacao/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/consultaSituacao/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/consultaSituacao/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.ContaService/ContaService/ConsultaSituacao").
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
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();
        long timeResponsesLegado = legadoResponse.getTime();
        String contentTypeLegado = legadoResponse.contentType();

        FileWriter file2 = new FileWriter("src/test/resources/consultaSituacao/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/consultaSituacao/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLine(pathFileLegado, pathFileProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);

        assertEquals(statusCodeLegado, statusCodeProxy, "As chamadas não têm o mesmo StatusCode");
        assertEquals(contentTypeLegado, contentTypeProxy, "As chamadas não têm o mesmo Content-Type");
        assertEquals(-1, result);
    }
}
