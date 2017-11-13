/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import java.time.format.DateTimeFormatter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;

public class TestEValueMembershipProcess extends AutoSSBaseTest {

	private static final String E_VALUE_DISCOUNT = "eValue Discount"; //PAS-440 - rumors have it, that discount might be renamed
	private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
	private static String messageInfo2 = "This customer is eligible for the eValue discount, but the following steps must be completed in order to bind.";
	private static String messageBullet4 = "Maintain continuous insurance coverage and bodily injury limits of $50,000/$100,000 or higher"; //$50,000/$100,000 is configurable
	private static String messageBullet10 = "Agree to and sign the eValue acknowledgement";
	private static String messageBullet2 = "Self-Service online using the MyPolicy site";

	private GeneralTab generalTab = new GeneralTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab(); //TODO test with policy.dataGather().getView().getTab(DocumentsAndBindTab.class); instead of new Tab();
	private ErrorTab errorTab = new ErrorTab();


	private static final String RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_CHECK = "select  value\n"
			+ "from PROPERTYCONFIGURERENTITY\n"
			+ "where propertyname ='retrieveMembershipSummaryServiceImpl.endpointRetrieveMembershipSummaryUri'";

	private static final String MEMBERSHIP_ELIGIBILITY_CHECK_FOR_VA_EXISTS = "SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
			+ "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
			+ "    (SELECT ID \n"
			+ "    FROM LOOKUPLIST \n"
			+ "    WHERE LOOKUPNAME='AAAeMemberQualifications')\n"
			+ "and riskstatecd = 'VA'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'membershipEligibility'\n";

	private static final String MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_CHECK = "select displayValue from \n"
			+ "(SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n"
			+ "FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n"
			+ "    (SELECT ID \n"
			+ "    FROM LOOKUPLIST \n"
			+ "    WHERE LOOKUPNAME='AAAeMemberQualifications')\n"
			+ "and riskstatecd = 'VA'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'membershipEligibility')\n";
			//+ "and displayvalue = '%s')"

	static final String EVALUE_MEMBERSHIP_CONFIG_INSERT = "INSERT INTO LOOKUPVALUE\n" +
			"(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n" +
			"values\n" +
			"('BaseProductLookupValue', 'membershipEligibility', 'TRUE', 'AAA_SS', 'VA', null, null,(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeMemberQualifications'))\n";


	static final String MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_UPDATE = "update LOOKUPVALUE\n"
			+ "set displayValue = '%s'\n"
			+ "WHERE LOOKUPLIST_ID IN \n"
			+ "    (SELECT ID \n"
			+ "    FROM LOOKUPLIST \n"
			+ "    WHERE LOOKUPNAME='AAAeMemberQualifications')\n"
			+ "and riskstatecd = 'VA'\n"
			+ "and productCD = 'AAA_SS'\n"
			+ "and code = 'membershipEligibility'";


	@Test
	private void checkMembershipEligibility(){
		String eligibilitySwitch = "FALSE";
		if(!DBService.get().getValue(MEMBERSHIP_ELIGIBILITY_CHECK_FOR_VA_EXISTS).isPresent()) {
			DBService.get().executeUpdate(EVALUE_MEMBERSHIP_CONFIG_INSERT);
			log.info("INSERT MEMBERSHIP ELIGIBILITY CONFIGURATION IS REQUIRED");
		}
		if(DBService.get().getValue(MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_CHECK).get().equals(eligibilitySwitch)){
			log.info("MEMBERSHIP ELIGIBILITY CONFIGURATION IS CORRECT, NO UPDATES REQUIRED");
		} else {
			DBService.get().executeUpdate(String.format(MEMBERSHIP_ELIGIBILITY_SWITCH_FOR_VA_UPDATE, eligibilitySwitch));
			log.info("UPDATE OF MEMBERSHIP ELIGIBILITY CONFIGURATION WAS EXECUTED HERE");
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
		}
	}



	@Test
	@TestInfo(isAuxiliary = true)
	public static void retrieveMembershipSummaryCheck() {
		CustomAssert.assertTrue("retrieveMembershipSummary doesn't use stub endpoint. Please run retrieveMembershipSummaryUpdate", DBService.get()
				.getValue(RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_CHECK).get().contains(APP_HOST));
	}





	@Test
	@TestInfo(isAuxiliary = true)
	public void precondJobAdding() {
		adminApp().open();
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_BATCH_MARKER_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_AUTOMATED_PROCESSING_INITIATION_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AUTOMATED_PROCESSING_RATING_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AUTOMATED_PROCESSING_ISSUING_OR_PROPOSING_JOB);
	}

	/**
	 * @author Megha Gubbala
	 * @name Test Configuration for eValue for Membership eligibility
	 * @scenario 1. Create new eValue eligible quote for VA
	 * 2. set Membership = no
	 * 3. Check eValueDiscount field is disabled in P&C tab
	 * 4. change the effective date when configuration added for membership eligibility
	 * 5. Check eValueDiscount field is enabled in P&C tab
	 * 6. set eValue = Yes in P&C tab
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3007")
	public void pas3007_eValueMembershipConfiguration(@Optional("VA") String state) {

		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.eValueQuoteCreation();

		CustomAssert.enableSoftMode();

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("No");
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("");

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled(false);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE)
				.setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(8).format(DateTimeUtils.MM_DD_YYYY));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		testEValueDiscount.simplifiedQuoteIssue();

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	//TODO OSI: Blocked by Membership discount and eValue discount not resetting on NB+30

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount not shown for state where it is not configured
	 * @scenario 1. Create new eValue eligible quote but for the not eligible state (PA)
	 * 1.1. Check "Has the insured ever been enrolled in eValue?" is not shown for Non-Applicable state
	 * 2. Check eValue Discount field is not shown in P&C
	 * 3. Check eValue Discount field is not shown in Rating Details
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3697")
	public void pas3697_eValueNotApplicableForState(@Optional("VA") String state) {
		precondJobAdding();

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(3));

		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Membership Pending");
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER)
				.setValue("4333333333333457"); //4382122719291009 - cancelled memembrship in SOA3

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		testEValueDiscount.simplifiedQuoteIssue();

		String policyNumber = PolicySummaryPage.getPolicyNumber();
		jobsNBpluss15pluss30(policyNumber);

		jobsNBpluss15pluss30(policyNumber);

        /*5251111111111118 Active
4333333333333457 Expired
9920702826992041 Inactive
4343433333333335 Failed
3111111111111121 Cancelled
4222222222222123 No Hit
8800400000000002 Lapsed
4290061311384005 Pending
4333333333333338 Expired

*/

	}

	private void jobsNBpluss15pluss30(String policyNumber) {
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		//NB+30 jobs
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(15));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);

		JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		String membershipLogicNote = "Membership information was updated for the policy based on best membership logic.";
		String descriptionTask1 = "Task Created Complete or Cancel Pended Endorsement";
		String descriptionNote1 = "No message [automatedEndorsementInit]";
		CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(3).getCell("Description").getValue().contains(membershipLogicNote));
		CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(2).getCell("Description").getValue().contains(descriptionTask1));
		CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description").getValue().contains(descriptionNote1));

		PolicySummaryPage.buttonPendedEndorsement.verify.present();
		JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		String descriptionNote2 = "No message [automatedEndorsementRate]";
		//CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description").getValue().contains(descriptionNote2));

		JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		String descriptionTask3 = "Complete Task Complete or Cancel Pended Endorsement";
		String descriptionNote3 = "Bind Endorsement effective " + TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " for Policy " + policyNumber;
		//CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(2).getCell("Description").getValue().contains(descriptionTask3));
		//CustomAssert.assertTrue(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description").getValue().contains(descriptionNote3));
	}

	private void testEvalueDiscount(String membershipStatus, String currentCarrier, boolean evalueIsSelected, boolean evalueIsPresent, String evalueStatus) {
		prefillEvalueTestData(membershipStatus, currentCarrier);
		fillPremiumAndCoveragesTab(evalueIsSelected);
		fillDriverActivityReportsTab();
		TestData tdPolicyCreation = fillDocumentAndBindTab(evalueIsPresent);
		new PurchaseTab().fillTab(tdPolicyCreation).submitTab();

		validateEvalueStatus(evalueStatus);
		validatePolicyStatus();
	}

	private void prefillEvalueTestData(String aaaProductOwned, String currentCarrierInformation) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();

		CustomAssert.enableSoftMode();
		String currentCarrierInformationKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel());
		String policyInformationKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel());
		String aaaProductOwnedKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

		TestData tdPolicyCreation = getPolicyTD("DataGather", "TestData")
				.adjust(currentCarrierInformationKey, getTestSpecificTD(currentCarrierInformation))
				.adjust(policyInformationKey, getTestSpecificTD("PolicyInformation"))
				.adjust(aaaProductOwnedKey, getTestSpecificTD(aaaProductOwned));

		policy.getDefaultView().fillUpTo(tdPolicyCreation, PremiumAndCoveragesTab.class, true);
	}

	private void fillPremiumAndCoveragesTab(boolean eValueIsPresent) {
		if (eValueIsPresent) {
			new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
			PremiumAndCoveragesTab.buttonCalculatePremium.click();
			PremiumAndCoveragesTab.discountsAndSurcharges.verify.contains(E_VALUE_DISCOUNT);
		} else {
			CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
		}
		PremiumAndCoveragesTab.buttonContinue.click();
	}

	private void fillDriverActivityReportsTab() {
		DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
		AbstractContainer<?, ?> assetList = driverActivityReportsTab.getAssetList();
		assetList.getAsset(AutoSSMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_QUOTE).setValue("Yes");
		assetList.getAsset(AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY).click();
		driverActivityReportsTab.submitTab();
	}

	private TestData fillDocumentAndBindTab(boolean evalueIsPresent) {
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
		String metaKey = documentsAndBindTab.getMetaKey();
		String evalueAcknowledgementKey = TestData.makeKeyPath(metaKey,
				AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(),
				AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT.getLabel());
		String proofOfPriorInsuranceKey = TestData.makeKeyPath(metaKey,
				AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel(),
				AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PRIOR_INSURANCE.getLabel());
		TestData tdPolicyCreation = getPolicyTD("DataGather", "TestData")
				.adjust(proofOfPriorInsuranceKey, "Yes");
		if (evalueIsPresent) {
			tdPolicyCreation = tdPolicyCreation.adjust(evalueAcknowledgementKey, "Physically Signed");
		}
		documentsAndBindTab.fillTab(tdPolicyCreation).submitTab();
		return tdPolicyCreation;
	}

	private void validateEvalueStatus(String expectedEvalueStatus) {
		PolicySummaryPage.tableGeneralInformation.getRows().get(0)
				.getCell("eValue Status").verify.valueByRegex("Invalid eValue status", expectedEvalueStatus);
	}

	private void validatePolicyStatus() {
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: Policy created with #{}", PolicySummaryPage.labelPolicyNumber.getValue());
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

}
