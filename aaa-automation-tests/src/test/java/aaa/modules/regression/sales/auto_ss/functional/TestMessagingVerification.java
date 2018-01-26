package aaa.modules.regression.sales.auto_ss.functional;

import java.text.MessageFormat;
import java.util.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.preconditions.TestEValueDiscountPreConditions;
import aaa.toolkit.webdriver.customcontrols.AddPaymentMethodsMultiAssetList;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

public class TestMessagingVerification extends AutoSSBaseTest implements TestEValueDiscountPreConditions {

    private GeneralTab generalTab = new GeneralTab();
    private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    private ErrorTab errorTab = new ErrorTab();
    private PurchaseTab purchaseTab = new PurchaseTab();
    private BillingAccount billingAccount = new BillingAccount();
    private UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();
    private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();

    private static final String EVALUE_PAYPLAN_ACKNOWLEDGEMENT_CHECK =
            MessageFormat.format(EVALUE_CONFIG_FOR_ACKNOWLEDGEMENT_CHECK, "AAAeMemberQualifications", "paymentPlanRequired", "FALSE");

    @Test(description = "Precondition")
    public static void eValueAcknowledgementConfigCheck() {
        CustomAssert.enableSoftMode();
        verifyAcknowledgementConfiguration(20, 17);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }
    private static void verifyAcknowledgementConfiguration(int sysAndEffDateDelta, int sysAndExpDateDelta) {
        String query = MessageFormat.format(EVALUE_PAYPLAN_ACKNOWLEDGEMENT_CHECK, sysAndEffDateDelta + 1, sysAndEffDateDelta, sysAndExpDateDelta + 1, sysAndExpDateDelta);
        CustomAssert.assertTrue("Configuration for acknowledgement should be present. Please run eValuePayPlanAcknowledgementConfigInsert", DBService.get().getValue(query).isPresent());
    }

    /**
     * *@author Viktoriia Lutsenko
     * *@name Test messaging based on different Pay Plans/Pay Terms and payment types.
     * *@scenario
     * 1. Create active quote with eValue.
     * 2. Select  pay plan/ pay term and bind policy with payment type based on next different variations:
     * a) "Annual", "Eleven Pay - Standard", debit card (payPlanRequired = true).
     * b) "Semi-annual", "Five Pay - Standard", debit card (payPlanRequired = true)
     * c) "Annual", "Quarterly", ACH (payPlanRequired = true)
     * d) "Semi-annual", "Semi-Annual", Cash (payPlanRequired = true)
     * e) "Annual", "Annual", debit card (payPlanRequired = true)
     * f) "Annual", "Quarterly", cash (payPlanRequired = false)
     * g) "Annual", "Eleven Pay - Standard", credit card (payPlanRequired = false)
     * j) "Semi-annual", "Five Pay - Standard", ACH (payPlanRequired = false)
     * h) "Annual", "Semi-Annual", Check (payPlanRequired = false)
     * 3. Verify that on Purchase tab Cash and Check payments are present for: d), e), f), g), j) and h) (because payPlanRequired = false or Annual/Annual or Semi-annual/Semi-annual)
     * 4. Verify that if Autopay isn't selected Error "You must activate AutoPay for the selected payment plan in order to keep eValue discount." is displayed during purchasing for: a), b) and c).
     * 5. Verify that if on Update Billing Account page Autopay is removed but payPlanRequired = true eValue removing pop-up will be displayed: a) and b)
     * *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueAcknowledgementConfigCheck")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-7185, PAS-7192")
    public void pas7185_messagingConfigurablePayPlan(@org.testng.annotations.Optional("OR") String state) {

        TestData ccVisa = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(0);
        TestData eft = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(1);
        TestData dcVisa = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(2);

        CustomAssert.enableSoftMode();
        creationPolicyWithDiffPayPlan("Annual", "Eleven Pay - Standard",true,"Debit");
        updatePaymentMethodBillingAccount(true,true, Optional.of(ccVisa), Optional.of("Credit"));
        verifyEvalueDiscount(false);

        creationPolicyWithDiffPayPlan("Semi-annual", "Five Pay - Standard",true, "Debit");
        updatePaymentMethodBillingAccount(true, false, Optional.empty(), Optional.empty());
        verifyEvalueDiscount(false);

        creationPolicyWithDiffPayPlan("Annual", "Quarterly",true, "ACH");
        updatePaymentMethodBillingAccount(false,true, Optional.of(dcVisa), Optional.of("Debit"));
        verifyEvalueDiscount(true);

        creationPolicyWithDiffPayPlan("Semi-annual", "Semi-Annual",true, "Cash");
        updatePaymentMethodBillingAccount(false,true, Optional.of(ccVisa), Optional.of("Credit"));
        verifyEvalueDiscount(true);

        creationPolicyWithDiffPayPlan("Annual", "Annual",true, "Debit");
        updatePaymentMethodBillingAccount(false,false, Optional.empty(), Optional.empty());
        verifyEvalueDiscount(true);

        creationPolicyWithDiffPayPlan("Annual", "Quarterly",false,"Cash");
        updatePaymentMethodBillingAccount(false,true, Optional.of(ccVisa), Optional.of("Credit"));
        verifyEvalueDiscount(true);

        creationPolicyWithDiffPayPlan("Annual", "Eleven Pay - Standard",false, "Credit");
        updatePaymentMethodBillingAccount(false,true, Optional.of(eft), Optional.of("ACH"));
        verifyEvalueDiscount(true);

        creationPolicyWithDiffPayPlan("Semi-annual", "Five Pay - Standard",false, "ACH");
        updatePaymentMethodBillingAccount(false,false, Optional.empty(), Optional.empty());
        verifyEvalueDiscount(true);

        creationPolicyWithDiffPayPlan("Annual", "Semi-Annual",false, "Check");
        updatePaymentMethodBillingAccount(false,true, Optional.of(dcVisa), Optional.of("Debit"));
        verifyEvalueDiscount(true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    private void verifyEvalueDiscount(boolean evaluePresent) {
        BillingSummaryPage.openPolicy(1);
        PolicySummaryPage.tableAppliedDiscountsPolicy.getRowContains(2, "eValue Discount").verify.present(evaluePresent);
    }

    private void updatePaymentMethodBillingAccount(boolean removeEvalueDialogPresent, boolean updateToAutopay, Optional<TestData> paymentMethod, Optional<String> paymentType) {
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        billingAccount.update().start();
        if (updateToAutopay) {
            AddPaymentMethodsMultiAssetList.buttonAddUpdateCreditCard.click();
            updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.PAYMENT_METHODS).getAsset(BillingAccountMetaData.AddPaymentMethodTab.PAYMENT_METHOD).setValueContains(paymentType.get());
            if ("ACH".equals(paymentType.get())){
                updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.PAYMENT_METHODS).getAsset(BillingAccountMetaData.AddPaymentMethodTab.TRANSIT)
                        .fill(paymentMethod.get());
                updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHODS).getAsset(BillingAccountMetaData.AddPaymentMethodTab.ACCOUNT).fill(paymentMethod.get());
            }
            else {
                updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.PAYMENT_METHODS).getAsset(BillingAccountMetaData.AddPaymentMethodTab.TYPE)
                        .fill(paymentMethod.get());
                updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHODS).getAsset(BillingAccountMetaData.AddPaymentMethodTab.NUMBER)
                        .fill(paymentMethod.get());
            }
            AddPaymentMethodsMultiAssetList.buttonAddUpdatePaymentMethod.click();
            acceptPaymentActionTab.back();
            updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).setValue(true);
            updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.AUTOPAY_SELECTION).setValueContains(paymentType.get());
            if("ACH".equals(paymentType.get())){
                updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.SIGNATURE_ON_FILE_INDICATOR).setValue("Yes");
            }
            verifyRemoveEvalueDialog(removeEvalueDialogPresent);
        }
        else {
            updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).setValue(false);
            verifyRemoveEvalueDialog(removeEvalueDialogPresent);
        }
    }

    private void verifyRemoveEvalueDialog(boolean removeEvalueDialogPresent) {
        Tab.buttonSave.click();
        if (removeEvalueDialogPresent) {
            CustomAssert.assertEquals(Page.dialogConfirmation.labelMessage.getValue(), "Customer acknowledges that removing recurring payments will cause the eValue to be removed.");
            Page.dialogConfirmation.confirm();
        }
    }

    private void creationPolicyWithDiffPayPlan(String payTerm, String payPlan, boolean payPlanRequired, String paymentPlan) {
        TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
        testEValueDiscount.eValueQuoteCreation();
        int days = 0;
        if (!payPlanRequired) {
            days = 18;
        }
        policy.dataGather().start();
        fillGeneralTab(days);
        fillPremiumAndCoveragesTab(payTerm, payPlan);
        fillDocumentAndBindTab();
        if (!payPlanRequired || payPlanRequired && (isAnnual(payTerm, payPlan) || isSemiAnnual(payTerm, payPlan))) {
            purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.PAYMENT_METHOD_CASH).isPresent();
            purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.PAYMENT_METHOD_CHECK).isPresent();
        } else {
            purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.PAYMENT_METHOD_CASH).verify.present(false);
            purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.PAYMENT_METHOD_CHECK).verify.present(false);
        }
        TestData purchaseTabData = getPolicyTD("DataGather", "TestData");
        purchaseTabData.adjust("PurchaseTab", getTestSpecificTD("PurchaseTab_" + paymentPlan));
        purchaseTab.fillTab(purchaseTabData);
        purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.AUTOPAY_MESSAGE_WARNING_BLOCK).verify.present(false);
        if ("Cash".equals(paymentPlan) || "Check".equals(paymentPlan)) {
            purchaseTab.submitTab();
        } else if (!payPlanRequired || payPlanRequired && (isAnnual(payTerm, payPlan) || isSemiAnnual(payTerm, payPlan))) {
            applyAutoPay(paymentPlan);
        } else {
            purchaseTab.submitTab();
            purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.AUTOPAY_MESSAGE_WARNING_BLOCK).verify.present(true);
            applyAutoPay(paymentPlan);
        }
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        log.info("policyNumber: {}", policyNumber);
    }

    private boolean isSemiAnnual(String payTerm, String payPlan) {
        return "Semi-annual".equals(payTerm) && "Semi-Annual".equals(payPlan);
    }

    private boolean isAnnual(String payTerm, String payPlan) {
        return "Annual".equals(payTerm) && "Annual".equals(payPlan);
    }

    private void fillDocumentAndBindTab() {
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.AGREEMENT).setValue("I agree");
        documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION).setValue("Physically Signed");
        documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT).setValue("Physically Signed");
        documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PRIOR_INSURANCE).setValue("Yes");
        documentsAndBindTab.submitTab();
        if (errorTab.isVisible()) {
            errorTab.overrideAllErrors();
            errorTab.submitTab();
        }
    }

    private void fillPremiumAndCoveragesTab(String payTerm, String payPlan) {
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.POLICY_TERM).setValue(payTerm);
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue(payPlan);
        PremiumAndCoveragesTab.calculatePremium();
    }

    private void fillGeneralTab(int days) {
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
        generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).setValue(TimeSetterUtil
                .getInstance().getCurrentTime().minusDays(days).format(DateTimeUtils.MM_DD_YYYY));
    }

    private void applyAutoPay(String paymentPlan) {
        purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.ACTIVATE_AUTOPAY).setValue(true);
        purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.AUTOPAY_SELECTION).setValueByIndex(1);
        if ("ACH".equals(paymentPlan)){
            purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.SIGNATURE_ON_FILE_INDICATOR).setValue("Yes");
        }
        purchaseTab.submitTab();
    }
}


