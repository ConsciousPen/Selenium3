/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary.claim;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import aaa.main.pages.summary.SummaryPage;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;

public class ClaimSummaryPage extends SummaryPage {

    public static StaticElement labelClaimNumber = new StaticElement(By.xpath("//div[@id='producContextInfoForm:header_panel']/div[@class='header']/div"));
    public static StaticElement labelClaimStatus = new StaticElement(By.id("producContextInfoForm:headerCell_1_1"));
    public static StaticElement labelClaimWithoutPolicy = new StaticElement(By.id("producContextInfoForm:headerCell_2_0"));
    public static StaticElement labelClaimWithPolicy = new StaticElement(By.id("producContextInfoForm:headerLink_2_0"));
    public static StaticElement labelSpecialHandling = new StaticElement(By.id("producContextInfoForm:headerCell_3_1"));
    public static StaticElement labelDateOfLoss = new StaticElement(By.id("producContextInfoForm:headerCell_1_0"));
    public static StaticElement labelPolicyStatus = new StaticElement(By.id("producContextInfoForm:headerCell_2_1"));
    public static StaticElement labelTypeOfClaim = new StaticElement(By.id("producContextInfoForm:headerCell_0_1"));
    public static StaticElement labelInsuredName = new StaticElement(By.id("producContextInfoForm:headerCell_0_0"));
    public static StaticElement labelTotalIncurred = new StaticElement(By.id("producContextInfoForm:headerCell_3_0"));

    public static Table tableLossEvent = new Table(By.xpath("//div[@id='productConsolidatedViewForm:scolumn_LossEvent' or @id='productConsolidatedViewForm:scolumn_PrecCLLossEvent']//table"));
    public static Table tableClaimParties = new Table(By.id("productConsolidatedViewForm:body_scolumn_ClaimsConsolidatedParty"));
    public static Table tableListOfClaims = new Table(By.id("claimList:claimListTable"));
    public static Table tableClaimCreationResult = new Table(By.xpath("//div[@id='policyDataGatherForm:claimsCreationResultsTable_ClaimsCreationResults']//table"));
    public static Table tableSubrogationInformation = new Table(By.id("productConsolidatedViewForm:body_scolumn_SubrogationSummary"));
    public static Table tableResponsibleParties = new Table(By.id("productConsolidatedViewForm:body_scolumn_ClaimsSubrogationResponsiblePartyView"));
    public static Table tableSummaryOfClaimPaymentsAndRecoveries = new Table(By.xpath("//div[@id='productConsolidatedViewForm:scolumn_ClaimsFinancialRecords']//table"));
    public static Table tableSummaryOfClaimPaymentSeries = new Table(By.xpath("//div[@id='productConsolidatedViewForm:scolumn_ClaimsPaymentSeriesRecordsSummary']//table"));
    public static Table tableFeaturePayments = new Table(By.xpath("//div[@id='policyDataGatherForm:featurePaymentsInfoTable_ClaimsFeaturePayments']//table"));
    public static Table tableFeatureReserveHistory = new Table(By.xpath("//div[@id='policyDataGatherForm:componentInstancesTable_ClaimsTransactionHistory']//table"));

    public static Button buttonSubrogationOpen = new Button(By.xpath("//input[@value='Subrogation Open']"));
    public static Button buttonSecureClaim = new Button(By.id("policyDataGatherForm:okProcessClaimActionBtn_ClaimsSecureAction_footer"));
    public static Button buttonUnsecureClaim = new Button(By.id("policyDataGatherForm:okProcessClaimActionBtn_ClaimsUnsecureAction_footer"));
    public static Button buttonSIUPotential = new Button(By.xpath("//input[@value='Indicate SIU Potential']"));
    public static Button buttonSIUReview = new Button(By.xpath("//input[@value='SIU Review' or @value='Claims SIU Review Action']"));
    public static Button buttonSIUClear = new Button(By.xpath("//input[@value='SIU Clear' or @value='Claims SIU Clear Action']"));
    public static Button buttonCreateNewClaim = new Button(By.id("claimList:newClaim"));
    public static Button buttonCreateTask = new Button(By.xpath("//a[contains(@id,'createTask') and text()='Create Task']"));
    public static Button buttonTasks = new Button(By.xpath("//*[contains(@id,'tasksList') and text()='Tasks']"));

    public static String getClaimNumber() {
        String valueNumber = "";
        String content = labelClaimNumber.getValue();

        Pattern pattern = Pattern.compile("\\#TC\\d*|\\#OC\\d*");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            valueNumber = matcher.group(0);
        }

        return valueNumber.replace("#", "");
    }
}
