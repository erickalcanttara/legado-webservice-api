package utils;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.config.XmlPathConfig;
import org.apache.http.HttpStatus;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class Verification {
    final String LEGADO_URL_CLIENTE = "http://10.75.30.52:21634/Services/ClienteService.svc";

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
