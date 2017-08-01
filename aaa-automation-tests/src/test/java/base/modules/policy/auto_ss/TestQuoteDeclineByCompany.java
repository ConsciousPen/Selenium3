/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.auto_ss;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.pages.Page;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.actiontabs.DeclineByCompanyActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

/**
 * @author Viachaslau Markouski
 * @name Test Auto Quote Decline by Company
 * @scenario
 * 1. Create Customer
 * 2. Initiated Auto (Preconfigured) Quote
 * 3. Decline quote by Company
 * 4. Verify quote status is 'Company Declined'
 * @details
 */
public class TestQuoteDeclineByCompany extends AutoSSBaseTest {

    @Test(groups = "6.2.1_Benefits-Base_DeclineByCompanyMasterQuote")
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteDeclineByCompany() {
        mainApp().open();

        createCustomerIndividual();

        policy.initiate();
        PrefillTab.buttonSaveAndExit.click();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Initiated Quote #" + policyNumber);

        log.info("TEST: Decline by Company Quote #" + policyNumber);
        policy.declineByCompanyQuote().perform(getPolicyTD("DeclineByCompany", "TestData"));

        CustomAssert.enableSoftMode();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.COMPANY_DECLINED);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, String.format(
                "Company Decline Quote %s effective %s (%s)", policyNumber,
                TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY),
                getPolicyTD("DeclineByCompany", "TestData").getValue(
                        AutoSSMetaData.DeclineByCompanyActionTab.class.getSimpleName(),
                        AutoSSMetaData.DeclineByCompanyActionTab.DECLINE_REASON.getLabel())));
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
        PrefillTab.buttonSaveAndExit.click();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Initiated Quote #" + policyNumber);

        log.info("TEST: Decline by Company with invalid data for Quote #" + policyNumber);

        policy.declineByCompanyQuote().start().getView().fill(
                getPolicyTD("DeclineByCompany", "TestData")
                        .mask(AutoSSMetaData.DeclineByCompanyActionTab.class.getSimpleName(),
                                AutoSSMetaData.DeclineByCompanyActionTab.DECLINE_REASON.getLabel()));

        DeclineByCompanyActionTab.buttonOk.click();

        policy.declineByCompanyQuote().getView().getTab(DeclineByCompanyActionTab.class)
                .getAssetList().getWarning(AutoSSMetaData.DeclineByCompanyActionTab
                        .DECLINE_REASON.getLabel()).verify.contains(String.format("'%s' is mandatory",
                AutoSSMetaData.DeclineByCompanyActionTab.DECLINE_REASON.getLabel()));

        log.info("TEST: Decline by Company and reject confirmation for Quote #" + policyNumber);

        policy.declineByCompanyQuote().getView().fill(getPolicyTD("DeclineByCompany", "TestData"));

        DeclineByCompanyActionTab.buttonOk.click();
        Page.dialogConfirmation.reject();

        policy.declineByCompanyQuote().getView().getTab(DeclineByCompanyActionTab.class)
                .getAssetList().verify.present();

        log.info("TEST: Cancel Decline by Company for Quote #" + policyNumber);

        DeclineByCompanyActionTab.buttonCancel.click();

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
