/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.pages.general;

import org.openqa.selenium.By;

import aaa.admin.pages.AdminPage;
import toolkit.webdriver.controls.Link;

public class GeneralAsyncTasksPage extends AdminPage {

    public static Link linkStartManager = new Link(By.id("asyncTaskSummaryForm:startManager"));
    public static Link linkStopManager = new Link(By.id("asyncTaskSummaryForm:stopManager"));

}
