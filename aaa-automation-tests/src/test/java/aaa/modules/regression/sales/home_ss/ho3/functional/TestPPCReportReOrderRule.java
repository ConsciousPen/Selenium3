/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.regression.sales.home_ss.ho3.functional.TestDisableReorderReport;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author Olga Reva
 * @name Test Create SS Home Policy
 * @scenario 1. Create new or open existed customer.
 * 2. Initiate HSS quote creation.
 * 3. Fill all mandatory fields on all tabs, order reports, calculate premium.
 * 4. Purchase policy.
 * 5. Verify policy status is Active on Consolidated policy view.
 * @details
 */
public class TestPPCReportReOrderRule extends HomeSSHO3BaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = {States.CA})
	@Test(groups = {Groups.SMOKE, Groups.BLOCKER})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testPolicyCreation(@Optional("AZ") String state) {

		TestData endorsementTd = testDataManager.getDefault(TestDisableReorderReport.class).getTestData("TestData_Endorsement");

		mainApp().open();
		createCustomerIndividual();
		createPolicy();

		policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		TimeSetterUtil.getInstance().nextPhase(PolicySummaryPage.getExpirationDate());

		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		purchaseRenewal(TimeSetterUtil.getInstance().getCurrentTime(), policyNumber);

		policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.getExpirationDate()).isEqualTo(PolicySummaryPage.getEffectiveDate().plusYears(1));
	}
}
