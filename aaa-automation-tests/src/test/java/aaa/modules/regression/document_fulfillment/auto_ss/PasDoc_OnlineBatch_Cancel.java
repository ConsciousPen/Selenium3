package aaa.modules.regression.document_fulfillment.auto_ss;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

import static toolkit.verification.CustomAssertions.assertThat;

public class PasDoc_OnlineBatch_Cancel extends AutoSSBaseTest {

    @Parameters({"state"})
    @StateList(states = Constants.States.AZ)
    @Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
    public void testScenario26(@Optional("") String state) {
        mainApp().open();
        createCustomerIndividual();
        String policy_cancel = createPolicy();
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        PasDocImpl.verifyDocumentsGenerated(policy_cancel, DocGenEnum.Documents.AH61XX);

        String policy_cancel_in_3_days = createPolicy();
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData_Plus3Days"));
        PasDocImpl.verifyDocumentsGenerated(policy_cancel_in_3_days, DocGenEnum.Documents.AH61XX);
    }

    @Parameters({"state"})
    @StateList(states = Constants.States.AZ)
    @Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
    public void testScenario27(@Optional("") String state) {
        List<LocalDateTime> installmentDueDate;
        Dollar minDue;
        mainApp().open();
        TestData td = getPolicyDefaultTD().adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName()
                , AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), "Monthly - Zero Down");
        createCustomerIndividual();
        String policyNumber = createPolicy(td);
        BillingSummaryPage.open();
        installmentDueDate = BillingHelper.getInstallmentDueDates();
        new BillingAccount().generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableInstallmentSchedule.getRow(1).getCell(BillingConstants.BillingInstallmentScheduleTable.BILLED_STATUS).getValue())
                .isEqualTo(BillingConstants.InstallmentScheduleBilledStatus.BILLED);
        assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue())
                .isEqualTo(BillingConstants.BillsAndStatementsType.BILL);

        //DD1+8
        log.info(DateTimeUtils.getCurrentDateTime().toString());
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(installmentDueDate.get(0)));
        log.info(DateTimeUtils.getCurrentDateTime().toString());
        JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
        searchForPolicy(policyNumber);
        assertThat(PolicySummaryPage.labelCancelNotice).isPresent();
        log.info(DateTimeUtils.getCurrentDateTime().toString());
        //+8
        LocalDateTime cancelDueDate = getTimePoints().getCancellationDate(DateTimeUtils.getCurrentDateTime());
        TimeSetterUtil.getInstance().nextPhase(cancelDueDate);
        log.info(DateTimeUtils.getCurrentDateTime().toString());
        JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
        mainApp().open();
        SearchPage.openBilling(policyNumber);
        PasDocImpl.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.AH67XX);
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell("Subtype/Reason")
                .getValue()).isEqualTo(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_INSURED_NON_PAYMENT_OF_PREMIUM);
        //+10
        LocalDateTime reinstatementDueDate = getTimePoints().getInsuranceRenewalReminderDate(DateTimeUtils.getCurrentDateTime());
        TimeSetterUtil.getInstance().nextPhase(reinstatementDueDate);
        searchForPolicy(policyNumber);
        policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
        BillingSummaryPage.open();
        minDue = new Dollar(BillingSummaryPage.getMinimumDue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
        new BillingAccount().generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableInstallmentSchedule.getRow(3).getCell(BillingConstants.BillingInstallmentScheduleTable.BILLED_STATUS).getValue())
                .isEqualTo(BillingConstants.InstallmentScheduleBilledStatus.BILLED);
        assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue())
                .isEqualTo(BillingConstants.BillsAndStatementsType.BILL);


        //DD2+8
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(installmentDueDate.get(2)));
        JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
        //+8
        LocalDateTime cancelDueDate2 = getTimePoints().getCancellationDate(DateTimeUtils.getCurrentDateTime());
        TimeSetterUtil.getInstance().nextPhase(cancelDueDate2);
        JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
        mainApp().open();
        SearchPage.openBilling(policyNumber);
        PasDocImpl.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.AH67XX);
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell("Subtype/Reason")
                .getValue()).isEqualTo(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_INSURED_NON_PAYMENT_OF_PREMIUM);


        //+10
        LocalDateTime reinstatementDueDate2 = getTimePoints().getInsuranceRenewalReminderDate(DateTimeUtils.getCurrentDateTime());
        TimeSetterUtil.getInstance().nextPhase(reinstatementDueDate2);
        searchForPolicy(policyNumber);
        policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
        BillingSummaryPage.open();
        minDue = new Dollar(BillingSummaryPage.getMinimumDue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
        new BillingAccount().generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableInstallmentSchedule.getRow(5).getCell(BillingConstants.BillingInstallmentScheduleTable.BILLED_STATUS).getValue())
                .isEqualTo(BillingConstants.InstallmentScheduleBilledStatus.BILLED);
        assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue())
                .isEqualTo(BillingConstants.BillsAndStatementsType.BILL);

        //DD3+8
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(installmentDueDate.get(3)));
        JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
        searchForPolicy(policyNumber);
        assertThat(PolicySummaryPage.labelCancelNotice).isPresent();
        //+8
        LocalDateTime cancelDueDate3 = getTimePoints().getCancellationDate(DateTimeUtils.getCurrentDateTime());
        TimeSetterUtil.getInstance().nextPhase(cancelDueDate3);
        JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
        mainApp().open();
        SearchPage.openBilling(policyNumber);
        PasDocImpl.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.AH63XX);
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell("Subtype/Reason")
                .getValue()).isEqualTo(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_INSURED_NON_PAYMENT_OF_PREMIUM);

        SearchPage.openPolicy(policyNumber);
        policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
        PasDocImpl.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.AH63XX);
    }

    @Parameters({"state"})
    @StateList(states = Constants.States.AZ)
    @Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
    public void testScenario29(@Optional("") String state) {
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy();
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        PasDocImpl.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.AH61XX, DocGenEnum.Documents.AH61XXA);
    }
}
