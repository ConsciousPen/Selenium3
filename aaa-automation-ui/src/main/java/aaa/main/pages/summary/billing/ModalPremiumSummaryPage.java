/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary.billing;

import org.openqa.selenium.By;

import aaa.main.enums.BillingConstants;
import aaa.main.pages.summary.BillingSummaryGBPage;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Table;

public class ModalPremiumSummaryPage extends BillingSummaryGBPage {

    public static Table tableModalPremium = new Table(By.xpath("//div[@id='modalPremiumForm:billing_modal_premium']//table"));

    public static Table getModalPremiumsTableByBillableCoverage(int rowNumber) {
        return new Table(By.xpath(String.format("//div[@id='modalPremiumForm:billing_modal_premium:%s:modalPremiumHistoryTable']//table", rowNumber)));
    }

    /**
     * Use only when Coverage has ONLY one Payor
     * @param coverageName
     */
    public static void expandCoverageByName(String coverageName) {
        Link collapsedCoverage =
                tableModalPremium.getRow(BillingConstants.BillingModalPremiumTable.COVERAGE, coverageName).getCell(BillingConstants.BillingModalPremiumTable.COVERAGE).controls.links.getFirst();
        if (collapsedCoverage.isPresent()) {
            collapsedCoverage.click();
        }
    }

    public static void expandCoverageByRow(int rowNumber) {
        Link collapsedCoverage = tableModalPremium.getRow(rowNumber).getCell(BillingConstants.BillingModalPremiumTable.COVERAGE).controls.links.getFirst();
        if (collapsedCoverage.isPresent()) {
            collapsedCoverage.click();
        }
    }

}
