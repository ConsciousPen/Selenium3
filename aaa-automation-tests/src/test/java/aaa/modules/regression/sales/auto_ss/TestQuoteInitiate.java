/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.pages.summary.QuoteSummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * <b> Test Initiate Auto Quote </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Initiated AutoSS Quote
 * <p> 3. Verify quote status is 'Data Gathering' and policy number is present
 *
 */
public class TestQuoteInitiate extends AutoSSBaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteInitiate(@Optional("") String state) {
		mainApp().open();

		createCustomerIndividual();

		CustomerSummaryPage.buttonAddQuote.click();
		QuoteSummaryPage qsp = new QuoteSummaryPage();
		assertThat(qsp.buttonAddNewQuote).isEnabled();
		qsp.initiateQuote(getPolicyType());
		Tab.buttonSaveAndExit.click();
		assertThat(PolicySummaryPage.labelPolicyNumber).isPresent();

		log.info("Initiated Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);
	}
}
