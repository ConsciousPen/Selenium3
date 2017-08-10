/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.pages.summary.QuoteSummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test Initiate Auto Quote
 * @scenario
 * 1. Create Customer
 * 2. Initiated AutoSS Quote
 * 3. Verify quote status is 'Data Gathering' and policy number is present
 * @details
 */
public class TestQuoteInitiate extends AutoSSBaseTest {

	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testQuoteInitiate() {
        mainApp().open();

        createCustomerIndividual();

        CustomerSummaryPage.buttonAddQuote.click();
        QuoteSummaryPage.comboBoxProduct.setValue(PolicyType.AUTO_SS.getName());
        QuoteSummaryPage.buttonAddNewQuote.verify.enabled();
        QuoteSummaryPage.buttonAddNewQuote.click();
        new GeneralTab().getAssetList().verify.enabled();
        GeneralTab.buttonSaveAndExit.click();
        PolicySummaryPage.labelPolicyNumber.verify.present();

        log.info("Initiated Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
    }
}
