/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.common;

import aaa.admin.metadata.agencyvendor.AgencyTransferMetaData;
import aaa.admin.modules.agencyvendor.AgencyTransfer.defaulttabs.AgencyTransferTab;
import aaa.admin.pages.agencyvendor.AgencyTransferPage;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.service.EndorsementTest;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.service.auto_ss.TestPolicyEndorsementMidTerm;
import aaa.modules.regression.service.template.PolicyEndorsementMidTerm;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static aaa.main.pages.summary.PolicySummaryPage.tableTransactionHistory;
import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author S. Sivaram
 * @name Test Create Bort object
 * @scenario
 * 1. Create Policy
 * 2. Create Bort Object
 * 3. Add second Customer to Account
 * 4. Update Account
 * @details
 */
public class TestBortUserRole extends AutoSSBaseTest {
    GeneralTab gTab = new GeneralTab();
    public static Link linkTransactionHistory = new Link(By.id("historyForm:body_historyTable"));
    @Parameters({"state"})
    @Test(groups = { Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testBortCreateObject(@Optional("AZ") String state) {
        NotesAndAlertsSummaryPage notesAndAlertsSummaryPage = new NotesAndAlertsSummaryPage();
        String policyNumber = "SDSS203805910";

        //Inquiry leave notes

/*       loginAsA30();
       //Inquiry for active image
        String agentDetails = gettargetAgentDetails(policyNumber);
        //validate all transaction images
        validateAllTransactionImages();
        loginAsA30();
        //Leave and verify notes
        leaveAndVerifyNotes(notesAndAlertsSummaryPage, policyNumber);*/

        loginAsA30();
        SearchPage.openPolicy(policyNumber);
        TestData endorsementTd =testDataManager.getDefault(TestPolicyEndorsementMidTerm.class).getTestData("TestData");
        policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));
        assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);




      /*  loginAsF35();
        assertThat(gettargetAgentDetails(policyNumber)).isNotEmpty();
        log.info("Note left on policy :" + leaveNote(notesAndAlertsSummaryPage));
        mainApp().close();

        loginAsG36();
        assertThat(gettargetAgentDetails(policyNumber)).isNotEmpty();
        log.info("Note left on policy :" + leaveNote(notesAndAlertsSummaryPage));
        mainApp().close();

        loginAsE34();
        assertThat(gettargetAgentDetails(policyNumber)).isNotEmpty();
        log.info("Note left on policy :" + leaveNote(notesAndAlertsSummaryPage));
        mainApp().close();

        loginAsL41();
        assertThat(gettargetAgentDetails(policyNumber)).isNotEmpty();
        log.info("Note left on policy :" + leaveNote(notesAndAlertsSummaryPage));
        mainApp().close();*/
    }

    private void leaveAndVerifyNotes(NotesAndAlertsSummaryPage notesAndAlertsSummaryPage, String policyNumber) {
        SearchPage.openPolicy(policyNumber);
        log.info("Note left on policy :" + leaveNote(notesAndAlertsSummaryPage));
        mainApp().close();
    }

    private void validateAllTransactionImages() {
        PolicySummaryPage.TransactionHistory.open();
        int countOfTransactionType = (PolicySummaryPage.tableTransactionHistory.getRowsCount());
        for(int i=1; i<=countOfTransactionType;i++) {
        PolicySummaryPage.tableTransactionHistory.getRow(i).getCell("Type").controls.links.get(1).click();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        String newAgentDetails = gTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.AGENT).getValue();
        String newAgencyDetails = gTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY).getValue();

        assertThat(newAgentDetails.equalsIgnoreCase("Craig Crissman"));
        assertThat(newAgencyDetails.equalsIgnoreCase("AAA OKLAHOMA INSURANCE - 500001020"));
        log.info("*****************" + newAgentDetails);
        GeneralTab.buttonCancel.click();
        }
        mainApp().close();
    }

    //




    private String leaveNote(NotesAndAlertsSummaryPage notesAndAlertsSummaryPage) {
        String note = getSaltString();
        log.info("**************************************" + note);
        TestData td = getTestSpecificTD("TestData").adjust(TestData.makeKeyPath("NotesAndAlertsTab", "Note"), note);
        notesAndAlertsSummaryPage.add(td);
        CustomSoftAssertions.assertSoftly(softly -> {
            NotesAndAlertsSummaryPage.checkActivitiesAndUserNotes(note, true, softly);
        });
        return note;
    }

    private void loginAsA30() {
        mainApp().open(initiateLoginTD()
                .adjust("User","qa_roles")
                .adjust("Groups", "A30")
                .adjust("UW_AuthLevel", "00")
                .adjust("Billing_AuthLevel", "01")
        );
    }


    private void loginAsF35() {
        mainApp().open(initiateLoginTD()
                .adjust("User","qa_roles")
                .adjust("Groups", "F35")
                .adjust("UW_AuthLevel", "01")
                .adjust("Billing_AuthLevel", "01")
        );
    }

    private void loginAsG36() {
        mainApp().open(initiateLoginTD()
                .adjust("User","qa_roles")
                .adjust("Groups", "G36")
                .adjust("UW_AuthLevel", "01")
                .adjust("Billing_AuthLevel", "01")
        );
    }

    private void loginAsE34() {
        mainApp().open(initiateLoginTD()
                .adjust("User","qa_roles")
                .adjust("Groups", "E34")
                .adjust("UW_AuthLevel", "01")
                .adjust("Billing_AuthLevel", "01")
        );
    }


    private void loginAsL41() {
        mainApp().open(initiateLoginTD()
                .adjust("User","qa_roles")
                .adjust("Groups", "L41")
                .adjust("UW_AuthLevel", "04")
                .adjust("Billing_AuthLevel", "05")
        );
    }



    private String gettargetAgentDetails(String policyNumber) {

        SearchPage.openPolicy(policyNumber);
        //SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        policy.policyInquiry().start();
        String agentDetails = gTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.AGENT).getValue();
        //policy.policyInquiry().getView().fillFromTo(getTestSpecificTD("TestDataInquiryHomeSS"), aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab.class, BindTab.class, false);
       // policy.policyInquiry().getView().fillFromTo(getTestSpecificTD("TestDataInquiryAutoSS"), GeneralTab.class, DocumentsAndBindTab.class, false);
        TestData inqTD =testDataManager.getDefault(EndorsementTest.class).getTestData("TestDataInquiryAutoSS");
        //policy.policyInquiry().getView().fillFromTo(inqTD, GeneralTab.class, DocumentsAndBindTab.class, false);
        policy.policyInquiry().getView().fillFromTo(inqTD, GeneralTab.class, PremiumAndCoveragesTab.class, false);
        new PremiumAndCoveragesTab().cancel();
        /*assertThat(new BindTab().btnPurchase.isPresent()).isTrue();
        new BindTab().cancel();*/
        return agentDetails ;
    }

    private void createBortObject() {
        adminApp().open();
        AgencyTransferTab agencyTransferTab = new AgencyTransferTab();
        agencyTransferTab.createBortObject();
        TestData td = getTestSpecificTD("TestData");
        agencyTransferTab.getAssetList().fill(td);
        AgencyTransferTab.targetInsuranceAgent.setValue("House Agent AAA Az Ins Agcy Az");
        AgencyTransferTab.submit.click();

        AgencyTransferPage.buttonSearchTransfer.click();
        log.info( "No of transfer ID's: "+Integer.toString(AgencyTransferPage.tableTransfers.getRowsCount()));
        log.info("*************** "+AgencyTransferPage.tableTransfers.getRow(AgencyTransferPage.tableTransfers.getRowsCount()).getCell(3).getValue());
        log.info("Transfer ID:  "+ AgencyTransferMetaData.AgencyTransferTab.TRANSFER_ID.getLabel());

        log.info("Waiting for Object to be Submitted");
        do {AgencyTransferPage.buttonSearchTransfer.click();
            Waiters.SLEEP(3000).go();
        } while (!AgencyTransferPage.tableTransfers.getRow(AgencyTransferPage.tableTransfers.getRowsCount()).getCell(3).getValue().equalsIgnoreCase("Submitted to Batch"));
        adminApp().close();
    }

    private String getSourceAgentDetails() {
        policy.policyInquiry().start();
        String sourceAgent =  gTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.AGENT).getValue();
        assertThat(sourceAgent).contains("Foster Bottenberg");
        log.info("Source Agent: "+sourceAgent);
        mainApp().close();
        return sourceAgent;
    }


    private String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }



}
