package faturaService;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.CompareFiles.filesCompareByLine;
import static utils.CompareFiles.filesCompareByLine_for_FaturaService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BuscarParcelamentoFaturaCompletotest {

    final String PROXY_URL_FATURA = "http://internal-alb-renner-proxy-511841837.us-east-2.elb.amazonaws.com:21634/Services/FaturaService.svc";
    final String LEGADO_URL_FATURA = "http://10.75.30.52:21634/Services/FaturaService.svc";

    @DisplayName("Testes Regras de Negócio - StatusCode 200")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/buscarParcelamentoFaturaCompleto/buscarParcelamentoFaturaCompletoMassaDeDados7.csv", numLinesToSkip = 1, delimiter = ';')
    public void BuscarParcelamentoFaturaCompleto_Test_SC_OK(String ReferenceTest, String CPF, String dataVencimento, String id_conta, int statusConta,
                                                                String regraCamapanha, String statusAdesao) throws IOException {

        System.out.println("CPF: " + CPF + "\ndataVencimento é: " + dataVencimento + "\n" +
                "id_conta usado é: " + id_conta + "\nStatusConta: " + statusConta + "\nregraCampanha: " + regraCamapanha + "\nStatusAdesao: " + statusAdesao);

        String[] dataVencimentoFormatada = dataVencimento.split(" ");

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.FaturaService\" xmlns:br1=\"br.com.conductor.RealizeWs.Fatura.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:BuscarParcelamentoFaturaCompleto>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>" + CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>123</br1:Chapa>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:DataVencimento>" + dataVencimentoFormatada[0] + "T" + dataVencimentoFormatada[1]+ "</br1:DataVencimento>\n" +
                "         </br:request>\n" +
                "      </br:BuscarParcelamentoFaturaCompleto>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.FaturaService/FaturaService/BuscarParcelamentoFaturaCompleto\"").
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
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();
        long timeResponsesProxy = proxyResponse.getTime();
        String contentTypeProxy = proxyResponse.contentType();

        File testDirList = new File("src/test/resources/" + "/buscarParcelamentoFaturaCompleto/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.FaturaService/FaturaService/BuscarParcelamentoFaturaCompleto").
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
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();
        long timeResponsesLegado = legadoResponse.getTime();
        String contentTypeLegado = legadoResponse.contentType();

        FileWriter file2 = new FileWriter("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        //long result = filesCompareByLine(pathFileLegado, pathFileProxy);
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

    @Order(1)
    @DisplayName("Testes para CPF Vazio e Nulo")
    @ParameterizedTest
    @NullAndEmptySource
    public void BuscarParcelamentoFaturaCompleto_Estrutural(String CPF) throws IOException {

        String ReferenceTest = "CPF_VAZIO";
        if (CPF == null){
            ReferenceTest = "CPF_NULL";
        }

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.FaturaService\" xmlns:br1=\"br.com.conductor.RealizeWs.Fatura.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:BuscarParcelamentoFaturaCompleto>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>" + CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>123</br1:Chapa>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:DataVencimento>2020-01-10T00:00:00</br1:DataVencimento>\n" +
                "         </br:request>\n" +
                "      </br:BuscarParcelamentoFaturaCompleto>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.FaturaService/FaturaService/BuscarParcelamentoFaturaCompleto\"").
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
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();

        File testDirList = new File("src/test/resources/" + "/buscarParcelamentoFaturaCompleto/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.FaturaService/FaturaService/BuscarParcelamentoFaturaCompleto").
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
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();

        FileWriter file2 = new FileWriter("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLine_for_FaturaService(pathFileLegado, pathFileProxy);

        assertEquals(statusCodeLegado, statusCodeProxy);
        assertEquals(-1, result);
    }

    @Order(2)
    @DisplayName("Testes para DataVencimento Vazia ou Nula")
    @ParameterizedTest
    @NullAndEmptySource
    public void BuscarParcelamentoFaturaCompleto_Estrutural_DataVencimento(String dataVencimento) throws IOException {

        String ReferenceTest = "DataV_VAZIO";
        if (dataVencimento == null){
            ReferenceTest = "DataV_NULL";
        }

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.FaturaService\" xmlns:br1=\"br.com.conductor.RealizeWs.Fatura.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:BuscarParcelamentoFaturaCompleto>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>05123480811</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>123</br1:Chapa>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:DataVencimento>" + dataVencimento + "</br1:DataVencimento>\n" +
                "         </br:request>\n" +
                "      </br:BuscarParcelamentoFaturaCompleto>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.FaturaService/FaturaService/BuscarParcelamentoFaturaCompleto\"").
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
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();

        File testDirList = new File("src/test/resources/" + "/buscarParcelamentoFaturaCompleto/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.FaturaService/FaturaService/BuscarParcelamentoFaturaCompleto").
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
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();

        FileWriter file2 = new FileWriter("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLine_for_FaturaService(pathFileLegado, pathFileProxy);

        assertEquals(statusCodeLegado, statusCodeProxy);
        assertEquals(-1, result);
    }

    @Order(3)
    @DisplayName("Testes para CPF Inválido")
    @ParameterizedTest
    @ValueSource(strings = {"5531762607", "553176260722", "5531762607.", "5s317626072"})
    public void BuscarParcelamentoFaturaCompleto_CPF_Invalido(String CPF) throws IOException {

        String ReferenceTest = "CPF_invalido";

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.FaturaService\" xmlns:br1=\"br.com.conductor.RealizeWs.Fatura.Contracts\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <br:BuscarParcelamentoFaturaCompleto>\n" +
                "         <!--Optional:-->\n" +
                "         <br:request>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:CPF>"+ CPF + "</br1:CPF>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Chapa>123</br1:Chapa>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:DataVencimento>2020-04-10T00:00:00</br1:DataVencimento>\n" +
                "         </br:request>\n" +
                "      </br:BuscarParcelamentoFaturaCompleto>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Response proxyResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.FaturaService/FaturaService/BuscarParcelamentoFaturaCompleto\"").
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
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeProxy = proxyResponse.statusCode();

        File testDirList = new File("src/test/resources/" + "/buscarParcelamentoFaturaCompleto/");
        if (!testDirList.exists()){
            testDirList.mkdirs();
        }

        File testDir = new File("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/");
        if (!testDir.exists()){
            testDir.mkdirs();
        }

        FileWriter file = new FileWriter("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest + "-in.xml");
        file.write(proxyResponse.prettyPrint());
        file.flush();
        file.close();

        FileWriter fileRq = new FileWriter("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-rq.xml");
        fileRq.write(requestBody);
        fileRq.flush();
        fileRq.close();

        Path pathFileProxy = Paths.get("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-in.xml");

        Response legadoResponse =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "br.com.conductor.RealizeWs.FaturaService/FaturaService/BuscarParcelamentoFaturaCompleto").
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
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                        extract().
                        response();

        int statusCodeLegado = legadoResponse.statusCode();

        FileWriter file2 = new FileWriter("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/buscarParcelamentoFaturaCompleto/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        long result = filesCompareByLine_for_FaturaService(pathFileLegado, pathFileProxy);

        assertEquals(statusCodeLegado, statusCodeProxy);
        assertEquals(-1, result);

    }
}
