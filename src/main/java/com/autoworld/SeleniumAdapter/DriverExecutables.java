package com.autoworld.SeleniumAdapter;

import com.autoworld.ConfigProvider.ConfigProvider;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.codec.binary.Base64;
import org.testng.log4testng.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;

public class DriverExecutables {
    public static final String PROXY_URL;
    public static final String PROXY_UNAME;
    public static final String PROXY_PWD;
    public static final boolean PROXY_VALUE;
    public static final String CHROME_VERSION;
    public static final String EDGE_VERSION;
    public static final String FIREFOX_VERSION;
    public static final String CHROMEDRIVER_VERSION_PATH;
    public static final String EDGEDRIVER_VERSION_PATH;
    public static final String FIREFOXDRIVER_VERSION_PATH;
    public static final String EDGE_DRIVER_TYPE;

    private static Logger logger=Logger.getLogger(DriverExecutables.class);

    static {
        String var=ConfigProvider.getAsString("proxy.url");
        PROXY_URL=var+":"+ConfigProvider.getAsString("proxy.port");
        PROXY_UNAME=System.getProperty("PROXY_UNAME",ConfigProvider.getAsString("proxy.username"));
        PROXY_PWD=System.getProperty("PROXY_PWD",ConfigProvider.getAsString("proxy.pwd"));
        PROXY_VALUE=PROXY_UNAME!=null && PROXY_PWD!=null && (!PROXY_UNAME.isBlank() || !PROXY_UNAME.isEmpty() && !PROXY_PWD.isEmpty()||!PROXY_PWD.isBlank());

        CHROME_VERSION=System.getProperty("chrome.version",ConfigProvider.getAsString("chrome.version"));
        CHROMEDRIVER_VERSION_PATH=System.getProperty("chromeDriver.path",ConfigProvider.getAsString("chromeDriver.path"));

        EDGE_VERSION=System.getProperty("edge.version",ConfigProvider.getAsString("edge.version"));
        EDGEDRIVER_VERSION_PATH=System.getProperty("edgeDriver.path",ConfigProvider.getAsString("edgeDriver.path"));

        FIREFOX_VERSION=System.getProperty("firefox.version",ConfigProvider.getAsString("firefox.version"));
        FIREFOXDRIVER_VERSION_PATH=System.getProperty("firefoxDriver.path",ConfigProvider.getAsString("firefoxDriver.path"));

        EDGE_DRIVER_TYPE=System.getProperty("edge.driver",ConfigProvider.getAsString("edge.driver"));
    }

    public DriverExecutables(){}

    protected static void setBrowserExe(){
        String jdkVersion=System.getProperty("sun.arch.data.model");
        String browserName=System.getProperty("browser", ConfigProvider.getAsString("browser"));
        if(browserName.equalsIgnoreCase("chrome")){
            executeChrome();
        }else if(browserName.equalsIgnoreCase("edge")){
            executeEdge();
        }else if(browserName.equalsIgnoreCase("firefox")){
            executeFirefox(jdkVersion);
        }
    }

    protected static void executeChrome(){
        if(CHROMEDRIVER_VERSION_PATH!=null && !CHROMEDRIVER_VERSION_PATH.isBlank() && !CHROMEDRIVER_VERSION_PATH.isEmpty()){
            System.setProperty("webdriver.chrome.driver",CHROMEDRIVER_VERSION_PATH);
        }else{
            fnCallChromeWebDriverManager();
        }
    }

    protected static void executeEdge(){
        if(EDGEDRIVER_VERSION_PATH!=null && !EDGEDRIVER_VERSION_PATH.isEmpty() && !EDGEDRIVER_VERSION_PATH.isBlank()){
            System.setProperty("webdriver.edge.driver",EDGEDRIVER_VERSION_PATH);
        }else{
            fnCallEdgeWebDriverManager();
        }
    }

    protected static void executeFirefox(String jdkVersion){
        if(EDGEDRIVER_VERSION_PATH!=null && !EDGEDRIVER_VERSION_PATH.isEmpty() && !EDGEDRIVER_VERSION_PATH.isBlank()){
            System.setProperty("webdriver.gecko.driver",FIREFOXDRIVER_VERSION_PATH);
        }else{
            fnCallFirefoxWebDriverManager(jdkVersion);
        }
    }
    protected static void fnCallChromeWebDriverManager(){
        if(CHROME_VERSION!=null && CHROME_VERSION.isEmpty()){
            if(PROXY_VALUE){
                WebDriverManager.chromedriver().clearDriverCache().browserVersion(CHROME_VERSION).proxy(PROXY_URL).proxyUser(PROXY_UNAME).proxyPass(decryptedKey(PROXY_PWD)).setup();
            }else{
                WebDriverManager.chromedriver().clearDriverCache().browserVersion(CHROME_VERSION).setup();
            }
        }else if(PROXY_VALUE){
                WebDriverManager.chromedriver().clearDriverCache().proxy(PROXY_URL).proxyUser(PROXY_UNAME).proxyPass(decryptedKey(PROXY_PWD)).setup();
        }else if(PROXY_URL!=null &&  !PROXY_URL.isEmpty()){
                WebDriverManager.chromedriver().clearDriverCache().proxy(PROXY_URL).setup();
        }else {
            WebDriverManager.chromedriver().clearDriverCache().setup();
        }
    }

    protected static void fnCallEdgeWebDriverManager(){
        if(EDGE_DRIVER_TYPE.equalsIgnoreCase("32")){
            executeEdge32Bit();
        }else{
            executeEdge64Bit();
        }
    }
    protected static void executeEdge32Bit(){
        if(EDGE_VERSION!=null && !EDGE_VERSION.isEmpty()){
            if(PROXY_VALUE){
                WebDriverManager.edgedriver().clearDriverCache().browserVersion(EDGE_VERSION).arch32().proxy(PROXY_URL).proxyUser(PROXY_UNAME).proxyPass(decryptedKey(PROXY_PWD)).setup();
            }else{
                WebDriverManager.edgedriver().clearDriverCache().browserVersion(EDGE_VERSION).arch32().setup();
            }
        }else if(PROXY_VALUE){
            WebDriverManager.edgedriver().clearDriverCache().arch32().proxy(PROXY_URL).proxyUser(PROXY_UNAME).proxyPass(decryptedKey(PROXY_PWD)).setup();
        }else if(PROXY_URL!=null &&  !PROXY_URL.isEmpty()){
            WebDriverManager.edgedriver().clearDriverCache().arch32().proxy(PROXY_URL).setup();
        }else{
            WebDriverManager.edgedriver().clearDriverCache().arch32().setup();
        }

    }
    protected static void executeEdge64Bit(){
        if(EDGE_VERSION!=null && !EDGE_VERSION.isEmpty()){
            if(PROXY_VALUE){
                WebDriverManager.edgedriver().clearDriverCache().browserVersion(EDGE_VERSION).proxy(PROXY_URL).proxyUser(PROXY_UNAME).proxyPass(decryptedKey(PROXY_PWD)).setup();
            }else{
                WebDriverManager.edgedriver().clearDriverCache().browserVersion(EDGE_VERSION).setup();
            }

        }else if(PROXY_VALUE){
                WebDriverManager.edgedriver().clearDriverCache().proxy(PROXY_URL).proxyUser(PROXY_UNAME).proxyPass(decryptedKey(PROXY_PWD)).setup();
        }else if(PROXY_URL!=null &&  !PROXY_URL.isEmpty()) {
                WebDriverManager.edgedriver().clearDriverCache().proxy(PROXY_URL).setup();
        }else {
            WebDriverManager.edgedriver().clearDriverCache().setup();
        }
    }

    protected static void fnCallFirefoxWebDriverManager(String jdkVersion){
        if(jdkVersion.equals("32")){
            executeFirefox32Bit();
        }else{
            executeFirefox64Bit();
        }
    }

    protected static void executeFirefox32Bit(){
        if(FIREFOX_VERSION!=null && !FIREFOX_VERSION.isBlank() && !FIREFOX_VERSION.isEmpty()){
            if(PROXY_VALUE){
                WebDriverManager.firefoxdriver().clearDriverCache().browserVersion(FIREFOX_VERSION).arch32().proxy(PROXY_URL).proxyUser(PROXY_UNAME).proxyPass(decryptedKey(PROXY_PWD)).setup();
            }else {
                WebDriverManager.firefoxdriver().clearDriverCache().browserVersion(FIREFOX_VERSION).arch32().setup();
            }
        }else if(PROXY_VALUE){
            WebDriverManager.firefoxdriver().clearDriverCache().arch32().proxy(PROXY_URL).proxyPass(PROXY_UNAME).proxyPass(PROXY_PWD).setup();
        }else if(PROXY_URL!=null && !PROXY_URL.isEmpty()){
            WebDriverManager.firefoxdriver().clearDriverCache().arch32().proxy(PROXY_URL).setup();
        }else{
            WebDriverManager.firefoxdriver().clearDriverCache().arch32().setup();
        }
    }
    protected static void executeFirefox64Bit(){
        if(FIREFOX_VERSION!=null && !FIREFOX_VERSION.isBlank() && !FIREFOX_VERSION.isEmpty()){
            if(PROXY_VALUE){
                WebDriverManager.firefoxdriver().clearDriverCache().browserVersion(FIREFOX_VERSION).proxy(PROXY_URL).proxyUser(PROXY_UNAME).proxyPass(decryptedKey(PROXY_PWD)).setup();
            }else {
                WebDriverManager.firefoxdriver().clearDriverCache().browserVersion(FIREFOX_VERSION).setup();
            }
        }else if(PROXY_VALUE){
            WebDriverManager.firefoxdriver().clearDriverCache().proxy(PROXY_URL).proxyUser(PROXY_UNAME).proxyPass(decryptedKey(PROXY_PWD)).setup();
        }else if(PROXY_URL!=null && !PROXY_URL.isEmpty()){
            WebDriverManager.firefoxdriver().clearDriverCache().proxy(PROXY_URL).setup();
        }else{
            WebDriverManager.firefoxdriver().clearDriverCache().setup();
        }
    }
    protected static String decryptedKey(String encryptedString){
        String decryptedText="";
        if(encryptedString!=null){
            try{
                String myEncryptionKey="WelcomeToAutoWorldFramework";
                String myEncryptionScheme="DESede";
                byte[] arrayBytes=myEncryptionKey.getBytes(StandardCharsets.UTF_8);
                KeySpec ks=new DESedeKeySpec(arrayBytes);
                SecretKeyFactory skf=SecretKeyFactory.getInstance(myEncryptionScheme);
                Cipher cipher=Cipher.getInstance(myEncryptionScheme);
                SecretKey key=skf.generateSecret(ks);
                cipher.init(2,key);
                byte[] encryptedText= Base64.decodeBase64(encryptedString);
                byte[] plainText=cipher.doFinal(encryptedText);
                decryptedText=new String(plainText);
                return decryptedText;

            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            logger.warn("Proxy decrypted password is NULL");
        }
        return decryptedText;
    }
}
