/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.swaggerui;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.swaggerui.metadata.SwaggerUiMetaData;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */



public class SwaggerUiTab extends Tab {
    public static ComboBox swaggerServiceSelection = new ComboBox(By.id("baseUrl"));
    public static Link productOperations = new Link (By.xpath(".//*[@id='resource_product-operations']/div/h2/a"), Waiters.AJAX);
    public static Link productOperationsProcessOperations = new Link(By.xpath(".//*[@id='product-operations_processOperations']/div[1]/h3/span[2]/a"), Waiters.AJAX);
    public static Link productState = new Link (By.xpath(".//*[@id='resource_product-state']/div/h2/a"), Waiters.AJAX);
    public static Link productStateLoad = new Link(By.xpath(".//*[@id='product-state_load']/div[1]/h3/span[2]/a"), Waiters.AJAX);
    public static Link productStateStart = new Link(By.xpath(".//*[@id='product-state_start']/div[1]/h3/span[2]/a"), Waiters.AJAX);


    // public static StaticElement productOperationsModel = new StaticElement(By.xpath(".//*[@id='product-operations_processOperations_content']/p/span/div/div/div/div/div[2]/pre/code"));



    //ORIGNAL public static TextBox productOperationsBody = new TextBox(By.xpath("(//textarea[@class='body-textarea'])[3]"));



    //public static TextBox productOperationsBody = new TextBox(By.xpath(".//*[@id='product-operations_processOperations_content']//textarea[@class='body-textarea']"));
    //public static Button productOperationsTryItOut = new Button(By.xpath(".//*[@id='product-operations_processOperations_content']/form/div[2]/input"));
    public static StaticElement productOperationsRequestUrl = new StaticElement(By.xpath(".//*[@id='product-operations_processOperations_content']/div[4]/div[2]/pre"));
    //public static StaticElement productOperationsResponseBody = new StaticElement(By.xpath(".//*[@id='product-operations_processOperations_content']/div[4]/div[3]/pre/code"));
    //public static StaticElement productOperationsRsponseCode = new StaticElement(By.xpath(".//*[@id='product-operations_processOperations_content']/div[4]/div[4]/pre"));
    public static StaticElement productOperationsResponseHeaders = new StaticElement(By.xpath(".//*[@id='product-operations_processOperations_content']/div[4]/div[4]/pre"));


    public static String rootXpath = "/../../../../div[2]";
    public static String modelXpath = "/p/span/div/div/div/div/div[2]/pre/code";
    public static String bodyXpath = "//textarea[@class='body-textarea']";
    public static String buttonTryItXpath = "/form/div[2]/input";
    public static String responseBodyXpath = "/div[4]/div[3]/pre/code";
    public static String responseCodeXpath = "/div[4]/div[4]/pre";


    public SwaggerUiTab() {
        super(SwaggerUiMetaData.ServiceSelection.class);
    }

    public String getModelValueByLocator(By locator){
        Waiters.SLEEP(2000).go();
        String xpath0 = (locator+rootXpath).replace("By.xpath: ", "");
        StaticElement modelField = new StaticElement(By.xpath(xpath0+modelXpath));
        return modelField.getValue();
    }

    public void setBodyValueByLocator(By locator, String value){
        String xpath0 = (locator+rootXpath).replace("By.xpath: ", "");
        TextBox bodyField = new TextBox(By.xpath(xpath0+bodyXpath));
        bodyField.setValue(value);
    }

    public void clickButtonTryItByLocator(By locator){
        String xpath0 = (locator+rootXpath).replace("By.xpath: ", "");
        Button buttonTryItOut = new Button(By.xpath(xpath0+buttonTryItXpath));
        buttonTryItOut.click();
        Waiters.SLEEP(2000).go();
    }

    public String getResponseBodyValueByLocator(By locator){
        String xpath0 = (locator+rootXpath).replace("By.xpath: ", "");
        StaticElement responseBodyField = new StaticElement(By.xpath(xpath0+responseBodyXpath));
        return responseBodyField.getValue();
    }

    public String getResponseCodeValueByLocator(By locator){
        String xpath0 = (locator+rootXpath).replace("By.xpath: ", "");
        StaticElement responseCodeField = new StaticElement(By.xpath(xpath0+responseCodeXpath));
        return responseCodeField.getValue();
    }
}
