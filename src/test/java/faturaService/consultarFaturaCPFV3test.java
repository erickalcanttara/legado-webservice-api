package faturaService;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class consultarFaturaCPFV3test {

    final String PROXY_URL_FATURA = "http://internal-alb-renner-proxy-1897409209.us-east-2.elb.amazonaws.com:21634/Services/FaturaService.svc";
    final String LEGADO_URL_FATURA = "http://10.75.30.52:21634/Services/FaturaService.svc";

    @DisplayName("Testes Regras de Negócio - StatusCode 200")
    @ParameterizedTest
    @CsvFileSource(resources = "/massaDeTestes/consultarFaturaCPFv3/consultarFaturaCPFv3MassaDeTestes.csv", numLinesToSkip = 1, delimiter = ';')
    public void ConsultarFaturaCPFV3_Test_SC_OK(String ReferenceTest, String CPF, String id_conta) throws IOException {


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

        FileWriter file2 = new FileWriter("src/test/resources/consultarFaturasCPFV3/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");
        file2.write(legadoResponse.prettyPrint());
        file2.flush();
        file2.close();

        Path pathFileLegado = Paths.get("src/test/resources/consultarFaturasCPFV3/" + ReferenceTest + "/" + ReferenceTest +  "-ws.xml");

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

    /*
        Se todas as linhas forem idênticas para ambos os arquivos, retorna-se -1L,
        mas se houver discrepância, retorna-se o número da linha onde foi encontrada a primeira incompatibilidade.
        Se os arquivos forem de tamanhos diferentes,
        mas o arquivo menor corresponder às linhas correspondentes do arquivo maior, ele retornará o número de linhas do arquivo menor.
     */
    public static long filesCompareByLine(Path path1, Path path2) throws IOException {
        try (BufferedReader bf1 = Files.newBufferedReader(path1);
             BufferedReader bf2 = Files.newBufferedReader(path2)) {

            System.out.println(bf1.getClass());

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
