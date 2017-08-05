/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary.claim;

import org.openqa.selenium.By;

import aaa.main.pages.summary.SummaryPage;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Table;

public class ClaimPolicySummaryPage extends SummaryPage {

    public static Table tableGeneralPolicyInformation = new Table(By.id("policyDataGatherForm:policyInfo"));
    public static Table tableRiskItemTree = new Table(By.xpath("//div[@id='policyDataGatherForm:availableRiskItemTreeTable']//table"));
    public static Table tablePolicyParties = new Table(By.id("policyDataGatherForm:body_availableParties"));

    public static void expandPolicyInsurableRisksTable() {
        Link collapsedPolicyInsurableRisksTable = new Link(By.xpath("//div[@id='policyDataGatherForm:availableRiskItemTogglePanel:header']" +
                "//td[@class='rf-cp-ico']"));
        if (collapsedPolicyInsurableRisksTable.isPresent()) {
            collapsedPolicyInsurableRisksTable.click();
        }
    }

    public static void expandInsurableRiskItem(int row) {
        Link collapsedAvailableRiskItem =
                new Link(By.xpath(String.format("//tr[@id='policyDataGatherForm:availableRiskItemTreeTable_node_%s']" +
                        "//span[contains(@class, 'ui-treetable-toggler')]", row)));
        if (collapsedAvailableRiskItem.isPresent()) {
            collapsedAvailableRiskItem.click();
        }
    }

    public static void expandCoverage(int row) {
        Link collapsedAvailableRiskItem =
                new Link(By.xpath(String.format("//tr[@id='policyDataGatherForm:availableRiskItemTreeTable_node_0_%s']" +
                        "//span[contains(@class, 'ui-treetable-toggler')]", row)));
        if (collapsedAvailableRiskItem.isPresent()) {
            collapsedAvailableRiskItem.click();
        }
    }

    public static void expandCoverage(String coverageName) {
        Link collapsedAvailableRiskItem =
                new Link(By.xpath(String.format("//tr[contains(@id, 'policyDataGatherForm:availableRiskItemTreeTable_node_0') "
                        + "and ./td[contains(., '%s')]]//span[contains(@class, 'ui-treetable-toggler')]", coverageName)));
        if (collapsedAvailableRiskItem.isPresent()) {
            collapsedAvailableRiskItem.click();
        }
    }
}
