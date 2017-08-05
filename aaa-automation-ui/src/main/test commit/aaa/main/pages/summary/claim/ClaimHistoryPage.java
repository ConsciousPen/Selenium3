/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary.claim;

import org.openqa.selenium.By;

import aaa.main.pages.summary.SummaryPage;
import toolkit.webdriver.controls.composite.table.Table;

public class ClaimHistoryPage extends SummaryPage {

    public static Table tableClaimHistory = new Table(By.id("claimsHistoryInnerForm:versionsTable"));
}
