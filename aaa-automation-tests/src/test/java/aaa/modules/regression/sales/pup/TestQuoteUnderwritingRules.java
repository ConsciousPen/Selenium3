package aaa.modules.regression.sales.pup;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum.Errors;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.pup.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test Quote Underwriting rules
 * @scenario
 * 1. Create new or open existed customer.
 * 2. Initiate PUP quote creation.
 * 3. Fill all mandatory fields on all tabs, order reports, calculate premium. 
 * 4. Navigate to Underwriting And Approval tab.
 * 5. Fill tab according to "TestData_UW1",
 * 6. Press Continue and verify error messages displaying on tab:   
 * 7. Fill tab according to "TestData_UW2",
 * 8. Press Continue button and verify that all Remarks fields are displaying error messages. 
 * 9. Fill tab according to "TestData_UW3",
 * 10. Navigate to Bind tab and click Purchase button. 
 * 11. Verify Error tab is opened with errors messages: 
 * 12. Navigate to Underwriting And Approval tab and fill tab with correct values (TestData_UW4). 
 * 13. Purchase policy.
 * 14. Verify policy status is Active on Consolidated policy view.
 * @details
 */

public class TestQuoteUnderwritingRules extends PersonalUmbrellaBaseTest {
	private final String FIELDS_ERROR_KEY = "FieldsError";
	private final String BOTTOM_ERROR_KEY = "BottomError";
	private UnderwritingAndApprovalTab underwritingTab = policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class);
	private ErrorTab errorTab = policy.getDefaultView().getTab(ErrorTab.class);
	
	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void testQuoteUnderwritingRules(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		
		TestData tdPolicy = getPolicyTD();
		tdPolicy = adjustWithRealPolicies(tdPolicy, getPrimaryPoliciesForPup());
		TestData td_uw1 = getTestSpecificTD("TestData_UW1");
		TestData td_uw2 = getTestSpecificTD("TestData_UW2");
		TestData td_uw3 = getTestSpecificTD("TestData_UW3");
		TestData td_uw4 = getTestSpecificTD("TestData_UW4");
		
		policy.initiate();
		policy.getDefaultView().fillUpTo(tdPolicy, UnderwritingAndApprovalTab.class, false);

		// Enter td_uw1 and verify the wringing message
		underwritingTab.fillTab(td_uw1);
		underwritingTab.submitTab();
		checkErrorMsg(td_uw1);

		// Enter td_uw2 and verify the mandatory fields for Remark
		underwritingTab.fillTab(td_uw2);
		underwritingTab.submitTab();
		checkErrorMsg(td_uw2);

		// Enter td_uw3 for Remark fields and verify override messages
		underwritingTab.fillTab(td_uw3);
		underwritingTab.submitTab();
		policy.getDefaultView().fillFromTo(tdPolicy, DocumentsTab.class, BindTab.class, true);
		policy.getDefaultView().getTab(BindTab.class).btnPurchase.click();
		checkOverrideMsg();
		errorTab.cancel();
		
		// Enter td_uw4 to change all UW questions as No
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERWRITING_AND_APPROVAL.get());
		underwritingTab.fillTab(td_uw4);
		underwritingTab.submitTab();

		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
		policy.getDefaultView().fillFromTo(tdPolicy, BindTab.class, PurchaseTab.class, true);
		policy.getDefaultView().getTab(PurchaseTab.class).submitTab();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST Underwriting rules: PUP Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());

	}

	private void checkErrorMsg(TestData td) {
		if(td.containsKey(FIELDS_ERROR_KEY))
			checkFieldsError(td);
		if(td.containsKey(BOTTOM_ERROR_KEY))
			checkBottomError(td);
	}

	private void checkFieldsError(TestData td) {
		TestData tdFieldsError = td.getTestData(FIELDS_ERROR_KEY);
		for (String key : tdFieldsError.getKeys()) {
			assertThat(underwritingTab.getAssetList().getWarning(key)).valueContains(tdFieldsError.getValue(key));
		}
	}
	
	private void checkBottomError(TestData td) {
		for (String error : td.getList(BOTTOM_ERROR_KEY)) {
			assertThat(underwritingTab.getBottomWarning()).valueContains(error);
		}
	}
	
	private void checkOverrideMsg() {
		errorTab.verify.errorsPresent(Errors.ERROR_AAA_PUP_SS5310180);
		errorTab.verify.errorsPresent(Errors.ERROR_AAA_PUP_SS5310750);

		switch(getState()) {
		case "CT":
		case "KY":
			errorTab.verify.errorsPresent(Errors.ERROR_AAA_PUP_SS3171100);
			break;
		case "CA":
			errorTab.verify.errorsPresent(Errors.ERROR_AAA_PUP_SS5311428);
			//TODO need to add "Residents holding one of the special occupations or holder of any elected or ..."
			break;
		case "MD":
			errorTab.verify.errorsPresent(Errors.ERROR_AAA_PUP_SS3171100);
			errorTab.verify.errorsPresent(Errors.ERROR_AAA_PUP_SS7150344);
			break;
		default: 
			errorTab.verify.errorsPresent(Errors.ERROR_AAA_PUP_SS3171100);
			errorTab.verify.errorsPresent(Errors.ERROR_AAA_PUP_SS5311428);
		}
	}
}
