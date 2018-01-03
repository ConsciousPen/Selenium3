package aaa.modules.regression.sales.auto_ss.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestMessagingVerification extends AutoSSBaseTest {

    private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    private ErrorTab errorTab = new ErrorTab();
    private BillingAccount billingAccount = new BillingAccount();
    private UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();

    /**
     * @author Alex Tinkovan
     * @name Test that Pop-Up with message about removing discount appears when change the eValue policy auto pay selection
     * @scenario 1. Create new eValue eligible quote for VA (Prior BI and Membership Conditions)
     * 2. Set 'Apply eValue Discount' = Yes
     * 3. Bind Quote
     * 4. Make Deposit Payment with DC Visa and Autopay
     * 5. Update Billing Account
     * 6. Unchecked Autopay
     * 7. Verify message
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-245")
    public void pas245_autoPayModificationMessaging(@Optional("VA") String state) {

        TestEValueDiscount.eValueConfigCheck();
        CustomAssert.enableSoftMode();
        eValuePolicyCreation();
        verifyMessageOnPopUp();

    }

    public void eValuePolicyCreation() {

        TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

        testEValueDiscount.eValueQuoteCreation();

        applyEValueDiscountPaymentPlanElevenPaySt();

        bindQuote();
        documentsAndBindTab.submitTab();

        makeDepositPaymentWithDCVisa();

    }

    private void applyEValueDiscountPaymentPlanElevenPaySt() {
        policy.dataGather().start();
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue("Eleven Pay - Standard");
        PremiumAndCoveragesTab.calculatePremium();
    }

    private void bindQuote() {
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.AGREEMENT).setValue("I agree");
        DocumentsAndBindTab.btnPurchase.click();
        errorTab.overrideAllErrors();
        if(errorTab.buttonOverride.isPresent()){
            errorTab.override();
        }
    }

    private void makeDepositPaymentWithDCVisa() {
        PurchaseTab purchaseTab = new PurchaseTab();
        TestData purchaseTabData = getPolicyTD("DataGather", "TestData");
        purchaseTabData.adjust("PurchaseTab", getTestSpecificTD("PurchaseTab_DC"));
        purchaseTab.fillTab(purchaseTabData);
        purchaseTab.submitTab();

    }

    private void verifyMessageOnPopUp() {
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        billingAccount.update().start();
        updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).setValue(false);
        Tab.buttonSave.click();

        CustomAssert.assertEquals(Page.dialogConfirmation.labelMessage.getValue(), "Customer acknowledges that removing recurring payments will cause the eValue to be removed.");
        Page.dialogConfirmation.confirm();
    }
}


