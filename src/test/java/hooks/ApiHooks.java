package hooks;

import io.qameta.allure.Attachment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ApiHooks {

    @Attachment(value = "Читаемый Json файл", type = "application/json", fileExtension = ".txt")
    public static byte[] getJson(String resourceName) throws IOException {
        return Files.readAllBytes(Paths.get("src/test/resources", resourceName));
    }

}