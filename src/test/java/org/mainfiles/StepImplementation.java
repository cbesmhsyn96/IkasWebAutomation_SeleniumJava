package org.mainfiles;
import com.google.common.io.BaseEncoding;
import com.thoughtworks.gauge.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Log;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class StepImplementation extends Helpers{
    private static final Logger log = LoggerFactory.getLogger(StepImplementation.class);
    String[] months = {"Jenuary", "February", "March", "April","May","June","July","August","September","October","November","December"};
    Log logger = new Log();
    private Map<String, String> storedTexts = new HashMap<>();
    private static String currentURL;
    private static long startTime;
    private static long endTime;
    //private static long pageLoadTime = endTime - startTime;
    private Random random = new Random();
    private Map<String, Integer> monthList = new HashMap<>();
    private JavascriptExecutor js = (JavascriptExecutor)driver;

    private Map<String,Integer> getMonthList(String[] months){
        int indis = 0;
        while (indis<months.length){
            monthList.put(months[indis],indis);
            indis++;
        }
        return monthList;
    }

    @Step("<url> adresine git")
    public void goToURL(String url){
        currentURL = url;
        startTime = System.currentTimeMillis();
        driver.get(url);
        endTime = System.currentTimeMillis();
        driver.manage().window().maximize();
        logger.info(url+" adresine gidildi.");
    }
    @Step("<key> keyli element yok mu kontrol et")
    public void doesntExistElement(String key){
        Boolean isntExistElement = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(100))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.invisibilityOf(driver.findElement(getLocator(key))));
        if(isntExistElement){
           logger.info(key+" keyli element yok.");
        }else{
            Assertions.assertTrue(findElement(key).isDisplayed());
        }
    }
    @Step("<key> keyli elementi var mı kontrol et")
    public void isExistElement(String key){
        WebElement element = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(50))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.presenceOfElementLocated(getLocator(key)));
        if (element.isDisplayed()){
            logger.info("'"+key+"' keyli element görüldü.");
        }else{
            assertTrue(element.isDisplayed(),"'"+key+"' keyli element görülmedi.");
        }
    }

    @Step("<key> keyli elementi var mı kontrol et yoksa devam et")
    public void isntExistElementContinue(String key){
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(50))
                .ignoring(NoSuchElementException.class);
        WebElement element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(getLocator(key))));
        if (element.isDisplayed()){
            logger.info("'"+key+"' keyli element görüldü.");
        }else{
            logger.info(key+" keyli element görülmedi. Devam ediliyor.");
        }
    }

    @Step("<key> keyli elemente kaydır")
    public void scrollToElementFromThePage(String key){
        WebElement footer = findElement(key);
        int deltaY = footer.getRect().y;
        new Actions(driver)
                .scrollByAmount(0, deltaY)
                .perform();
        logger.info(key+" keyli elemente kaydırıldı.");
    }

    @Step("Sayfanın en aşağısına kaydır")
    public void scrollToEndOfPage(){
        js.executeScript("window.scrollBy(0, document.body.scrollHeight)");
        logger.info("Sayfanın en altına kaydırıldı.");
    }

    @Step("Şuanki URL' in <url> olduğunu kontrol et")
    public void checkCurrentURL(String url){
        assertEquals(url,driver.getCurrentUrl(),
                "Expected URL : "+url
                         +"Actual URL : "+ driver.getCurrentUrl());
        logger.info("Şuanki URL' in "+url+" olduğu kontrol edildi.");
    }
    @Step("<key> keyli elemente <text> değerini yaz")
    public void sendKeyToElement(String key,String text){
        try {
            WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(getLocator(key))).sendKeys(text);
        }catch (ElementNotInteractableException e){

        }

        logger.info("'"+key+"' keyli elemente "+text+" texti yazıldı.");
    }

    @Step("<key> keyli elemente <text> değerini yaz ve <storedKey> ile hafızada sakla")
    public void sendKeyToElementandStore(String key,String text,String storedKey){
        try {
            WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(10));
            wait.until(ExpectedConditions.elementToBeClickable(getLocator(key))).sendKeys(text);
            storedTexts.put(storedKey,text);
        }catch (ElementNotInteractableException e){

        }
        logger.info("'"+key+"' keyli elemente "+text+" texti yazıldı ve "+storedKey+" keyi ile hafıdada saklandı.");
    }

    @Step("<key> keyli listede <indis> indisli eleman hafızadaki <storedKey> ile aynı mı kontrol et")
    public void sendKeyToElementandStore(String key,int indis,String storedKey){
            if (storedTexts.get(storedKey).equals(findElements(key).get(indis).getText())){
                logger.info("'"+key+"' keyli listede "+indis+
                        " indisli eleman hafızadaki '"+storedTexts.get(storedKey)+"' değeri ile aynı.");
            }
            assertEquals(storedTexts.get(storedKey),findElements(key).get(indis).getText());
    }

    @Step("<key> keyli listeden <index> indisli elemana tıkla")
    public void clickElementByIndex(String key,int index){
        List<WebElement> elements = findElements(key);
        elements.get(index).click();
        logger.info(key+" keyli listeden"+ index+" indexli elemana tıklandı.");
    }

    @Step("<key> keyli listede <index> indisli elemanın texti <text> mi kontrol et")
    public void verifyTextElementByIndex(String key,int index, String text){
        if (findElements(key).get(index).getText().equals(text)){
            logger.info(key+" keyli listenin '"+index+"' indexli elementinin textinin '"+text+"' olduğu görüldü.");
        }else{
            Assertions.assertTrue(findElements(key).get(index).getText().equals(text));
        }
    }

    @Step("<key> keyli listeden rastgele bir elemana tıkla")
    public void clickElementByRandomIndex(String key){
        int randIndex = random.nextInt(findElements(key).size());
        findElements(key).get(randIndex).click();
        logger.info(key+" keyli listeden '"+ randIndex+ "' indexli olana tıklandı.");
    }


    @Step("<key> keyli elemente ENTER keyi yolla")
    public void sendKeyToElement(String key){
        findElement(key).sendKeys(Keys.ENTER);
        logger.info("'"+key+"' keyli elemente ENTER keyi yollandı.");
    }

    @Step("<key> keyli elementin textinin <text> olduğunu kontrol et")
    public void compareExpTextAndAcText(String key,String text){
        assertTrue(findElement(key).getText()==text,
                "'"+key+"' keyli elementin beklenen texti : "+text+
                        "'"+key+"' keyli elementin görülen texti : "+findElement(key).getText());
        logger.info("'"+key+"' keyli elementin textinin "+text+" olduğu kontrol edildi.");
    }

    @Step("<key> keyli elementin texti <text> içeriyor mu kontrol et")
    public void compareExpContaisTextAndAcText(String key,String text){
        assertTrue(findElement(key).getText().contains(text),
                     "'"+key+"' keyli elementin texti '"+text+"' içermiyor.");
        logger.info("'"+key+"' keyli elementin texti '"+text+"' içeriyor.");
    }

    @Step("<key> keyli listede texti <text> olan element var mı kontrol et")
    public void textIsEqualElementList(String key,String text){
        int i = 0;
        for (WebElement element : findElements(key)){
            i++;
            if (element.getText().equals(text)){
                logger.info("'"+key+" keyli listede texti '"+text+"' olan '"+i+"' indisli element var.");
                break;
            }else{
                if (i==findElements(key).size()){
                    assertSame(text,element.getText());
                }
            }
        }

    }

    @Step("<key> keyli listede texti <text> olan elemente tıkla")
    public void clickTextEqualElementList(String key,String text){
        int i = 0;
        for (WebElement element : findElements(key)){
            i++;
            if (element.getText().equals(text)){
                element.click();
                logger.info("'"+key+" keyli listede texti '"+text+"' olan elemente tıklandı.");
                break;
            }else{
                if (i==findElements(key).size()){
                    assertSame(text,element.getText());
                }
            }
        }

    }

    @Step("<key> keyli listede <elementCount> adet eleman var mı kontrol et")
    public void clickTextEqualElementList(String key,int elementCount){
        if (findElements(key).size() == elementCount){
            logger.info(key+" keyli listede "+ elementCount+ " adet eleman olduğu görüldü.");
        }else{
            logger.info(findElements(key).size()+" tene eleman var");
            Assertions.assertTrue(findElements(key).size() == elementCount);
        }
    }


    @Step("<key> keyli elemente tıkla")
    public void clickElement(String key){
        findElement(key).click();
        logger.info("'"+key+"' keyli elemente tıklandı.");
    }

    @Step("<key> keyli element varsa tıkla")
    public void clickElementIfExist(String key){
        if (findElement(key).isDisplayed()){
            findElement(key).click();
            logger.info("'"+key+"' keyli elemente tıklandı.");
        }
    }

    @Step("<key> keyli elemente js ile tıkla")
    public void clickElementWithJS(String key){
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("arguments[0].click();", findElement(key));
        logger.info("'"+key+"' keyli elemente js ile tıklandı.");
    }

    @Step("<saniye> saniye bekle")
    public void waitBySec(int saniye) throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(saniye));
        logger.info("'"+saniye+"' saniye beklendi.");
    }
    @Step("<ms> ms bekle")
    public void waitByMS(int ms) throws InterruptedException {
        Thread.sleep(Duration.ofMillis(ms));
        logger.info(ms+" saniye beklendi.");
    }
    @Step("Sayfa başlığı <title> mı kontrol et")
    public void pageControll(String title){
        assertEquals(driver.getTitle(),title,"Sayfa başlığı "+title+" ile aynı değil.");
    }
    @Step("<key> keyli elementin görünürlüğünü kontrol et")
    public void verifyVisiblityOfElement(String key){
        assertTrue(findElement(key).isDisplayed(),"'"+key+"' keyli element sayfada görünür değil");
    }
    @Step("<key> keyli elementin görünür olmadığını kontrol et")
    public void verifyNoVisiblityOfElement(String key){
        assertFalse(findElement(key).isDisplayed(),"'"+key+"' keyli element sayfada görünür değil");
    }
    @Step("<key> keyli elementinin enable olduğunu kontrol et")
    public void verifyEnabilityOfElement(String key){
        assertTrue(findElement(key).isEnabled(),"'"+key+"' keyli element enable değil");
    }
    @Step("<key> keyli elementinin enable olmadığını kontrol et")
    public void verifyNotEnabilityOfElement(String key){
        assertFalse(findElement(key).isEnabled(),"'"+key+"' keyli element enable.");
    }
    @Step("<key> keyli select menüsünden <indeks> indeksli öğeyi seç")
    public void selectByIndexFromSelect(String key,int indeks){
        Select listbox = new Select(findElement(key));
        listbox.selectByIndex(indeks);
        logger.info("'"+key+"' keyli dropdowndan '"+indeks+"' indeksli öğe seçildi.");
    }
    @Step("<key> keyli select menüsünden <value> valuelu öğeyi seç")
    public void selectByValueFromSelect(String key,String value){
        Select listbox = new Select(findElement(key));
        listbox.selectByValue(value);
        logger.info("'"+key+"' keyli dropdowndan '"+value+"' valuelu öğe seçildi.");
    }
    @Step("<key> keyli select menüsünden <visibleText> yazan öğeyi seç")
    public void selectByVisibleTextFromSelect(String key,String visibleText){
        Select listbox = new Select(findElement(key));
        listbox.selectByVisibleText(visibleText);
        logger.info("'"+key+"' keyli dropdowndan '"+visibleText+"' yazan öğe seçildi.");
    }
    @Step("<key> keyli select menüsünden <indeks> indeksli öğenin seçimini kaldır")
    public void deselectByIndexFromSelect(String key,int indeks){
        Select listbox = new Select(findElement(key));
        listbox.deselectByIndex(indeks);
        logger.info("'"+key+"' keyli dropdowndan '"+indeks+"' indeksli öğenin seçimi kaldırıldı.");
    }
    @Step("<key> keyli select menüsünden <value> valuelu öğenin seçimini kaldır")
    public void deselectByValueFromSelect(String key,String value){
        WebElement dropdown = findElement(key);
        Select listbox = new Select(dropdown);
        listbox.deselectByValue(value);
        logger.info("'"+key+"' keyli dropdowndan '"+value+"' valuelu öğenin seçimi kaldırıldı.");
    }
    @Step("<key> keyli select menüsünden <visibleText> yazan öğenin seçimini kaldır")
    public void deselectByVisibleTextFromSelect(String key,String visibleText){
        Select listbox = new Select(findElement(key));
        listbox.deselectByVisibleText(visibleText);
        logger.info("'"+key+"' keyli dropdowndan '"+visibleText+"' yazan öğenin seçimi kaldırıldı.");
    }
    @Step("<key> keyli checkboxı seç")
    public void clickCheckbox(String key){
        findElement(key).click();
        logger.info("'"+key+"' keyli checkbox seçildi.");
    }
    @Step("<key> keyli element seçili mi kontrol et")
    public void isSelectedCheckbox(String key){
        assertTrue(findElement(key).isSelected(),"'"+key+"' keyli element seçili değil.");
        logger.info("'"+key+"' keyli elementin seçili olduğu kontrol edildi.");
    }
    @Step("<key> keyli element seçili değil mi kontrol et")
    public void isNotSelectedCheckbox(String key){
        assertFalse(findElement(key).isSelected(),"'"+key+"' keyli element seçili.");
        logger.info("'"+key+"' keyli elementin seçili olmadığı kontrol edildi.");
    }
    @Step("<storedKey> keyli elementin textini hafızada sakla")
    public void saveTextByValueElement(String storedKey){
        logger.info("'"+storedKey+"' keyli keyli elementin texti hafızada saklanıyor.");
        storedTexts.put(storedKey, findElement(storedKey).getText());
    }
    @Step("<key> keyli elementin texti hafızadaki <storedKey> stored keyli elementin textini içeriyor mu kontrol et")
    public void compareEqualSavedTextCrrText(String key, String storedKey) {
        if (findElement(key).getText().contains(storedTexts.get(storedKey))){
            logger.info("'"+key+"' keyli elementin texti hafızadaki "+storedKey+" stored keyli elementin textini içeriyor");
        }else{
            assertTrue(findElement(key).getText().contains(storedTexts.get(storedKey)),
                    "Hafızadaki değer: " + storedTexts.get(storedKey) +
                            "\nŞuanki değer: " + findElement(key).getText() +
                            "\ndeğerler aynı değil.");
        }
    }
    @Step("<key> keyli elementin texti hafızadaki <storedKey> stored keyli elementin textini içermiyor mu kontrol et")
    public void compareNotEqualSavedTextCrrText(String key, String storedKey) {
        if (!findElement(key).getText().contains(storedTexts.get(storedKey))){
            logger.info("'"+key+"' keyli elementin texti hafızadaki "+storedKey+" stored keyli elementin textini içermiyor");
        }else{
            assertFalse(findElement(key).getText().contains(storedTexts.get(storedKey)),
                    "Hafızadaki değer: " + storedTexts.get(storedKey) +
                            "\nŞuanki değer: " + findElement(key).getText() +
                            "\ndeğerler aynı değil.");
        }
    }
    @Step("<key> keyli elemente hafızadaki <storedKey> keyli elementin textini yaz")
    public void sendKeySavedTextToElement(String key, String storedKey) {
        findElement(key).sendKeys(storedTexts.get(storedKey));
        logger.info(key + " keyli elemente hafızadaki " + storedTexts.get(storedKey) + " yazıldı.");
    }
    @Step("Bildirimde OK butonuna tıkla")
    public void alertOK() {
        driver.switchTo().alert().accept();
        logger.info("Bildirimde OK butonuna tıklandı.");
    }
    @Step("Bildirimde Cancel butonuna tıkla")
    public void alertCancel() {
        driver.switchTo().alert().dismiss();
        logger.info("Bildirimde Cancel butonuna tıklandı.");
    }
    @Step("Prompt bildiriminde <textValue> değerini gir ve girilen değeri hafızada <storedKey> keyi ile sakla")
    public void promptSendKeys(String textValue,String storedKey) {
        driver.switchTo().alert().sendKeys(textValue);
        storedTexts.put(storedKey,textValue);
        logger.info("Prompt bildiriminde "+textValue+" değeri girildi ve hafızada "+storedKey+" keyi ile saklandı.");
    }
    @Step("Bildirimdeki mesajın <textValue> olduğunu kontrol et")
    public void alertMassageGetText(String textValue) {
        assertTrue(driver.switchTo().alert().getText().equals(textValue),
                "Bildirimde beklenen text : " + textValue +
                        "Bildirimde görülen text : " + driver.switchTo().alert().getText());
        logger.info("Bildirimdeki mesajın " + textValue + " olduğunu kontrol edildi.");
    }
    @Step("<key> keyli elemente çift tıkla")
    public void doubleClickElement(String key) {
        new Actions(driver)
                .doubleClick(findElement(key))
                .perform();
        logger.info("'"+key+"' keyli elemente çift tıkla.");
    }
    @Step("<key> keyli elementte sağ tıkla")
    public void rightClickElement(String key) {
        new Actions(driver).contextClick(findElement(key)).perform();
        logger.info(key+" elementte sağ tıklandı.");
    }

    @Step("<key> keyli elementin <attribute> attributeunun valuesu <value> mu kontrol et")
    public void validateAttributeEqualsValueOfElement(String key, String attribute, String value) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                                .withTimeout(Duration.ofSeconds(5))
                                .pollingEvery(Duration.ofMillis(500));
        Boolean isCame = wait.until(ExpectedConditions.attributeToBe(getLocator(key),attribute,value));
        if (isCame){
            logger.info(key+" keyli elementin "+attribute+
                    " attribute unun valuesunun '"+value+"' olduğu doğrulandı.");
        }else{
            Assertions.assertEquals(findElement(key).getAttribute(attribute),value);
        }
    }

    @Step("<key> keyli elementin <attribute> attributeunun valuesu <value> içerene kadar bekle")
    public void waitUntilAttributeContainsValueX(String key, String attribute, String value) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .pollingEvery(Duration.ofMillis(100))
                .withTimeout(Duration.ofSeconds(20));
        Boolean isValue = wait.until(ExpectedConditions.attributeContains(getLocator(key),attribute,value));

        if (isValue == true){
            logger.info(key+" keyli elementin '"+attribute+"' attribute unun valuesu '"+value+"' içeriyor.");
        }else{
            Assertions.assertTrue(isValue);
        }
    }

    @Step("<key> keyli elementin <attribute> attributeunun valuesu <value> olana kadar bekle")
    public void waitUntilAttributeValueIsValueX(String key, String attribute, String value) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .pollingEvery(Duration.ofMillis(100))
                .withTimeout(Duration.ofSeconds(20));
        Boolean isValue = wait.until(ExpectedConditions.attributeToBe(getLocator(key),attribute,value));

        if (isValue == true){
            logger.info(key+" keyli elementin '"+attribute+"' attribute unun valuesu '"+value+"'.");
        }else{
            Assertions.assertTrue(isValue);
        }
    }

    @Step("<key> keyli elementin <attribute> attributeunun valuesu <value> içeriyor mu kontrol et")
    public void validateAttributeContainsValueOfElement(String key, String attribute, String containsValue) {
        if (findElement(key).getAttribute(attribute).contains(containsValue.toString())){
            logger.info(key+" keyli elementin "+attribute+
                    " attribute unun valuesunun '"+containsValue+"' içerdiği doğrulandı.");
        }else{
            Assertions.assertTrue(findElement(key).getAttribute(attribute).contains(containsValue));
        }
    }

    @Step("<key> keyli elementin <attribute> attributeunun valuesu <value> mu kontrol et")
    public void validateAttributeIsValueOfElement(String key, String attribute, String value) {
        if (findElement(key).getAttribute(attribute).equals(value)){
            logger.info(key+" keyli elementin "+attribute+
                    " attribute unun valuesunun '"+value+"' olduğu doğrulandı.");
        }else{
            Assertions.assertTrue(findElement(key).getAttribute(attribute).equals(value));
        }
    }

    @Step("<key> keyli elementin <attribute> attrnun valuesu hafızada saklanan <storedKey> keyli değeri içeriyor mu kontrol et")
    public void validateAttributeContainsStoredTextOfElement(String key, String attribute, String storedKey) {
        if (findElement(key).getAttribute(attribute).contains(storedTexts.get(storedKey))){
            logger.info(key+" keyli elementin "+attribute+
                    " attribute unun valuesunun '"+storedTexts.get(storedKey)+"' içerdiği doğrulandı.");
        }else{
            Assertions.assertTrue(findElement(key).getAttribute(attribute).contains(storedTexts.get(storedKey)));
        }
    }

    @Step("Yeni pencereye geç")
    public void switchToNewWindow() {
        //fetch handles of all windows, there will be two, [0]- default, [1] - new window
        Object[] windowHandles=driver.getWindowHandles().toArray();
        driver.switchTo().window((String) windowHandles[windowHandles.length-1]);
        logger.info(driver.getCurrentUrl()+" urlli pencereye geçildi.");
    }
    @Step("Son pencereyi kapat")
    public void closeToLastWindow() {
        Object[] windowHandles=driver.getWindowHandles().toArray();
        driver.close();
        driver.switchTo().window((String) windowHandles[windowHandles.length-2]);
        logger.info("Son pencere kapatıldı.");
    }
    //Dinamik elementlerde starts-width ends-width contains le falan locatorunu alırsın.
    //getText sendKeys isEnabled isSelected Metodlarını fazladan yazmaya gerek yok.
    @Step("En son sekmeye geç ve title ın textinin <titleText> olduğunu kontrol et")
    public void switchToLastTab(String titleText){
        Object[] windowHandles = driver.getWindowHandles().toArray();
        driver.switchTo().window((String) windowHandles[windowHandles.length - 1]);
        assertEquals(titleText, driver.getTitle(),
                "\nExpected title text : " + titleText +
                        "\nActual title text : " + driver.getTitle());
        logger.info("En son sekmeye geçildi ve title textinin " + titleText + " olduğu kontrol edildi.");
    }
    @Step("Son sekmeyi kapat. Şuanki title texti <titleText> mi kontrol et")
    public void closeLastTab(String titleText){
        Object[] windowHandles = driver.getWindowHandles().toArray();
        driver.close();
        driver.switchTo().window((String) windowHandles[windowHandles.length - 2]);
        assertEquals(titleText, driver.getTitle(),
                "\nExpected title text : " + titleText +
                        "\nActual title text : " + driver.getTitle());
        logger.info("Son sekme kapatıldı ve şuanki title textinin " + titleText + " olduğu kontrol edildi.");
    }

    @Step("Download klasöründe <fileName> dosyasının varlığı kontrol edilir")
    public void isFileDownloaded(String fileName) {
        String downloadPath = downloadsFilePath;
        File dir = new File(downloadPath);
        File[] dirContents = dir.listFiles();
        for (File file : dirContents) {
            if (file.getName().equals(fileName)) {
                logger.info(fileName+" dosyası "+downloadPath+" klasörüne "
                + "başarılı şekilde indi."+
                        "\nDosya yolu : "+file.getPath());
                break;
            }
        }
    }

    @Step("<key> keyli elemente dosya yükleme <filePath> dosya yolundan gerçekleştirilir")
    public void uploadFile(String key, String filePath) {
        findElement(key).sendKeys(filePath);
        logger.info("'"+key+"' keyli elemente dosya yükleme "+filePath+" dosya yolundan gerçekleştirildi.");
    }

    @Step("<fileName> dosyasını /Downloads klasöründen sil")
    public void deleteFileFromDownloads(String fileName) {
        File obj = new File(downloadsFilePath+"/"+fileName);
        if (obj.delete()) {
            logger.info(obj.getName()+" dosyası /Downloads klasöründen silindi.");
        } else {
            logger.fail(obj.getName()+" dosyası /Downloads klasöründen silinemedi.");
        }
    }

    @Step("<key> keyli elementin <checkSecs> .saniyeden itibaren enable olduğunu kontrol et")
    public void checkElementEnableBySecs(String key, int checkSecs){
        driver.get(currentURL);
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .pollingEvery(Duration.ofMillis(500))
                .withTimeout(Duration.ofSeconds(checkSecs));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(getLocator(key)));
        if (element.isEnabled()){
            logger.info("'"+key+"' keyli element "+checkSecs+" saniye sonra enable.");
        }else{
            assertTrue(element.isEnabled(),"'"+key+"' keyli element "+checkSecs+" saniye sonra enable değil.");
        }

    }
    @Step("Bildirim tıklanabilir olana kadar bekle.")
    public void notifOKClickAfterXSec(){
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        logger.info(text+" bildirimi tıklanabilir olana kadar beklendi.");
    }

    @Step("<key> keyli elementin <checkSecs> .saniyeden itibaren <attribute> attributeunun valuesu <value> olduğunu kontrol et")
    public void checkElementAttributeValueBySecs(String key, int checkSecs, String attribute, String value){
        driver.get(currentURL);
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .pollingEvery(Duration.ofMillis(500))
                .withTimeout(Duration.ofSeconds(checkSecs));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator(key)));
        if (element.getAttribute(attribute).equals(value)){
            logger.info(key+ " keyli elementin "+attribute+
                    " attribute unun valuesu '"+checkSecs+" saniye sonra "+value+"'.");
        }else{
            assertEquals(element.getAttribute(attribute),value,key+ " keyli elementin "+attribute+
                    " attribute unun valuesu '"+checkSecs+" saniye sonra "+value+"' değil.");
        }

    }

    @Step("<key> keyli elementin <checkSecs> .saniyeden itibaren görünür olduğunu kontrol et")
    public void checkElementDisplayBySecs(String key, int checkSecs){
        driver.get(currentURL);
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(checkSecs))
                .pollingEvery(Duration.ofMillis(500));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator(key)));
        if (element.isDisplayed()){
            logger.info("'"+key+"' keyli element "+checkSecs+" saniye sonra görünür.");
        }else{
            assertTrue(element.isDisplayed(),"'"+key+"' keyli element "+checkSecs+" saniye sonra görünür olmadı.");
        }
    }

    @Step("<key> keyli iframe e geç")
    public void switchToIframe(String key){
        driver.switchTo().frame(findElement(key));
        logger.info("'"+key+"' keyli iframe e geçildi.");
    }

    @Step("<key> keyli iframe varsa iframe e geç")
    public void isExistSwitchToIframe(String key){
        if (findElement(key).isDisplayed()){
            driver.switchTo().frame(findElement(key));
            logger.info("'"+key+"' keyli iframe e geçildi.");
        }else{
            logger.info(key+" keyli iframe görülmedi. Devam ediliyor.");
        }

    }

    @Step("Iframeden çık")
    public void switchToIndexIframe(){
        driver.switchTo().defaultContent();
        logger.info(0 +" indisli iframe e geçildi.");
    }

    @Step("<key> keyli elemente js ile <inputValue> yaz")
    public void sendKeyWithJS(String key, String inputValue){
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].value='" + inputValue + "';", findElement(key));
        logger.info("'"+key+ "' keyli elemente '"+inputValue+"' değeri yazıldı.");
    }

    //Date time pickera uygun metodlar
    @Step("<key> keyli headerın ay alanı <text1> yıl alanı <text2> olana kadar <key1> butonuna tıkla")
    public void clickElementUntilVisibleText(String key, String text1,String text2, String key1){
    while(!findElement(key).getText().equals(text1+" "+text2)){
        findElement(key1).click();
    }
    logger.info("'"+key+"' keyli headerın ay alanı '"+text1+"' yıl alanı '"+text2+"' olana kadar '"+key1+"' keyli butona tıklandı.");
    }

    @Step("Locatoru <containsText> içeren <key> keyli listededen rastgele bir elemente tıkla ve textini <storedKey> keyi ile sakla")
    public void selectContainsTextFromListElement(String containsText, String key, String storedKey) {
        List<WebElement> elements = new ArrayList<WebElement>();
        int index = 0;
        int randIndex;
        for (WebElement element : findElements(key)) {
            if (element.getAttribute("aria-label").contains(containsText)) {
                elements.add(element);
                index++;
            }
        }
        randIndex = random.nextInt(elements.size());
        storedTexts.put(storedKey,elements.get(randIndex).getText());
        elements.get(randIndex).click();
        logger.info("Locatoru '"+containsText+"' içeren '"+key+"' keyli listededen '"+(randIndex+1)+"'. elemente tıklandı.");
    }

    @Step("<key> keyli date pickerda ay <monthStr> gün <storedKey> keyli yıl <year> seçili mi kontrol et")
    public void validateDate(String key, String monthStr, String storedKey, String year) {
        String expectedDate;
        String actualValue = findElement(key).getAttribute("value");

        if ((getMonthList(months).get(monthStr) + 1) < 10) {
            expectedDate = "0" + (getMonthList(months).get(monthStr) + 1) + "/" + storedTexts.get(storedKey) + "/" + year;
        } else {
            expectedDate = (getMonthList(months).get(monthStr) + 1) + "/" + storedTexts.get(storedKey) + "/" + year;
        }

        if (expectedDate.equals(actualValue)) {
            logger.info("Date time pickerda seçilen tarihin '" + expectedDate + "' olduğu doğrulandı.");
        } else {
            assertEquals(expectedDate, actualValue);
        }
    }

    @Step("<key> keyli elemente hover et")
    public void hoverElement(String key) {
        new Actions(driver).moveToElement(findElement(key)).perform();
        logger.info(key+" keyli elemente hover edildi.");
    }

    @Step("<key> keyli listeden texti <text> olan elemente hover et")
    public void hoverElement(String key, String text) {
        List<WebElement> elements = findElements(key);
        int index = 0;
        for (WebElement element: elements){
            index++;
            if (element.getText().equals(text)){
                new Actions(driver).moveToElement(element).perform();
                logger.info(key+" keyli listeden texti "+text+" olan elemente hover edildi.");
                break;
            }
            else {
                if (elements.size()==index){
                    Assertions.assertEquals(text,element.getText());
                    break;
                }
            }
        }

    }

    @Step("Hover edildiğinde görünen textin <text> olduğunu kontrol et")
    public void hoverElementByText(String text) {
        By toolTipTextLocator = By.cssSelector(".tooltip-inner");
        wait.until(ExpectedConditions.visibilityOfElementLocated(toolTipTextLocator));
        WebElement toolTipTextElement = driver.findElement(toolTipTextLocator);
        String toolTipText = toolTipTextElement.getText();
        if (toolTipText.equals(text)){
         logger.info("Hover edince '"+ text+"' textinin görüldüğü doğrulandı.");
        }
    }

    @Step("<key> keyli listede rastgele bir elemana hover et ve textini <storedKey> keyli ile sakla")
    public void hoverElementFromList(String key, String storedKey) {
        List<WebElement> elements = findElements(key);
        int randIndex = random.nextInt(elements.size());
        Actions actions = new Actions(driver);
        WebElement randomElement = elements.get(randIndex);

        storedTexts.put(storedKey, randomElement.getText());
        actions.moveToElement(randomElement).perform();
        logger.info(key + " keyli listeden texti '" + randomElement.getText() + "' olana kaydırıldı.");
    }


    @Step("<key> keyli listede texti <storedKey> keyi ile saklanan elemana tıkla")
    public void clickElementFromList(String key, String storedKey) {
        List<WebElement> elements = findElements(key);
        String storedText = storedTexts.get(storedKey);
        for (WebElement element : elements) {
            if (element.getText().equals(storedText)) {
                logger.info(element.getText()+" textli elemente tıklandı.");
                element.click();
                break;
            }
        }
    }

    @Step("Genişliği <width> pixel olan min değeri <min> max değeri <max> olan <key> keyli sliderın düğmesini <targetValue> değerine sürükle")
    public void slideDragAndDropToTargetValue(int width, int min,int max, String key, String targetValue) {
        int distance = Integer.valueOf(targetValue) - Integer.valueOf(findElement(key).getAttribute("value"));
        int xOffset = (distance*width)/(max-min);
        new Actions(driver).clickAndHold(findElement(key)).moveByOffset(xOffset,0).release().perform();
        logger.info(findElement(key).getAttribute("value"));
        //tam randımanlı değil. Alttaki metod güzel çalışıyor.
    }

    @Step("<key> keyli sliderı <targetValue> değerine kadar sürükle")
    public void setSliderValue(String key, int targetValue) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String script =
                "var slider = arguments[0];" +
                        "var value = arguments[1];" +
                        "slider.value = value;" +
                        "slider.dispatchEvent(new Event('input'));" +
                        "slider.dispatchEvent(new Event('change'));";
        js.executeScript(script, findElement(key), targetValue);
        logger.info(key+" keyli sliderın value değeri "+findElement(key).getAttribute("value"));
    }

    @Step("<key1> keyli elementi <key2> keyli konuma sürükle")
    public void dragElementToTheLocator(String key1, String key2) {
        WebElement srcElement = findElement(key1);
        WebElement tarElement = findElement(key2);
        new Actions(driver).clickAndHold(srcElement).pause(Duration.ofSeconds(2))
                .moveToElement(tarElement).pause(Duration.ofSeconds(2)).release()
                .build().perform();
    }

    @Step("OTP kodunu <storedKey> keyi ile sakla")
    public void getTOTPCode(String storedKey) throws NoSuchAlgorithmException, InvalidKeyException {
        String secretKeyBase32 = "3T63MQ5SSHHGVPNGR6ZHMCVVHDHN5QM5";
        SecretKey secretKey = new SecretKeySpec(BaseEncoding.base32().decode(secretKeyBase32), "HmacSHA1");
        // OTP kodunu oluşturun
        String otpCode = getTOTPCode(secretKey);
        storedTexts.put(storedKey,otpCode);
        logger.info(otpCode+ " OTP kodu '"+storedKey+"' keyi ile saklandı.");
    }
    @Step("storedTexts mapini temizle")
    public void clearStoredTextsMap(){
        storedTexts.clear();
        logger.info("storedTexts mapini temizlendi");
    }

    @Step("<key> keyli recaptchadaki <key1> keyli elemente tıklayıp iframe'i geç")
    public void handleRecaptcha(String key, String key1) {
        // Sayfaya gidin
        driver.get("file:///Users/huseyinakcan/Desktop/yeniKlasor/WebTestAutomation_Templates_1/src/test/resources/recaptca.html");

        // reCAPTCHA iframe'ini bekleyin ve geçiş yapın
        WebElement iframeElement = wait.until(ExpectedConditions.presenceOfElementLocated(getLocator(key)));

        // reCAPTCHA iframe'ine geçin
        driver.switchTo().frame(iframeElement);

        // reCAPTCHA kutusuna tıklayın
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(getLocator(key1)));
        element.click();

        // Iframe'den çıkın
        driver.switchTo().defaultContent();

        // Formu gönderin
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type='submit']")));
        submitButton.click();
    }

    @Step("debug et")
    public void debugTest() {
        logger.info("debug ediliyor");
    }

}