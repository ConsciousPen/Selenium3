package aaa.modules.regression.billing_and_payments.home_ss.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AddHoldActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

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
public class TestPolicyBillingAccountOnHold extends HomeSSHO3BaseTest {

	private TestData tdBilling = testDataManager.billingAccount;

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups= {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3)
	public void hssPolicyBillingAccountOnHold(@Optional("") String state) {
		BillingAccount billingAccount = new BillingAccount();
		AddHoldActionTab ahaTab = new AddHoldActionTab();

		mainApp().open();
		getCopiedPolicy();
		BillingSummaryPage.open();
		billingAccount.addHold().start();

		CustomSoftAssertions.assertSoftly(softly -> {
					ahaTab.fillTab(getTestSpecificTD("TestData_1"));
					AddHoldActionTab.buttonAddUpdate.click();
			assertThat(ahaTab.getAssetList().getAsset(BillingAccountMetaData.AddHoldActionTab.HOLD_NAME).getWarning().toString()).contains("Value is required");
			assertThat(ahaTab.getAssetList().getAsset(BillingAccountMetaData.AddHoldActionTab.HOLD_DESCRIPTION).getWarning().toString()).contains("Value is required");
			assertThat(ahaTab.getAssetList().getAsset(BillingAccountMetaData.AddHoldActionTab.HOLD_TYPE).getWarning().toString()).contains("Value is required");
			assertThat(ahaTab.getAssetList().getAsset(BillingAccountMetaData.AddHoldActionTab.HOLD_EFFECTIVE_DATE).getWarning().toString()).contains("Cannot be earlier than today");
			assertThat(ahaTab.getAssetList().getAsset(BillingAccountMetaData.AddHoldActionTab.HOLD_EXPIRATION_DATE).getWarning().toString()).contains("Date must be after effective date");

					ahaTab.fillTab(getTestSpecificTD("TestData_1").adjust(TestData.makeKeyPath("AddHoldActionTab", "Reason"), "Other"));
					AddHoldActionTab.buttonAddUpdate.click();
			assertThat(ahaTab.getAssetList().getAsset(BillingAccountMetaData.AddHoldActionTab.ADDITIONAL_INFO).getWarning().toString()).contains("Value is required");
				});

		//Step #10
		ahaTab.fillTab(getTestSpecificTD("TestData_2"));
		ahaTab.getAssetList().getAsset(BillingAccountMetaData.AddHoldActionTab.HOLD_TYPE).setValue("Cancellation"); //due to Ajax no ability to set multiple values for ListBox. So it is needed to set them manually
		billingAccount.addHold().submit();

		new BillingAccountPoliciesVerifier().setBillingStatus(BillingConstants.BillingStatus.ON_HOLD).verify(1);

		billingAccount.removeHold().perform(tdBilling.getTestData("RemoveHold", "TestData"));
		new BillingAccountPoliciesVerifier().setBillingStatus(BillingConstants.BillingStatus.ACTIVE).verify(1);
	}
}
