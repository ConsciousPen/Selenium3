package aaa.modules.regression.sales.auto_ca.select.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
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
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.ViewRatingDetailsPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.StaticElement;

@StateList(states = Constants.States.CA)
public class TestMembershipOverride extends AutoCaSelectBaseTest {

	// "AAA Product Owned" section fields for validation in Inquiry mode
	private static final StaticElement CURRENT_AAA_MEMBER_INQUIRY = new GeneralTab().getInquiryAssetList().getAsset(AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED)
			.getAsset(AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), StaticElement.class);
	private static final StaticElement OVERRIDE_TYPE_INQUIRY = new GeneralTab().getInquiryAssetList().getAsset(AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED)
			.getAsset(AutoCaMetaData.GeneralTab.AAAProductOwned.OVERRIDE_TYPE.getLabel(), StaticElement.class);

	//Timepoint 1 and Timepoint 2 when Membership validation happens at renewal
	private static final long TIME_POINT_1_CA = 80;
	private static final long TIME_POINT_2_CA = 66;

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-6311 AC#1, AC#2, AC#3 (New Business)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Initiate Auto CA Quote.
	 * 3. Select 'Membership Override' option in the 'Current AAA Member' drop-down (AC#1)
	 * 4. Select the 'Override Type' (Term or Life). (AC#1)
	 * 5. Do not enter Membership number. (AC#3)
	 * 6. Navigate to Reports tab ----> Option to order Membership report is not displayed. (AC#1)
	 * 7. Navigate to Premium & Coverages tab
	 * 8. Click on 'View rating Details' link ----> overridden membership details should be displayed (AC#1)
	 * 9. Bind policy ---> Policy should be bound (AC#1)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6311")
	public void pas6311_Validate_Membership_Override_NewBusiness(@Optional("") String state) {

		TestData testData = getPolicyTD();
		TestData tdSpecific = getTestSpecificTD("AAAProductOwned").resolveLinks();
		testData.adjust(TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(),
				AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
				tdSpecific);

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();

		policy.getDefaultView().fillUpTo(testData, MembershipTab.class, false);

		// Check AAA Membership report component is not displayed
		MembershipTab membershipTab = new MembershipTab();
		assertThat(membershipTab.getAssetList().getAsset("AAAMembershipReport").isPresent()).isFalse();

		policy.getDefaultView().fillFromTo(testData, MembershipTab.class, PremiumAndCoveragesTab.class, true);

		// Validate that membership discount is applied (displayed) in P&C tab
		checkMembershipInPCTab("AAA Members", "");

		policy.getDefaultView().fillFromTo(testData, PremiumAndCoveragesTab.class, DocumentsAndBindTab.class, true);
		new DocumentsAndBindTab().submitTab();
		new PurchaseTab().fillTab(testData).submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-6311 AC#1, AC#2, AC#3, PAS-9626 AC#3 (Endorsement)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Create Auto CA Policy with Membership "NO"
	 * 3. Initiate Midterm Endorsement for the policy
	 * 4. Select 'Membership Override' option in the 'Current AAA Member' drop-down (AC#1)
	 * 5. Select the 'Override Type' (Term or Life). (AC#1)
	 * 6. Do not enter Membership number. (AC#3)
	 * 7. Navigate to Reports tab ----> Option to order Membership report should NOT be displayed. (PAS-9626 AC#3)
	 * 8. Navigate to Premium & Coverages tab
	 * 9. Click on 'View rating Details' link ----> overridden membership details should be displayed (PAS-9626 AC#3)
	 * 10. Bind Endorsement ---> Endorsement should be bound (PAS-9626 AC#3)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6311 PAS-9626")
	public void pas6311_pas9626_Validate_Membership_Override_Endorsement1(@Optional("") String state) {

		TestData testData = getPolicyTD();
		getTestSpecificTD("AAAProductOwned").resolveLinks();
		testData.adjust(TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(),
				AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
				getTestSpecificTD("AAAProductOwned_MSNo").resolveLinks());

		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);

		//initiate endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));

		TestData tdSpecific = getTestSpecificTD("TestData_Endorsement").resolveLinks();

		policy.getDefaultView().fillFromTo(tdSpecific, GeneralTab.class, MembershipTab.class, false);

		// Check AAA Membership report component is not displayed
		MembershipTab membershipTab = new MembershipTab();
		assertThat(membershipTab.getAssetList().getAsset("AAAMembershipReport").isPresent()).isFalse();

		policy.getDefaultView().fillFromTo(tdSpecific, MembershipTab.class, PremiumAndCoveragesTab.class, true);

		checkMembershipInPCTab("Regular", "AAA Members");
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-9626 AC#1 (Endorsement)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Create Auto CA Policy with Membership "Membership Override"
	 * 3. Initiate Midterm Endorsement for the policy
	 * 4. Select 'Yes' option in the 'Current AAA Member' drop-down, and enter ACTIVE membership number
	 * 5. Navigate to Reports tab ----> Option to order Membership report IS displayed. (AC#1)
	 * 6. Navigate to Premium & Coverages tab
	 * 7. Click on 'View rating Details' link ----> overridden membership details should be displayed (AC#1)
	 * 8. Bind Endorsement ---> Endorsement should be bound (AC#1)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-9626")
	public void pas9626_Validate_Membership_Override_Endorsement2(@Optional("") String state) {

		TestData tdSpecific = getTestSpecificTD("AAAProductOwned").resolveLinks();
		TestData testData = getPolicyTD();

		// Extract Original General tab from common testdata
		TestData testDataGeneralTab = getPolicyTD().getTestData("GeneralTab");
		// Swap AAAProductOwned from yaml file to general tab
		testDataGeneralTab.adjust("AAAProductOwned", tdSpecific);
		// Put testDataGeneralTab to common testdata
		testData.adjust("GeneralTab", testDataGeneralTab);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));

		tdSpecific = getTestSpecificTD("TestData_Endorsement").resolveLinks();

		//Adjust all AAA product owned Section
		tdSpecific.adjust(TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(),
				AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
				getTestSpecificTD("AAAProductOwned_MS_Active").resolveLinks());
		tdSpecific.adjust(tdSpecific).adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "C$<rx:\\d{7}>");

		policy.getDefaultView().fillFromTo(tdSpecific, GeneralTab.class, MembershipTab.class, false);

		// Check AAA Membership report component is displayed
		MembershipTab membershipTab = new MembershipTab();
		assertThat(membershipTab.getAssetList().getAsset("AAAMembershipReport").isPresent()).isTrue();

		policy.getDefaultView().fillFromTo(tdSpecific, MembershipTab.class, PremiumAndCoveragesTab.class, true);
		checkMembershipInPCTab("AAA Members", "");
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-9626 AC#2 (Endorsement)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Create Auto CA Policy with ACTIVE Membership
	 * 3. Initiate Midterm Endorsement for the policy
	 * 4. Select 'Membership Override' option in the 'Current AAA Member' drop-down
	 * 5. Enter 'Member Since Date' and click 'Continue'
	 * 5. Navigate to Reports tab ----> Option to order Membership report should not be displayed. (AC#2)
	 * 6. Navigate to Premium & Coverages tab
	 * 7. Click on 'View rating Details' link ----> overridden membership details should be displayed (AC#2)
	 * 8. Bind Endorsement ---> Endorsement should be bound (#AC#2)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-9626")
	public void pas9626_Validate_Membership_Override_Endorsement3(@Optional("") String state) {

		TestData tdSpecific = getTestSpecificTD("AAAProductOwned_MS_Active").resolveLinks();
		TestData testData = getPolicyTD();

		// Extract Original General tab from common testdata
		TestData testDataGeneralTab = getPolicyTD().getTestData("GeneralTab");
		// Swap AAAProductOwned from yaml file to general tab
		testDataGeneralTab.adjust("AAAProductOwned", tdSpecific);
		// Put testDataGeneralTab to common testdata
		testData.adjust("GeneralTab", testDataGeneralTab);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));

		tdSpecific = getTestSpecificTD("TestData_Endorsement").resolveLinks();

		//Adjust all AAA product owned Section
		tdSpecific.adjust(TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(),
				AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
				getTestSpecificTD("AAAProductOwned_MS_Override_Term").resolveLinks());

		new GeneralTab().fillTab(tdSpecific);
		policy.getDefaultView().fillFromTo(tdSpecific, GeneralTab.class, MembershipTab.class, false);

		// Check AAA Membership report component is not displayed
		MembershipTab membershipTab = new MembershipTab();
		assertThat(membershipTab.getAssetList().getAsset("AAAMembershipReport").isPresent()).isFalse();

		policy.getDefaultView().fillFromTo(tdSpecific, MembershipTab.class, PremiumAndCoveragesTab.class, true);
		checkMembershipInPCTab("AAA Members", "");
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}

	//////////Start PAS-6316

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-6316 (AC#2)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Log in as user from group E-34
	 * 2. Create Customer.
	 * 3. Initiate Auto CA Quote.
	 * 4. Validate that 'Membership Override' option is displayed in the 'Current AAA Member' drop-down for non E34 group user
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6316")
	public void pas6316_Validate_Membership_Override_NewBusiness_E34_AC1(@Optional("") String state) {

		TestData loginTD = getLoginTD().adjust("Groups", "E34");
		loginTD.adjust("User", "qa_roles");
		mainApp().open(loginTD);

		testMembershipOverridePrivilege(true);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-6316 (AC#2)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Log in as user from group L-41
	 * 2. Create Customer.
	 * 3. Initiate Auto CA Quote.
	 * 4. Validate that 'Membership Override' option is displayed in the 'Current AAA Member' drop-down for non E34 group user
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6316")
	public void pas6316_Validate_Membership_Override_NewBusiness_L41_AC1(@Optional("") String state) {

		TestData loginTD = getLoginTD().adjust("Groups", "L41");
		loginTD.adjust("User", "qa_roles");
		mainApp().open(loginTD);

		testMembershipOverridePrivilege(true);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-6316 (AC#2)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Log in as user from DIFFERENT group than E-34/L41
	 * 2. Create Customer.
	 * 3. Initiate Auto CA Quote.
	 * 4. Validate that 'Membership Override' option not displayed in the 'Current AAA Member' drop-down for non E34/L41 group user
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6316")
	public void pas6316_Validate_Membership_Override_NewBusiness_OtherThan_E34_L41_AC2(@Optional("") String state) {

		TestData loginTD = getLoginTD().adjust("Groups", "A30");
		loginTD.adjust("User", "qa_roles");
		mainApp().open(loginTD);

		testMembershipOverridePrivilege(false);
	}
	//////////End of PAS-6316

	//////////Start of PAS-6313

	/**
	 * @author Maris Strazds
	 * @name Policy with overridden Membership should not be considered for Membership validation at NB+15/+30
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy with overridden Membership
	 * 3. Run Membership Validation Batch Jobs at NB+15
	 * 4. Retrieve policy in Inquiry mode and verify that policy is not considered for Membership validation and the membership discount is retained.
	 * 5. Run Membership Validation Batch Jobs at NB+30
	 * 6. Retrieve policy in Inquiry mode and verify that policy is not considered for Membership validation and the membership discount is retained.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6313")
	public void pas6313_Validate_Membership_Override_NB15NB30(@Optional("") String state) {

		TestData tdSpecific = getTestSpecificTD("AAAProductOwned").resolveLinks();
		TestData testData = getPolicyTD().adjust(tdSpecific);

		// Extract Original General tab from common testdata
		TestData testDataGeneralTab = getPolicyTD().getTestData("GeneralTab");
		// Swap AAAProductOwned from yaml file to general tab
		testDataGeneralTab.adjust("AAAProductOwned", tdSpecific);
		// Put testDataGeneralTab to common testdata
		testData.adjust("GeneralTab", testDataGeneralTab);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		LocalDateTime nbPlus15 = policyEffectiveDate.plusDays(15);
		LocalDateTime nbPlus30 = policyEffectiveDate.plusDays(30);

		TimeSetterUtil.getInstance().nextPhase(nbPlus15);
		JobUtils.executeJob(Jobs.membershipValidationJob);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);//is this needed

		policy.policyInquiry().start();

		//Check AAA Product Owned section in Inquiry
		checkAAAProductsOwnedSectionInquiry("Term");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//validate that membership discount is applied (displayed) in P&C tab
		checkMembershipInPCTab("AAA Members", "");

		TimeSetterUtil.getInstance().nextPhase(nbPlus30);
		JobUtils.executeJob(Jobs.membershipValidationJob);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.policyInquiry().start();

		//Check AAA Product Owned section in Inquiry
		checkAAAProductsOwnedSectionInquiry("Term");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//validate that membership discount is applied (displayed) in P&C tab
		checkMembershipInPCTab("AAA Members", "");

	}
	//////////End of PAS-6313

	//////////Start of PAS-6314

	/**
	 * @author Maris Strazds
	 * @name PAS-6314 Membership batch order job needs to account for membership override (Renewal) (AC#1)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Create Auto CA Policy with Membership Override for Term
	 * 3. Run Membership Validation Batch Jobs at Renewal Time point 1 and Time point 2 (no membership found)
	 * 4. Retrieve Renewal image and verify that Membership Override is discarded
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6314")
	public void pas6314_Validate_Membership_Override_AC1(@Optional("") String state) {

		TestData testData = getPolicyTD();
		TestData tdSpecific = getTestSpecificTD("AAAProductOwned_MS_Override_Term").resolveLinks();
		testData.adjust(TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(),
				AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
				tdSpecific);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info(policyNumber);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//Generate renewal image at renewal image generation date
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		log.info("Policy Renewal Image Generation Date " + renewImageGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		LocalDateTime timePoint1 = policyExpirationDate.minusDays(TIME_POINT_1_CA);
		LocalDateTime timePoint2 = policyExpirationDate.minusDays(TIME_POINT_2_CA);
		LocalDateTime ratingTimepoint = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);

		TimeSetterUtil.getInstance().nextPhase(timePoint1);
		runRenewalBatchJobs();
		reopenAppAndInquiryRenewalImage(policyNumber);
		//Check AAA Product Owned section in Inquiry
		checkAAAProductsOwnedSectionInquiry("Term");

		TimeSetterUtil.getInstance().nextPhase(timePoint2);
		runRenewalBatchJobs();
		reopenAppAndInquiryRenewalImage(policyNumber);
		//Check AAA Product Owned section in Inquiry
		assertThat(CURRENT_AAA_MEMBER_INQUIRY).valueContains("No");

		TimeSetterUtil.getInstance().nextPhase(ratingTimepoint);
		runRenewalBatchJobs();
		reopenAppAndInquiryRenewalImage(policyNumber);
		//Check AAA Product Owned section in Inquiry
		assertThat(CURRENT_AAA_MEMBER_INQUIRY).valueContains("No");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//validate that membership discount is applied (displayed) in P&C tab
		checkMembershipInPCTab("Regular", "");
	}

	/**
	 * @author Maris Strazds
	 * @name PAS-6314 Membership batch order job needs to account for membership override (Renewal) (AC#2)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Create Auto CA Policy with Membership Override for Life
	 * 3. Run Membership Validation Batch Jobs at Renewal Time point 1 and Time point 2 (no membership found)
	 * 4. Retrieve Renewal image and verify that Membership Override is ratained
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6314")
	public void pas6314_Validate_Membership_Override_AC2(@Optional("") String state) {

		TestData testData = getPolicyTD();
		TestData tdSpecific = getTestSpecificTD("AAAProductOwned_MS_Override_Life").resolveLinks();
		testData.adjust(TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(),
				AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
				tdSpecific);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info(policyNumber);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//Generate renewal image at renewal image generation date
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		log.info("Policy Renewal Image Generation Date " + renewImageGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		LocalDateTime timePoint1 = policyExpirationDate.minusDays(TIME_POINT_1_CA);
		LocalDateTime timePoint2 = policyExpirationDate.minusDays(TIME_POINT_2_CA);
		LocalDateTime ratingTimepoint = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);

		TimeSetterUtil.getInstance().nextPhase(timePoint1);
		runRenewalBatchJobs();
		reopenAppAndInquiryRenewalImage(policyNumber);
		//Check AAA Product Owned section in Inquiry
		checkAAAProductsOwnedSectionInquiry("Life");

		TimeSetterUtil.getInstance().nextPhase(timePoint2);
		runRenewalBatchJobs();
		reopenAppAndInquiryRenewalImage(policyNumber);
		//Check AAA Product Owned section in Inquiry
		checkAAAProductsOwnedSectionInquiry("Life");

		TimeSetterUtil.getInstance().nextPhase(ratingTimepoint);
		runRenewalBatchJobs();
		reopenAppAndInquiryRenewalImage(policyNumber);
		//Check AAA Product Owned section in Inquiry
		checkAAAProductsOwnedSectionInquiry("Life");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//validate that membership discount is applied (displayed) in P&C tab
		checkMembershipInPCTab("AAA Members", "");
	}

	/**
	 * @author Maris Strazds
	 * @name PAS-6314 Membership batch order job needs to account for membership override (Renewal) (AC#3)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Create Auto CA Policy without Membership Override for Term
	 * 3. Revise Renewal before Renewal Time point 1 and Time point 2 (before renewal membership override job), by Overriding Membership for Term
	 * 3. Run Membership Validation Batch Jobs at Renewal Time point 1 and Time point 2
	 * 4. Retrieve Renewal image and verify that Membership Override is ratained
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6314")
	public void pas6314_Validate_Membership_Override_AC3(@Optional("") String state) {

		TestData testData = getPolicyTD();
		TestData tdSpecific = getTestSpecificTD("AAAProductOwned_MSNo").resolveLinks();
		testData.adjust(TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(),
				AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
				tdSpecific);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//Generate renewal image at renewal image generation date
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		log.info("Policy Renewal Image Generation Date " + renewImageGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		//adjust test data for Renewal image updating
		tdSpecific = getTestSpecificTD("AAAProductOwned_MS_Override_Term").resolveLinks();
		testData = getTestSpecificTD("TestData_Endorsement").resolveLinks();
		testData.adjust(TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(),
				AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
				tdSpecific);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		PolicySummaryPage.buttonRenewals.click();

		PolicySummaryPage.tableRenewals.getRow(1).getCell("Action").controls.comboBoxes.getFirst().setValue("Data Gathering");
		PolicySummaryPage.tableRenewals.getRow(1).getCell("Action").controls.buttons.get("Go").click();

		CreateQuoteVersionTab createQuoteVersionTab = new CreateQuoteVersionTab();
		createQuoteVersionTab.getAssetList().getAsset(AutoCaMetaData.CreateQuoteVersionTab.DESCRIPTION).setValue("test");
		createQuoteVersionTab.submitTab();

		policy.getDefaultView().fillUpTo(testData, DocumentsAndBindTab.class, true);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		LocalDateTime timePoint1 = policyExpirationDate.minusDays(TIME_POINT_1_CA);
		LocalDateTime timePoint2 = policyExpirationDate.minusDays(TIME_POINT_2_CA);
		LocalDateTime ratingTimepoint = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);

		TimeSetterUtil.getInstance().nextPhase(timePoint1);
		runRenewalBatchJobs();
		reopenAppAndInquiryRenewalImage(policyNumber);
		//Check AAA Product Owned section in Inquiry
		checkAAAProductsOwnedSectionInquiry("Term");

		TimeSetterUtil.getInstance().nextPhase(timePoint2);
		runRenewalBatchJobs();
		reopenAppAndInquiryRenewalImage(policyNumber);
		//Check AAA Product Owned section in Inquiry
		checkAAAProductsOwnedSectionInquiry("Term");

		TimeSetterUtil.getInstance().nextPhase(ratingTimepoint);
		runRenewalBatchJobs();
		reopenAppAndInquiryRenewalImage(policyNumber);
		//Check AAA Product Owned section in Inquiry
		checkAAAProductsOwnedSectionInquiry("Term");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//validate that membership discount is applied (displayed) in P&C tab
		checkMembershipInPCTab("AAA Members", "");
	}

	//////////End of PAS-6314

	private void checkMembershipInPCTab(String Value1, String Value2) {

		//click on 'View Rating Details' and validate that overridden membership details are displayed
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		//RatingDetailsTable ratingDetailsTable = new RatingDetailsTable();

		assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "Program")).exists();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "Program").getCell(2).getValue().contains(Value1)).isTrue();
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "Program").getCell(3).getValue().contains(Value2)).isTrue();
		ViewRatingDetailsPage.buttonRatingDetailsOk.click();

	}

	private void testMembershipOverridePrivilege(Boolean hasPrivilege) {

		TestData testData = getPolicyTD();

		createCustomerIndividual();
		policy.initiate();

		policy.getDefaultView().fillUpTo(testData, GeneralTab.class, false);
		GeneralTab generalTab = new GeneralTab();

		//Validate that 'Membership Override' option is/is not displayed in "Current AAA Member" field
		if (hasPrivilege) {
			assertThat(generalTab.getAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER)).containsOption("Membership Override");
		} else {
			assertThat(generalTab.getAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER)).doesNotContainOption("Membership Override");
		}

	}

	private void checkAAAProductsOwnedSectionInquiry(String overrideType) {

		assertThat(CURRENT_AAA_MEMBER_INQUIRY).valueContains("Membership Override"); // Still "Membership Override"
		assertThat(OVERRIDE_TYPE_INQUIRY).hasValue(overrideType); //value like at NB
	}

	//Run Membership Validation Batch Jobs at Renewal Time point 1 and Time point 2
	private void runRenewalBatchJobs() {

		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

	}

	private void reopenAppAndInquiryRenewalImage(String policyNumber) {
		//reopen app and retrieve policy by number
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonRenewals.click();

		// Open Renewal Image in Inquiry mode
		policy.policyInquiry().start();
	}
}
