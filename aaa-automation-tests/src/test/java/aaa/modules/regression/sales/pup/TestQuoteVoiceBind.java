package aaa.modules.regression.sales.pup;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.abstract_tabs.Purchase;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderwritingAndApprovalTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Ryan Yu
 * @name Test Create PUP Pup Quote Voice Bind
 * @scenario
 * 1. Create new or open existent Customer;
 * 2. Create Umbrella Quote
 * 3. Issue policy;
 * 4. Check Policy status is Active.
 */
public class TestQuoteVoiceBind extends PersonalUmbrellaBaseTest {
	//private final String VOICE_SIGNATURE_ERROR = "Voice Signature is not available if cash or check is selected as a payment method.";
	private PurchaseTab purchaseTab = policy.getDefaultView().getTab(PurchaseTab.class);

	//verify alert works specifically on different browsers (doesn't work on chrome, temp switch off)
	/*
    @Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
    @TestInfo(component = ComponentConstant.Sales.PUP)
    public void TC01_testQuoteVoiceBind(@Optional("") String state) {
        mainApp().open();
        precondition();
        //Set Voice Signature = Yes
        //Set amount cash = due value;
        purchaseTab.fillTab(getTestSpecificTD("TestData_Voice_Cash"));
        //Press "Apply payment" button and confirm it.
        //Verify ER-0589 error appears
        verifyAlertError();
        //Clear amount cash input box
        reSetCashVaue();
        //Set amount check = due value;
        purchaseTab.fillTab(getTestSpecificTD("TestData_Voice_Check"));
        //Press "Apply payment" button and confirm it.
        //Verify ER-0589 error appears
        verifyAlertError();
        //Clear amount check input box
        reSetCheckVaue();
        //Set amount credit card = due value;
        //Press "Apply payment" button and confirm it.
        //Verify Voice signature dialogue appears.
        purchaseTab.fillTab(getTestSpecificTD("TestData_Voice_CreditCard"));
        purchaseTab.submitTab();
        assertThat(Purchase.confirmVoiceSignature.isVisible()).isTrue();
        //CustomAssert.assertTrue(purchaseTab.confirmVoiceSignature.isVisible());
        Purchase.confirmVoiceSignature.confirm();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
    */

	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void TC02_testQuoteVoiceBind(@Optional("") String state) {
		mainApp().open();
		precondition();
		purchaseTab.fillTab(getTestSpecificTD("TestData_Voice_ACH"));
		purchaseTab.submitTab();
		//Verify Voice signature dialogue appears.
		//Verify it contains due amount.
		//Confirm the dialogue
		assertThat(Purchase.confirmVoiceSignature.isVisible()).isTrue();
		//CustomAssert.assertTrue(purchaseTab.confirmVoiceSignature.isVisible());
		Purchase.confirmVoiceSignature.confirm();
		//Verify that the policy is in active state.

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}

	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void TC03_testQuoteVoiceBind(@Optional("") String state) {
		mainApp().open();
		precondition();
		purchaseTab.fillTab(getTestSpecificTD("TestData_Voice_ACH_CreditCard"));
		// Press "Apply payment" button and confirm it.
		purchaseTab.submitTab();
		// Verify Voice signature dialogue appears.
		// Verify it contains two numbers (50$ and due amount - 50$) and last
		// four digits of credit card number.
		// Confirm the dialogue
		//CustomAssert.assertTrue(purchaseTab.confirmVoiceSignature.isVisible());
		assertThat(Purchase.confirmVoiceSignature.isVisible()).isTrue();
		Purchase.confirmVoiceSignature.confirm();
		// Verify that the policy is in active state.
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	private void precondition() {
		createQuote();
		policy.calculatePremium(getPolicyTD());
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERWRITING_AND_APPROVAL.get());
		policy.getDefaultView().getTab(UnderwritingAndApprovalTab.class).fillTab(getPolicyTD());
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
		policy.getDefaultView().getTab(BindTab.class).submitTab();
	}

	/*
	private void verifyAlertError() {
		try{
			//purchaseTab.submitTab();
			purchaseTab.btnApplyPayment.click();
			purchaseTab.confirmPurchase.buttonYes.click(Waiters.NONE); 
		} catch (Exception e) {}
		// Verify ER-0589 error appears
		CustomAssert.assertEquals(BrowserController.get().driver().switchTo().alert().getText(), VOICE_SIGNATURE_ERROR);
		BrowserController.get().driver().switchTo().alert().accept();
	}
	
	private void reSetCashVaue() {
		purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.PAYMENT_METHOD_CASH.getLabel(), TextBox.class).setValue("");
    }

    private void reSetCheckVaue() {
    	purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.PAYMENT_METHOD_CHECK.getLabel(), TextBox.class).setValue("");
    }

*/
}
