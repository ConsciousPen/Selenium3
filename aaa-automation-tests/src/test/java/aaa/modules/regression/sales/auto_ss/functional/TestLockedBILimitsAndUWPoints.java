/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
* CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestLockedBILimitsAndUWPoints extends AutoSSBaseTest {

	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	/**
	*@author Dominykas Razgunas
	*@name PA Auto Policy - UI Changes to display locked BI Limits & UW Points.
	*@scenario
	 * 1. Initiate quote creation
	 * 2. Rate quote
	 * 3. Open rating detail view
	 * 4. Save the sum of UW Points. Check all of the UW components.
	 * 5. Issue Policy.
	 * 6. Cancel Policy.
	 * 7. Change system date +2months.
	 * 8. Reinstate Policy.
	 * 9. Endorse Policy and Navigate to P&C View Rating Details.
	 * 10. Check the sum of UW Points. Check that all of the UW components are blank.
	 * 11. Save and Exit Endorsement.
	 * 12. Initiate Renewal.
	 * 13. Navigate to P&C View Rating Details.
	 * 14. Check the sum of UW Points. Check that all of the UW components are blank.
	 * 15. Close Application Change system date to current date.
	*@details
	*/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9063")
	public void pas9063_verifyLockedUWPointsAndBILimits(@Optional("PA") String state) {

		// Get current system date
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime().plusHours(1);

		verifyAlgoDate();

		// Get Reinstatement with lapse date.
		LocalDateTime reinstatementDate = TimeSetterUtil.getInstance().getCurrentTime().plusMonths(2);

		TestData testData = getPolicyTD();

		// Initiate Policy, calculate premium and open Rating Details View
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, PremiumAndCoveragesTab.class, true);
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		// Save Locked UW Points value.
		String lockedTotalUWPoints = PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue();

		// Verify that Age of NI, AAA persistency, Days lapsed, Prior Term BI, Reinstatement history is present
		List<String> pas9063FieldsRow1a = Arrays.asList("Age of the Named Insured Used in Tier","AAA Insurance Persistency","Days Lapsed", "Prior Term Bodily Injury (BI) limit", "Reinstatement History");
		pas9063FieldsRow1a.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, f).isPresent()).isTrue());

		// Verify that Insurance Score, YAFAF and YCF scores are displayed.
		List<String> pas9063FieldsRow1 = Arrays.asList("Insurance Score","Years At Fault Accident Free","Years Conviction Free");
		pas9063FieldsRow1.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, f).isPresent()).isTrue());

		pas9063FieldsRow1.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, f).getCell("Score").getValue().isEmpty()).isFalse());

		// Verify that Number of Comp Claims, Number of NAFA and Emergency Roadside Usage scores are displayed.
		List<String> pas9063FieldsRow2 = Arrays.asList("Number of Comprehensive Claims","Number of Not-At-Fault Accidents","Emergency Roadside Usage (ERS) Activity");
		pas9063FieldsRow2.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, f).isPresent()).isTrue());

		pas9063FieldsRow2.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, f).getCell(6).getValue().isEmpty()).isFalse());

		// Issue Policy
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		premiumAndCoveragesTab.submitTab();
		policy.getDefaultView().fillFromTo(testData, DriverActivityReportsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		// Cancel Policy
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		//Change system date to get policy reinstated with lapse
		TimeSetterUtil.getInstance().nextPhase(reinstatementDate);
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		//Reinstate policy
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));

		// Initiate Endorsement and Navigate to P&C Page.
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.calculatePremium();
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		// Verify that UW Points are the same
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);

		// Verify that Age of NI, AAA persistency, Days lapsed, Prior Term BI, Reinstatement History scores are not displayed
		pas9063FieldsRow1a.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, f).getCell("Score").getValue().isEmpty()).isTrue());

		// Verify that Insurance Score, YAFAF, YCF scores are not displayed.
		pas9063FieldsRow1.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, f).getCell("Score").getValue().isEmpty()).isTrue());

		// Verify that Number of Comp Claims, Number of NAFA and Emergency Roadside Usage scores are not displayed.
		pas9063FieldsRow2.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, f).getCell(6).getValue().isEmpty()).isTrue());

		// Save and Exit Endorsement. Renew Policy and Navigate to P&C Page.
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		PremiumAndCoveragesTab.buttonSaveAndExit.click();
		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.calculatePremium();
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		// Verify that UW Points are the same
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);

		// Verify that Age of NI, AAA persistency, Days lapsed, Prior Term BI, Reinstatement History scores are not displayed
		pas9063FieldsRow1a.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, f).getCell("Score").getValue().isEmpty()).isTrue());

		// Verify that Insurance Score, YAFAF, YCF scores are not displayed.
		pas9063FieldsRow1.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, f).getCell("Score").getValue().isEmpty()).isTrue());

		// Verify that Number of Comp Claims, Number of NAFA and Emergency Roadside Usage scores are not displayed.
		pas9063FieldsRow2.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, f).getCell(6).getValue().isEmpty()).isTrue());

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(today);
	}

	//TODO remove verify algo date after 2018-06-20
	private void verifyAlgoDate() {
		LocalDateTime algoEffectiveDate = LocalDateTime.of(2018, Month.JUNE, 20, 0, 0);
		if (TimeSetterUtil.getInstance().getCurrentTime().isBefore(algoEffectiveDate)) {
			TimeSetterUtil.getInstance().nextPhase(algoEffectiveDate);
		}
	}

}
