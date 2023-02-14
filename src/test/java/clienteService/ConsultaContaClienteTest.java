package clienteService;


import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.config.XmlPathConfig;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsultaContaClienteTest {

    final String PROXY_URL_CLIENTE = "http://internal-alb-renner-proxy-1897409209.us-east-2.elb.amazonaws.com:21634/Services/ClienteService.svc";
    final String LEGADO_URL_CLIENTE = "http://10.75.30.52:21634/Services/ClienteService.svc";

    @DisplayName("Testes Regras de Negócio - StatusCode 200")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/consultaContaCliente/consultaContaClienteMassaDeTestes.csv", numLinesToSkip = 1, delimiter = ';')
    public void ConsultaContaClienteTest_SC_OK(String ReferenceTest, String CPF, String Chapa, String id_conta, String descricaoStatus) throws IOException, ParserConfigurationException, SAXException {

        String statusAtual = verificaStatusConta(ReferenceTest, CPF, Chapa, descricaoStatus);

        if(!descricaoStatus.equals(statusAtual)) {
            System.out.println("O Status deveria ser " + descricaoStatus + ", mas o status atual da conta é: " + statusAtual);
        }
        assertEquals(descricaoStatus, statusAtual);

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
                        statusCode(HttpStatus.SC_OK).
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
                        statusCode(HttpStatus.SC_OK).
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

        System.out.println("O result é: " + result);
        if (result == -1){
            System.out.println("Os arquivos têm o mesmo conteúdo.");
        } else {
            System.out.println("Os arquivos NÃO têm o mesmo conteúdo");
            System.out.println("A linha com a primeira diferença é: " + result);
        }

        assertEquals(statusCodeLegado, statusCodeProxy);
        assertEquals(-1, result);

        System.out.println("O tempo de respota do Proxy é: " + timeResponsesProxy + "\n" + "O tempo de resposta do Legado é: " + timeResponsesLegado);

    }



    @DisplayName("Testes Regras de Negócio - StatusCode 500")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/buscarDadosAlteracaoLimites/buscarDadosMassaDeTestes_SC_ERR.csv", numLinesToSkip = 1, delimiter = ';')
    public void ConsultaContaCliente_SC_INT_ERR(String ReferenceTest, String CPF, String Chapa) throws IOException {

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

        FileWriter file2 = new FileWriter("src/test/resources/consultaContaCliente/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/consultaContaCliente/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

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

    private String verificaStatusConta(String referenceTest, String CPF, String Chapa, String descricaoStatus) throws ParserConfigurationException, SAXException, IOException {

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

        String response =
                given().
                        header("Content-Type", "text/xml; charset=utf-8").
                        header("SOAPAction", "\"br.com.conductor.RealizeWs.ClienteService/ClienteService/ConsultaContaCliente\"").
                        accept("*/*").
                        body(requestBody).
                when().
                        post(LEGADO_URL_CLIENTE).
                then().
                        log().status().
                        assertThat().
                        statusCode(HttpStatus.SC_OK).
                        extract().
                        response().prettyPrint();

        XmlPath xmlPath = new XmlPath(response).using(new XmlPathConfig("UTF-8"));

        String statusContaAtual = xmlPath.get("Envelope.Body.ConsultaContaClienteResponse.ConsultaContaClienteResult.StatusContaDescricao").toString();

        if (descricaoStatus.equals(statusContaAtual)){
            System.out.println("O statusConta continua o mesmo!");
        }else {
            System.out.println("Refaça a massa de teste, pois o status conta não é o mesmo.\nA referência do teste no Zephyr Scale é: " + referenceTest + "\n");

            System.out.println("Utilize a Query abaixo para auxiliar a busca de massa de testes na base 52 da Renner\n");

            System.out.println("SELECT top 50 CO.Status, pf.CPF, * from contas CO \n" +
                    "INNER JOIN PessoasFisicas pf \n" +
                    "ON CO.Id_Pessoa = pf.Id_PessoaFisica \n" +
                    "where CO.DataCadastramento < '2020-01-30 12:18:00.000' -- limita a uma conta mais antiga\n" +
                    "and CO.Id_Pessoa <> 5128773 -- retira o id_pessoa asssocioado ao NoName\n" +
                    "and CO.Status = 3 -- seleciona o statusConta desejado\n" +
                    "and CO.id_conta in (select id_conta from cartoes) order by Id_Conta desc;\n");
        }
        return statusContaAtual;
    }

}
