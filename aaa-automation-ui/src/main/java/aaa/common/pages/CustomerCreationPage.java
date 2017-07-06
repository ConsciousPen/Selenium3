/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.pages;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.composite.table.Table;

public class CustomerCreationPage extends Page {

    public static final Table tableADDRESS_DETAILS = new Table(By.xpath("//*[@id=\"crmForm:editAddressSection_0\"]/table"));
}
