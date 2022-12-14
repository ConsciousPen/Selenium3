/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

/**
 * @author Xiaolan Ge
 * <b> Test Backdated Auto CA Select policy creation </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Create Backdated Auto CA Select policy
 * <p> 3. Verify Policy status is 'Policy Active'
 * <p> 4. Verify Policy is backdated
 *
 */
public class TestPolicyBackdated extends AutoCaSelectBaseTest {

	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void testPolicyBackdated(@Optional("CA") String state) {
		mainApp().open();

		createCustomerIndividual();

		log.info("Policy Creation Started...");

		//adjust default policy data with
		//1. effective date = today minus 10 days
		//2. error tab: "Policy cannot be backdated" error should be overridden 
		String date = DateTimeUtils.getCurrentDateTime().minusDays(10).format(DateTimeUtils.MM_DD_YYYY);
		TestData td = getBackDatedPolicyTD(date);
		getPolicyType().get().createPolicy(td);

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		assertThat(PolicySummaryPage.labelPolicyEffectiveDate).valueContains(date);

	}
}
