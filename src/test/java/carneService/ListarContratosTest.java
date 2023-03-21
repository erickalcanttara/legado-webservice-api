package carneService;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

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
public class ListarContratosTest {

    final String PROXY_URL_CARNE = "http://internal-alb-renner-proxy-511841837.us-east-2.elb.amazonaws.com:21634/Services/CarneService.svc";
    final String LEGADO_URL_CARNE = "http://10.75.30.52:21634/Services/CarneService.svc";

    @Order(4)
    @DisplayName("Testes Regras de Negócio - StatusCode 200")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/listarcontratos/listarContratosMassaDeTestes.csv", delimiter = ';')
    public void ListarContratosTest_SC_OK(String ReferenceTest, String CPF, String Chapa, String Contrato, String FiltroDataFim, String FiltroDataInicio,
                                    String Produto, String StatusContrato) throws IOException {

        String[] dataFim = FiltroDataFim.split(" ");
        String[] dataInicio = FiltroDataInicio.split(" ");

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
                "            <br1:Chapa>"+ Chapa + "</br1:Chapa>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Contrato>" + Contrato + "</br1:Contrato>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:FiltroDataFim>" + dataFim[0] + "</br1:FiltroDataFim>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:FiltroDataInicio>" + dataInicio[0] + "</br1:FiltroDataInicio>\n" +
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
        long timeResponsesProxy = proxyResponse.getTime();

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
        long timeResponsesLegado = legadoResponse.getTime();

        FileWriter file2 = new FileWriter("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        filesCompareByLine(pathFileLegado, pathFileProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);

        assertEquals(statusCodeLegado, statusCodeProxy);

    }

    @Order(3)
    @DisplayName("Testes Regras de Negócio - StatusCode 500")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/listarcontratos/listarContratosMassaDeTestes_SC_ERR.csv", numLinesToSkip = 1, delimiter = ';')
    public void ListarContratosTest_SC_INT_ERR(String ReferenceTest, String CPF, String Chapa, String Contrato, String FiltroDataFim, String FiltroDataInicio,
                                          String Produto, String StatusContrato) throws IOException {

        String[] dataFim = FiltroDataFim.split(" ");
        String[] dataInicio = FiltroDataInicio.split(" ");

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
                "            <br1:Chapa>"+ Chapa + "</br1:Chapa>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:Contrato>" + Contrato + "</br1:Contrato>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:FiltroDataFim>" + dataFim[0] + "</br1:FiltroDataFim>\n" +
                "            <!--Optional:-->\n" +
                "            <br1:FiltroDataInicio>" + dataInicio[0] + "</br1:FiltroDataInicio>\n" +
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
        long timeResponsesProxy = proxyResponse.getTime();

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
        long timeResponsesLegado = legadoResponse.getTime();

        FileWriter file2 = new FileWriter("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        filesCompareByLine(pathFileLegado, pathFileProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);

        assertEquals(statusCodeLegado, statusCodeProxy);
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

        String requestBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.CarneService\" xmlns:br1=\"br.com.conductor.RealizeWs.Carne.Contracts\">\n" +
                "  <soapenv:Header/>\n" +
                "  <soapenv:Body>\n" +
                "    <br:ListarContratos>\n" +
                "      <br:request>\n" +
                "        <br1:CPF>" + CPF + "</br1:CPF>\n" +
                "        <br1:Chapa>1234</br1:Chapa>\n" +
                "        <br1:Contrato>2759059916511</br1:Contrato>\n" +
                "        <br1:FiltroDataFim>2024-02-16</br1:FiltroDataFim>\n" +
                "        <br1:FiltroDataInicio>2021-02-16</br1:FiltroDataInicio>\n" +
                "        <br1:Produto>COMPRA</br1:Produto>\n" +
                "        <br1:StatusContrato>Liquidado</br1:StatusContrato>\n" +
                "      </br:request>\n" +
                "    </br:ListarContratos>\n" +
                "  </soapenv:Body>\n" +
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
        long timeResponsesProxy = proxyResponse.getTime();

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
        long timeResponsesLegado = legadoResponse.getTime();

        FileWriter file2 = new FileWriter("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        filesCompareByLine(pathFileLegado, pathFileProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);

        assertEquals(statusCodeLegado, statusCodeProxy);
    }

    @Order(2)
    @DisplayName("Testes para Numero Contrato Nulo e Vazio")
    @ParameterizedTest
    @NullAndEmptySource
    public void ListarContratosTest_SC_INT_ERR_TESTE_CONTRATO_VAZIO_NULL(String NumeroContrato) throws IOException {

        String ReferenceTest = "ER_NUM_CONTRATO_VAZIO";
        if (NumeroContrato == null ){
            ReferenceTest = "ER_NUM_CONTRATO_NULL";
        }

        String requestBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:br=\"br.com.conductor.RealizeWs.CarneService\" xmlns:br1=\"br.com.conductor.RealizeWs.Carne.Contracts\">\n" +
                "  <soapenv:Header/>\n" +
                "  <soapenv:Body>\n" +
                "    <br:ListarContratos>\n" +
                "      <br:request>\n" +
                "        <br1:CPF>19379579268</br1:CPF>\n" +
                "        <br1:Chapa>1234</br1:Chapa>\n" +
                "        <br1:Contrato>" + NumeroContrato + "</br1:Contrato>\n" +
                "        <br1:FiltroDataFim>2024-02-16</br1:FiltroDataFim>\n" +
                "        <br1:FiltroDataInicio>2021-02-16</br1:FiltroDataInicio>\n" +
                "        <br1:Produto>COMPRA</br1:Produto>\n" +
                "        <br1:StatusContrato>Liquidado</br1:StatusContrato>\n" +
                "      </br:request>\n" +
                "    </br:ListarContratos>\n" +
                "  </soapenv:Body>\n" +
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
        long timeResponsesProxy = proxyResponse.getTime();

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
        long timeResponsesLegado = legadoResponse.getTime();

        FileWriter file2 = new FileWriter("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/listarContratos/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

        filesCompareByLine(pathFileLegado, pathFileProxy);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);

        assertEquals(statusCodeLegado, statusCodeProxy);
    }

}
