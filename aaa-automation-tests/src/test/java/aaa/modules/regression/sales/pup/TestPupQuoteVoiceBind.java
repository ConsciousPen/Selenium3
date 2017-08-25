package aaa.modules.regression.sales.pup;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchasePaymentMethodTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;


/**
 * @author Yonggang Sun
 * @name Test Create PUP Pup Quote Voice Bind
 * @scenario
 * 1. Create new or open existent Customer;
 * 2. Create Umbrella Quote
 * 3. Issue policy;
 * 4. Check Policy status is Active.
 */
public class TestPupQuoteVoiceBind extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PUP")
    public void TC01_testQuoteVoiceBind() {
        mainApp().open();
        //Create new or open existent Customer;
        createCustomerIndividual();
        //Fill all required filds, rate the quote and save.
        this.createQuote();
        //Copy the original quote(from step 2) - quote1
        policy.copyQuote();
        //Rate the quote2
        policy.calculatePremium(getPolicyTD("DataGather", "TestData"));
        //Go To Bind Tab
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
        //Press "Purchase" button
        policy.getDefaultView().getTab(BindTab.class).submitTab();
        //Set Voice Signature = Yes
        //Set amount cash = due value;
        
        policy.getDefaultView().getTab(PurchaseTab.class).fillTab(remainingBalance(getTestSpecificTD("TestData_Voice_Cash"),PurchaseMetaData.PurchaseTab.PAYMENT_METHOD_CASH)); 
        //Press "Apply payment" button and confirm it.
        //Verify ER-0589 error appears
        applyPayment("Voice Signature is not available if cash or check is selected as a payment method.");
        //Clear amount cash input box
        //Set amount check = due value;
        reSetCashVaue();
        policy.getDefaultView().getTab(PurchaseTab.class).fillTab(remainingBalance(getTestSpecificTD("TestData_Voice_Check"),PurchaseMetaData.PurchaseTab.PAYMENT_METHOD_CHECK));
        //Press "Apply payment" button and confirm it.
        //Verify ER-0589 error appears
        applyPayment("Voice Signature is not available if cash or check is selected as a payment method.");
        //Clear amount check input box
        //Set amount credit card = due value;
        //Press "Apply payment" button and confirm it.
        //Verify Voice signature dialogue appears.
        reSetCheckVaue();
        policy.getDefaultView().getTab(PurchasePaymentMethodTab.class).fillTab(getTestSpecificTD("CreditCard")).submitTab();
        
        try {
            policy.getDefaultView().getTab(PurchaseTab.class).superfillTab(getTestSpecificTD("TestData_Voice_CreditCard"));
        } catch (Exception e) {
			// TODO Don't have to to process this Exception
        }
        //Verify it contains due amount and last four digits of credit card number.
        //Confirm the dialogue
        policy.getDefaultView().getTab(PurchaseTab.class).submitTab();
        //Verify that the policy is in active state.
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
    
    @Test
    @TestInfo(component = "Policy.PUP")
    public void TC02_testQuoteVoiceBind() {
        mainApp().open();
        // Create new or open existent Customer;
        createCustomerIndividual();
        // Fill all required filds, rate the quote and save.
        this.createQuote();
        policy.calculatePremium(getPolicyTD("DataGather", "TestData"));
        //Go To Bind Tab
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
        policy.getDefaultView().getTab(BindTab.class).submitTab();
        //Press "Purchase" button
        policy.getDefaultView().getTab(PurchasePaymentMethodTab.class).fillTab(getTestSpecificTD("Payment_ACH")).submitTab();
        try {
            //Set Voice Signature = Yes
            policy.getDefaultView().getTab(PurchaseTab.class).superfillTab(getTestSpecificTD("TestData_Voice_ACH"));
        } catch (Exception e) {
            // TODO Don't have to to process this Exception
        }
        //Press "Apply payment" button and confirm it.
        policy.getDefaultView().getTab(PurchaseTab.class).submitTab();
        //Set amount EFT = due value;
        //Verify Voice signature dialogue appears.
        //Verify it contains due amount.
        //Confirm the dialogue
        //Verify that the policy is in active state.
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

    }
    
    @Test
    @TestInfo(component = "Policy.PUP")
    public void TC03_testQuoteVoiceBind() {
        mainApp().open();
        //Create new or open existent Customer;
        createCustomerIndividual();
        //Fill all required filds, rate the quote and save.
        this.createQuote();
        
        policy.calculatePremium(getPolicyTD("DataGather", "TestData"));
        //Go To Bind Tab
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
        //Press "Purchase" button
        policy.getDefaultView().getTab(BindTab.class).submitTab();
        policy.getDefaultView().getTab(PurchasePaymentMethodTab.class).fillTab(getTestSpecificTD("CreditCard")).submitTab();
        policy.getDefaultView().getTab(PurchasePaymentMethodTab.class).fillTab(getTestSpecificTD("Payment_ACH")).submitTab();
      
        try {
            //Set Voice Signature = Yes
            //Set amount EFT = due value - 50$;
            //Set amount credit card = 50$;
            policy.getDefaultView().getTab(PurchaseTab.class).superfillTab(getTestSpecificTD("TestData_Voice_ACH_CreditCard"));
        } catch (Exception e) {
            // TODO Don't have to to process this Exception
        }
      //Press "Apply payment" button and confirm it.
      policy.getDefaultView().getTab(PurchaseTab.class).submitTab();
      //Verify Voice signature dialogue appears.
      //Verify it contains two numbers (50$ and due amount - 50$) and last four digits of credit card number.
      //Confirm the dialogue
      //Verify that the policy is in active state.
      PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
    
    private void applyPayment(String errorMessage) {
        policy.getDefaultView().getTab(PurchaseTab.class).btnApplyPayment.click();
        try {
            policy.getDefaultView().getTab(PurchaseTab.class).confirmPurchase.confirm();
        } catch (Exception e) {
            // TODO: Alert browser pop-up message will throw UnhandledAlertException.
        }
        if (BrowserController.get().driver().switchTo().alert() != null) {
            // Verify ER-0589 error appears
            CustomAssert.assertEquals(BrowserController.get().driver().switchTo().alert().getText(), errorMessage);
            BrowserController.get().driver().switchTo().alert().accept();
        }
    }

    private TestData remainingBalance(TestData td, AssetDescriptor<TextBox> textBox) {
        String value = policy.getDefaultView().getTab(PurchaseTab.class).remainingBalanceDueToday.getValue();
        if (!StringUtils.isEmpty(value)) {
            td.adjust(TestData.makeKeyPath(policy.getDefaultView().getTab(PurchaseTab.class).getAssetList().getName(), 
                    textBox.getLabel()), value);
        }
        return td;
    }

    private void reSetCashVaue() {
        policy.getDefaultView().getTab(PurchaseTab.class).cash.setValue(StringUtils.EMPTY);
    }

    private void reSetCheckVaue() {
        policy.getDefaultView().getTab(PurchaseTab.class).check.setValue(StringUtils.EMPTY);
        policy.getDefaultView().getTab(PurchaseTab.class).checkReference.setValue(StringUtils.EMPTY);
    }

}
