/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.pages;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;

public class ErrorPage extends MainPage {

    public static Table tableError = new Table(By.xpath("//table[@id='errorsForm:msgList' or contains(@id,'errorsForm')]"));

    public static Table tableErrorInformation = new Table(By.xpath("//div[@class='ui-datatable-tablewrapper']/table"));

    public static StaticElement provideLabelErrorMessage(String errorMessage) {
        return new StaticElement(By.xpath("//*[text()='" + errorMessage + "']"));
    }
}
