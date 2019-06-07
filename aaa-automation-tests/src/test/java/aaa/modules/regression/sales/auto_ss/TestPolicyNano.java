/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * <b> Test Named-non-owner auto policy creation </b>
 * <p> Steps:
 *
 */
public class TestPolicyNano extends AutoSSBaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA, States.NY })
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testPolicyNano(@Optional("") String state) {

		mainApp().open();

		createCustomerIndividual();

		log.info("Nano Policy Creation Started...");

		TestData td = getTestSpecificTD("TestData");
		getPolicyType().get().createPolicy(td);

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}

}
