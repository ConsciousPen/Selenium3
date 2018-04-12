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
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceBeforeAssetList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

public class TestLockedUWPoints extends AutoSSBaseTest {

	private DriverTab driverTab = new DriverTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private ErrorTab errorTab = new ErrorTab();
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
	 * 10. Check the sum of UW Points. Check that all of the UW components are not blank.
	 * 11. Bind Endorsement.
	 * 12. Change system date to Have upcoming renewal active.
	 * 13. Initiate Renewal.
	 * 14. Navigate to P&C View Rating Details.
	 * 15. Check the sum of UW Points. Check that all of the UW components are blank.
	 * 16. Override Errors.
	 * 17. Issue Renewal.
	 * 18. Pay min due for renewal and navigate to renewal
	 * 19. Endorse renewal and Navigate to P&C View Rating Details.
	 * 20. Check the sum of UW Points. Check that all of the UW components are blank.
	*@details
	*/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9063, PAS-12443")
	public void pas9063_verifyLockedUWPoints(@Optional("PA") String state) {

		// Get Reinstatement with lapse date.
		LocalDateTime reinstatementDate = TimeSetterUtil.getInstance().getCurrentTime().plusMonths(2);
        TimeSetterUtil.getInstance().confirmDateIsAfter(LocalDateTime.of(2018, Month.JUNE, 20, 0, 0));

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

		// Add At Fault Accident, Add Conviction date, Add Comprehensive Claim, Add 2 Non-Fault Accidents
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		driverTab.fillTab(getTestSpecificTD("TestData_DriverTab"));

		// Override insurance score
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		ratingDetailReportsTab.fillTab(getTestSpecificTD("RatingDetailReportsTab_ASD"));

		openVRD();

		// Verify that UW Points are the same
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);

		// Endorsement for NB which is initiated should show behave the same as NB
		verifyLockedLimitsNB();

		// Bind Endorsement. Renew Policy and Navigate to P&C Page.
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.fillTab(getTestSpecificTD("TestData_Authorized"));
		documentsAndBindTab.submitTab();

		// Change system date
		LocalDateTime reneweff = TimeSetterUtil.getInstance().getCurrentTime().plusMonths(10);
		TimeSetterUtil.getInstance().nextPhase(reneweff);
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		// Initiate Renewal Navigate to P&C and calculate premium
		policy.renew().start();
		openVRD();

		// Verify that UW Points are the same
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);
		verifyLockedLimitsRenewalAndEndorsement();

		// Override errors Issue Renewal
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();
		errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_200005);
		errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA__200009_PA);
		errorTab.override();
		documentsAndBindTab.submitTab();

		purchaseRenewal(reneweff, policyNumber);

		// Navigate to Renewal
		PolicySummaryPage.buttonRenewals.click();

		// Initiate Endorsement and Navigate to P&C Page.
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		openVRD();

		// Verify that Total UW points are shown and other UW components are hidden
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);
		verifyLockedLimitsRenewalAndEndorsement();
	}

	/**
	 *@author Dominykas Razgunas
	 *@name PA Auto Policy - UI Changes to display locked UW Points. Endorsement.
	 *@scenario
	 * 1. Create Policy
	 * 2. Change system date for the renewal
	 * 3. Issue Renewal
	 * 4. Initiate Endorsement
	 * 3. Navigate to P&C View Rating Details.
	 * 4. Check that all of the UW components are blank.
	 *@details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9063, PAS-12443")
	public void pas9063_verifyLockedUWPointsEndorsement(@Optional("PA") String state) {

        TimeSetterUtil.getInstance().confirmDateIsAfter(LocalDateTime.of(2018, Month.JUNE, 20, 0, 0));

		// Create Policy
		mainApp().open();
		getCopiedPolicy();
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		// Change Time to renew policy and have and issued renewal
		LocalDateTime reneweff = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1);
		TimeSetterUtil.getInstance().nextPhase(reneweff);

		// Issue Renewal
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();

		purchaseRenewal(reneweff, policyNumber);

		// Navigate to Renewal
		PolicySummaryPage.buttonRenewals.click();

		// Initiate Endorsement and Navigate to P&C Page.
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		openVRD();

		// Verify that Total UW points are shown and other UW components are hidden
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9063")
	public void pas9063_verifyLockedUWPointsRenewal(@Optional("PA") String state) {

        TimeSetterUtil.getInstance().confirmDateIsAfter(LocalDateTime.of(2018, Month.JUNE, 20, 0, 0));

		// Create Policy
		mainApp().open();
		getCopiedPolicy();

		// Initiate Renewal and Navigate to P&C Page.
		policy.renew().start();
		openVRD();

		// Verify that Total UW points are shown and other UW components are hidden
		verifyLockedLimitsRenewalAndEndorsement();
	}

	/**
	 *@author Dominykas Razgunas
	 *@name PA Auto Policy - UI Changes to display locked UW Points. Conversion.
	 *@scenario
	 * 1. Initiate Conversion Policy
	 * 2. Navigate to P&C page calculate premium
	 * 3. Check that all of the UW components are have scores and Save Total UW score.
	 * 4. Fill The Policy details
	 * 5. Override errors
	 * 6. Bind Policy
	 * 7. Navigate to Billing Account
	 * 8. Accept payment of min due
	 * 9. Navigate to Policy Summary
	 * 10. Check that Policy is active
	 * 11. Endorse Policy change components and Navigate to P&C View Rating Details.
	 * 10. Check the sum of UW Points. Check that all of the UW components are not blank.
	 * 11. Bind Endorsement.
	 * 12. Change system date to Have upcoming renewal active.
	 * 11. Initiate Renewal
	 * 12. Navigate to P&C
	 * 13. Calculate Premium
	 * 14. Check that Total UW score is the same as in Conversion policy
	 * 15. Check that all UW components scores are hidden
	 * 16. Issue Renewal
	 * 17. Pay for the renewal
	 * 18. Navigate to renewal
	 * 19. Endorse Renewal and Navigate to P&C View Rating Details
	 * 20. Check the sum of UW Points. Check that all of the UW components are blank
	 *@details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9063, PAS-12443")
	public void pas9063_verifyLockedUWPointsConversion(@Optional("PA") String state) {

        TimeSetterUtil.getInstance().confirmDateIsAfter(LocalDateTime.of(2018, Month.JUNE, 20, 0, 0));

		// get time for min due payments
		String today = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		LocalDateTime effDate = TimeSetterUtil.getInstance().getCurrentTime();

		mainApp().open();
		createCustomerIndividual();

		// Get Testdata for initiating and issuing conversion policy
		TestData tdPolicy = getConversionPolicyDefaultTD().adjust(TestData.makeKeyPath(DocumentsAndBindTab.class.getSimpleName(),
				AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(),
				AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.PENNSYLVANIA_NOTICE_TO_NAMED_INSURED_REGARDING_TORT_OPTIONS.getLabel()), "Physically Signed");

		TestData tdManualConversionInitiation = getManualConversionInitiationTd().adjust(TestData.makeKeyPath(InitiateRenewalEntryActionTab.class.getSimpleName(),
				CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_EFFECTIVE_DATE.getLabel()), today);

		// Initiate conversion and fill policy up to P&C tab
		customer.initiateRenewalEntry().perform(tdManualConversionInitiation);
		policy.getDefaultView().fillUpTo(tdPolicy, PremiumAndCoveragesTab.class, true);

		// Save Locked UW Points value. Verify VRD Page for NB
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		String lockedTotalUWPoints = PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue();
		verifyLockedLimitsNB();

		// Issue Policy
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		premiumAndCoveragesTab.submitTab();
		policy.getDefaultView().fillFromTo(tdPolicy, DriverActivityReportsTab.class, DocumentsAndBindTab.class, true);
		documentsAndBindTab.submitTab();
		errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_CSACN0100);
		errorTab.override();
		documentsAndBindTab.submitTab();
		String policyNum = PolicySummaryPage.getPolicyNumber();

		purchaseRenewal(effDate, policyNum);

		// Initiate Endorsement and Navigate to P&C Page.
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		// Add At Fault Accident, Add Conviction date, Add Comprehensive Claim, Add 2 Non-Fault Accidents
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		driverTab.fillTab(getTestSpecificTD("TestData_DriverTab"));

		// Override insurance score
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		ratingDetailReportsTab.fillTab(getTestSpecificTD("RatingDetailReportsTab_ASD"));

		openVRD();

		// Verify that UW Points are the same
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);

		// Endorsement for NB which is initiated should show behave the same as NB
		verifyLockedLimitsRenewalAndEndorsement();

		// Bind Endorsement. Renew Policy and Navigate to P&C Page.
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();

		// Change system date
		LocalDateTime reneweff = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1);
		TimeSetterUtil.getInstance().nextPhase(reneweff);
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNum);

		// Initiate Renewal Navigate to P&C and calculate premium
		policy.renew().start();
		openVRD();

		// Verify that UW Points are the same
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);
		verifyLockedLimitsRenewalAndEndorsement();

		// Issue Renewal
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();

		purchaseRenewal(reneweff, policyNum);

		// Navigate to Renewal
		PolicySummaryPage.buttonRenewals.click();

		// Initiate Endorsement and Navigate to P&C Page.
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		openVRD();

		// Verify that Total UW score is shown and other UW components are hidden
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);
		verifyLockedLimitsRenewalAndEndorsement();
	}

	private void openVRD(){
		// Navigate to P&C. Calculate Premium. Open VRD.
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.calculatePremium();
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
	}

	private void purchaseRenewal(LocalDateTime minDueDate, String policyNumber){

		// Open Billing account and Pay min due for the renewal
		SearchPage.openBilling(policyNumber);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(minDueDate, BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);

		// Open Policy
		SearchPage.openPolicy(policyNumber);
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