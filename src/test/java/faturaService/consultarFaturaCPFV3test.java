package faturaService;

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
import static utils.CompareFiles.filesCompareByLine_for_FaturaService;

public class consultarFaturaCPFV3test {

    final String PROXY_URL_FATURA = "http://internal-alb-renner-proxy-511841837.us-east-2.elb.amazonaws.com:21634/Services/FaturaService.svc";
    final String LEGADO_URL_FATURA = "http://10.75.30.52:21634/Services/FaturaService.svc";

    @DisplayName("Testes Regras de Negócio - StatusCode 200")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/consultarFaturaCPFv3/consultarFaturaCPFv3MassaDeTestes.csv", numLinesToSkip = 1, delimiter = ';')
    public void ConsultarFaturaCPFV3_Test_SC_OK(String ReferenceTest, String CPF, String id_conta) throws IOException {

        System.out.println("O CPF usado é: " + CPF + " e o id_conta usado é: " + id_conta);

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.FaturaService\" xmlns:br1=\"br.com.conductor.RealizeWs.Fatura.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:ConsultarFaturasCPFV3>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>" + CPF + "</br1:CPF>\n" +
                "         </br:request>\n" +
                "      </br:ConsultarFaturasCPFV3>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.FaturaService/FaturaService/ConsultarFaturasCPFV3\"").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(PROXY_URL_FATURA).
                then().
                        log().status().
                        assertThat().
                        statusCode(HttpStatus.SC_OK).
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();
        long timeResponsesProxy = proxyResponse.getTime();
        String contentTypeProxy = proxyResponse.contentType();

        File testDirList = new File("src/test/resources/" + "/consultarFaturasCPFV3/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/consultarFaturasCPFV3/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/consultarFaturasCPFV3/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/consultarFaturasCPFV3/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/consultarFaturasCPFV3/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.FaturaService/FaturaService/ConsultarFaturasCPFV3").
                        accept("*/*").
                        body(requestBody).
                        log().method().
                        log().uri().
                        log().headers().
                        log().body().
                when().
                        post(LEGADO_URL_FATURA).
                then().
                        log().status().
                        statusCode(HttpStatus.SC_OK).
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();
        long timeResponsesLegado = legadoResponse.getTime();
        String contentTypeLegado = legadoResponse.contentType();

        FileWriter file2 = new FileWriter("src/test/resources/consultarFaturasCPFV3/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/consultarFaturasCPFV3/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        System.out.println("A primeira comparação se dá entre Legado e Proxy: \n");
        long result = filesCompareByLine_for_FaturaService(pathFileLegado, pathFileProxy);
        System.out.println("A segunda comparação se dá entre Proxy e Legado: \n");
        long result2 = filesCompareByLine_for_FaturaService(pathFileProxy, pathFileLegado);

        // Para as duas listas retornadas, não se deve ter nenhum dado nelas, pois foram
        // excluídos com a comparação
        // Com isso o resultado esperado é zero no assert.
        long resultadoFinal = result + result2;

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);

        assertEquals(statusCodeLegado, statusCodeProxy, "As chamadas não têm o mesmo StatusCode");
        assertEquals(contentTypeLegado, contentTypeProxy, "As chamadas não têm o mesmo Content-Type");
        assertEquals(0, resultadoFinal, "Os arquivos não têm o mesmo conteúdo");
    }

}
