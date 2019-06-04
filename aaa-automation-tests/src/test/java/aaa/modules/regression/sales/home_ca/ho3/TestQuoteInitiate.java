/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.pages.summary.QuoteSummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Ryan Yu
 * <b> Test Initiate Home Quote </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Initiated Home Quote
 * <p> 3. Verify quote status is 'Data Gathering' and policy number is present
 *
 */
public class TestQuoteInitiate extends HomeCaHO3BaseTest {

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
	public void testQuoteInitiate(@Optional("CA") String state) {
		mainApp().open();

		createCustomerIndividual();

		CustomerSummaryPage.buttonAddQuote.click();
		QuoteSummaryPage qsp = new QuoteSummaryPage();
		assertThat(qsp.buttonAddNewQuote).isEnabled();
		qsp.initiateQuote(getPolicyType());

		assertThat(policy.getDefaultView().getTab(GeneralTab.class).getAssetList()).isEnabled();
		GeneralTab.buttonSaveAndExit.click();
		assertThat(PolicySummaryPage.labelPolicyNumber).isPresent();

		log.info("Initiated Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);
	}
}
