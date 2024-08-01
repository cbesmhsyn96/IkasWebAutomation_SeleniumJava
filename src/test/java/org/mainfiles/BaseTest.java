package org.mainfiles;

import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.BeforeScenario;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Log;

import java.util.Set;

public class BaseTest extends Log {
    protected static WebDriver driver;
    protected WebDriverWait wait;
    protected String downloadsFilePath = "/Users/huseyinakcan/Downloads";
    private static Set<Cookie> allCookies;
    private static String localStorageData;

    @BeforeScenario
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("profile.default_content_settings.popups=0");
        options.addArguments("--ignore-certifcate-errors");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-gpu");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-plugins");
        options.addArguments("--disable-plugins-discovery");
        // options.addArguments("--disable-preconnect"); // Bu argümanı kaldırın
        options.addArguments("--disable-notifications");
        // options.addArguments("'--dns-prefetch-disable'"); // Bu argümanı kaldırın
        driver = new ChromeDriver(options);

        // Cookies'leri yeniden yükleme
        if (allCookies != null) {
            for (Cookie cookie : allCookies) {
                driver.manage().addCookie(cookie);
            }
        }

        // Local Storage'ı yeniden yükleme
        if (localStorageData != null) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("var items = JSON.parse(arguments[0]); for (var key in items) { window.localStorage.setItem(key, items[key]); }", localStorageData);
        }
    }

    @AfterScenario
    public void tearDown() {
        // Cookies'leri saklama
        allCookies = driver.manage().getCookies();

        // Local Storage'ı saklama
        JavascriptExecutor js = (JavascriptExecutor) driver;
        localStorageData = (String) js.executeScript("return JSON.stringify(window.localStorage);");
        //driver.quit();
    }
}
