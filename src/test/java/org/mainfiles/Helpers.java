package org.mainfiles;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.fasterxml.jackson.databind.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import utils.Log;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.*;

public class Helpers extends BaseTest {
    Log logger = new Log();
    protected By getLocator(String key) {
        String directoryPath = "src/test/resources";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<File> jsonFiles = getJsonFiles(directoryPath);
            for (File file : jsonFiles) {
                URL resource = Helpers.class.getClassLoader().getResource(file.getName());
                if (resource == null) {
                    System.out.println("Resource not found: " + file.getName());
                    continue;
                }
                byte[] bytes = Files.readAllBytes(Paths.get(resource.toURI()));
                String json = new String(bytes);
                JsonNode jsonNode = objectMapper.readTree(json);
                if (jsonNode.isArray()) {
                    for (JsonNode node : jsonNode) {
                        if (key.equals(node.get("key").asText())) {
                            String type = node.get("type").asText();
                            String value = node.get("value").asText();
                            switch (type) {
                                case "name":
                                    return By.name(value);
                                case "id":
                                    return By.id(value);
                                case "css":
                                    return By.cssSelector(value);
                                case "class":
                                    return By.className(value);
                                case "xpath":
                                    return By.xpath(value);
                                case "link-text":
                                    return By.linkText(value);
                                default:
                                    System.out.println("Unknown locator type: " + type);
                            }
                        }
                    }
                } else {
                    System.out.println("JSON is not an array: " + json);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static List<File> getJsonFiles(String directoryPath) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }

    protected static List<String> findFile(String fileName, String startDir) throws IOException {
        List<String> result = new ArrayList<>();
        Files.walkFileTree(Paths.get(startDir), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.getFileName().toString().equalsIgnoreCase(fileName)) {
                    result.add(file.toAbsolutePath().toString());
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return result;
    }

    protected WebElement findElement(String key){
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        return wait.until(ExpectedConditions.presenceOfElementLocated(getLocator(key)));
    }

    protected List<WebElement> findElements(String key){
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        List<WebElement> elements = driver.findElements(getLocator(key));
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(getLocator(key)));
    }

    public String getTOTPCode(SecretKey secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();
        Instant now = Instant.now();
        return String.format("%06d", totp.generateOneTimePassword(secretKey, now));
    }


}
