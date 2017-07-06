/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.mywork.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.main.metadata.MyWorkMetaData;
import toolkit.webdriver.controls.Button;

public class CompleteTaskActionTab extends ActionTab {

    public static Button buttonComplete = new Button(By.id("taskCompleteForm:submitComplete_footer"));
    public static Button buttonCancel = new Button(By.id("taskCompleteForm:navigateBack_footer"));

    public CompleteTaskActionTab() {
        super(MyWorkMetaData.CompleteTaskActionTab.class);
    }
}
