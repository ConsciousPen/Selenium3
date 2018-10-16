/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.pages.agencyvendor;

import aaa.admin.pages.AdminPage;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;

public class AgencyTransferPage extends AdminPage {
    public static Button buttonAddNewTransfer = new Button(By.id("borManagementForm:addButton"));
    public static Button buttonSearchTransfer = new Button(By.id("borManagementForm:searchButton"));
    public static Table tableTransfers = new Table(By.id("borManagementForm:body_borInfoTable"));
}
