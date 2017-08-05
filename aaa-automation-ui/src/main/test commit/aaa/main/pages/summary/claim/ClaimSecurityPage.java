/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary.claim;

import org.openqa.selenium.By;

import aaa.main.pages.summary.SummaryPage;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Table;

public class ClaimSecurityPage extends SummaryPage {

    public static Table tableAccessControlList = new Table(By.xpath("//div[@id='policyDataGatherForm:aclEntriesTable']//table"));
    public static Link linkAddAdjuster = new Link(By.id("policyDataGatherForm:addAdjuster"));

    public static TextBox textBoxFirstName = new TextBox(By.id("adjusterSearchForm:criteriaTable_firstName"));
    public static Button buttonSearch = new Button(By.id("adjusterSearchForm:search"));
    public static Link linkResultName = new Link(By.id("adjusterSearchForm:results_internal:0:nameLink"));
    public static Link linkGrantedUsers = new Link(By.xpath("//div[@id='policyDataGatherForm:grantedUsersHeaderPanel:header']//div[.='Granted Users' and @class='rf-cp-lbl-colps']"));
    public static Table tableGrantedUsers = new Table(By.xpath("//div[@id='policyDataGatherForm:grantedUsersTable']//table"));
}
