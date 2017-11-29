package aaa.modules.regression.sales.auto_ss.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMembershipValidation extends AutoSSBaseTest {
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3786")
	public void pas3786_Validate_Override_Manual_Renewal_SomeMatch(@Optional("AZ") String state) {
		TestData tdSpecificNB = getTestSpecificTD("TestData_MembershipValidation_MembershipNo_SomeMatch").resolveLinks();
		TestData tdSpecificEnd = getTestSpecificTD("TestData_MembershipValidation_End_Ren_MembershipYes").resolveLinks();
		validate_Manual_Renewal(tdSpecificNB, tdSpecificEnd, false);
	}

	private void goToBindAndVerifyError(ErrorEnum.Errors errorCode) {
		DocumentsAndBindTab.btnPurchase.click();
		new ErrorTab().verify.errorsPresent(errorCode);
	}

	private void validate_NewBusiness(TestData tdSpecific, Boolean ruleShouldFire) {
		TestData testData = getPolicyTD().adjust(tdSpecific);

		mainApp().open();
		createCustomerIndividual();
		log.info("Policy Creation Started...");
		policy.initiate();

		policy.getDefaultView().fillUpTo(testData, DocumentsAndBindTab.class, true);
		if (ruleShouldFire) {
			goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME);
			new ErrorTab().overrideAllErrors();
		}

		new DocumentsAndBindTab().submitTab();
		new PurchaseTab().fillTab(testData).submitTab();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	private void validate_Endorsement(TestData tdSpecificNB, TestData tdSpecificEnd, Boolean ruleShouldFire) {
		TestData testData = getPolicyTD().adjust(tdSpecificNB);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.endorse().performAndFill(tdSpecificEnd);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		checkAndOverrideErrors(ruleShouldFire);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE); //just to see that bind was successful

	}

	private void validate_Manual_Renewal(TestData tdSpecificNB, TestData tdSpecificEnd, Boolean ruleShouldFire) {
		TestData testData = getPolicyTD().adjust(tdSpecificNB);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.renew().start();
		policy.getDefaultView().fillUpTo(tdSpecificEnd, DocumentsAndBindTab.class, true);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		checkAndOverrideErrors(ruleShouldFire);
	}

	////if rule should fire, check the error and override
	private void checkAndOverrideErrors(boolean ruleShouldFire) {
		if (ruleShouldFire) {
			new ErrorTab().verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME);
			new ErrorTab().overrideAllErrors();
			new DocumentsAndBindTab().submitTab();
		}

	}

}
