package aaa.modules.regression.billing_and_payments.home_ss.ho3;

import org.testng.annotations.Test;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AddHoldActionTab;
import aaa.main.modules.billing.account.defaulttabs.BillingAccountTab;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Kazarnovskiy Lev
 * @name Test Add Billing Account on Hold
 * @scenario:
 *1. Create Customer and issue Hss HO3 Policy.
 *2. Navigate to Billing tab.
 *3. Select 'Hold' from Select Action drop down and click 'Go' button.
 *4. Enter 'Effective Date' which is LESS than the current system date.         TestData1
 *5. Enter 'End Date' which is earlier than 'Effective Date' + 1.               TestData1
 *6. Set reason.                                                                TestData1
 *7. Click 'Ok' button and verify errors.
 *8. Set Reason field = "Other" and click 'Ok' button.                          AdjustedData
 *9. Verify error.
 *10. Fill all fields and Additional information field and click 'Ok' button.   TestData_2
 *11. Verify billing account status is "On hold".
 *12. Select 'Update Hold' from Select Action drop down.
 *13. Click 'Go' button.
 *14. Remove hold.                                                              VERIFIED ACTIVE BA STATUS
 *15. Clicks on 'Hold' link against the policy details displayed under          DEPRICATED STEP
 * the 'Billing Account Policies' section of Billing screen.                    DEPRICATED STEP
 *16. Fill required fields and click 'Ok' button.                               DEPRICATED STEP
 *17. Verify billing account status is "On hold".                               DEPRICATED STEP
 *
 * @details
 */
public class TestPolicyBillingAccountOnHold extends TestPolicyBilling {

	private TestData tdBilling = testDataManager.billingAccount;

	@Test
	@TestInfo(component = "Billing.PersonalLines")
	public void hssPolicyBillingAccountOnHold() {
		BillingAccount ba = new BillingAccount();
		AddHoldActionTab ahaTab = new AddHoldActionTab();
		BillingAccountTab baTab = new BillingAccountTab();

		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		BillingSummaryPage.open();
		ba.addHold().start();

		ahaTab.fillTab(tdBilling.getTestData("AddHold", "TestData_1"));
		AddHoldActionTab.buttonAddUpdate.click();
		ahaTab.verifyFieldHasMessage(BillingAccountMetaData.AddHoldActionTab.HOLD_NAME.getLabel(), "Value is required");
		ahaTab.verifyFieldHasMessage(BillingAccountMetaData.AddHoldActionTab.HOLD_DESCRIPTION.getLabel(), "Value is required");
		ahaTab.verifyFieldHasMessage(BillingAccountMetaData.AddHoldActionTab.HOLD_TYPE.getLabel(), "Value is required");
		ahaTab.verifyFieldHasMessage(BillingAccountMetaData.AddHoldActionTab.HOLD_EFFECTIVE_DATE.getLabel(), "Cannot be earlier than today");
		ahaTab.verifyFieldHasMessage(BillingAccountMetaData.AddHoldActionTab.HOLD_EXPIRATION_DATE.getLabel(), "Date must be after effective date");

		ahaTab.fillTab(tdBilling.getTestData("AddHold", "TestData_1").adjust(TestData.makeKeyPath("AddHoldActionTab", "Reason"), "Other"));
		AddHoldActionTab.buttonAddUpdate.click();
		ahaTab.verifyFieldHasMessage(BillingAccountMetaData.AddHoldActionTab.ADDITIONAL_INFO.getLabel(), "Value is required");

		//Step #10
		ahaTab.fillTab(tdBilling.getTestData("AddHold", "TestData_2"));
		AddHoldActionTab.buttonAddUpdate.click();
		AddHoldActionTab.buttonCancel.click();

		baTab.verifyFieldHasValue(BillingAccountMetaData.BillingAccountTab.BILLING_ACCOUNT_POLICIES_STATUS.getLabel(), "On Hold");

		ba.removeHold().start().submit();
		AddHoldActionTab.buttonCancel.click();
		baTab.verifyFieldHasValue(BillingAccountMetaData.BillingAccountTab.BILLING_ACCOUNT_POLICIES_STATUS.getLabel(), "Active");
	}
}
