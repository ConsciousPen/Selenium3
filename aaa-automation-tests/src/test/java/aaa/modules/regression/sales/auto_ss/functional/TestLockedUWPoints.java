package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

@StateList(states = Constants.States.PA)
public class TestLockedUWPoints extends AutoSSBaseTest {

	private DriverTab driverTab = new DriverTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private ErrorTab errorTab = new ErrorTab();

	private List<String> pas9063FieldsRow1 = Collections.synchronizedList(new ArrayList<>(Arrays.asList("Insurance Score","Years At Fault Accident Free","Years Conviction Free")));
	private List<String> pas9063FieldsRow2 = Collections.synchronizedList(new ArrayList<>(Arrays.asList("Number of Comprehensive Claims","Number of Not-At-Fault Accidents","Emergency Roadside Usage (ERS) Activity")));

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
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9063, PAS-12443")
	public void pas9063_verifyLockedUWPoints(@Optional("PA") String state) {

		TestData testData = getPolicyTD();

		// Initiate Policy, calculate premium and open Rating Details View
		createQuoteAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		// Save Locked UW Points value.
		PremiumAndCoveragesTab.RatingDetailsView.open();
		String lockedTotalUWPoints = PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue();

		// Verify VRD Page for NB
		verifyLockedLimitsNB();

		// Issue Policy
		premiumAndCoveragesTab.submitTab();
		policy.getDefaultView().fillFromTo(testData, DriverActivityReportsTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime reinstatementDate = PolicySummaryPage.getEffectiveDate().plusMonths(2);

		// Cancel Policy
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		//Change system date to get policy reinstated with lapse
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(reinstatementDate);
		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		//Reinstate policy
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));

		// Initiate Endorsement
		endorsementChanges();

		openVRD();

		// Verify that UW Points are the same
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);

		// Endorsement Validations
		verifyLockedLimitsRenewalAndEndorsement();

		// Bind Endorsement. Renew Policy and Navigate to P&C Page.
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.fillTab(getTestSpecificTD("TestData_Authorized"));
		documentsAndBindTab.submitTab();
		setDoNotRenewFlag(policyNumber);

		// Change system date
		//LocalDateTime renewalEff = reinstatementDate.plusMonths(10);
		LocalDateTime renewalOfferDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);	
		
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(renewalOfferDate);
		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		// Initiate Renewal Navigate to P&C and calculate premium
        policy.removeDoNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));
		policy.renew().start();
		openVRD();

		// Verify that UW Points are the same
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);
		verifyLockedLimitsRenewalAndEndorsement();

		// Override errors Issue Renewal
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();
		errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_200005);
		errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_200009);
		errorTab.override();
		documentsAndBindTab.submitTab();

		payTotalAmtDue(policyNumber);

		// Navigate to Renewal
		PolicySummaryPage.buttonRenewals.click();
		String renewalEffectiveDate = PolicySummaryPage.labelPolicyEffectiveDate.getValue();

		// Initiate Endorsement and Navigate to P&C Page.
		//policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		TestData adjustedEndorsementActionData = getPolicyTD("Endorsement", "TestData").getTestData("EndorsementActionTab").adjust("Endorsement Date", renewalEffectiveDate);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData").adjust("EndorsementActionTab", adjustedEndorsementActionData));
		
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
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9063, PAS-12443")
	public void pas9063_verifyLockedUWPointsEndorsement(@Optional("PA") String state) {

		// Create Policy
		String policyNumber = openAppAndCreatePolicy();
		LocalDateTime renewalEff = PolicySummaryPage.getExpirationDate();
		setDoNotRenewFlag(policyNumber);

		// Change Time to renew policy and have and issued renewal
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(renewalEff);

		// Issue Renewal
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
        policy.removeDoNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));
		policy.renew().start();
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();

		payTotalAmtDue(policyNumber);

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
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9063")
	public void pas9063_verifyLockedUWPointsRenewal(@Optional("PA") String state) {

		// Create Policy
		openAppAndCreatePolicy();

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
	 * 14. Check that Total UW score is the same as in Conversion policies Endorsement
	 * 15. Check that all UW components scores are hidden
	 * 16. Issue Renewal
	 * 17. Pay for the renewal
	 * 18. Navigate to renewal
	 * 19. Endorse Renewal and Navigate to P&C View Rating Details
	 * 20. Check the sum of UW Points. Check that all of the UW components are blank
	 *@details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9063, PAS-12443")
	public void pas9063_verifyLockedUWPointsConversion(@Optional("PA") String state) {

		// get time for min due payments
		LocalDateTime effDate = TimeSetterUtil.getInstance().getCurrentTime();
		String today = effDate.format(DateTimeUtils.MM_DD_YYYY);

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
		PremiumAndCoveragesTab.RatingDetailsView.open();
		String lockedTotalUWPoints = PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue();
		verifyLockedLimitsNB();

		// Issue Policy
		PremiumAndCoveragesTab.RatingDetailsView.close();
		premiumAndCoveragesTab.submitTab();
		policy.getDefaultView().fillFromTo(tdPolicy, DriverActivityReportsTab.class, DocumentsAndBindTab.class, true);
		documentsAndBindTab.submitTab();
		errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_CSACN0100);
		errorTab.override();
		documentsAndBindTab.submitTab();
		PolicySummaryPage.buttonBackFromRenewals.click();
		String policyNum = PolicySummaryPage.getPolicyNumber();

		payTotalAmtDue(policyNum);

		// Initiate Endorsement
		endorsementChanges();

		openVRD();

		// Verify that UW Points are the same
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);

		// Endorsement Validations
		verifyLockedLimitsRenewalAndEndorsement();

		// Bind Endorsement. Renew Policy and Navigate to P&C Page.
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();
        setDoNotRenewFlag(policyNum);

		// Change system date
        LocalDateTime renewalEff = effDate.plusYears(1);
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(renewalEff);
		mainApp().open();
		SearchPage.openPolicy(policyNum);

		// Initiate Renewal Navigate to P&C and calculate premium
        policy.removeDoNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));
		policy.renew().start();
		openVRD();

		// Verify that UW Points are the same
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);
		verifyLockedLimitsRenewalAndEndorsement();

		// Issue Renewal
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();

		payTotalAmtDue(policyNum);

		// Navigate to Renewal
		PolicySummaryPage.buttonRenewals.click();

		// Initiate Endorsement and Navigate to P&C Page.
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		openVRD();

		// Verify that Total UW score is shown and other UW components are hidden
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue()).contains(lockedTotalUWPoints);
		verifyLockedLimitsRenewalAndEndorsement();
	}

	private void endorsementChanges(){
		// Initiate Endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		// Add At Fault Accident, Add Conviction date, Add Comprehensive Claim, Add 2 Non-Fault Accidents
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		driverTab.fillTab(getTestSpecificTD("TestData_DriverTab"));

		// Override insurance score
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		ratingDetailReportsTab.fillTab(getTestSpecificTD("RatingDetailReportsTab_ASD"));
	}

	private void openVRD(){
		// Navigate to P&C. Calculate Premium. Open VRD.
		new PremiumAndCoveragesTab().calculatePremium();
		PremiumAndCoveragesTab.RatingDetailsView.open();
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

        PremiumAndCoveragesTab.RatingDetailsView.close();
	}
}