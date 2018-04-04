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
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceBeforeAssetList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestLockedUWPoints extends AutoSSBaseTest {

	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private MultiInstanceBeforeAssetList aiAssetList = new DriverTab().getActivityInformationAssetList();

	private List<String> pas9063FieldsRow1 = Arrays.asList("Insurance Score","Years At Fault Accident Free","Years Conviction Free");
	private List<String> pas9063FieldsRow2 = Arrays.asList("Number of Comprehensive Claims","Number of Not-At-Fault Accidents","Emergency Roadside Usage (ERS) Activity");

	/**
	*@author Dominykas Razgunas
	*@name PA Auto Policy - UI Changes to display locked UW Points.
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
	*@details
	*/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9063")
	public void pas9063_verifyLockedUWPoints(@Optional("PA") String state) {

		verifyAlgoDate();

		// Get Reinstatement with lapse date.
		LocalDateTime reinstatementDate = TimeSetterUtil.getInstance().getCurrentTime().plusMonths(2);

		TestData testData = getPolicyTD();

		// Initiate Policy, calculate premium and open Rating Details View
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, PremiumAndCoveragesTab.class, true);

		// Save Locked UW Points value.
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String lockedTotalUWPoints = PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue();

		// Verify VRD Page for NB
		verifyLockedLimitsNB();

		// Issue Policy
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

		// Add At Fault Accident
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY).click();
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).setValue("At-Fault Accident");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).setValue("index=1");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue("05/05/2018");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).setValue("15000");

		// Add Comprehensive Claim
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY).click();
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).setValue("Comprehensive Claim");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).setValue("index=1");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue("05/05/2018");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).setValue("15000");

		// Add Non-Fault Accident
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY).click();
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).setValue("Non-Fault Accident");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).setValue("index=1");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue("05/05/2018");

		// Override insurance score
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		ratingDetailReportsTab.fillTab(getTestSpecificTD("RatingDetailReportsTab_ASD"));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.calculatePremium();

		// Verify that UW Points are the same
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);

		verifyLockedLimitsRenewalAndEndorsement();

		// Bind Endorsement. Renew Policy and Navigate to P&C Page.
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getAssetList().getAsset("Authorized By").setName("Me");
		documentsAndBindTab.submitTab();
		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.calculatePremium();

		// Verify that UW Points are the same
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);

		verifyLockedLimitsRenewalAndEndorsement();
	}

	/**
	 *@author Dominykas Razgunas
	 *@name PA Auto Policy - UI Changes to display locked UW Points. Endorsement.
	 *@scenario
	 * 1. Create Policy
	 * 2. Initiate Endorsement
	 * 3. Navigate to P&C View Rating Details.
	 * 4. Check that all of the UW components are blank.
	 *@details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9063")
	public void pas9063_verifyLockedUWPointsEndorsement(@Optional("PA") String state) {

		verifyAlgoDate();

		// Create Policy
		mainApp().open();
		getCopiedPolicy();

		// Initiate Endorsement and Navigate to P&C Page.
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.calculatePremium();

		// Verify that Total UW points are shown and other UW components are hidden
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		verifyLockedLimitsRenewalAndEndorsement();
	}

	/**
	 *@author Dominykas Razgunas
	 *@name PA Auto Policy - UI Changes to display locked UW Points. Renewal.
	 *@scenario
	 * 1. Create Policy
	 * 2. Initiate Renewal
	 * 3. Navigate to P&C View Rating Details.
	 * 4. Check that all of the UW components are blank.
	 *@details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9063")
	public void pas9063_verifyLockedUWPointsRenewal(@Optional("PA") String state) {

		verifyAlgoDate();

		// Create Policy
		mainApp().open();
		getCopiedPolicy();

		// Initiate Renewal and Navigate to P&C Page.
		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.calculatePremium();

		// Verify that Total UW points are shown and other UW components are hidden
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		verifyLockedLimitsRenewalAndEndorsement();
	}

	//TODO remove verify algo date after 2018-06-20
	private void verifyAlgoDate() {
		LocalDateTime algoEffectiveDate = LocalDateTime.of(2018, Month.JUNE, 20, 0, 0);
		if (TimeSetterUtil.getInstance().getCurrentTime().isBefore(algoEffectiveDate)) {
			TimeSetterUtil.getInstance().nextPhase(algoEffectiveDate);
		}
	}

	private void verifyLockedLimitsNB(){
		// Verify that Insurance Score, YAFAF and YCF scores are displayed.
		pas9063FieldsRow1.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, f)).isPresent());
		pas9063FieldsRow1.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, f).getCell("Score").getValue()).isNotEmpty());

		// Verify that Number of Comp Claims, Number of NAFA and Emergency Roadside Usage scores are displayed.
		pas9063FieldsRow2.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, f)).isPresent());
		pas9063FieldsRow2.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, f).getCell(6).getValue()).isNotEmpty());
	}

	private void verifyLockedLimitsRenewalAndEndorsement(){
		//Verify that Total UW points are not blank
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).isNotEmpty();

		// Verify that Insurance Score, YAFAF, YCF scores are not displayed.
		pas9063FieldsRow1.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, f)).isPresent());
		pas9063FieldsRow1.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, f).getCell("Score").getValue()).isEmpty());

		// Verify that Number of Comp Claims, Number of NAFA and Emergency Roadside Usage scores are not displayed.
		pas9063FieldsRow2.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, f)).isPresent());
		pas9063FieldsRow2.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, f).getCell(6).getValue()).isEmpty());
	}

}
