/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.home_ca_ho3;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.pages.summary.QuoteSummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Ryan Yu
 * @name Test Initiate Home Quote
 * @scenario
 * 1. Create Customer
 * 2. Initiated Home Quote
 * 3. Verify quote status is 'Data Gathering' and policy number is present
 * @details
 */
public class TestQuoteInitiate extends HomeCaHO3BaseTest {

	@Test
	@TestInfo(component = "Policy.HomeCA")
	public void testQuoteInitiate() {
		mainApp().open();

		createCustomerIndividual();

		CustomerSummaryPage.buttonAddQuote.click();
		QuoteSummaryPage.comboBoxProduct.setValue(getPolicyType().getName());
		QuoteSummaryPage.buttonAddNewQuote.verify.enabled();
		QuoteSummaryPage.buttonAddNewQuote.click();
		policy.getDefaultView().getTab(GeneralTab.class).getAssetList().verify.enabled();
		GeneralTab.buttonSaveAndExit.click();
		PolicySummaryPage.labelPolicyNumber.verify.present();

		log.info("Initiated Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
	}
}
