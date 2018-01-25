package aaa.modules.regression.billing_and_payments.auto_ss.functional;

import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT;
import java.util.Map;
import java.util.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.DbAwaitHelper;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.regression.billing_and_payments.auto_ss.functional.preconditions.TestRefundProcessPreConditions;
import aaa.modules.regression.billing_and_payments.helpers.RefundProcessHelper;
import aaa.modules.regression.billing_and_payments.template.PolicyBilling;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.StaticElement;

public class TestRefundProcess extends PolicyBilling implements TestRefundProcessPreConditions {

    private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
    private static final String MESSAGE_CREDIT_CARD = "Credit Card Visa-4113 expiring 01/22";
    private static final String MESSAGE_DEBIT_CARD = "Debit Card MasterCard-4444 expiring 05/20";
    private static final String MESSAGE_ACH = "Checking/Savings (ACH) #,1542";
    private static final String AMOUNT_CREDIT_CARD = "10";
    private static final String AMOUNT_DEBIT_CARD = "22";
    private static final String AMOUNT_ACH = "33";
    private static final String AMOUNT_CHECK = "10.01";
    private static final String PAYMENT_METHOD_CREDIT_CARD = "Credit card";
    private static final String PAYMENT_METHOD_DEBIT_CARD = "Debit card";
    private static final String PAYMENT_METHOD_ACH = "ACH";
    private static final String PAYMENT_METHOD_CHECK = "Check";
    private static final String APPROVED_REFUND_AMOUNT = "499.99";
    private static final String PENDING_REFUND_AMOUNT = "500";
    private static final String BILLING_PAYMENT_METHOD_CHECK = "PaymentDetailsCheque";
    private static final String BILLING_PAYMENT_METHOD_CARD = "AAAPaymentDetailsPCICreditCard";
    private static final String BILLING_PAYMENT_METHOD_ACH = "AAAPaymentDetailsEFT";
    private TestData tdBilling = testDataManager.billingAccount;
    private TestData tdRefund = tdBilling.getTestData("Refund", "TestData_Check");
    private BillingAccount billingAccount = new BillingAccount();
    private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
    private RefundProcessHelper refundProcessHelper = new RefundProcessHelper();

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }

    @Test(description = "Precondition for TestRefundProcess tests")
    public void precondJobAdding() {
        adminApp().open();
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());
        GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_REFUND_GENERATION_ASYNC_JOB);
        GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_REFUND_DISBURSEMENT_ASYNC_JOB);
        GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_REFUND_DISBURSEMENT_RECEIVE_INFO_JOB);
        GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_REFUNDS_DISBURSMENT_REJECTIONS_ASYNC_JOB);
    }

    @Test(description = "Precondition for TestRefundProcess tests")
    public static void refundDocumentGenerationConfigCheck() {
        CustomAssert.assertTrue("The configuration is missing, run refundDocumentGenerationConfigInsert and restart the env.", DbAwaitHelper
                .waitForQueryResult(REFUND_DOCUMENT_GENERATION_CONFIGURATION_CHECK_SQL, 5));
    }

    @Test(description = "Precondition for TestRefundProcess tests")
    public static void pendingRefundPaymentMethodConfigCheck() {
        CustomAssert.assertEquals("The configuration is missing, run pendingRefundConfigurationUpdate and restart the env.", DBService.get().getValue(PENDING_REFUND_PAYMENT_METHOD_CONFIG_CHECK)
                .get(), "pendingRefund");
    }

    @Test(description = "Precondition for refund last payment method")
    public static void eRefundLastPaymentMethodConfigCheck() {
        CustomAssert.enableSoftMode();
        CustomAssert.assertTrue("eRefunds lookup value is not true, please run REFUND_CONFIG_INSERT", DBService.get().getValue(REFUND_CONFIG_CHECK).isPresent());
        CustomAssert.assertTrue("eRefund stub point is set incorrect, please run LAST_PAYMENT_METHOD_STUB_POINT_UPDATE", DBService.get()
                .getValue(String.format(LAST_PAYMENT_METHOD_STUB_END_POINT_CHECK, APP_HOST)).get()
                .contains(APP_HOST));
        CustomAssert.assertTrue("Authentication stub point is set incorrect, please run AUTHENTICATION_STUB_POINT_UPDATE", DBService.get()
                .getValue(String.format(AUTHENTICATION_STUB_END_POINT_CHECK, APP_HOST)).get()
                .contains(APP_HOST));

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-2186", "PAS-1936", "PAS-7057"})
    public void pas2186_ManualRefundUnissuedVoidedCheck(@org.testng.annotations.Optional("VA") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(10.01), "Approved");
        refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        //PAS-2186, PAS-1940, PAS-7858, PAS-352
        refundProcessHelper.unissuedManualRefundGeneration(Optional.empty(), billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0, false);
        refundProcessHelper.unissuedRefundVerification(billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "ManualRefund", BILLING_PAYMENT_METHOD_CHECK, false,false);

        //PAS-1939
        refundProcessHelper.voidedManualRefundGeneration(refund);
        refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 1, false);
        //PAS-7057
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "RefundPaymentVoided", null, true, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-2719", "PAS-1939", "PAS-7057"})
    public void pas2719_ManualRefundUnissuedVoidedCreditCard(@org.testng.annotations.Optional("VA") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(10), "Approved");
        refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        //PAS-2719, PAS-1940, PAS-6210, PAS-7858, PAS-352, PAS-3619
        refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_CREDIT_CARD), billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0, false);
        refundProcessHelper.unissuedRefundVerification(billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "ManualRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

        //PAS-1939
        refundProcessHelper.voidedManualRefundGeneration(refund);
        refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 1, false);
        //PAS-7057
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "RefundPaymentVoided", null, true, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-2719", "PAS-1939", "PAS-7057"})
    public void pas2719_ManualRefundUnissuedVoidedDebitCard(@org.testng.annotations.Optional("AZ") String state) {
        String refundDate1 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate1, "Refund", "Manual Refund", new Dollar(22), "Approved");
        refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        //PAS-2719, PAS-1940, PAS-6210, PAS-7858, PAS-352, PAS-3619
        refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_DEBIT_CARD), billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0, false);
        refundProcessHelper.unissuedRefundVerification(billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_DEBIT_CARD, "ManualRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

        //PAS-1939
        refundProcessHelper.voidedManualRefundGeneration(refund);
        refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 1, false);
        //PAS-7057
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_DEBIT_CARD, "RefundPaymentVoided", null, true, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-2719", "PAS-1939", "PAS-7057"})
    public void pas2719_ManualRefundUnissuedVoidedACH(@org.testng.annotations.Optional("MD") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(33), "Approved");
        refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        //PAS-2719, PAS-1940, PAS-6210, PAS-7858, PAS-352, PAS-3619
        refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_ACH), billingAccountNumber, MESSAGE_ACH, refund, false, 0, false);
        refundProcessHelper.unissuedRefundVerification(billingAccountNumber, MESSAGE_ACH, refund, false, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_ACH, "ManualRefund", BILLING_PAYMENT_METHOD_ACH, false, false);

        //PAS-1939
        refundProcessHelper.voidedManualRefundGeneration(refund);
        refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, MESSAGE_ACH, refund, false, 1, false);
        //PAS-7057
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_ACH, "RefundPaymentVoided", null, true, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7063", "PAS-1939", "PAS-7231"})
    public void pas7231_AutomatedRefundUnissuedVoidedCheck(@org.testng.annotations.Optional("VA") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(10.01), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        //PAS-7063, PAS-7858, PAS-453
        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, false);
        refundProcessHelper.unissuedRefundVerification(billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "AutomatedRefund", BILLING_PAYMENT_METHOD_CHECK, false, false);

        //PAS-1939
        refundProcessHelper.voidedManualRefundGeneration(refund);
        refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 1, false);
        //PAS-7231
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "RefundPaymentVoided", null, true, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7063", "PAS-1939", "PAS-7231"})
    public void pas7231_AutomatedRefundUnissuedVoidedCreditCard(@org.testng.annotations.Optional("VA") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(10), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        //PAS-7063, PAS-7858, PAS-453
        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, false);
        refundProcessHelper.unissuedRefundVerification(billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "AutomatedRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

        //PAS-1939
        refundProcessHelper.voidedManualRefundGeneration(refund);
        refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 1, false);
        //PAS-7231
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "RefundPaymentVoided", null, true, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7063", "PAS-1939", "PAS-7231"})
    public void pas7231_AutomatedRefundUnissuedVoidedDebitCard(@org.testng.annotations.Optional("AZ") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(21.99), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        //PAS-7063, PAS-7858, PAS-453
        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, false);
        refundProcessHelper.unissuedRefundVerification(billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "AutomatedRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

        //PAS-1939
        refundProcessHelper.voidedManualRefundGeneration(refund);
        refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 1, false);
        //PAS-7231
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "RefundPaymentVoided", null, true, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7063", "PAS-1936", "PAS-7231"})
    public void pas7063_AutomatedRefundUnissuedVoidedACH(@org.testng.annotations.Optional("MD") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(30.01), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        //PAS-7063, PAS-7858, PAS-453
        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, false);
        refundProcessHelper.unissuedRefundVerification(billingAccountNumber, MESSAGE_ACH, refund, false, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "AutomatedRefund", BILLING_PAYMENT_METHOD_ACH, false, false);

        //PAS-1939
        refundProcessHelper.voidedManualRefundGeneration(refund);
        refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, MESSAGE_ACH, refund, false, 1, false);
        //PAS-7231
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "RefundPaymentVoided", null, true, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-1939", "PAS-6152", "PAS-2732"})
    public void pas1939_ManualRefundUnissuedIssuedVoidedCheck(@org.testng.annotations.Optional("VA") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(10.01), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedManualRefundGeneration(Optional.empty(), billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0, false);

        //PAS-1939
        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
        refundProcessHelper.issuedRefundVerification(billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0);
        //PAS-6152
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "ManualRefund", BILLING_PAYMENT_METHOD_CHECK, false, false);

        //PAS-2732
        refundProcessHelper.voidedAutomatedRefundGeneration(true, PAYMENT_METHOD_CHECK, billingAccountNumber, policyNumber);
        refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0, false);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "RefundPaymentVoided", null, true, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-1939", "PAS-6152", "PAS-2732"})
    public void pas1939_ManualRefundUnissuedIssuedVoidedCreditCard(@org.testng.annotations.Optional("VA") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(10.00), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_CREDIT_CARD), billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0, false);

        //PAS-1939
        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
        refundProcessHelper.issuedRefundVerification(billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0);
        //PAS-6152
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "ManualRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

        //PAS-2732
        refundProcessHelper.voidedAutomatedRefundGeneration(true, PAYMENT_METHOD_CREDIT_CARD, billingAccountNumber, policyNumber);
        refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 2, false);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "RefundPaymentVoided", null, true, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-1939", "PAS-6152", "PAS-2732"})
    public void pas1936_ManualRefundUnissuedIssuedVoidedDebitCard(@org.testng.annotations.Optional("AZ") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(21.99), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_DEBIT_CARD), billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0, false);

        //PAS-1939
        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
        refundProcessHelper.issuedRefundVerification(billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0);
        //PAS-6152
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "ManualRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

        //PAS-2732
        refundProcessHelper.voidedAutomatedRefundGeneration(true, PAYMENT_METHOD_DEBIT_CARD, billingAccountNumber, policyNumber);
        refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 2, false);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "RefundPaymentVoided", null, true, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-1939", "PAS-6152", "PAS-2732"})
    public void pas1936_ManualRefundUnissuedIssuedVoidedACH(@org.testng.annotations.Optional("MD") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(30.01), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_ACH), billingAccountNumber, MESSAGE_ACH, refund, false, 0, false);

        //PAS-1939
        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
        refundProcessHelper.issuedRefundVerification(billingAccountNumber, MESSAGE_ACH, refund, false, 0);
        //PAS-6152
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "ManualRefund", BILLING_PAYMENT_METHOD_ACH, false, false);

        //PAS-2732
        refundProcessHelper.voidedAutomatedRefundGeneration(true, PAYMENT_METHOD_ACH, billingAccountNumber, policyNumber);
        refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_ACH, refund, false, 2, false);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "RefundPaymentVoided", null, true, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-6144", "PAS-7193", "PAS-6415"})
    public void pas6415_AutomatedRefundUnissuedIssuedVoidedCheck(@org.testng.annotations.Optional("VA") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(10.01), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, false);

        //PAS-453, PAS-6144
        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
        refundProcessHelper.issuedRefundVerification(billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0);
        //PAS-7193
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "AutomatedRefund", BILLING_PAYMENT_METHOD_CHECK, false, false);

        //PAS-6415
        refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_CHECK, billingAccountNumber, policyNumber);
        refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0, false);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "RefundPaymentVoided", null, true, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-6144", "PAS-7193", "PAS-6415"})
    public void pas6415_AutomatedRefundUnissuedIssuedVoidedCreditCard(@org.testng.annotations.Optional("VA") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(10.00), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, false);

        //PAS-453, PAS-6144
        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
        refundProcessHelper.issuedRefundVerification(billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0);
        //PAS-7193
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "AutomatedRefund", BILLING_PAYMENT_METHOD_CARD, false, true);

        //PAS-6415
        refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_CREDIT_CARD, billingAccountNumber, policyNumber);
        refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 2, false);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "RefundPaymentVoided", null, true, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-6144", "PAS-7193", "PAS-6415"})
    public void pas6415_AutomatedRefundUnissuedIssuedVoidedDebitCard(@org.testng.annotations.Optional("AZ") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(21.99), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, false);

        //PAS-453, PAS-6144
        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
        refundProcessHelper.issuedRefundVerification(billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0);
        //PAS-7193
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "AutomatedRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

        //PAS-6415
        refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_DEBIT_CARD, billingAccountNumber, policyNumber);
        refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 2, false);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "RefundPaymentVoided", null, true, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-6144", "PAS-7193", "PAS-6415"})
    public void pas6415_AutomatedRefundUnissuedIssuedVoidedACH(@org.testng.annotations.Optional("MD") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(30.01), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, false);

        //PAS-453, PAS-6144
        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
        refundProcessHelper.issuedRefundVerification(billingAccountNumber, MESSAGE_ACH, refund, false, 0);
        //PAS-7193
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "AutomatedRefund", BILLING_PAYMENT_METHOD_ACH, false, false);

        //PAS-6415
        refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_ACH, billingAccountNumber, policyNumber);
        refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_ACH, refund, false, 2, false);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "RefundPaymentVoided", null, true, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-2727")
    public void pas2727_ManualRefundUnissuedIssuedProcessedCheck(@org.testng.annotations.Optional("VA") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(10.01), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedManualRefundGeneration(Optional.empty(), billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0, false);

        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

        //PAS-2727
        refundProcessHelper.processedRefundGeneration(true, PAYMENT_METHOD_CHECK, billingAccountNumber, policyNumber);
        refundProcessHelper.processedRefundVerification(billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "ManualRefund", BILLING_PAYMENT_METHOD_CHECK, false, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-2728")
    public void pas2728_ManualRefundUnissuedIssuedProcessedCreditCard(@org.testng.annotations.Optional("VA") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(10.00), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_CREDIT_CARD), billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0, false);

        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

        //PAS-2728
        refundProcessHelper.processedRefundGeneration(true, PAYMENT_METHOD_CREDIT_CARD, billingAccountNumber, policyNumber);
        refundProcessHelper.processedRefundVerification(billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "ManualRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-2728")
    public void pas2728_ManualRefundUnissuedIssuedProcessedDebitCard(@org.testng.annotations.Optional("AZ") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(21.99), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_DEBIT_CARD), billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0, false);

        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

        //PAS-2728
        refundProcessHelper.processedRefundGeneration(true, PAYMENT_METHOD_DEBIT_CARD, billingAccountNumber, policyNumber);
        refundProcessHelper.processedRefundVerification(billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "ManualRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-2728")
    public void pas2728_ManualRefundUnissuedIssuedProcessedACH(@org.testng.annotations.Optional("MD") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(30.01), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_ACH), billingAccountNumber, MESSAGE_ACH, refund, false, 0, false);

        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

        //PAS-2728
        refundProcessHelper.processedRefundGeneration(true, PAYMENT_METHOD_ACH, billingAccountNumber, policyNumber);
        refundProcessHelper.processedRefundVerification(billingAccountNumber, MESSAGE_ACH, refund, false, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "ManualRefund", BILLING_PAYMENT_METHOD_ACH, false, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-4251")
    public void pas4251_AutomatedRefundUnissuedIssuedProcessedCheck(@org.testng.annotations.Optional("VA") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(10.01), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, false);

        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

        //PAS-4251
        refundProcessHelper.processedRefundGeneration(false, PAYMENT_METHOD_CHECK, billingAccountNumber, policyNumber);
        refundProcessHelper.processedRefundVerification(billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "AutomatedRefund", BILLING_PAYMENT_METHOD_CHECK, false, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-6144")
    public void pas6144_AutomatedRefundUnissuedIssuedProcessedCreditCard(@org.testng.annotations.Optional("VA") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(10.00), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, false);

        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

        //PAS-6144, PAS-453
        refundProcessHelper.processedRefundGeneration(false, PAYMENT_METHOD_CREDIT_CARD, billingAccountNumber, policyNumber);
        refundProcessHelper.processedRefundVerification(billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "AutomatedRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-6144")
    public void pas6144_AutomatedRefundUnissuedIssuedProcessedDebitCard(@org.testng.annotations.Optional("AZ") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(21.99), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, false);

        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

        //PAS-6144, PAS-453
        refundProcessHelper.processedRefundGeneration(false, PAYMENT_METHOD_DEBIT_CARD, billingAccountNumber, policyNumber);
        refundProcessHelper.processedRefundVerification(billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "AutomatedRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-6144")
    public void pas6144_AutomatedRefundUnissuedIssuedProcessedACH(@org.testng.annotations.Optional("MD") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(30.01), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, false);

        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

        //PAS-6144, PAS-453
        refundProcessHelper.processedRefundGeneration(false, PAYMENT_METHOD_ACH, billingAccountNumber, policyNumber);
        refundProcessHelper.processedRefundVerification(billingAccountNumber, MESSAGE_ACH, refund, false, 0);
        refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "AutomatedRefund", BILLING_PAYMENT_METHOD_ACH, false, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-455", "PAS-456"})
    public void pas455_ManualRefundVoidedWithAllocationCreditCard(@org.testng.annotations.Optional("VA") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(9.00), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_CREDIT_CARD), billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0, true);

        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

        //PAS-455, PAS-456
        refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_CREDIT_CARD, billingAccountNumber, policyNumber);
        refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 2, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-455", "PAS-456"})
    public void pas455_ManualRefundVoidedWithAllocationDebitCard(@org.testng.annotations.Optional("AZ") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(21.00), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_DEBIT_CARD), billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0, true);

        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

        //PAS-455, PAS-456
        refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_DEBIT_CARD, billingAccountNumber, policyNumber);
        refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 2, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-455", "PAS-456"})
    public void pas455_ManualRefundVoidedWithAllocationACH(@org.testng.annotations.Optional("AZ") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(33.00), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_ACH), billingAccountNumber, MESSAGE_ACH, refund, false, 0, true);

        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

        //PAS-455, PAS-456
        refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_ACH, billingAccountNumber, policyNumber);
        refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_ACH, refund, false, 2, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-456")
    public void pas456_AutomatedRefundVoidedWithAllocationCreditCard(@org.testng.annotations.Optional("VA") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(9.00), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, true);

        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

        //PAS-456
        refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_CREDIT_CARD, billingAccountNumber, policyNumber);
        refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 2, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-456")
    public void pas456_AutomatedRefundVoidedWithAllocationDebitCard(@org.testng.annotations.Optional("AZ") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(21.00), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, true);

        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

        //PAS-456
        refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_DEBIT_CARD, billingAccountNumber, policyNumber);
        refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 2, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-456")
    public void pas456_AutomatedRefundVoidedWithAllocationACH(@org.testng.annotations.Optional("MD") String state) {
        String refundDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(33.00), "Approved");
        String policyNumber = refundProcessHelper.policyCreation();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        CustomAssert.enableSoftMode();

        refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refund, true);

        refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

        //PAS-456
        refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_ACH, billingAccountNumber, policyNumber);
        refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_ACH, refund, false, 2, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    //*
    //* See test method for details

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
    public void pas7298_pendingManualRefundsCheck(@org.testng.annotations.Optional("VA") String state) {

        String paymentMethod = "Check";

        refundProcessHelper.policyCreation();

        CustomAssert.enableSoftMode();
        refundProcessHelper.pas7298_pendingManualRefunds(PENDING_REFUND_AMOUNT, APPROVED_REFUND_AMOUNT, paymentMethod);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    //*
    //* See test method for details

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
    public void pas7298_pendingManualRefundsCC(@org.testng.annotations.Optional("VA") String state) {

        String paymentMethod = "contains=Credit Card";

        refundProcessHelper.policyCreation();

        CustomAssert.enableSoftMode();
        refundProcessHelper.pas7298_pendingManualRefunds(PENDING_REFUND_AMOUNT, APPROVED_REFUND_AMOUNT, paymentMethod);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    //*
    //* See test method for details

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
    public void pas7298_pendingManualRefundsDC(@org.testng.annotations.Optional("AZ") String state) {

        String paymentMethod = "contains=Debit Card";

        refundProcessHelper.policyCreation();

        CustomAssert.enableSoftMode();
        refundProcessHelper.pas7298_pendingManualRefunds(PENDING_REFUND_AMOUNT, APPROVED_REFUND_AMOUNT, paymentMethod);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    //*
    //* See test method for details

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
    public void pas7298_pendingManualRefundsACH(@org.testng.annotations.Optional("MD") String state) {

        String paymentMethod = "contains=ACH";

        refundProcessHelper.policyCreation();

        CustomAssert.enableSoftMode();
        refundProcessHelper.pas7298_pendingManualRefunds(PENDING_REFUND_AMOUNT, APPROVED_REFUND_AMOUNT, paymentMethod);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    //*
    //* See test method for details

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
    public void pas7298_pendingAutomatedRefundsCheck(@org.testng.annotations.Optional("VA") String state) {

        String paymentMethod = "Check";

        String policyNumber = refundProcessHelper.policyCreation();

        CustomAssert.enableSoftMode();
        refundProcessHelper.pas7298_pendingAutomatedRefunds(policyNumber, APPROVED_REFUND_AMOUNT, PENDING_REFUND_AMOUNT, paymentMethod, 1);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    //*
    //* See test method for details

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
    public void pas7298_pendingAutomatedRefundsCC(@org.testng.annotations.Optional("VA") String state) {

        String paymentMethod = "Credit Card";

        String policyNumber = refundProcessHelper.policyCreation();

        CustomAssert.enableSoftMode();
        //TODO OSI: Refund with Check is created because the stubbed amount for VA
        refundProcessHelper.pas7298_pendingAutomatedRefunds(policyNumber, APPROVED_REFUND_AMOUNT, PENDING_REFUND_AMOUNT, paymentMethod, 1);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    //*
    //* See test method for details

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
    public void pas7298_pendingAutomatedRefundsDC(@org.testng.annotations.Optional("AZ") String state) {

        String paymentMethod = "Debit Card";

        String policyNumber = refundProcessHelper.policyCreation();

        CustomAssert.enableSoftMode();
        refundProcessHelper.pas7298_pendingAutomatedRefunds(policyNumber, APPROVED_REFUND_AMOUNT, PENDING_REFUND_AMOUNT, paymentMethod, 1);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    //*
    //* See test method for details

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
    public void pas7298_pendingAutomatedRefundsACH(@org.testng.annotations.Optional("MD") String state) {

        String paymentMethod = "ACH";

        String policyNumber = refundProcessHelper.policyCreation();

        CustomAssert.enableSoftMode();
        refundProcessHelper.pas7298_pendingAutomatedRefunds(policyNumber, APPROVED_REFUND_AMOUNT, PENDING_REFUND_AMOUNT, paymentMethod, 1);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     /* @author Megha Gubbala
     /* @name Refund Message when only method is check
     /* @scenario 1. Create new policy for DC
     /* 2. go to manual refund
     /* 3. see the message (Only check refund is currently available)
     /* 4.see the message for CT (No payment method available for electronic refund)
     /* @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eRefundLastPaymentMethodConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-1952")
    public void pas1952_MessageWhenOnlyMethodIsCheck(@org.testng.annotations.Optional("DC") String state) {

        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy();
        log.info("policyNumber: {}", policyNumber);

        CustomAssert.enableSoftMode();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        billingAccount.refund().start();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.MESSAGE_WHEN_ONLY_PAYMENT_METHOD_CHECK.getLabel(), StaticElement.class).verify
                .value("Only check refund is currently available.");
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eRefundLastPaymentMethodConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-1952")
    public void pas1952_MessageWhenOnlyMethodIsCheckNoElectronicRefund(@org.testng.annotations.Optional("CT") String state) {

        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy();
        log.info("policyNumber: {}", policyNumber);

        CustomAssert.enableSoftMode();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        billingAccount.refund().start();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.MESSAGE_WHEN_ONLY_PAYMENT_METHOD_CHECK.getLabel(), StaticElement.class).verify
                .value("No payment method available for electronic refund.");
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

}

