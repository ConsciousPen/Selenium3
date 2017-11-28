package aaa.modules.regression.sales.auto_ss.functional;

import java.lang.reflect.Method;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
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

	@DataProvider(name = "TdProvider")
	public Object[][] credentialsNB(ITestContext context, Method m) {
		String stateValue = getStateValue(context);

		switch (m.getName()) {
			case "pas3786_Validate_Override_NewBusiness":
				return new Object[][] {
						{stateValue, "TestData_MembershipValidation_NB_NoMatch", true},
						{stateValue, "TestData_MembershipValidation_NB_AllMatch", false},
						{stateValue, "TestData_MembershipValidation_NB_FNmatch", false},
						{stateValue, "TestData_MembershipValidation_NB_LNmatch", false},
						{stateValue, "TestData_MembershipValidation_NB_DOBmatch", false}
				};
			case "pas3786_Validate_Override_Endorsement":
				return new Object[][] {
						{stateValue, "TestData_MembershipValidation_MembershipNo", "TestData_MembershipValidation_End_Ren_MembershipYes", true},
						{stateValue, "TestData_MembershipValidation_MembershipNo_SomeMatch", "TestData_MembershipValidation_End_Ren_MembershipYes", false},
				};
			default: //for "pas3786_Validate_Override_Manual_Renewal"
				return new Object[][] {
						{stateValue, "TestData_MembershipValidation_MembershipNo", "TestData_MembershipValidation_End_Ren_MembershipYes", true},
						{stateValue, "TestData_MembershipValidation_MembershipNo_SomeMatch", "TestData_MembershipValidation_End_Ren_MembershipYes", false},
				};

		}

	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation (New business)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Quote.
	 * 3. Add member number on General tab with:
	 *              case a. - mismatching First Name, Last Name and DOB
	 *              case b. - matching First Name, Last Name and DOB
	 *              case c. - matching First Name
	 *              case d. - matching Last Name
	 *              case e. - matching DOB
	 * 4. Fill All other required data up to Documents and Bind Tab.
	 * 5. Verify that Error "Membership Validation Failed. Please review the Membership Report and confirm..." is:
	 *              case a. - displayed
	 *              case b. - not displayed
	 *              case c. - not displayed
	 *              case d. - not displayed
	 *              case e. - not displayed
	 * 6. Override the error (if displayed)
	 * 7. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization", dataProvider = "TdProvider")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3786")
	public void pas3786_Validate_Override_NewBusiness(String state, String tdName, boolean ruleShouldFire) {
		TestData tdSpecific = getStateTestData(testDataManager.getDefault(this.getClass()), tdName).resolveLinks();
		validate_NewBusiness(tdSpecific, ruleShouldFire);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation (Endorsement)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy without Membership Number.
	 * 3. Initiate endorsement for the policy.
	 * 4. Add member number on General tab with
	 *              case a. - mismatching First Name, Last Name and DOB
	 *              case b. - matching FN, LN or DOB match (FN match)
	 * 5. Fill All other required data up to Documents and Bind Tab.
	 * 6. Verify that Error "Membership Validation Failed. Please review the Membership Report and confirm..." is:
	 *              case a. - displayed
	 *              case b. - not displayed
	 * 7. Override the error (if displayed).
	 * 8. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization", dataProvider = "TdProvider")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3786")
	public void pas3786_Validate_Override_Endorsement(String state, String tdName1, String tdName2, boolean ruleShouldFire) {
		TestData tdSpecificNB = getStateTestData(testDataManager.getDefault(this.getClass()), tdName1).resolveLinks();
		TestData tdSpecificEnd = getStateTestData(testDataManager.getDefault(this.getClass()), tdName2).resolveLinks();
		validate_Endorsement(tdSpecificNB, tdSpecificEnd, ruleShouldFire);
	}

	/**
	 * @author Maris Strazds
	 * @name Test Membership validation (Manual Renewal)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS Policy without Membership Number.
	 * 3. Initiate Manual renewal for the policy.
	 * 4. Add member number on General tab with:
	 *              case a. - mismatching First Name, Last Name and DOB
	 *              case b. - matching FN, LN or DOB match (FN match)
	 * 5. Fill All other required data up to Documents and Bind Tab.
	 * 6. Verify that Error "Membership Validation Failed. Please review the Membership Report and confirm..." is:
	 *              case a. - displayed
	 *              case b. - not displayed
	 * 7. Override the error (if displayed).
	 * 8. Bind.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization", dataProvider = "TdProvider")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3786")
	public void pas3786_Validate_Override_Manual_Renewal(String state, String tdName1, String tdName2, boolean ruleShouldFire) {
		TestData tdSpecificNB = getStateTestData(testDataManager.getDefault(this.getClass()), tdName1).resolveLinks();
		TestData tdSpecificEnd = getStateTestData(testDataManager.getDefault(this.getClass()), tdName2).resolveLinks();
		validate_Manual_Renewal(tdSpecificNB, tdSpecificEnd, ruleShouldFire);
	}

	private void goToBindAndVerifyError(ErrorEnum.Errors errorCode) {
		DocumentsAndBindTab.btnPurchase.click();
		new ErrorTab().verify.errorsPresent(errorCode);
	}

	private void validate_NewBusiness(TestData tdSpecific, boolean ruleShouldFire) {
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

	private void validate_Endorsement(TestData tdSpecificNB, TestData tdSpecificEnd, boolean ruleShouldFire) {
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

	private void validate_Manual_Renewal(TestData tdSpecificNB, TestData tdSpecificEnd, boolean ruleShouldFire) {
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

	//if rule should fire, check the error and override
	private void checkAndOverrideErrors(boolean ruleShouldFire) {
		if (ruleShouldFire) {
			new ErrorTab().verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME);
			new ErrorTab().overrideAllErrors();
			new DocumentsAndBindTab().submitTab();
		}

	}

	private String getStateValue(ITestContext context) {
		String stateValue = context.getCurrentXmlTest().getParameter("state");
		if (stateValue == null) {
			stateValue = Constants.States.AZ;
		}
		return stateValue;
	}

}
