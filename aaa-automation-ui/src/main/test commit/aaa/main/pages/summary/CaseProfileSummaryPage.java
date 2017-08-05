/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;

public class CaseProfileSummaryPage extends SummaryPage {

    public static StaticElement labelCaseProfileName = new StaticElement(By.id("caseProfileContextInfoForm:caseProfile_caseProfileName"));
    public static StaticElement labelCaseProfileNumber = new StaticElement(By.id("caseProfileContextInfoForm:caseProfile_caseNum"));
    public static StaticElement labelCaseProfileStatus = new StaticElement(By.id("caseProfileContextInfoForm:caseProfile_timedStatusCdText"));
    public static StaticElement labelCaseProfileEffectiveDate = new StaticElement(By.id("caseProfileContextInfoForm:caseProfile_effectiveDate"));

    public static Table tableSelectCaseProfile = new Table(By.id("casesList:body_cases"));
    public static Table tableProposal = new Table(By.xpath("//div[@id='groupProposalConsolidatedForm:consolidatedProposalsTable']//table"));
    public static Table tableCaseProfileVersionHistory = new Table(By.id("caseVersionHistoryView:body_caseVersionHistoryTable"));
    public static Table tableClassificationGroups = new Table(By.id("consolidatedView:body_classificationGroupsTable"));

    public static Button buttonAddNewProposal = new Button(By.id("groupProposalConsolidatedForm:groupProposalConsolidatedAddProposal"));
    public static Button buttonCaseProfileVersionHistory = new Button(By.id("caseProfileContextInfoForm:lnkCaseProfileHistory"));

    public static String getEffectiveDate() {
        return CaseProfileSummaryPage.labelCaseProfileEffectiveDate.getValue().replace("(", "").replace(")", "").trim();

    }
}
