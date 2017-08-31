package aaa.modules.regression.sales.pup;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.UnhandledAlertException;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.BrowserController;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import aaa.toolkit.webdriver.customcontrols.PaymentMethodAllocationControl;


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
	private final String VOICE_SIGNATURE_ERROR = "Voice Signature is not available if cash or check is selected as a payment method.";
	private PurchaseTab purchaseTab = policy.getDefaultView().getTab(PurchaseTab.class);
	
    @Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
    @TestInfo(component = ComponentConstant.Sales.PUP)
    public void TC01_testQuoteVoiceBind() {
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
        CustomAssert.assertTrue(purchaseTab.confirmVoiceSignature.isVisible());
        purchaseTab.confirmVoiceSignature.confirm();
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
    
    @Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
    @TestInfo(component = ComponentConstant.Sales.PUP)
    public void TC02_testQuoteVoiceBind() {
    	mainApp().open();
    	precondition();
        purchaseTab.fillTab(getTestSpecificTD("TestData_Voice_ACH"));
        purchaseTab.submitTab();
        //Verify Voice signature dialogue appears.
        //Verify it contains due amount.
        //Confirm the dialogue
        CustomAssert.assertTrue(purchaseTab.confirmVoiceSignature.isVisible());
        purchaseTab.confirmVoiceSignature.confirm();
        //Verify that the policy is in active state.
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

    }
    
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void TC03_testQuoteVoiceBind() {
		mainApp().open();
		precondition();
		purchaseTab.fillTab(getTestSpecificTD("TestData_Voice_ACH_CreditCard"));
		// Press "Apply payment" button and confirm it.
		purchaseTab.submitTab();
		// Verify Voice signature dialogue appears.
		// Verify it contains two numbers (50$ and due amount - 50$) and last
		// four digits of credit card number.
		// Confirm the dialogue
		CustomAssert.assertTrue(purchaseTab.confirmVoiceSignature.isVisible());
        purchaseTab.confirmVoiceSignature.confirm();
		// Verify that the policy is in active state.
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}
	
	private void precondition(){
		createCustomerIndividual();
		createQuote();
        policy.calculatePremium(getPolicyTD());
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
        policy.getDefaultView().getTab(BindTab.class).submitTab();
	}
    
	private void verifyAlertError() {
		try{
			purchaseTab.submitTab();
		} catch (UnhandledAlertException e) {}
		// Verify ER-0589 error appears
		CustomAssert.assertEquals(BrowserController.get().driver().switchTo().alert().getText(), VOICE_SIGNATURE_ERROR);
		BrowserController.get().driver().switchTo().alert().accept();
	}
	
	private void reSetCashVaue() {
		purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.PAYMENT_ALLOCATION.getLabel(), PaymentMethodAllocationControl.class).fill(DataProviderFactory.dataOf("Cash", StringUtils.EMPTY));
    }

    private void reSetCheckVaue() {
    	purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.PAYMENT_ALLOCATION.getLabel(), PaymentMethodAllocationControl.class).fill(DataProviderFactory.dataOf("Check", StringUtils.EMPTY));
    }


}
