package steps;

import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static hooks.ApiHooks.getJson;
import static utils.Configuration.getConfigurationValue;
import static io.restassured.RestAssured.given;

public class Tomato {
    JSONObject baseJO;
    JSONObject responseJO;
    @Когда("^Берём JSONObject из файла (.*)$")
    public void getJO(String fileName) throws IOException {
        getJson(fileName);
        baseJO = new JSONObject(new String(Files.readAllBytes(Paths.get("src/test/resources/" + fileName))));
    }
    @Тогда("^Ставим заданные значения по ключам (.*) и (.*)$")
    public void putJO(String key1, String key2) {
        baseJO.put(key1, getConfigurationValue("key1"));
        baseJO.put(key2, getConfigurationValue("key2"));
    }
    @И("^Отправляем запрос на (.*) и получаем ответ$")
    public void inOutJO(String url) {
        Response response = given()
                .baseUri(getConfigurationValue(url))
                .contentType("application/json;charset=UTF-8")
                .log().all()
                .when()
                .body(baseJO.toString())
                .post(getConfigurationValue("postUri"))
                .then()
                .statusCode(201)
                .log().all()
                .extract().response();
        String tomato = response.getBody().asString();
        responseJO = new JSONObject(response.getBody().asString());
    }
    @И("Делаем проверку")
    public void assertJO() {
        Assertions.assertEquals(responseJO.getString("name"),getConfigurationValue("key1"));
        Assertions.assertEquals(responseJO.getString("job"),getConfigurationValue("key2"));
        Allure.addAttachment("Результат проверки", "Значения по ключам совпадают");
    }
}
