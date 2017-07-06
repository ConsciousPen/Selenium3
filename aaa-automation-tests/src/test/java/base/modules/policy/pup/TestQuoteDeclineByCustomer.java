/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.common.pages.Page;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.actiontabs.DeclineByCustomerActionTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Polina Kaziuchyts
 * @name Test Umbrella Quote Decline by Customer
 * @scenario
 * 1. Create Customer
 * 2. Initiated Home (Preconfigured) Quote
 * 3. Decline quote by Customer
 * 4. Verify quote status is 'Customer Declined'
 * @details
 */
public class TestQuoteDeclineByCustomer extends PersonalUmbrellaBaseTest {

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
        policy.declineByCustomerQuote().perform(tdPolicy.getTestData("DeclineByCustomer", "TestData"));
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
        policy.declineByCustomerQuote().start().getView().fill(tdPolicy.getTestData("DeclineByCustomer", "TestData")
                .mask(PersonalUmbrellaMetaData.DeclineByCustomerActionTab.class.getSimpleName(),
                        PersonalUmbrellaMetaData.DeclineByCustomerActionTab.DECLINE_REASON.getLabel()));

        DeclineByCustomerActionTab.buttonOk.click();

        policy.declineByCustomerQuote().getView().getTab(DeclineByCustomerActionTab.class).getAssetList()
                .getWarning(PersonalUmbrellaMetaData.DeclineByCustomerActionTab.DECLINE_REASON.getLabel()).verify
                        .contains(String.format("'%s' is mandatory", PersonalUmbrellaMetaData.DeclineByCustomerActionTab.DECLINE_REASON.getLabel()));

        log.info("TEST: Decline by Customer and reject confirmation for Quote #" + policyNumber);
        policy.declineByCustomerQuote().getView().fill(tdPolicy.getTestData("DeclineByCustomer", "TestData"));

        DeclineByCustomerActionTab.buttonOk.click();
        Page.dialogConfirmation.reject();

        policy.declineByCustomerQuote().getView().getTab(DeclineByCustomerActionTab.class).getAssetList().verify.present();

        log.info("TEST: Cancel Decline by Customer for Quote #" + policyNumber);
        DeclineByCustomerActionTab.buttonCancel.click();
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
    }
}
