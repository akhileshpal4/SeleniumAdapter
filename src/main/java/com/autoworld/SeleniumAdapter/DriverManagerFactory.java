package com.autoworld.SeleniumAdapter;

public class DriverManagerFactory {

    public DriverManagerFactory(){}

    public static DriverManager getManager(String browserName){
        DriverManager driverManager=null;
        if(browserName.equalsIgnoreCase("chrome")){
            driverManager=new ChromeDriverManager();
        }else if(browserName.equalsIgnoreCase("edge")){
            driverManager=new EdgeDriverManager();
        }else if(browserName.equalsIgnoreCase("firefox")){
            driverManager=new FirefoxDriverManager();
        }
        return driverManager;
    }
}
