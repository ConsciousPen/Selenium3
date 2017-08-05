/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.components;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;

public class DialogPartySearch {
    public Button buttonCancel;
    public StaticElement labelMessage;

    public DialogPartySearch(String dialogLocator) {
        buttonCancel = new Button(By.xpath(dialogLocator + "//input[@value = 'Cancel']"));
        labelMessage = new StaticElement(By.xpath(dialogLocator + "//div[contains(text(),'Party Search')]"));
    }
}