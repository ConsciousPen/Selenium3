/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.pages;

import org.openqa.selenium.By;

import aaa.common.pages.Page;
import toolkit.webdriver.controls.Button;

public class AdminPage extends Page {
    public static Button buttonSearch = new Button(By.xpath("//input[@value='Search' and not(ancestor::div[contains(@style,'visibility: hidden')])]"));
    public static Button buttonClear = new Button(By.xpath("//input[@value='Clear' and not(ancestor::div[contains(@style,'visibility: hidden')])]"));
}
