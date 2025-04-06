package ee.inbank.pas.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;

@UtilityClass
public class FileUtil {

    @SneakyThrows
    public static String readFromFileToString(String filepath) {
        var resource = new ClassPathResource(filepath).getFile();
        var byteArray = Files.readAllBytes(resource.toPath());

        return new String(byteArray);
    }
}
