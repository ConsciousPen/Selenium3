package aaa.modules.regression.billing_and_payments.auto_ss.functional;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.DisbursementEngineHelper;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.DbAwaitHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.AdvancedAllocationsActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.billing_and_payments.auto_ss.functional.preconditions.TestRefundProcessPreConditions;
import aaa.modules.regression.billing_and_payments.template.PolicyBilling;
import aaa.toolkit.webdriver.customcontrols.AddPaymentMethodsMultiAssetList;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;

public class TestRefundProcess extends PolicyBilling implements TestRefundProcessPreConditions {

    private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
    private TestData tdBilling = testDataManager.billingAccount;
    private TestData tdRefund = tdBilling.getTestData("Refund", "TestData_Check");
    private BillingAccount billingAccount = new BillingAccount();
    private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
    private AdvancedAllocationsActionTab advancedAllocationsActionTab = new AdvancedAllocationsActionTab();

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
        CustomAssert.assertTrue("Erefunds lookup value is not true, please run REFUND_CONFIG_INSERT", DBService.get().getValue(REFUND_CONFIG_CHECK).isPresent());
        CustomAssert.assertTrue("Erefund stub point is set incorrect, please run LAST_PAYMENT_METHOD_STUB_POINT_UPDATE", DBService.get()
                .getValue(String.format(LAST_PAYMENT_METHOD_STUB_END_POINT_CHECK, APP_HOST)).get()
                .contains(APP_HOST));
        CustomAssert.assertTrue("Authentication stub point is set incorrect, please run AUTHENTICATION_STUB_POINT_UPDATE", DBService.get()
                .getValue(String.format(AUTHENTICATION_STUB_END_POINT_CHECK, APP_HOST)).get()
                .contains(APP_HOST));

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @author Oleg Stasyuk
     * @name Test Installment Fee split to Credit Card and Debit Card
     * @scenario 1. Create new policy
     * 2. Create manual Check refund
     * 3. Check Refund record in Payment and Other Transactions
     * 4. Check Actions are Void and Issue
     * 5. Check values of fields when Opening the Refund transaction's details
     * 6. run aaaRefundDisbursementAsyncJob
     * 7. check Refund's status is Issued
     * 4. Check Actions are Void, Stop, Clear
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-2186")
    public void pas2186_RefundProcessCheck(@Optional("VA") String state) {
        Dollar refundAmount1 = new Dollar(25);
        Dollar refundAmount2 = new Dollar(100);
        Dollar refundAmount3 = new Dollar(100);
        String checkDate1 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        String checkDate2 = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        String checkDate3 = TimeSetterUtil.getInstance().getCurrentTime().plusDays(2).format(DateTimeUtils.MM_DD_YYYY);
        String paymentMethod = "Check";
        precondJobAdding();
        mainApp().open();
        createCustomerIndividual();
        getPolicyType().get().createPolicy(getPolicyTD());
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        log.info("policyNumber: {}", policyNumber);

        CustomAssert.enableSoftMode();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

        //"Check" payment type refund creation
        //PAS-1462 start
        billingAccount.refund().start();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).setValue("Check");
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).verify.present(false);
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE.getLabel(), TextBox.class).verify.present(false);
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME.getLabel(), TextBox.class).verify.present();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME.getLabel(), TextBox.class).verify.enabled(false);
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.present();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.enabled();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.value("");
        AddPaymentMethodsMultiAssetList.buttonAddUpdateCreditCard.verify.present(false);
        acceptPaymentActionTab.cancel();
        //PAS-1462 end

        billingAccount.refund().perform(tdRefund, new Dollar(refundAmount1));

        Map<String, String> refund1 = new HashMap<>();
        refund1.put(TRANSACTION_DATE, checkDate1);
        refund1.put(TYPE, "Refund");
        refund1.put(SUBTYPE_REASON, "Manual Refund");
        pas453_unissuedRefundActionsCheck(refund1, true);
        unissuedRefundRecordDetailsCheck(refundAmount1, checkDate1, refund1, true);

        //PAS-6615 start
        getRefundTransactionID();
        //PAS-6615 end

        //PAS-1939 Start
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).controls.links.get("Void").click();
        Page.dialogConfirmation.confirm();
        pas1939_voidedRefundTransactionCheck(refundAmount1, checkDate1, "Manual Refund");
        //PAS-1939 End

        billingAccount.refund().perform(tdRefund, new Dollar(refundAmount1));

        //TODO workaround for Time-setter parallel execution
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));
        JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
        checkRefundDocumentInDb(state, policyNumber, 1);
        pas1939_issuedRefundActionsCheck(refund1, policyNumber, true);
        pas453_issuedUnprocessedRefundRecordDetailsCheck(refundAmount1, checkDate1, refund1, true, true);

        Dollar totalDue = BillingSummaryPage.getTotalDue();
        billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue.add(refundAmount2));
        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(1));
        JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        Map<String, String> refund2 = new HashMap<>();
        refund2.put(TRANSACTION_DATE, checkDate2);
        refund2.put(TYPE, "Refund");
        refund2.put(SUBTYPE_REASON, "Automated Refund");
        pas453_unissuedRefundActionsCheck(refund2, false);
        //BUG PAS-4251, PAS-6144 - waiting for implementation, the fields display requirements will change
        unissuedRefundRecordDetailsCheck(refundAmount2, checkDate2, refund2, false);

        //PAS-1939 Start
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund2).getCell(ACTION).controls.links.get("Void").click();
        Page.dialogConfirmation.confirm();
        pas1939_voidedRefundTransactionCheck(refundAmount2, checkDate2, "Automated Refund");
        //PAS-1939 End

        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(1));
        JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
        JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
        checkRefundDocumentInDb(state, policyNumber, 2);
        Map<String, String> refund3 = new HashMap<>();
        refund3.put(TRANSACTION_DATE, checkDate3);
        refund3.put(TYPE, "Refund");
        refund3.put(SUBTYPE_REASON, "Automated Refund");
        pas1939_issuedRefundActionsCheck(refund3, policyNumber, false);
        pas453_issuedUnprocessedRefundRecordDetailsCheck(refundAmount3, checkDate3, refund3, false, false);

        getResponseFromPC(paymentMethod, policyNumber, "R", "SUCC", "DSB_E_DSBCTRL_PASSYS_7035_D");
        pas1939_issuedRefundActionsCheck(refund3, policyNumber, true);
        pas453_issuedProcessedRefundRecordDetailsCheck(refundAmount3, checkDate3, refund3, false, true, paymentMethod);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @author Megha Gubbala
     * @name Manual Check Refund Data - FROM DE
     * @scenario 1. Create new policy
     * 2. Create manual Check refund
     * 3. MANUAL CHECK refunds sent to PC run aaaRefundDisbursementAsyncJob
     * 4. Mock data check number transactionid in file
     * 5. transactions are updated with the Check Number from PC
     * 6. Transaction History shows status = Issued
     * 7. Available Actions = Void, Clear
     * 8. click the REFUNDS link
     * 9. see the refund detail page
     * 10. check number generated by Payment Central is displayed
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-2727")
    public void pas2727_ManualChkRefundFromDe(@Optional("VA") String state) {

        String message = "Check";
        String amount = "56";
        String transactionDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        String paymentMethod = "Check";
        precondJobAdding();
        mainApp().open();
        createCustomerIndividual();
        getPolicyType().get().createPolicy(getPolicyTD());
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        log.info("policyNumber: {}", policyNumber);
        CustomAssert.enableSoftMode();
        manualRefundPerform(message, amount);
        JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
        Map<String, String> refund = ImmutableMap.of(
                TRANSACTION_DATE, transactionDate,
                STATUS, "Issued",
                TYPE, "Refund",
                SUBTYPE_REASON, "Manual Refund"
        );
        pas1939_issuedRefundActionsCheck(refund, policyNumber, true);
        pas453_issuedUnprocessedRefundRecordDetailsCheck(new Dollar(amount), transactionDate, refund, true, true);
        getResponseFromPC(paymentMethod, policyNumber, "M", "SUCC", "DSB_E_DSBCTRL_PASSYS_7035_D");
        pas1939_issuedRefundActionsCheck(refund, policyNumber, true);
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(TYPE).controls.links.get(1).click();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).verify.value("123456789");
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    private void pas1939_voidedRefundTransactionCheck(Dollar refundAmount, String checkDate, String subtypeReason) {
        Map<String, String> refundVoided1 = new HashMap<>();
        refundVoided1.put(TRANSACTION_DATE, checkDate);
        refundVoided1.put(TYPE, "Refund");
        refundVoided1.put(SUBTYPE_REASON, subtypeReason);
        CustomAssert.assertEquals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoided1).getIndex(), 2);
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoided1).getCell(ACTION).verify.value("");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoided1).getCell(STATUS).verify.value("Voided");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoided1).getCell(AMOUNT).verify.value(refundAmount.toString());

        Map<String, String> refundVoidedAdjustment1 = new HashMap<>();
        refundVoidedAdjustment1.put(TRANSACTION_DATE, checkDate);
        refundVoidedAdjustment1.put(TYPE, "Adjustment");
        refundVoidedAdjustment1.put(SUBTYPE_REASON, "Refund Payment Voided");
        CustomAssert.assertEquals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoidedAdjustment1).getIndex(), 1);
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoidedAdjustment1).getCell(ACTION).verify.value("");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoidedAdjustment1).getCell(STATUS).verify.value("Applied");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoidedAdjustment1).getCell(AMOUNT).verify.value(refundAmount.negate().toString());
        //PAS-1939 End
    }

    /**
     * @author Megha Gubbala
     * @name Refund Methods and the Drop down - Last Payment Method
     * @scenario 1. Create new policy
     * 2. go to manual refund
     * 3. Verify the last payment method in drop down is same as stub
     * 4. verify last 4  digits of the account and expiration date (for CC-Visa )
     * 5. Select the payment method Visa Credit Card from drop down
     * 6.When we select payment method verify message how much is available for that refund method
     * 7.If Payment method is credit card we can see only amount field and it blank
     * 8.issue refund with Credit card visa verify Transaction History shows Status = Approved and available actions are Void and Issue
     * 9.Click on refund and verify card name and amount is same on refund detail page
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "pendingRefundPaymentMethodConfigCheck")//TODO when running suite, the test which has Depends on is not being executed
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-352")
    public void pas352_RefundMethodAndDropdownLastPaymentMethodCreditCard(@Optional("VA") String state) {

        String message = "Credit Card Visa-4113 expiring 01/22";
	    String billingPaymentMethod="AAAPaymentDetailsPCICreditCard";
        String amount = "10";
        String paymentMethod = "Credit card";
        CustomAssert.enableSoftMode();
	    pas352_RefundMethodAndDropdownLastPaymentMethodTest(message, amount, paymentMethod, billingPaymentMethod);
	    CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @author Megha Gubbala
     * @name Refund Methods and the Drop down - Last Payment Method
     * @scenario 1. Create new policy
     * 2. go to manual refund
     * 3. Verify the last payment method in drop down is same as stub
     * 4. verify last 4  digits of the account and expiration date (for Debit card -Master )
     * 5. Select the payment method Visa Debit Card from drop down
     * 6.When we select payment method verify message how much is available for that refund method
     * 7.If Payment method is credit card we can see only amount field and it blank
     * 8.issue refund with Credit card visa verify Transaction History shows Status = Approved and available actions are Void and Issue
     * 9.Click on refund and verify card name and amount is same on refund detail page
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eRefundLastPaymentMethodConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-352")
    public void pas352_RefundMethodAndDropdownLastPaymentMethodDebitCardMc(@Optional("AZ") String state) {

        String message = "Debit Card MasterCard-4444 expiring 05/20";
	    String billingPaymentMethod="AAAPaymentDetailsPCICreditCard";
        String amount = "22";
        String paymentMethod = "Debit card";
        CustomAssert.enableSoftMode();
	    pas352_RefundMethodAndDropdownLastPaymentMethodTest(message, amount, paymentMethod, billingPaymentMethod);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @author Megha Gubbala
     * @name Refund Methods and the Drop down - Last Payment Method
     * @scenario 1. Create new policy
     * 2. go to manual refund
     * 3. Verify the last payment method in drop down is same as stub
     * 4. verify last 4  digits of the account (ACH Checking/Saving )
     * 5. Select the payment method ACH Checking/Saving from drop down
     * 6.When we select payment method verify message how much is available for that refund method
     * 7.If Payment method is ACH Checking/Saving we can see only amount field and it blank
     * 8.issue refund with Credit card visa verify Transaction History shows Status = Approved and available actions are Void and Issue
     * 9.Click on refund and verify card name and amount is same on refund detail page
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eRefundLastPaymentMethodConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-352")
    public void pas352_RefundMethodAndDropdownLastPaymentMethodEFT(@Optional("MD") String state) {

        String message = "Checking/Savings (ACH) #,1542";
	    String billingPaymentMethod ="AAAPaymentDetailsEFT";
        String amount = "33";
        String paymentMethod = "ACH";
        CustomAssert.enableSoftMode();
	    pas352_RefundMethodAndDropdownLastPaymentMethodTest(message, amount, paymentMethod, billingPaymentMethod);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    private void pas352_RefundMethodAndDropdownLastPaymentMethodTest(String message, String amount, String paymentMethod, String billingPaymentMethod ) {
        precondJobAdding();
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy();
        log.info("policyNumber: {}", policyNumber);
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        billingAccount.refund().start();
        //PAS-352 Start
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).verify.option(message);
        //PAS-3619 Start
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).setValue(message);
        CustomAssert.assertTrue(message.equals(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).getValue()));
        //PAS-352 End
        //PAS-3619 End
        //PAS-1937 Start
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD_MESSAGE_TABLE.getLabel(), StaticElement.class).getValue();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD_MESSAGE_TABLE.getLabel(), StaticElement.class).verify
                .value("$" + amount + " is the maximum amount available for this payment method.");
        //PAS-1937 End
        //PAS-1940 Start
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.value("");
        //PAS-1940 End
        //PAS-2719 Start
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).setValue(amount);
        acceptPaymentActionTab.submitTab();

        String transactionDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund1 = new HashMap<>();
        refund1.put(TRANSACTION_DATE, transactionDate);
        refund1.put(STATUS, "Approved");
        refund1.put(TYPE, "Refund");
        refund1.put(SUBTYPE_REASON, "Manual Refund");
        pas453_unissuedRefundActionsCheck(refund1, true);
        //PAS-2719 End
        //PAS-1939 Start
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).controls.links.get("Void").click();
        Page.dialogConfirmation.confirm();
        pas1939_voidedRefundTransactionCheck(new Dollar(amount), transactionDate, "Manual Refund");
        //PAS-1939 End

        //PAS-2732 Start
        manualRefundPerform(message, amount);
        getResponseFromPC(paymentMethod, policyNumber, "M", "ERR", "DSB_E_DSBCTRL_PASSYS_7036_D");
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        automationRefundAfterManualFailedCheck(new Dollar(amount), transactionDate);
        //PAS-2732 End

        //PAS-455 start
        String paymentAndRefundAmount = "9";
        String allocationPaymentAmount = "3";
        String paymantMethod = "Cash";
        allocationPaymentPerform(paymentAndRefundAmount, allocationPaymentAmount, paymantMethod);
        allocationManualRefundPerform(paymentAndRefundAmount, message);
        JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
        getResponseFromPC(paymentMethod, policyNumber, "M", "ERR", "DSB_E_DSBCTRL_PASSYS_7036_D");
        mainApp().reopen();
        SearchPage.openBilling(policyNumber);
        automationRefundAfterManualFailedCheck(new Dollar(paymentAndRefundAmount), transactionDate);
        checkRefundAllocationAmount(paymentAndRefundAmount, "3");
        //Pas-455 end

        billingAccount.refund().start();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).setValue(message);
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).setValue(amount);
        //BUG PAS-6539 View eRefunds and the Debit/Credit Card - Auto
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).verify.value("Visa ****4113 expiring 01/22");
        acceptPaymentActionTab.submitTab();
        //PAS-3619 End
        //PAS-2728 Start
        //TODO workaround for Time-setter parallel execution
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));
        JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);

        Map<String, String> refund2 = new HashMap<>();
        refund2.put(TRANSACTION_DATE, transactionDate);
        refund2.put(STATUS, "Issued");
        refund2.put(TYPE, "Refund");
        refund2.put(SUBTYPE_REASON, "Manual Refund");
        pas1939_issuedRefundActionsCheck(refund2, policyNumber, false);
        pas453_issuedUnprocessedRefundRecordDetailsCheck(new Dollar(amount), transactionDate, refund2, true, false);
        //PAS-2728 End

	    //PAS-6152 Start

	    manualRefundPerform(message, amount);
	    String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
	    String transactionDate7 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
	    Map<String, String> refund9= ImmutableMap.of(
			    TRANSACTION_DATE, transactionDate7,
			    STATUS, "Approved",
			    TYPE, "Refund",
			    SUBTYPE_REASON, "Manual Refund"
	    );
	    BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund9).getCell(TYPE).controls.links.get(1).click();
	    String transactionID1 = acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).getValue();
	    CustomAssert.enableSoftMode();
	    Map<String,String> transactionsFromDB = getRefundTransactionsFromDB( transactionID1, billingAccountNumber,  "CREDIT" );
	    CustomAssert.assertEquals(amount, transactionsFromDB.get("ENTRYAMT"));

	    CustomAssert.assertEquals("ManualRefund", transactionsFromDB.get("TRANSACTIONTYPE"));
	    CustomAssert.assertEquals("1060", transactionsFromDB.get("LEDGERACCOUNTNO"));
	    CustomAssert.assertEquals(billingPaymentMethod, transactionsFromDB.get("BILLINGPAYMENTMETHOD"));

	    Map<String,String> transactionsFromDBDebit = getRefundTransactionsFromDB( transactionID1, billingAccountNumber,  "DEBIT" );

	    CustomAssert.assertEquals(amount, transactionsFromDBDebit.get("ENTRYAMT"));
	    CustomAssert.assertEquals("ManualRefund", transactionsFromDBDebit.get("TRANSACTIONTYPE"));
	    CustomAssert.assertEquals("1044", transactionsFromDBDebit.get("LEDGERACCOUNTNO"));
	    CustomAssert.assertEquals(billingPaymentMethod, transactionsFromDBDebit.get("BILLINGPAYMENTMETHOD"));
	    acceptPaymentActionTab.back();
	    CustomAssert.disableSoftMode();

	    //PAS-6152 End

        //PAS-453 Start
        Dollar refundAmount3 = new Dollar(100);
        Dollar totalDue = BillingSummaryPage.getTotalDue();
        billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue.add(refundAmount3));

        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(1));
        JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        String checkDate3 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund3 = new HashMap<>();
        refund3.put(TRANSACTION_DATE, checkDate3);
        refund3.put(TYPE, "Refund");
        refund3.put(SUBTYPE_REASON, "Automated Refund");
        pas453_unissuedRefundActionsCheck(refund3, false);
        //BUG PAS-4251, PAS-6144 - waiting for implementation, the fields display requirements will change
        unissuedRefundRecordDetailsCheck(refundAmount3, checkDate3, refund3, false);

        //PAS-1939 Start
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund3).getCell(ACTION).controls.links.get("Void").click();
        Page.dialogConfirmation.confirm();
        pas1939_voidedRefundTransactionCheck(refundAmount3, checkDate3, "Automated Refund");
        //PAS-1939 End

        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(1));
        Dollar refundAmount4 = new Dollar(100);
        String checkDate4 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        Map<String, String> refund4 = new HashMap<>();
        refund4.put(TRANSACTION_DATE, checkDate4);
        refund4.put(TYPE, "Refund");
        refund4.put(SUBTYPE_REASON, "Automated Refund");
        JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
        JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
        pas1939_issuedRefundActionsCheck(refund4, policyNumber, false);
        pas453_issuedUnprocessedRefundRecordDetailsCheck(refundAmount4, checkDate4, refund4, false, false);
        getResponseFromPC(paymentMethod, policyNumber, "R", "SUCC", "DSB_E_DSBCTRL_PASSYS_7035_D");
        pas1939_issuedRefundActionsCheck(refund4, policyNumber, false);
        pas453_issuedProcessedRefundRecordDetailsCheck(refundAmount4, checkDate4, refund4, false, false, message);
        //PAS-453 End
        //6415 Start
        Dollar refundAmount5 = new Dollar(100);
        Dollar totalDue1 = BillingSummaryPage.getTotalDue();
        billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue1.add(refundAmount5));

        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(1));
        JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
        JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        String checkDate5 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);

        Map<String, String> refund5 = ImmutableMap.of(
                TRANSACTION_DATE, checkDate5,
                TYPE, "Refund",
                SUBTYPE_REASON, "Automated Refund"

        );
        pas1939_issuedRefundActionsCheck(refund5, policyNumber, false);
        pas453_issuedUnprocessedRefundRecordDetailsCheck(refundAmount5, checkDate5, refund5, false, false);

        getResponseFromPC(paymentMethod, policyNumber, "R", "SUCC", "DSB_E_DSBCTRL_PASSYS_7035_D");
        pas1939_issuedRefundActionsCheck(refund5, policyNumber, false);
        pas453_issuedProcessedRefundRecordDetailsCheck(refundAmount5, checkDate5, refund5, false, false, message);

        getResponseFromPC(paymentMethod, policyNumber, "R", "ERR", "DSB_E_DSBCTRL_PASSYS_7036_D");

        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        pas1939_voidedRefundTransactionCheck(new Dollar(amount), checkDate5, "Automated Refund");
        //6415 END
	    //Start PAS-7193
	    Dollar refundAmount7 = new Dollar(10);
	    Dollar totalDue3 = BillingSummaryPage.getTotalDue();
	    billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue3.add(refundAmount7));
	    String billingAccountNumber2 = BillingSummaryPage.labelBillingAccountNumber.getValue();
	    TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(1));
	    JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);

	    mainApp().reopen();
	    SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

	    String checkDate7 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);

	    Map<String, String> refund7 = ImmutableMap.of(
			    TRANSACTION_DATE, checkDate7,
			    TYPE, "Refund",
			    SUBTYPE_REASON, "Automated Refund"

	    );
	    BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund7).getCell(TYPE).controls.links.get(1).click();

	    String transactionID3 = acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).getValue();
	    CustomAssert.enableSoftMode();
	    Map<String,String> transactionsFromDB2 = getRefundTransactionsFromDB( transactionID3, billingAccountNumber2,  "CREDIT" );

	    CustomAssert.assertEquals("10", transactionsFromDB2.get("ENTRYAMT"));
	    CustomAssert.assertEquals("AutomatedRefund", transactionsFromDB2.get("TRANSACTIONTYPE"));
	    CustomAssert.assertEquals("1060", transactionsFromDB2.get("LEDGERACCOUNTNO"));
	    CustomAssert.assertEquals(billingPaymentMethod, transactionsFromDB2.get("BILLINGPAYMENTMETHOD"));

	    Map<String,String> transactionsFromDBDebit2 = getRefundTransactionsFromDB( transactionID3, billingAccountNumber2,  "DEBIT" );
	    CustomAssert.assertEquals("10", transactionsFromDBDebit2.get("ENTRYAMT"));
	    CustomAssert.assertEquals("AutomatedRefund", transactionsFromDBDebit2.get("TRANSACTIONTYPE"));
	    CustomAssert.assertEquals("1044", transactionsFromDBDebit2.get("LEDGERACCOUNTNO"));
	    CustomAssert.assertEquals(billingPaymentMethod, transactionsFromDBDebit2.get("BILLINGPAYMENTMETHOD"));
	    acceptPaymentActionTab.back();
	    CustomAssert.disableSoftMode();

	    Dollar refundAmount6 = new Dollar(100);
	    Dollar totalDue2 = BillingSummaryPage.getTotalDue();
	    billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue2.add(refundAmount6));
	    String billingAccountNumber1 = BillingSummaryPage.labelBillingAccountNumber.getValue();
	    String billingPaymentMethodCheck="PaymentDetailsCheque";
	    TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(1));

	    JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
	    JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
	    mainApp().reopen();
	    SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	    String checkDate6 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);

	    Map<String, String> refund6 = ImmutableMap.of(
			    TRANSACTION_DATE, checkDate6,
			    TYPE, "Refund",
			    SUBTYPE_REASON, "Automated Refund"

	    );
	    pas1939_issuedRefundActionsCheck(refund6, policyNumber, true);
	    pas453_issuedUnprocessedRefundRecordDetailsCheck(refundAmount6, checkDate6, refund6, false, true);

	    getResponseFromPC(paymentMethod, policyNumber, "R", "SUCC", "DSB_E_DSBCTRL_PASSYS_7035_D");
	    mainApp().reopen();
	    SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

	    BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund6).getCell(TYPE).controls.links.get(1).click();

	    String transactionID2 = acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).getValue();
	    CustomAssert.enableSoftMode();

	    Map<String,String> transactionsFromDB1 = getRefundTransactionsFromDB( transactionID2, billingAccountNumber1,  "CREDIT" );
	    CustomAssert.assertEquals("100", transactionsFromDB1.get("ENTRYAMT"));
	    CustomAssert.assertEquals("AutomatedRefund", transactionsFromDB1.get("TRANSACTIONTYPE"));
	    CustomAssert.assertEquals("1060", transactionsFromDB1.get("LEDGERACCOUNTNO"));
	    CustomAssert.assertEquals(billingPaymentMethodCheck, transactionsFromDB1.get("BILLINGPAYMENTMETHOD"));

	    Map<String,String> transactionsFromDBDebit1 = getRefundTransactionsFromDB( transactionID2, billingAccountNumber1,  "DEBIT" );
	    CustomAssert.assertEquals("100", transactionsFromDBDebit1.get("ENTRYAMT"));
	    CustomAssert.assertEquals("AutomatedRefund", transactionsFromDBDebit1.get("TRANSACTIONTYPE"));
	    CustomAssert.assertEquals("1044", transactionsFromDBDebit1.get("LEDGERACCOUNTNO"));
	    CustomAssert.assertEquals(billingPaymentMethodCheck, transactionsFromDBDebit1.get("BILLINGPAYMENTMETHOD"));
	    acceptPaymentActionTab.back();
	    CustomAssert.disableSoftMode();
	    //End- 7193
    }

    /**
     * @author Megha Gubbala
     * @name Refund Message when only method is check
     * @scenario 1. Create new policy for DC
     * 2. go to manual refund
     * 3. see the message (Only check refund is currently available)
     * 4.see the message for CT (No payment method available for electronic refund)
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eRefundLastPaymentMethodConfigCheck")
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-1952")
    public void pas1952_MessageWhenOnlyMethodIsCheck(@Optional("DC") String state) {

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
    public void pas1952_MessageWhenOnlyMethodIsCheckNoElectronicRefund(@Optional("CT") String state) {

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

    private void unissuedRefundRecordDetailsCheck(Dollar amount, String checkDate, Map<String, String> refund, boolean isManual) {
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(TYPE).controls.links.get(1).click();
        if (!isManual) {
            acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).verify.value("Pending");
        } else {
            acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).verify.value("Processing");
            acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE.getLabel(), TextBox.class).verify.value(checkDate);
        }
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME.getLabel(), TextBox.class).verify.present();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.value(amount.toString());
        acceptPaymentActionTab.back();
    }

    private void pas453_unissuedRefundActionsCheck(Map<String, String> refund, boolean isManual) {
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(STATUS).verify.value("Approved");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(ACTION).controls.links.get(1).verify.value("Void");
        if (isManual) {
            //TODO rumors have it there is a new story, which supercedes existing story and removes Issue link for Manual and Automated refunds.
            BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(ACTION).controls.links.get(2).verify.value("Issue");
        } else {
            CustomAssert.assertFalse(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(ACTION).getValue().contains("Issue"));
        }
    }

    private void pas453_issuedUnprocessedRefundRecordDetailsCheck(Dollar amount, String checkDate, Map<String, String> refund, boolean isManual, boolean isCheck) {
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(TYPE).controls.links.get(1).click();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).verify.present();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.value(amount.toString());
        if (isManual) {
            if (isCheck) {
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).verify.value("Processing");
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE.getLabel(), TextBox.class).verify.value(checkDate);
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).verify.value("Check");
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME.getLabel(), TextBox.class).verify.present();
            } else {
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).verify.present(false);
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE.getLabel(), TextBox.class).verify.present(false);
            }
        } else {
            acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).verify.value("Pending");
            acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME.getLabel(), TextBox.class).verify.present();
        }
        acceptPaymentActionTab.back();
    }

    private void pas453_issuedProcessedRefundRecordDetailsCheck(Dollar amount, String checkDate, Map<String, String> refund, boolean isManual, boolean isCheck, String paymentMethod) {
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(TYPE).controls.links.get(1).click();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).verify.present();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.value(amount.toString());
        if (isManual) {
            if (isCheck) {
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).verify.value("Processing");
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE.getLabel(), TextBox.class).verify.value(checkDate);
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).verify.value("Check");
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME.getLabel(), TextBox.class).verify.present();
            } else {
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).verify.present(false);
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE.getLabel(), TextBox.class).verify.present(false);
            }
        } else {
            if (isCheck) {
                CustomAssert.assertFalse("Processing"
                        .equals(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).getValue()));
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE.getLabel(), TextBox.class).verify.value(checkDate);
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).verify.value("Check");
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME.getLabel(), TextBox.class).verify.present();
            } else {
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).verify.present(false);
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE.getLabel(), TextBox.class).verify.present(false);
                acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME.getLabel(), TextBox.class).verify.present(false);
            }
            //TODO Should be returned when expiration date for credit/debit card will be implemented in new story (PAS-6210)
            acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).verify.value("paymentMethod");

        }
        acceptPaymentActionTab.back();
    }

    private void pas1939_issuedRefundActionsCheck(Map<String, String> refund1, String policyNumber, boolean isCheck) {
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(STATUS).verify.value("Issued");
        if (isCheck) {
            BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).controls.links.get(1).verify.value("Void");
            BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).controls.links.get(2).verify.value("Clear");
        } else {
            BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).verify.value("");
        }
        //PAS-2727 start
        CustomAssert.assertFalse(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).getValue().contains("Stop"));
        //PAS-2727 end
    }

    private static void checkRefundDocumentInDb(String state, String policyNumber, int numberOfDocuments) {
        //PAS-443 start
        if ("VA".equals(state)) {
            if (DbAwaitHelper.waitForQueryResult(REFUND_DOCUMENT_GENERATION_CONFIGURATION_CHECK_SQL, 5)) {
                String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "55 3500", "REFUND");
                CustomAssert.assertFalse(DbAwaitHelper.waitForQueryResult(query, 5));
            }
        } else {
            String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "55 3500", "REFUND");
            CustomAssert.assertTrue(DbAwaitHelper.waitForQueryResult(query, 5));
            String query2 = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNumber, "55 3500", "REFUND");
            CustomAssert.assertEquals(Integer.parseInt(DBService.get().getValue(query2).get()), numberOfDocuments);
        }
        //PAS-443 end
    }

    /**
     *
     * @param paymentMethod - can be "ACH", "Credit Card", "Debit Card".
     * @param policyNumber - current policy number
     * @param refundMethod - can be "M" - manual or "R" - automation
     * @param refundStatus - can be "SUCC" - success response from PC and "ERR" - failed response from PC
     * @param folderName - name of the folder where the file will be generate e.g. "DSB_E_DSBCTRL_PASSYS_7035_D", "DSB_E_DSBCTRL_PASSYS_7036_D"
     */
    private void getResponseFromPC(String paymentMethod, String policyNumber, String refundMethod, String refundStatus, String folderName) {

        String transactionID = getRefundTransactionID();

        if (transactionID == null) {
            CustomAssert.assertTrue("Transaction number isn't found on UI", transactionID != null);
            return;
        }

        DisbursementEngineHelper.DisbursementEngineFileBuilder builder = new DisbursementEngineHelper.DisbursementEngineFileBuilder()
                .setRefundMethod(refundMethod)
                .setPolicyNumber(policyNumber)
                .setProductType("PA")
                .setRefundStatus(refundStatus);

        switch (paymentMethod) {
            case "ACH":
                builder = builder.setTransactionNumber(transactionID)
                        .setPaymentType("EFT")
                        .setRefundAmount("100.00")
                        .setAccountLast4("1542")
                        .setAccountType("CHKG");
                break;
            case "Credit card":
                builder = builder.setTransactionNumber(transactionID)
                        .setPaymentType("CRDC")
                        .setRefundAmount("100.00")
                        .setAccountLast4("4113")
                        .setAccountType("VISA")
                        .setCardSubType("Credit");
                break;
            case "Debit card":
                builder = builder.setTransactionNumber(transactionID)
                        .setPaymentType("CRDC")
                        .setRefundAmount("100.00")
                        .setAccountLast4("4444")
                        .setAccountType("MASTR")
                        .setCardSubType("Debit");
                break;
            case "Check":
                builder = builder.setTransactionNumber(transactionID)
                        .setPaymentType("CHCK")
                        .setRefundAmount("100.00")
                        .setCheckNumber("123456789");
                break;
            default:
                log.info("never reached");

        }
        File disbursementEngineFile = DisbursementEngineHelper.createFile(builder, folderName);
        DisbursementEngineHelper.copyFileToServer(disbursementEngineFile, folderName);
        //TODO workaround for Time-setter parallel execution
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));
        if ("ERR".equals(refundStatus)) {
            JobUtils.executeJob(Jobs.aaaRefundsDisbursementRejectionsAsyncJob);
        } else if ("SUCC".equals(refundStatus)) {
            JobUtils.executeJob(Jobs.aaaRefundDisbursementRecieveInfoJob);
        }
    }

    private void manualRefundPerform(String message, String amount) {
        if (!BillingSummaryPage.tableBillingGeneralInformation.isPresent()) {
            NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        }
        billingAccount.refund().start();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).setValue(message);
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).setValue(amount);
        acceptPaymentActionTab.submitTab();
    }

    //	PAS-6615 check that TransactionID in DB is the same as on UI
    private String getRefundTransactionID() {
        BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains("Type", "Refund").getCell(TYPE).controls.links.get("Refund").click();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).isPresent();
        String transactionID = acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).getValue();
        acceptPaymentActionTab.back();
        CustomAssert.assertEquals("TranzactionID in DB is differen from TranzactionID on UI", getRefundTransactionIDFromDB(), transactionID);
        return transactionID;
    }

	private Map<String, String> getRefundTransactionsFromDB(String transactionID,String billingAccountNumber, String entryType  ) {


		return DBService.get().getRows("select le.ledgeraccountno, le.TRANSACTIONTYPE,le.BILLINGPAYMENTMETHOD,le.TRANSACTIONID,le.ENTRYAMT , le.entrytype\n"
				+ " from ledgerentry le join ledgertransaction lt on lt.id = le.LEDGERTRANSACTION_ID  \n"
				+ " where 1=1\n"
				+ " and BILLINGACCOUNTNUMBer = '"+billingAccountNumber+"'  and TRANSACTIONID ='"+ transactionID +"'  and entrytype = '"+ entryType +"'").get(0);
	}

    private String getRefundTransactionIDFromDB() {
        String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

        String transactionIDFromDB = DBService.get().getRows("select TRANSACTIONNUMBER from BILLINGTRANSACTION "
                + "where account_id = (select id from BILLINGACCOUNT where ACCOUNTNUMBER = '" + billingAccountNumber + "') "
                + "order by CREATIONDATE desc").get(0).get("TRANSACTIONNUMBER");
        return transactionIDFromDB;
    }

    private void allocationPaymentPerform(String paymentAmount, String allocationPaymentAmount, String paymantMethod) {

        billingAccount.acceptPayment().start();

        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).setValue(paymantMethod);
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).setValue(paymentAmount);

        BillingSummaryPage.linkAdvancedAllocation.click();

        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.NET_PREMIUM.getLabel(), TextBox.class).setValue(allocationPaymentAmount);
        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.OTHER.getLabel(), TextBox.class).setValue(allocationPaymentAmount);
        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.POLICY_FEE.getLabel(), TextBox.class).setValue(allocationPaymentAmount);

        advancedAllocationsActionTab.submitTab();
    }

    private void automationRefundAfterManualFailedCheck(Dollar refundAmount, String checkDate) {
        Map<String, String> manualRefundVoided = new HashMap<>();
        manualRefundVoided.put(TRANSACTION_DATE, checkDate);
        manualRefundVoided.put(TYPE, "Refund");
        manualRefundVoided.put(SUBTYPE_REASON, "Manual Refund");
        CustomAssert.assertEquals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(manualRefundVoided).getIndex(), 3);
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(manualRefundVoided).getCell(ACTION).verify.value("");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(manualRefundVoided).getCell(STATUS).verify.value("Voided");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(manualRefundVoided).getCell(AMOUNT).verify.value(refundAmount.toString());

        Map<String, String> refundVoidedAdjustment = new HashMap<>();
        refundVoidedAdjustment.put(TRANSACTION_DATE, checkDate);
        refundVoidedAdjustment.put(TYPE, "Adjustment");
        refundVoidedAdjustment.put(SUBTYPE_REASON, "Refund Payment Voided");
        CustomAssert.assertEquals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoidedAdjustment).getIndex(), 2);
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoidedAdjustment).getCell(ACTION).verify.value("");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoidedAdjustment).getCell(STATUS).verify.value("Applied");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoidedAdjustment).getCell(AMOUNT).verify.value(refundAmount.negate().toString());

        Map<String, String> automatedRefundVoidedAdjustment = new HashMap<>();
        automatedRefundVoidedAdjustment.put(TRANSACTION_DATE, checkDate);
        automatedRefundVoidedAdjustment.put(TYPE, "Refund");
        automatedRefundVoidedAdjustment.put(SUBTYPE_REASON, "Automated Refund");
        CustomAssert.assertEquals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(automatedRefundVoidedAdjustment).getIndex(), 1);
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(automatedRefundVoidedAdjustment).getCell(ACTION).controls.links.get(1).verify.value("Void");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(automatedRefundVoidedAdjustment).getCell(ACTION).controls.links.get(2).verify.value("Issue");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(automatedRefundVoidedAdjustment).getCell(STATUS).verify.value("Approved");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(automatedRefundVoidedAdjustment).getCell(AMOUNT).verify.value(refundAmount.toString());
    }

    private void allocationManualRefundPerform(String allocationAmount, String message) {

        String allocationRefundAmount = "3";

        billingAccount.refund().start();

        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).setValue(message);
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).setValue(allocationAmount);

        BillingSummaryPage.linkAdvancedAllocation.click();

        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.NET_PREMIUM.getLabel(), TextBox.class).setValue(allocationRefundAmount);
        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.OTHER.getLabel(), TextBox.class).setValue(allocationRefundAmount);
        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.POLICY_FEE.getLabel(), TextBox.class).setValue(allocationRefundAmount);

        advancedAllocationsActionTab.submitTab();
    }

    private void checkRefundAllocationAmount(String totalAmount, String allocationRefundAmount) {

        BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains("Type", "Refund").getCell(TYPE).controls.links.get("Refund").click();

        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).verify.value("Check");
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).verify.value("Processing");

        BillingSummaryPage.linkAdvancedAllocation.click();

        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.PRODUCT_SUB_TOTAL.getLabel(), TextBox.class).verify.value("$" + totalAmount + ".00");
        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.TOTAL_AMOUNT.getLabel(), TextBox.class).verify.value("$" + totalAmount + ".00");
        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.NET_PREMIUM.getLabel(), TextBox.class).verify
                .value("$" + allocationRefundAmount + ".00");
        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.OTHER.getLabel(), TextBox.class).verify.value("$" + allocationRefundAmount + ".00");
        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.POLICY_FEE.getLabel(), TextBox.class).verify
                .value("$" + allocationRefundAmount + ".00");

        advancedAllocationsActionTab.back();

        acceptPaymentActionTab.back();
    }
}

