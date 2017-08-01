/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.auto_ca;

import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.actiontabs.DeclineActionTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Auto Quote Decline by Customer
 * @scenario
 * 1. Create Customer
 * 2. Initiated Auto Quote
 * 3. Decline quote by Customer
 * 4. Verify quote status is 'Customer Declined'
 * @details
 */
public class TestQuoteDeclineByCustomer extends AutoCaSelectBaseTest {

    @Test(groups = "6.2.1_Benefits-Base_DeclineByCustomerMasterQuote")
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteDeclineByCustomer() {
        mainApp().open();

        createCustomerIndividual();

        policy.initiate();
        PrefillTab.buttonSaveAndExit.click();

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
        PrefillTab.buttonSaveAndExit.click();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Initiated Quote #" + policyNumber);

        log.info("TEST: Decline by Customer with invalid data for Quote #" + policyNumber);

        policy.declineByCustomerQuote().start().getView().fill(
                getPolicyTD("DeclineByCustomer", "TestData")
                        .mask(AutoCaMetaData.DeclineActionTab.class.getSimpleName(),
                                AutoCaMetaData.DeclineActionTab.DECLINE_REASON.getLabel()));

        Tab.buttonOk.click();

        policy.declineByCustomerQuote().getView().getTab(DeclineActionTab.class)
                .getAssetList().getWarning(AutoCaMetaData.DeclineActionTab.DECLINE_REASON.getLabel()).verify
                .contains(String.format("'%s' is mandatory",
                        AutoCaMetaData.DeclineActionTab.DECLINE_REASON.getLabel()));

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
