package aaa.modules.regression.billing_and_payments.helpers;

import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.DisbursementEngineHelper;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.regression.billing_and_payments.template.PolicyBilling;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.StaticElement;

public class RefundProcessHelper extends PolicyBilling {

    private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
    private static final String REFUND_GENERATION_FOLDER = "DSB_E_PASSYS_DSBCTRL_7025_D/outbound/";
    private static final String REFUND_GENERATION_FOLDER_PATH = PropertyProvider.getProperty(CustomTestProperties.JOB_FOLDER) + REFUND_GENERATION_FOLDER;
    private static final String LOCAL_FOLDER_PATH = "src/test/resources/stubs/";
    private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
    private BillingAccount billingAccount = new BillingAccount();
    private TestData tdBilling = testDataManager.billingAccount;

    public void refundDebug(String policyNumber, String refundType, String refundMethod, String productType, String companyId, String deceasedNamedInsuredFlag, String policyState, String refundAmount,
            String email, String refundEligible)
            throws IOException {
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains("Type", "Refund").getCell(TYPE).controls.links.get("Refund").click();
        String transactionID = acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).getValue();
        acceptPaymentActionTab.back();

        //TODO doesn't work in VDMs
/*        RemoteHelper.waitForFilesAppearance(REFUND_GENERATION_FOLDER_PATH, 10, policyNumber);
        String neededFilePath = RemoteHelper.waitForFilesAppearance(REFUND_GENERATION_FOLDER_PATH, "csv", 10, policyNumber).get(0);
        String fileName = neededFilePath.replace(REFUND_GENERATION_FOLDER_PATH, "");

        RemoteHelper.downloadFile(neededFilePath, LOCAL_FOLDER_PATH + fileName);*/
        String fileName = "20180110_164313_DSB_E_PASSYS_DSBCTRL_7025_D.csv";
        List<DisbursementEngineHelper.DisbursementFile> listOfRecordsInFile = DisbursementEngineHelper.readDisbursementFile(LOCAL_FOLDER_PATH + fileName);
        DisbursementEngineHelper.DisbursementFile neededLine = null;
        for (DisbursementEngineHelper.DisbursementFile s : listOfRecordsInFile) {
            if (s.getAgreementNumber().equals(policyNumber)) {
                neededLine = s;
            }
        }
        CustomAssert.assertEquals(neededLine.getRecordType(), "D");
        CustomAssert.assertEquals(neededLine.getRequestRefereceId(), transactionID);
        CustomAssert.assertEquals(neededLine.getRefundType(), refundType);
        // RefundMethod = 'CHCK' - check, 'EFT' - eft, 'CRDC' - credit/debit card
        if (refundMethod.contains("Check")) {
            CustomAssert.assertEquals(neededLine.getRefundMethod(), refundMethod);
        } else if (refundMethod.contains("ACH")) {
            CustomAssert.assertEquals(neededLine.getRefundMethod(), "EFT");
        } else if (refundMethod.contains("Card")) {
            CustomAssert.assertEquals(neededLine.getRefundMethod(), "CRDC");
        } else {
            CustomAssert.assertEquals(neededLine.getRefundMethod(), refundMethod);
        }
        CustomAssert.assertEquals(neededLine.getIssueDate(), TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MMddyyyy")));
        CustomAssert.assertEquals(neededLine.getAgreementNumber(), policyNumber);
        CustomAssert.assertEquals(neededLine.getAgreementSourceSystem(), "PAS");
        CustomAssert.assertEquals(neededLine.getProductType(), productType);
        CustomAssert.assertEquals(neededLine.getCompanyId(), companyId);
        CustomAssert.assertFalse(neededLine.getInsuredFirstName().isEmpty());
        CustomAssert.assertFalse(neededLine.getInsuredLastName().isEmpty());
        //TODO update once the deceased indicator is implemented
        CustomAssert.assertEquals(neededLine.getDeceasedNamedInsuredFlag(), deceasedNamedInsuredFlag);
        if (null == policyState) {
            CustomAssert.assertFalse(neededLine.getPolicyState().isEmpty());
        } else {
            CustomAssert.assertEquals(neededLine.getPolicyState(), policyState);
        }
        CustomAssert.assertEquals(neededLine.getRefundAmount(), refundAmount + ".00");
        CustomAssert.assertEquals(neededLine.getPayeeName(), neededLine.getInsuredFirstName() + " " + neededLine.getInsuredLastName());
        CustomAssert.assertFalse(neededLine.getPayeeStreetAddress1().isEmpty());
        CustomAssert.assertFalse(neededLine.getPayeeCity().isEmpty());
        CustomAssert.assertFalse(neededLine.getPayeeState().isEmpty());
        CustomAssert.assertFalse(neededLine.getPayeeZip().isEmpty());
        CustomAssert.assertEquals(neededLine.getInsuredEmailId(), email);
        CustomAssert.assertEquals(neededLine.getCheckNumber(), "");
        CustomAssert.assertEquals(neededLine.getPrinterIdentificationCode(), "FFD");
        CustomAssert.assertEquals(neededLine.getRefundReason(), "Overpayment");
        CustomAssert.assertEquals(neededLine.getRefundReasonDescription(), "");
        CustomAssert.assertEquals(neededLine.getReferencePaymentTransactionNumber(), "");
        CustomAssert.assertEquals(neededLine.geteRefundEligible(), refundEligible);
    }

    public void refundRecordInFileCheck(String policyNumber, String refundType, String refundMethod, String productType, String companyId, String deceasedNamedInsuredFlag, String policyState,
            String refundAmount, String email, String refundEligible)
            throws IOException {
        //TODO waitForFilesAppearance doesn't work in VDMs
        if (!StringUtils.isEmpty(PropertyProvider.getProperty("scrum.envs.ssh")) && !"true".equals(PropertyProvider.getProperty("scrum.envs.ssh"))) {
            mainApp().open();
            SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
            BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains("Type", "Refund").getCell(TYPE).controls.links.get("Refund").click();
            String transactionID = acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).getValue();
            acceptPaymentActionTab.back();

            //TODO doesn't work in VDMs
            RemoteHelper.waitForFilesAppearance(REFUND_GENERATION_FOLDER_PATH, 10, policyNumber);
            String neededFilePath = RemoteHelper.waitForFilesAppearance(REFUND_GENERATION_FOLDER_PATH, "csv", 10, policyNumber).get(0);
            String fileName = neededFilePath.replace(REFUND_GENERATION_FOLDER_PATH, "");

            RemoteHelper.downloadFile(neededFilePath, LOCAL_FOLDER_PATH + fileName);

            List<DisbursementEngineHelper.DisbursementFile> listOfRecordsInFile = DisbursementEngineHelper.readDisbursementFile(LOCAL_FOLDER_PATH + fileName);
            DisbursementEngineHelper.DisbursementFile neededLine = null;
            for (DisbursementEngineHelper.DisbursementFile s : listOfRecordsInFile) {
                if (s.getAgreementNumber().equals(policyNumber)) {
                    neededLine = s;
                }
            }
            CustomAssert.assertEquals(neededLine.getRecordType(), "D");
            CustomAssert.assertEquals(neededLine.getRequestRefereceId(), transactionID);
            CustomAssert.assertEquals(neededLine.getRefundType(), refundType);
            // RefundMethod = 'CHCK' - check, 'EFT' - eft, 'CRDC' - credit/debit card
            if (refundMethod.contains("Check")) {
                CustomAssert.assertEquals(neededLine.getRefundMethod(), "CHCK");
            } else if (refundMethod.contains("ACH")) {
                CustomAssert.assertEquals(neededLine.getRefundMethod(), "EFT");
            } else if (refundMethod.contains("Card")) {
                CustomAssert.assertEquals(neededLine.getRefundMethod(), "CRDC");
            } else {
                CustomAssert.assertEquals(neededLine.getRefundMethod(), refundMethod);
            }
            CustomAssert.assertEquals(neededLine.getIssueDate(), TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MMddyyyy")));
            CustomAssert.assertEquals(neededLine.getAgreementNumber(), policyNumber);
            CustomAssert.assertEquals(neededLine.getAgreementSourceSystem(), "PAS");
            CustomAssert.assertEquals(neededLine.getProductType(), productType);
            CustomAssert.assertEquals(neededLine.getCompanyId(), companyId);
            CustomAssert.assertFalse(neededLine.getInsuredFirstName().isEmpty());
            CustomAssert.assertFalse(neededLine.getInsuredLastName().isEmpty());
            //TODO update once the deceased indicator is implemented
            CustomAssert.assertEquals(neededLine.getDeceasedNamedInsuredFlag(), deceasedNamedInsuredFlag);
            if (null == policyState) {
                CustomAssert.assertFalse(neededLine.getPolicyState().isEmpty());
            } else {
                CustomAssert.assertEquals(neededLine.getPolicyState(), policyState);
            }
            CustomAssert.assertEquals(neededLine.getRefundAmount(), refundAmount + ".00");
            CustomAssert.assertEquals(neededLine.getPayeeName(), neededLine.getInsuredFirstName() + " " + neededLine.getInsuredLastName());
            CustomAssert.assertFalse(neededLine.getPayeeStreetAddress1().isEmpty());
            CustomAssert.assertFalse(neededLine.getPayeeCity().isEmpty());
            CustomAssert.assertFalse(neededLine.getPayeeState().isEmpty());
            CustomAssert.assertFalse(neededLine.getPayeeZip().isEmpty());
            CustomAssert.assertEquals(neededLine.getInsuredEmailId(), email);
            CustomAssert.assertEquals(neededLine.getCheckNumber(), "");
            CustomAssert.assertEquals(neededLine.getPrinterIdentificationCode(), "FFD");
            CustomAssert.assertEquals(neededLine.getRefundReason(), "Overpayment");
            CustomAssert.assertEquals(neededLine.getRefundReasonDescription(), "");
            CustomAssert.assertEquals(neededLine.getReferencePaymentTransactionNumber(), "");
            CustomAssert.assertEquals(neededLine.geteRefundEligible(), refundEligible);
        }
    }

    /**
     * @author Oleg Stasyuk
     * @name pending manual refund processing
     * @scenario 1. Create new policy
     * 2. create a manual refund using specific payment method for the amount < threshold
     * 3. Verify the refund goes to Payments and Other Transactions
     * 4. Void
     * 5. create a manual refund using specific payment method for the amount >= threshold
     * 6. Verify the refund goes to Pending Transactions
     * 7. Void
     *
     * Note: for the test to work LastPaymentMethod needs to be configured for the payments to be > threshold
     * @details
     */
    public void pas7298_pendingManualRefunds(String pendingRefundAmount, String approvedRefundAmount, String paymentMethod) {


        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), new Dollar(pendingRefundAmount));

        billingAccount.refund().manualRefundPerform(paymentMethod, approvedRefundAmount);
        CustomAssert.assertTrue("Refund".equals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue()));
        approvedRefundVoid();

        billingAccount.refund().manualRefundPerform(paymentMethod, pendingRefundAmount);
        CustomAssert.assertFalse("Refund".equals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue()));
        pendingRefundLinksCheck();
        pendingRefundVoid();
    }

    /**
     * @author Oleg Stasyuk
     * @name pending manual refund processing
     * @scenario 1. Create new policy
     * 2. create an overpayment for the amount < threshold
     * 3. Run aaaRefundGenerationAsyncJob (Refund will be created with a specific payment type)
     * 4. Check refund was created using specific payment method for the amount < threshold in Payments and Other Transactions
     * 4. Void
     * 2. create an overpayment for the amount >= threshold
     * 3. Run aaaRefundGenerationAsyncJob (Refund will be created with a specific payment type)
     * 4. Check refund was created using specific payment method for the amount >= threshold in Pending Transactions
     * 4. Void
     *
     * Note: for the test to work LastPaymentMethod needs to be configured for the payments to be > threshold
     * @details
     * @param daysDelay - 8 days for HO, 1 day for Auto
     */
    public void pas7298_pendingAutomatedRefunds(String policyNumber, String approvedRefundAmount, String pendingRefundAmount, String paymentMethod, int daysDelay) {

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        Dollar totalDue1 = BillingSummaryPage.getTotalDue();
        billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue1.add(new Dollar(approvedRefundAmount)));
        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(daysDelay));
        JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);

        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        CustomAssert.assertTrue("Refund".equals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue()));
        approvedRefundVoid();

        Dollar totalDue2 = BillingSummaryPage.getTotalDue();
        billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue2.add(new Dollar(pendingRefundAmount)));
        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(8));
        JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);

        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        CustomAssert.assertFalse("Refund".equals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue()));
        pendingRefundLinksCheck();
        pendingRefundPaymentMethodCheck(paymentMethod);
        pendingRefundVoid();
    }

    private void pendingRefundPaymentMethodCheck(String paymentMethod) {
        BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(TYPE).controls.links.get("Refund").click();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).verify.valueContains(paymentMethod);
        acceptPaymentActionTab.back();
    }

    private void approvedRefundVoid() {
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(ACTION).controls.links.get("Void").click();
        Page.dialogConfirmation.confirm();
    }

    private void pendingRefundLinksCheck() {
        CustomAssert.assertTrue("Refund".equals(BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(TYPE).getValue()));
        BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(ACTION).controls.links.get(1).verify.value("Approve");
        BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(ACTION).controls.links.get(2).verify.value("Reject");
        BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(ACTION).controls.links.get(3).verify.value("Void");
        BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(ACTION).controls.links.get(4).verify.value("Change");
    }

    private void pendingRefundVoid() {
        BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(ACTION).controls.links.get("Void").click();
        Page.dialogConfirmation.confirm();
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).verify.value("Adjustment");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON).verify.value("Pending Refund Payment Voided");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS).verify.value("Applied");

        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(TYPE).verify.value("Refund");
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(STATUS).verify.value("Voided");
    }
}
