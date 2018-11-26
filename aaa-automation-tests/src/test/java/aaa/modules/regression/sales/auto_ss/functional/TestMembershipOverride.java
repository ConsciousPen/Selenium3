package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.exigen.ipb.etcsa.utils.batchjob.JobGroup;
import com.exigen.ipb.etcsa.utils.batchjob.SoapJobActions;
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
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.StaticElement;

@StateList(states = Constants.States.AZ)
public class TestMembershipOverride extends AutoSSBaseTest {

	private static final String MEMBER_SINCE_DATE_WARNING_MESSAGE = "'Member Since Date' is required";

	// "AAA Product Owned" section fields for validation in Inquiry mode
	private static final StaticElement CURRENT_AAA_MEMBER_INQUIRY = new GeneralTab().getInquiryAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED)
			.getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), StaticElement.class);
	private static final StaticElement OVERRIDE_TYPE_INQUIRY = new GeneralTab().getInquiryAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED)
			.getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.OVERRIDE_TYPE.getLabel(), StaticElement.class);

	//Timepoint 1 and Timepoint 2 when Membership validation happens at renewal
	private static final long TIME_POINT_1_AZ = 63;
	private static final long TIME_POINT_2_AZ = 48;

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-6311 AC#1, AC#2, AC#3 (New Business)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Initiate Auto SS Quote.
	 * 3. Select 'Membership Override' option in the 'Current AAA Member' drop-down (AC#1)
	 * 4. Select the 'Override Type' (Term or Life). (AC#1)
	 * 5. Do not enter Membership number. (AC#3)
	 * 6. Do not enter 'Member Since Date' and click 'Continue' -----> Error 'Member Since Date'
	 *    is required for override' should be displayed (AC#2)
	 * 7. Enter the 'Member Since Date'. (AC#1)
	 * 8. Navigate to Reports tab ----> Option to order Membership report is not displayed. (AC#1)
	 * 9. Navigate to Premium & Coverages tab
	 * 10. Click on 'View rating Details' link ----> overridden membership details should be displayed (AC#1)
	 * 11. Bind policy ---> Policy should be bound (AC#1)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6311")
	public void pas6311_Validate_Membership_Override_NewBusiness(@Optional("") String state) {

		TestData testData = getPolicyTD();
		TestData tdSpecific = getTestSpecificTD("AAAProductOwned_Override_NoDate").resolveLinks();
		testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
				AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
				tdSpecific);

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();

		policy.getDefaultView().fillUpTo(testData, GeneralTab.class, true);
		new GeneralTab().submitTab();

		checkMemberSinceWarningMessage();

		new GeneralTab().getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBER_SINCE_DATE).setValue("01/04/2009");
		new GeneralTab().submitTab();

		policy.getDefaultView().fillFromTo(testData, DriverTab.class, RatingDetailReportsTab.class, false);

		// Check AAA Membership report component is not displayed
		RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
		assertThat(ratingDetailReportsTab.getAssetList().getAsset("AAAMembershipReport").isPresent()).isFalse();

		policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, PremiumAndCoveragesTab.class, true);

		// Validate that membership discount is applied (displayed) in P&C tab
		checkMembershipInPCTab(true, false, "Yes", "", "1/04/2009");

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
	 * 2. Create Auto SS Policy with Membership "NO"
	 * 3. Initiate Midterm Endorsement for the policy
	 * 4. Select 'Membership Override' option in the 'Current AAA Member' drop-down (AC#1)
	 * 5. Select the 'Override Type' (Term or Life). (AC#1)
	 * 6. Do not enter Membership number. (AC#3)
	 * 7. Do not enter 'Member Since Date' and click 'Continue' -----> Error ''Member Since Date'
	 *    is required for override' should be displayed (AC#2)
	 * 8. Enter the 'Member Since Date'. (AC#1)
	 * 9. Navigate to Reports tab ----> Option to order Membership report should NOT be displayed. (PAS-9626 AC#3)
	 * 10. Navigate to Premium & Coverages tab
	 * 11. Click on 'View rating Details' link ----> overridden membership details should be displayed (PAS-9626 AC#3)
	 * 12. Bind Endorsement ---> Endorsement should be bound (PAS-9626 AC#3)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6311 PAS-9626")
	public void pas6311_pas9626_Validate_Membership_Override_Endorsement1(@Optional("") String state) {

		TestData testData = getPolicyTD();
		getTestSpecificTD("AAAProductOwned_Override_NoDate").resolveLinks();
		testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
				AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
				getTestSpecificTD("AAAProductOwned_MSNo").resolveLinks());

		//testData.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "A00000000");

		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);

		//initiate endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));

		TestData tdSpecific = getTestSpecificTD("TestData_Endorsement").resolveLinks();

		new GeneralTab().fillTab(tdSpecific);
		new GeneralTab().submitTab();

		checkMemberSinceWarningMessage();

		// Enter "Current AAA Membership:" = "Override Membership"
		new GeneralTab().getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Membership Override");

		// Enter "Override Type" = "Life"
		new GeneralTab().getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.OVERRIDE_TYPE).setValue("Life");

		// Enter 'Member Since Date' and click Continue
		new GeneralTab().getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBER_SINCE_DATE).setValue("01/04/2009");
		new GeneralTab().submitTab();

		policy.getDefaultView().fillFromTo(tdSpecific, DriverTab.class, RatingDetailReportsTab.class, false);

		// Check AAA Membership report component is not displayed
		RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
		assertThat(ratingDetailReportsTab.getAssetList().getAsset("AAAMembershipReport").isPresent()).isFalse();

		policy.getDefaultView().fillFromTo(tdSpecific, RatingDetailReportsTab.class, PremiumAndCoveragesTab.class, true);

		checkMembershipInPCTab(true, true, "", "Yes", "1/04/2009");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		new ErrorTab().overrideAllErrors();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership Override - PAS-9626 AC#1 (Endorsement)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy with Membership "Membership Override"
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9626")
	public void pas9626_Validate_Membership_Override_Endorsement2(@Optional("") String state) {

		TestData tdSpecific = getTestSpecificTD("AAAProductOwned2").resolveLinks();
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
		tdSpecific.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
				AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
				getTestSpecificTD("AAAProductOwned_MS_Active2").resolveLinks());

		policy.getDefaultView().fillFromTo(tdSpecific, GeneralTab.class, RatingDetailReportsTab.class, false);

		// Check AAA Membership report component is displayed
		RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
		assertThat(ratingDetailReportsTab.getAssetList().getAsset("AAAMembershipReport").isPresent()).isTrue();

		policy.getDefaultView().fillFromTo(tdSpecific, RatingDetailReportsTab.class, PremiumAndCoveragesTab.class, true);
		checkMembershipInPCTab(true, true, "Yes", "", "01/01/2005"); //01/01/2005 is "Member Since Date" value from Stub for Active membership
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
	 * 2. Create Auto SS Policy with ACTIVE Membership
	 * 3. Initiate Midterm Endorsement for the policy
	 * 4. Select 'Membership Override' option in the 'Current AAA Member' drop-down
	 * 5. Enter 'Member Since Date' and click 'Continue'
	 * 6. Navigate to Reports tab ----> Option to order Membership report should not be displayed. (AC#2)
	 * 7. Navigate to Premium & Coverages tab
	 * 8. Click on 'View rating Details' link ----> overridden membership details should be displayed (AC#2)
	 * 9. Bind Endorsement ---> Endorsement should be bound (#AC#2)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9626")
	public void pas9626_Validate_Membership_Override_Endorsement3(@Optional("") String state) {

		TestData tdSpecific = getTestSpecificTD("AAAProductOwned_MS_Active2").resolveLinks();
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
		tdSpecific.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
				AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
				getTestSpecificTD("AAAProductOwned_MS_Override_Term").resolveLinks());

		new GeneralTab().fillTab(tdSpecific);
		policy.getDefaultView().fillFromTo(tdSpecific, GeneralTab.class, RatingDetailReportsTab.class, false);

		// Check AAA Membership report component is not displayed
		RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
		assertThat(ratingDetailReportsTab.getAssetList().getAsset("AAAMembershipReport").isPresent()).isFalse();

		policy.getDefaultView().fillFromTo(tdSpecific, RatingDetailReportsTab.class, PremiumAndCoveragesTab.class, true);
		checkMembershipInPCTab(true, true, "Yes", "", "01/04/2009");
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
	 * 3. Initiate Auto SS Quote.
	 * 4. Validate that 'Membership Override' option is displayed in the 'Current AAA Member' drop-down for non E34 group user
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6316")
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
	 * 3. Initiate Auto SS Quote.
	 * 4. Validate that 'Membership Override' option is displayed in the 'Current AAA Member' drop-down for non E34 group user
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6316")
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
	 * 3. Initiate Auto SS Quote.
	 * 4. Validate that 'Membership Override' option not displayed in the 'Current AAA Member' drop-down for non E34/L41 group user
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6316")
	public void pas6316_Validate_Membership_Override_NewBusiness_OtherThan_E34_L41_AC2(@Optional("") String state) {

		TestData loginTD = getLoginTD().adjust("Groups", "A30");
		loginTD.adjust("User", "qa_roles");
		mainApp().open(loginTD);

		testMembershipOverridePrivilege(false);
	}

	//////////End of PAS-6316

	////////// Start of PAS-6313

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
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6313")
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

		TestEValueMembershipProcess.jobsNBplus15plus30runNoChecks();

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		policy.policyInquiry().start();

		//Check AAA Product Owned section in Inquiry
		checkAAAProductsOwnedSectionInquiry("Term");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//validate that membership discount is applied (displayed) in P&C tab
		checkMembershipInPCTab(true, false, "Yes", "", "01/04/2009");

		TestEValueMembershipProcess.jobsNBplus15plus30runNoChecks();

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.policyInquiry().start();

		//Check AAA Product Owned section in Inquiry
		checkAAAProductsOwnedSectionInquiry("Term");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//validate that membership discount is applied (displayed) in P&C tab
		checkMembershipInPCTab(true, false, "Yes", "", "01/04/2009");

	}

	/**
	 * @author Maris Strazds
	 * @name Policy with overridden Membership should not be considered for Membership validation at NB+15/+30 (NEGATIVE SCENARIO)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy with "Membership Pending"
	 * 3. Run Membership Validation Batch Jobs at NB+15 (Membership not found)
	 * 4. Retrieve policy in Inquiry mode and verify that policy is considered for Membership validation and the membership discount is NOT retained.
	 * 5. Run Membership Validation Batch Jobs at NB+30 (Membership not found)
	 * 6. Retrieve policy in Inquiry mode and verify that policy is considered for Membership validation and the membership discount is NOT retained.
	 * @details
	 */
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6313")
	public void pas6313_Validate_Membership_Override_NB15NB30_Negative(@Optional("AZ") String state) {
		TestData tdSpecific = getTestSpecificTD("AAAProductOwned_MS_Pending").resolveLinks();
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

		jobsNBplus15plus30runNoChecks();

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		policy.policyInquiry().start();

		//Check AAA Product Owned section in Inquiry---> "Current AAA Member" should be "Membership Pending"
		assertThat(CURRENT_AAA_MEMBER_INQUIRY).valueContains("Membership Pending");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//validate that membership discount is applied (displayed) in P&C tab
		checkMembershipInPCTab(true, false, "Yes", "", "");

		TestEValueMembershipProcess.jobsNBplus15plus30runNoChecks();

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.policyInquiry().start();

		//Check AAA Product Owned section in Inquiry---> "Current AAA Member" should be "No"
		assertThat(CURRENT_AAA_MEMBER_INQUIRY).valueContains("No");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//validate that membership discount is applied (displayed) in P&C tab
		checkMembershipInPCTab(false, true, "Yes", "None", "");
	}
	////////// End of PAS-6313

	//////////Start of PAS-6314


	/**
	 * @author Maris Strazds
	 * @name PAS-6314 Membership batch order job needs to account for membership override (Renewal) (AC#1)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy with Membership Override for Term
	 * 3. Run Membership Validation Batch Jobs at Renewal Time point 1 and Time point 2 (no membership found)
	 * 4. Retrieve Renewal image and verify that Membership Override is discarded
	 * @details
	 */
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6314")
	public void pas6314_Validate_Membership_Override_AC1(@Optional("AZ") String state) {

		TestData testData = getPolicyTD();
		TestData tdSpecific = getTestSpecificTD("AAAProductOwned_MS_Override_Term").resolveLinks();
		testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
				AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
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

		LocalDateTime timePoint1 = policyExpirationDate.minusDays(TIME_POINT_1_AZ);
		LocalDateTime timePoint2 = policyExpirationDate.minusDays(TIME_POINT_2_AZ);
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
		checkMembershipInPCTab(false, false, "None", "", "");
	}

	/**
	 * @author Maris Strazds
	 * @name PAS-6314 Membership batch order job needs to account for membership override (Renewal) (AC#2)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy with Membership Override for Life
	 * 3. Run Membership Validation Batch Jobs at Renewal Time point 1 and Time point 2 (no membership found)
	 * 4. Retrieve Renewal image and verify that Membership Override is retained
	 * @details
	 */
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6314")
	public void pas6314_Validate_Membership_Override_AC2(@Optional("AZ") String state) {

		TestData testData = getPolicyTD();
		TestData tdSpecific = getTestSpecificTD("AAAProductOwned_MS_Override_Life").resolveLinks();
		testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
				AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
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

		LocalDateTime timePoint1 = policyExpirationDate.minusDays(TIME_POINT_1_AZ);
		LocalDateTime timePoint2 = policyExpirationDate.minusDays(TIME_POINT_2_AZ);
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
		checkMembershipInPCTab2(true, "Yes", "", "");
	}

	/**IGNORED TEST:@Test(enabled = false)
	 * @author Maris Strazds
	 * @name PAS-6314 Membership batch order job needs to account for membership override (Renewal) (AC#3)
	 * @scenario
	 * Precondition: Agent is expected to have the Membership override privilege.
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy without Membership Override for Term
	 * 3. Revise Renewal before Renewal Time point 1 and Time point 2 (before renewal membership override job), by Overriding Membership for Term
	 * 4. Run Membership Validation Batch Jobs at Renewal Time point 1 and Time point 2
	 * 5. Retrieve Renewal image and verify that Membership Override is retained
	 * @details
	 */
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6314")
	public void pas6314_Validate_Membership_Override_AC3(@Optional("AZ") String state) {

		TestData testData = getPolicyTD();
		TestData tdSpecific = getTestSpecificTD("AAAProductOwned_MSNo").resolveLinks();
		testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
				AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
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
		testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
				AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel()),
				tdSpecific);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		PolicySummaryPage.buttonRenewals.click();

		PolicySummaryPage.tableRenewals.getRow(1).getCell("Action").controls.comboBoxes.getFirst().setValue("Data Gathering");
		PolicySummaryPage.tableRenewals.getRow(1).getCell("Action").controls.buttons.get("Go").click();

		CreateQuoteVersionTab createQuoteVersionTab = new CreateQuoteVersionTab();
		createQuoteVersionTab.getAssetList().getAsset(AutoSSMetaData.CreateQuoteVersionTab.DESCRIPTION).setValue("test");
		createQuoteVersionTab.submitTab();

		policy.getDefaultView().fillUpTo(testData, DocumentsAndBindTab.class, true);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		LocalDateTime timePoint1 = policyExpirationDate.minusDays(TIME_POINT_1_AZ);
		LocalDateTime timePoint2 = policyExpirationDate.minusDays(TIME_POINT_2_AZ);
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
		checkMembershipInPCTab2(true,  "Yes", "", "01/04/2009");
	}
	//////////End of PAS-6314

	private void checkMembershipInPCTab(Boolean shouldHaveDiscount, Boolean isEndorsement, String Value1, String Value2, String memberSinceDate) {
		//validate that membership discount is applied (displayed) in P&C tab
		if (shouldHaveDiscount) {
			assertThat(PremiumAndCoveragesTab.discountsAndSurcharges).valueContains("Membership Discount");
		} else {
			assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("Membership Discount")).isFalse();
		}

		//click on 'View Rating Details' and validate that overridden membership details are displayed
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		if (isEndorsement) {
			assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(4, "AAA Membership Discount")).isPresent();
			assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(4, "AAA Membership Discount").getCell(5).getValue().contains(Value1)).isTrue();
			assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(4, "AAA Membership Discount").getCell(6).getValue().contains(Value2)).isTrue();
			assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(4, "Member Since Date").getCell(6)).isPresent();
			assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(4, "Member Since Date").getCell(6).getValue().contains(memberSinceDate)).isTrue();

		} else {
			assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, "AAA Membership Discount")).exists();
			assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, "AAA Membership Discount").getCell(4).getValue().contains(Value1)).isTrue();
			assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, "Member Since Date").getCell(3).getValue().contains("Member Since Date")).isTrue();
			assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, "Member Since Date").getCell(4).getValue().contains(memberSinceDate)).isTrue();
		}

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

	}

    private void checkMembershipInPCTab2(Boolean shouldHaveDiscount, String Value1, String Value2, String memberSinceDate) {
        //validate that membership discount is applied (displayed) in P&C tab
        if (shouldHaveDiscount) {
            assertThat(PremiumAndCoveragesTab.discountsAndSurcharges).valueContains("Membership Discount");
        } else {
            assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("Membership Discount")).isFalse();
        }

        //click on 'View Rating Details' and validate that overridden membership details are displayed
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();

        assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, "AAA Membership Discount")).exists();
        assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, "AAA Membership Discount").getCell(4).getValue().contains(Value1)).isTrue();
        assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, "Member Since Date").getCell(3).getValue().contains("Member Since Date")).isTrue();
        assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, "Member Since Date").getCell(4).getValue().contains(memberSinceDate)).isTrue();


        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

    }

	private void checkMemberSinceWarningMessage() {
		GeneralTab generalTab = new GeneralTab();
		assertThat(generalTab.getAAAProductOwnedAssetList().
				getAsset("Member Since Date").
				getWarning().orElse("")).contains(MEMBER_SINCE_DATE_WARNING_MESSAGE);
	}

	private void testMembershipOverridePrivilege(Boolean hasPrivilege) {

		TestData testData = getPolicyTD();

		createCustomerIndividual();
		policy.initiate();

		policy.getDefaultView().fillUpTo(testData, GeneralTab.class, false);
		GeneralTab generalTab = new GeneralTab();

		//Validate that 'Membership Override' option not displayed in "Current AAA Member" field
		if (hasPrivilege) {
			assertThat(generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).getAllValues()).contains("Membership Override");
		} else {
			assertThat(generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).getAllValues()).doesNotContain("Membership Override");
		}

	}

	private void checkAAAProductsOwnedSectionInquiry(String overrideType) {

		assertThat(CURRENT_AAA_MEMBER_INQUIRY).valueContains("Membership Override"); // Still "Membership Override"
		assertThat(OVERRIDE_TYPE_INQUIRY).hasValue(overrideType); //value like at NB
		GeneralTab generalTab = new GeneralTab();
		assertThat(generalTab.getAAAProductOwnedAssetList().getAsset("Member Since Date").getValue().toString().contains("01/04/2009")).isTrue();
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

	static void jobsNBplus15plus30runNoChecks() {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(15));
		//the job might not exist in AWS
		if(new SoapJobActions().isJobExist(JobGroup.fromSingleJob(Jobs.membershipValidationJob.getJobName()))){
			JobUtils.executeJob(Jobs.membershipValidationJob);
		} else {
			//JobUtils.executeJob(Jobs.aaaBatchMarkerJob); //OSI: job is not required
			JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
			JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
			JobUtils.executeJob(Jobs.automatedProcessingRunReportsServicesJob);
			JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
			JobUtils.executeJob(Jobs.automatedProcessingStrategyStatusUpdateJob);
			//BUG INC0635200 PAS-ASM: multiple VDMs: We have a failing job on the VDMs. - the next line is closed as not a defect and this one was opened
			//BUG PAS-6162 automatedProcessingBypassingAndErrorsReportGenerationJob is failing with Error, failed to retrieve 'placeholder' Report Entity
			JobUtils.executeJob(Jobs.automatedProcessingBypassingAndErrorsReportGenerationJob);
		}
	}


}
