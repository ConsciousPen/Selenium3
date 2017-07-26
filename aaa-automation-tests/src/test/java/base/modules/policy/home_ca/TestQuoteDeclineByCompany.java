/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.home_ca;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.actiontabs.DeclineActionTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

/**
 * @author Ivan Kisly
 * @name Test Home Quote Decline by Company
 * @scenario
 * 1. Create Customer
 * 2. Initiated Home Quote
 * 3. Decline quote by Company
 * 4. Verify quote status is 'Company Declined'
 * @details
 */
public class TestQuoteDeclineByCompany extends HomeCaHO3BaseTest {

    @Test(groups = "6.2.1_Benefits-Base_DeclineByCompanyMasterQuote")
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteDeclineByCompany() {
        mainApp().open();

        createCustomerIndividual();

        policy.initiate();
        Tab.buttonSaveAndExit.click();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Initiated Quote #" + policyNumber);

        log.info("TEST: Decline by Company Quote #" + policyNumber);
        policy.declineByCompanyQuote().perform(tdPolicy.getTestData("DeclineByCompany", "TestData"));

        CustomAssert.enableSoftMode();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.COMPANY_DECLINED);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, String.format(
                "Company Decline Quote %s effective %s (%s)", policyNumber,
                TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY),
                tdPolicy.getTestData("DeclineByCompany", "TestData").getValue(
                        HomeCaMetaData.DeclineActionTab.class.getSimpleName(),
                        HomeCaMetaData.DeclineActionTab.DECLINE_REASON.getLabel())));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Finished");

        CustomAssert.assertAll();
        CustomAssert.disableSoftMode();
    }

    @Test(groups = "6.2.1_Benefits-Base_DeclineByCompanyMasterQuote")
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteDeclineByCompanyCancellations() {
        mainApp().open();

        createCustomerIndividual();

        policy.initiate();
        Tab.buttonSaveAndExit.click();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Initiated Quote #" + policyNumber);

        log.info("TEST: Decline by Company with invalid data for Quote #" + policyNumber);

        policy.declineByCompanyQuote().start().getView().fill(
                tdPolicy.getTestData("DeclineByCompany", "TestData")
                        .mask(HomeCaMetaData.DeclineActionTab.class.getSimpleName(),
                                HomeCaMetaData.DeclineActionTab.DECLINE_REASON.getLabel()));

        Tab.buttonOk.click();

        policy.declineByCompanyQuote().getView().getTab(DeclineActionTab.class)
                .getAssetList().getWarning(HomeCaMetaData.DeclineActionTab.DECLINE_REASON.getLabel()).verify
                .contains(String.format("'%s' is mandatory",
                        HomeCaMetaData.DeclineActionTab.DECLINE_REASON.getLabel()));

        log.info("TEST: Decline by Company and reject confirmation for Quote #" + policyNumber);

        policy.declineByCompanyQuote().getView().fill(tdPolicy.getTestData("DeclineByCompany", "TestData"));

        Tab.buttonOk.click();
        Page.dialogConfirmation.reject();

        policy.declineByCompanyQuote().getView().getTab(DeclineActionTab.class)
                .getAssetList().verify.present();

        log.info("TEST: Cancel Decline by Company for Quote #" + policyNumber);

        Tab.buttonCancel.click();

        CustomAssert.enableSoftMode();

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, String.format(
                "Company Decline Quote %s effective %s", policyNumber,
                TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY)));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Canceled");

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);

        CustomAssert.assertAll();
        CustomAssert.disableSoftMode();
    }
}
