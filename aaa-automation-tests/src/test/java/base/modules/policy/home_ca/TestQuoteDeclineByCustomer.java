/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.home_ca;

import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.actiontabs.DeclineActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Ivan Kisly
 * @name Test Home Quote Decline by Customer
 * @scenario
 * 1. Create Customer
 * 2. Initiated Home Quote
 * 3. Decline quote by Customer
 * 4. Verify quote status is 'Customer Declined'
 * @details
 */
public class TestQuoteDeclineByCustomer extends HomeCaHO3BaseTest {

    @Test(groups = "6.2.1_Benefits-Base_DeclineByCustomerMasterQuote")
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteDeclineByCustomer() {
        mainApp().open();

        createCustomerIndividual();

        policy.initiate();
        Tab.buttonSaveAndExit.click();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Initiated Quote #" + policyNumber);

        log.info("TEST: Decline by Customer Quote #" + policyNumber);
        policy.declineByCustomerQuote().perform(getPolicyTD("DeclineByCustomer", "TestData"));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.CUSTOMER_DECLINED);
    }

    @Test(groups = "6.2.1_Benefits-Base_DeclineByCustomerMasterQuote")
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteDeclineByCustomerCancellations() {
        mainApp().open();

        createCustomerIndividual();

        policy.initiate();
        Tab.buttonSaveAndExit.click();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Initiated Quote #" + policyNumber);

        log.info("TEST: Decline by Customer with invalid data for Quote #" + policyNumber);

        policy.declineByCustomerQuote().start().getView().fill(
                getPolicyTD("DeclineByCustomer", "TestData")
                        .mask(HomeCaMetaData.DeclineActionTab.class.getSimpleName(),
                                HomeCaMetaData.DeclineActionTab.DECLINE_REASON.getLabel()));

        Tab.buttonOk.click();

        policy.declineByCustomerQuote().getView().getTab(DeclineActionTab.class)
                .getAssetList().getWarning(HomeCaMetaData.DeclineActionTab.DECLINE_REASON.getLabel()).verify
                .contains(String.format("'%s' is mandatory",
                        HomeCaMetaData.DeclineActionTab.DECLINE_REASON.getLabel()));

        log.info("TEST: Decline by Customer and reject confirmation for Quote #" + policyNumber);

        policy.declineByCustomerQuote().getView().fill(getPolicyTD("DeclineByCustomer", "TestData"));

        Tab.buttonOk.click();
        Page.dialogConfirmation.reject();

        policy.declineByCustomerQuote().getView().getTab(DeclineActionTab.class)
                .getAssetList().verify.present();

        log.info("TEST: Cancel Decline by Customer for Quote #" + policyNumber);

        Tab.buttonCancel.click();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
    }
}
