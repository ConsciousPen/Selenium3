package aaa.modules.regression.sales.auto_ss.functional;

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
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.AZ)
public class TestMembershipValidation extends AutoSSBaseTest {
	private ErrorTab errorTab = new ErrorTab();

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - FN, LN and DOB don't match (New business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Quote.
	 * 3. Add member number on General tab with mismatching First Name, Last Name and DOB.
	 * 4. Fill All other required data up to Documents and Bind Tab.
	 * 5. Verify that Error is displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 6. Override the error and bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3786")
	public void pas3786_Validate_Override_NewBusiness_NoMatch(@Optional("AZ") String state) {
		TestData tdSpecific = getTestSpecificTD("TestData_MembershipValidation_NB_NoMatch").resolveLinks();
		validate_NewBusiness(tdSpecific, true);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - FN, LN and DOB match (New business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Quote.
	 * 3. Add member number on General tab with matching First Name, Last Name and DOB.
	 * 4. Fill All other required data up to Documents and Bind Tab.
	 * 5. Verify that Error is not displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 6. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3786")
	public void pas3786_Validate_Override_NewBusiness_AllMatch(@Optional("AZ") String state) {
		TestData tdSpecific = getTestSpecificTD("TestData_MembershipValidation_NB_AllMatch").resolveLinks();
		validate_NewBusiness(tdSpecific, false);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - FN match, LN and DOB don't match (New business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Quote.
	 * 3. Add member number on General tab with matching First Name.
	 * 4. Fill All other required data up to Documents and Bind Tab.
	 * 5. Verify that Error is not displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 6. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3786")
	public void pas3786_Validate_Override_NewBusiness_FNmatch(@Optional("AZ") String state) {
		TestData tdSpecific = getTestSpecificTD("TestData_MembershipValidation_NB_FNmatch").resolveLinks();
		validate_NewBusiness(tdSpecific, false);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - LN match, FN and DOB don't match (New business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Quote.
	 * 3. Add member number on General tab with matching Last Name .
	 * 4. Fill All other required data up to Documents and Bind Tab.
	 * 5. Verify that Error is not displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 6. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3786")
	public void pas3786_Validate_Override_NewBusiness_LNmatch(@Optional("AZ") String state) {
		TestData tdSpecific = getTestSpecificTD("TestData_MembershipValidation_NB_LNmatch").resolveLinks();
		validate_NewBusiness(tdSpecific, false);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - DOB match, FN and LN don't match (New business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Quote.
	 * 3. Add member number on General tab with matching DOB
	 * 4. Fill All other required data up to Documents and Bind Tab.
	 * 5. Verify that Error is not displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 6. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3786")
	public void pas3786_Validate_Override_NewBusiness_DOBmatch(@Optional("AZ") String state) {
		TestData tdSpecific = getTestSpecificTD("TestData_MembershipValidation_NB_DOBmatch").resolveLinks();
		validate_NewBusiness(tdSpecific, false);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - FN, LN, DOB don't match (Endorsement)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy without Membership Number.
	 * 3. Initiate endorsement for the policy.
	 * 4. Add member number on General tab with mismatching First Name, Last Name and DOB.
	 * 5. Fill All other required data up to Documents and Bind Tab.
	 * 6. Verify that Error is displayed -  "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 7. Override the error and bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3786")
	public void pas3786_Validate_Override_Endorsement_NoMatch(@Optional("AZ") String state) {
		TestData tdSpecificNB = getTestSpecificTD("TestData_MembershipValidation_MembershipNo").resolveLinks();
		TestData tdSpecificEnd = getTestSpecificTD("TestData_MembershipValidation_End_Ren_MembershipYes").resolveLinks();
		validate_Endorsement(tdSpecificNB, tdSpecificEnd, true);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - FN, LN or DOB match (FN match) (Endorsement)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy without Membership Number.
	 * 3. Initiate endorsement for the policy.
	 * 4. Add member number on General tab with matching FN, LN or DOB match (FN match)
	 * 5. Fill All other required data up to Documents and Bind Tab.
	 * 6. Verify that Error is not displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 7. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3786")
	public void pas3786_Validate_Override_Endorsement_SomeMatch(@Optional("AZ") String state) {
		TestData tdSpecificNB = getTestSpecificTD("TestData_MembershipValidation_MembershipNo_SomeMatch").resolveLinks();
		TestData tdSpecificEnd = getTestSpecificTD("TestData_MembershipValidation_End_Ren_MembershipYes").resolveLinks();
		validate_Endorsement(tdSpecificNB, tdSpecificEnd, false);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - FN, LN, DOB don't match (Manual Renewal)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy without Membership Number.
	 * 3. Initiate Manual renewal for the policy.
	 * 4. Add member number on General tab with mismatching First Name, Last Name and DOB.
	 * 5. Fill All other required data up to Documents and Bind Tab.
	 * 6. Verify that Error is displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 7. Override the error and bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3786")
	public void pas3786_Validate_Override_Manual_Renewal_NoMatch(@Optional("AZ") String state) {
		TestData tdSpecificNB = getTestSpecificTD("TestData_MembershipValidation_MembershipNo").resolveLinks();
		TestData tdSpecificEnd = getTestSpecificTD("TestData_MembershipValidation_End_Ren_MembershipYes").resolveLinks();
		validate_Manual_Renewal(tdSpecificNB, tdSpecificEnd, true);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - FN, LN or DOB match (FN match) (Manual Renewal)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy without Membership Number.
	 * 3. Initiate Manual renewal for the policy.
	 * 4. Add member number on General tab with matching FN, LN or DOB match (FN match)
	 * 5. Fill All other required data up to Documents and Bind Tab.
	 * 6. Verify that Error is not displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 7. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3786")
	public void pas3786_Validate_Override_Manual_Renewal_SomeMatch(@Optional("AZ") String state) {
		TestData tdSpecificNB = getTestSpecificTD("TestData_MembershipValidation_MembershipNo_SomeMatch").resolveLinks();
		TestData tdSpecificEnd = getTestSpecificTD("TestData_MembershipValidation_End_Ren_MembershipYes").resolveLinks();
		validate_Manual_Renewal(tdSpecificNB, tdSpecificEnd, false);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - validate that rule fires at manual renewal if Membership dummy number is used
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy.
	 * 3. Initiate Manual renewal for the policy.
	 * 4. Enter Membership DUMMY number.
	 * 5. Fill All other required data and bind.
	 * 6. Verify that Error is displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6668 PAS-8815")
	public void pas6668_pas8815_Validate_Override_Manual_Renewal_DummyNumber(@Optional("AZ") String state) {
		TestData tdSpecificNB = getTestSpecificTD("TestData_MembershipValidation_MembershipNo_SomeMatch").resolveLinks();
		TestData tdSpecificEnd = getTestSpecificTD("TestData_MembershipValidation_Renewal_DummyNumber").resolveLinks();
		validate_Manual_Renewal(tdSpecificNB, tdSpecificEnd, true);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - validate that rule fires at automated renewal if Membership dummy number is used
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy.
	 * 3. Run renewal batch jobs to generate renewal image
	 * 4. Retrieve renewal image
	 * 5. Enter Membership DUMMY number.
	 * 5. Fill All other required data and bind.
	 * 6. Verify that Error is displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6668 PAS-8815")
	public void pas6668_pas8815_Validate_Override_Automated_Renewal_DummyNumber(@Optional("AZ") String state) {
		TestData tdSpecificNB = getTestSpecificTD("TestData_MembershipValidation_MembershipNo_SomeMatch").resolveLinks();
		TestData tdSpecificEnd = getTestSpecificTD("TestData_MembershipValidation_Renewal_DummyNumber").resolveLinks();
		validate_Automated_Renewal(tdSpecificNB, tdSpecificEnd, true);
	}


	//Start of PAS-6800
	//Note: mismatching/matching First Name, Last Name and DOB in all PAS-6800 TCs are meant to be mismatching/matching with
	//member from membership response, which IS NOT the first in membership response xml.
	//matching with first from response xml is in the scope of PAS-3786)

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - FN, LN and DOB don't match (New business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Quote.
	 * 3. Add member number on General tab with mismatching First Name, Last Name and DOB.
	 * 4. Fill All other required data up to Documents and Bind Tab.
	 * 5. Verify that Error is displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 6. Override the error and bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6800")
	public void pas6800_Validate_Override_NewBusiness_NoMatch(@Optional("AZ") String state) {
		TestData tdSpecific = getTestSpecificTD("TestData_MembershipValidation_NB_NoMatch_PAS6800").resolveLinks();
		validate_NewBusiness(tdSpecific, true);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - FN, LN and DOB match (New business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Quote.
	 * 3. Add member number on General tab with matching First Name, Last Name and DOB.
	 * 4. Fill All other required data up to Documents and Bind Tab.
	 * 5. Verify that Error is not displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 6. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6800")
	public void pas6800_Validate_Override_NewBusiness_AllMatch(@Optional("AZ") String state) {
		TestData tdSpecific = getTestSpecificTD("TestData_MembershipValidation_NB_AllMatch_PAS6800").resolveLinks();
		validate_NewBusiness(tdSpecific, false);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - FN match, LN and DOB don't match (New business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Quote.
	 * 3. Add member number on General tab with matching First Name.
	 * 4. Fill All other required data up to Documents and Bind Tab.
	 * 5. Verify that Error is not displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 6. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6800")
	public void pas6800_Validate_Override_NewBusiness_FNmatch(@Optional("AZ") String state) {
		TestData tdSpecific = getTestSpecificTD("TestData_MembershipValidation_NB_FNmatch_PAS6800").resolveLinks();
		validate_NewBusiness(tdSpecific, false);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - LN match, FN and DOB don't match (New business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Quote.
	 * 3. Add member number on General tab with matching Last Name .
	 * 4. Fill All other required data up to Documents and Bind Tab.
	 * 5. Verify that Error is not displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 6. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6800")
	public void pas6800_Validate_Override_NewBusiness_LNmatch(@Optional("AZ") String state) {
		TestData tdSpecific = getTestSpecificTD("TestData_MembershipValidation_NB_LNmatch_PAS6800").resolveLinks();
		validate_NewBusiness(tdSpecific, false);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - DOB match, FN and LN don't match (New business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Quote.
	 * 3. Add member number on General tab with matching DOB
	 * 4. Fill All other required data up to Documents and Bind Tab.
	 * 5. Verify that Error is not displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 6. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6800")
	public void pas6800_Validate_Override_NewBusiness_DOBmatch(@Optional("AZ") String state) {
		TestData tdSpecific = getTestSpecificTD("TestData_MembershipValidation_NB_DOBmatch_PAS6800").resolveLinks();
		validate_NewBusiness(tdSpecific, false);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - FN, LN, DOB don't match (Endorsement)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy without Membership Number.
	 * 3. Initiate endorsement for the policy.
	 * 4. Add member number on General tab with mismatching First Name, Last Name and DOB.
	 * 5. Fill All other required data up to Documents and Bind Tab.
	 * 6. Verify that Error is displayed -  "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 7. Override the error and bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6800")
	public void pas6800_Validate_Override_Endorsement_NoMatch(@Optional("AZ") String state) {
		TestData tdSpecificNB = getTestSpecificTD("TestData_MembershipValidation_MembershipNo_PAS6800").resolveLinks();
		TestData tdSpecificEnd = getTestSpecificTD("TestData_MembershipValidation_End_Ren_MembershipYes_PAS6800").resolveLinks();
		validate_Endorsement(tdSpecificNB, tdSpecificEnd, true);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - FN, LN or DOB match (FN match) (Endorsement)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy without Membership Number.
	 * 3. Initiate endorsement for the policy.
	 * 4. Add member number on General tab with matching FN, LN or DOB match (LN match)
	 * 5. Fill All other required data up to Documents and Bind Tab.
	 * 6. Verify that Error is not displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 7. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6800")
	public void pas6800_Validate_Override_Endorsement_SomeMatch(@Optional("AZ") String state) {
		TestData tdSpecificNB = getTestSpecificTD("TestData_MembershipValidation_MembershipNo_SomeMatch_PAS6800").resolveLinks();
		TestData tdSpecificEnd = getTestSpecificTD("TestData_MembershipValidation_End_Ren_MembershipYes_PAS6800").resolveLinks();
		validate_Endorsement(tdSpecificNB, tdSpecificEnd, false);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - FN, LN, DOB don't match (Manual Renewal)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy without Membership Number.
	 * 3. Initiate Manual renewal for the policy.
	 * 4. Add member number on General tab with mismatching First Name, Last Name and DOB.
	 * 5. Fill All other required data up to Documents and Bind Tab.
	 * 6. Verify that Error is displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 7. Override the error and bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6800")
	public void pas6800_Validate_Override_Manual_Renewal_NoMatch(@Optional("AZ") String state) {
		TestData tdSpecificNB = getTestSpecificTD("TestData_MembershipValidation_MembershipNo_PAS6800").resolveLinks();
		TestData tdSpecificEnd = getTestSpecificTD("TestData_MembershipValidation_End_Ren_MembershipYes_PAS6800").resolveLinks();
		validate_Manual_Renewal(tdSpecificNB, tdSpecificEnd, true);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation - FN, LN or DOB match (FN match) (Manual Renewal)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy without Membership Number.
	 * 3. Initiate Manual renewal for the policy.
	 * 4. Add member number on General tab with matching FN, LN or DOB match (LN match)
	 * 5. Fill All other required data up to Documents and Bind Tab.
	 * 6. Verify that Error is not displayed - "Membership Validation Failed. Please review the Membership Report and confirm..."
	 * 7. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6800")
	public void pas6800_Validate_Override_Manual_Renewal_SomeMatch(@Optional("AZ") String state) {
		TestData tdSpecificNB = getTestSpecificTD("TestData_MembershipValidation_MembershipNo_SomeMatch_PAS6800").resolveLinks();
		TestData tdSpecificEnd = getTestSpecificTD("TestData_MembershipValidation_End_Ren_MembershipYes_PAS6800").resolveLinks();
		validate_Manual_Renewal(tdSpecificNB, tdSpecificEnd, false);
	}
	//End of PAS-6800

	private void goToBindAndVerifyError(ErrorEnum.Errors errorCode) {
		DocumentsAndBindTab.btnPurchase.click();
		errorTab.verify.errorsPresent(errorCode);
	}

	private void validate_NewBusiness(TestData tdSpecific, Boolean ruleShouldFire) {
		TestData testData = getPolicyTD().adjust(tdSpecific);
		testData.adjust(tdSpecific).adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "A$<rx:\\d{8}>");
		mainApp().open();
		createCustomerIndividual();
		log.info("Policy Creation Started...");
		policy.initiate();

		policy.getDefaultView().fillUpTo(testData, DocumentsAndBindTab.class, true);
		if (ruleShouldFire) {
			goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME);
			errorTab.overrideAllErrors();
			errorTab.buttonOverride.click();
		}

		new DocumentsAndBindTab().submitTab();
		new PurchaseTab().fillTab(testData).submitTab();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	private void validate_Endorsement(TestData tdSpecificNB, TestData tdSpecificEnd, Boolean ruleShouldFire) {
		TestData testData = getPolicyTD().adjust(tdSpecificNB);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.endorse().performAndFill(tdSpecificEnd);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab(true);
		checkAndOverrideErrors(ruleShouldFire);
	}

	private void validate_Manual_Renewal(TestData tdSpecificNB, TestData tdSpecificEnd, Boolean ruleShouldFire) {
		TestData testData = getPolicyTD().adjust(tdSpecificNB);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.renew().start();
		policy.getDefaultView().fillUpTo(tdSpecificEnd, DocumentsAndBindTab.class, true);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab(true);
		checkAndOverrideErrors(ruleShouldFire);
	}

	private void validate_Automated_Renewal(TestData tdSpecificNB, TestData tdSpecificEnd, Boolean ruleShouldFire) {
		TestData testData = getPolicyTD().adjust(tdSpecificNB);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		log.info("Policy Renewal Image Generation Date" + renewImageGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);

		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		PolicySummaryPage.buttonRenewals.click();

		PolicySummaryPage.tableRenewals.getRow(1).getCell("Action").controls.comboBoxes.getFirst().setValue("Data Gathering");
		PolicySummaryPage.tableRenewals.getRow(1).getCell("Action").controls.buttons.get("Go").click();

		CreateQuoteVersionTab createQuoteVersionTab = new CreateQuoteVersionTab();
		createQuoteVersionTab.getAssetList().getAsset(AutoSSMetaData.CreateQuoteVersionTab.DESCRIPTION).setValue("test");
		createQuoteVersionTab.submitTab();

		policy.getDefaultView().fillUpTo(tdSpecificEnd, DocumentsAndBindTab.class, true);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab(true);
		checkAndOverrideErrors(ruleShouldFire);

	}

	////if rule should fire, check the error and override
	private void checkAndOverrideErrors(boolean ruleShouldFire) {
		if (ruleShouldFire) {
			errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME);
			errorTab.overrideAllErrors();
			errorTab.submitTab();
		}
		PolicySummaryPage.labelPolicyStatus.isVisible(); //this indicates that transaction was completed and errors was not displayed

	}

}
