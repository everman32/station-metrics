package by.victory.randomgenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class PropertyReader {
    private static final Properties properties = new Properties();

    static {
        var path = Path.of("app.properties");
        try (var is = Files.newInputStream(path)) {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PropertyReader() {
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
