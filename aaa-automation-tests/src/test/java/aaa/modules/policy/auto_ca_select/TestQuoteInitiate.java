/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  aaa.modules.policy.auto_ca_select;

import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.pages.summary.QuoteSummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Xiaolan Ge
 * @name Test Initiate Auto Quote
 * @scenario
 * 1. Create Customer
 * 2. Initiated Auto Quote
 * 3. Verify quote status is 'Data Gathering' and policy number is present
 * @details
 */
public class TestQuoteInitiate extends AutoCaSelectBaseTest {

    @Test()
    @TestInfo(component = "Policy.AutoCA")
    public void testQuoteInitiate() {
        mainApp().open();

        createCustomerIndividual();

        CustomerSummaryPage.buttonAddQuote.click();
        QuoteSummaryPage.comboBoxProduct.setValue(PolicyType.AUTO_CA_SELECT.getName());
        QuoteSummaryPage.buttonAddNewQuote.verify.enabled();
        QuoteSummaryPage.buttonAddNewQuote.click();
        //new GeneralPolicyTab().getAssetList().verify.enabled();
        Tab.buttonSaveAndExit.click();
        PolicySummaryPage.labelPolicyNumber.verify.present();

        log.info("Initiated Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
    }
}
