package Steps;

import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import io.qameta.allure.Allure;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import static Utils.Configuration.getConfigurationValue;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class Morty {
    JSONObject mortyJson;
    JSONObject episodeJson;
    JSONObject charJson;
    @Когда("Забираем информацию о Морти")
    public void stringmorty() {
        Response response = given()
                .baseUri(getConfigurationValue("baseUri"))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get(getConfigurationValue("mortyUri"))
                .then()
                .statusCode(200)
                .extract().response();
        mortyJson = new JSONObject(response.getBody().asString());
    }
    @Тогда("Находим последний эпизод с Морти и забираем о нем информацию")
    public void episode() {
        JSONArray episodesWithMorty = mortyJson.getJSONArray("episode");
        int episodeWithMorty = episodesWithMorty.length();
        String lastEpisode = episodesWithMorty.getString(episodeWithMorty - 1);
        Allure.addAttachment("Последний эпизод с Морти",
                lastEpisode.substring(lastEpisode.length() - 2));
        Response response1 = given()
                .contentType(ContentType.JSON)
                .get(lastEpisode)
                .then().extract().response();
        episodeJson = new JSONObject(response1.getBody().asString());
    }
    @И("Находим последнего персонажа и забираем о нем информацию")
    public void lastChar() {
        JSONArray allChars = episodeJson.getJSONArray("characters");
        int charCount = allChars.length();
        String lastChar = allChars.getString(charCount - 1);
        Response response2 = given()
                .contentType(ContentType.JSON)
                .get(lastChar)
                .then().extract().response();
        charJson = new JSONObject(response2.getBody().asString());
    }
    @И("Делаем сравнение Морти и последнего персонажа по рассе и локации")
    public void match() {
        String charName = charJson.getString("name");
        String charSpecies = charJson.getString("species");
        String charLocation = charJson.getJSONObject("location").getString("name");
        String Char = "\nПолное имя последнего персонажа — "+charName+
                "\nРасса — "+charSpecies+ "" +
                "\nЛокация — " + charLocation;
        Allure.addAttachment("Информация о персонаже", Char);
        String mortyName = mortyJson.getString("name");
        String mortySpecies = mortyJson.getString("species");
        String mortyLocation = mortyJson.getJSONObject("location").getString("name");
        String morty =  "\nПолное имя главного персонажа — " +mortyName+ "" +
                "\nРасса — " +mortySpecies+
                "\nЛокация — " + mortyLocation;
        Allure.addAttachment("Информация о главном персонаже", morty);
        assertEquals(mortySpecies,charSpecies);
        assertNotEquals(mortyLocation, charLocation);
        String compareResult = "Расы " + mortyName + " и " + charName + " совпадают"
                + "\nЛокации " + mortyName + " и " + charName + " не совпадают";
        Allure.addAttachment("Результат сравнения", compareResult);
    }
}