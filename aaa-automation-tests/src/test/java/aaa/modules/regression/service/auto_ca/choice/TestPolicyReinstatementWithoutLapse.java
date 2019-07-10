package aaa.modules.regression.service.auto_ca.choice;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.auto_ca.actiontabs.CancelNoticeActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;


public class TestPolicyReinstatementWithoutLapse extends AutoCaChoiceBaseTest {

    /**
     * @author Denis Semenov
     * <b> scenario id C-AU-CAC-CA-244 </b>
     * <p> Steps: 1. Initiate Auto quote. Select Product as 'CA Choice
     * <p> 2. Run aaaDocgen batch job. Search for AARFIXX form in POLICY_ISSUE event in DB
     * <p> 3. Run aaaBillingInvoiceAsyncTaskJob to generate the Installment Bill
     * <p> 4. Run aaaCancellationNoticeAsyncJob and aaaDocGen
     * <p> 5. Search for AH34XX form in CANCEL_NOTICE event in DB
     * <p> 6. Make payment
     * <p> 6. Select 'Cancel Notice' action
     * <p> 7. Run aaaDocGenBatchJob
     * <p> 8. Search for AH61XX form in CANCEL_NOTICE event in DB
     * <p> 9. Run aaaCancellationConfirmationAsyncJob and policyStatusUpdateJob
     * <p> 10. Select 'Reinstatement' action
     * <p> 11. Run aaaBillingInvoiceAsyncTaskJob to generate the Installment Bill
     * <p> 12. Make Payment
     * <p> 13. Run aaaBillingInvoiceAsyncTaskJob to generate the Installment Bill
     * <p> 14. Make Payment
     * <p> 15. Run the following jobs: Renewal_Offer_Generation_Part2, Renewal_Offer_Generation_Part1,
     * <p> Renewal_Offer_Generation_Part2, Renewal_Offer_Generation_Part2
     * <p> 16. Make payment for renewal term
     * <p> 17. Run policyStatusUpdateJob
     *
     */
    @Parameters({"state"})
    @StateList(states = Constants.States.CA)
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM, Groups.TIMEPOINT})
    public void testPolicyReinstatementWithoutLapse(@Optional("") String state) {
        TestData td = getTestSpecificTD("TestData");
        List<LocalDateTime> installmentDueDates;
        LocalDateTime billGenDate;
        LocalDateTime renewalDate;
        LocalDateTime installmentDD1;
        LocalDateTime installmentDD2;
        LocalDateTime installmentDD3;
        LocalDateTime installmentDD1_plus_1_month;
        Dollar minDue;

        //Initiate Auto quote. Select Product as 'CA Choice'
        mainApp().open();
        createCustomerIndividual();
        log.info("Policy Creation Started...");
        createPolicy(td);
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        BillingSummaryPage.open();
        installmentDueDates = BillingHelper.getInstallmentDueDates();
        renewalDate = installmentDueDates.get(0).plusYears(1);
        installmentDD1 = installmentDueDates.get(1);
        installmentDD2 = installmentDueDates.get(2);
        installmentDD3 = installmentDueDates.get(3);
        installmentDD1_plus_1_month = installmentDD1.plusMonths(1);

        //Search for AARFIXX form in POLICY_ISSUE event in DB
        DocGenHelper.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.AARFIXX);

        //(DD3-20) Run aaaBillingInvoiceAsyncTaskJob to generate the Installment Bill
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDD1));
        JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
        mainApp().open();
        SearchPage.openBilling(policyNumber);
        billGenDate = getTimePoints().getBillGenerationDate(installmentDD1);
        new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDD1, billGenDate);
        new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate)
                .setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();

        //(DD3+5) Run aaaCancellationNoticeAsyncJob and aaaDocGen
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(installmentDD1));
        JobUtils.executeJob(BatchJob.aaaCancellationNoticeAsyncJob);
        JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
        searchForPolicy(policyNumber);
        PolicySummaryPage.verifyCancelNoticeFlagPresent();
        //Search for AH34XX form in CANCEL_NOTICE event in DB
        DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents.AH34XX);

        //(DD4-20) Make payment
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDD1_plus_1_month));
        mainApp().open();
        SearchPage.openBilling(policyNumber);
        minDue = new Dollar(BillingSummaryPage.getMinimumDue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
        new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillGenerationDate(installmentDD1_plus_1_month))
                .setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();
        SearchPage.openPolicy(policyNumber);
        PolicySummaryPage.verifyCancelNoticeFlagNotPresent();

        //(DD4) Select 'Cancel Notice' action
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDD1_plus_1_month));
        searchForPolicy(policyNumber);
        log.info("Cancel Notice for Policy #" + policyNumber);
        policy.cancelNotice().start();
        policy.cancelNotice().getView().fill(getPolicyTD("CancelNotice", "TestData"));
        int daysOfNotice = Integer.parseInt(new CancelNoticeActionTab().getAssetList()
                .getAsset(AutoCaMetaData.CancelNoticeActionTab.DAYS_OF_NOTICE).getValue());
        policy.cancelNotice().submit();

        assertThat(PolicySummaryPage.labelCancelNotice).isPresent();

        //Run aaaDocGenBatchJob
        JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
        //Search for AH61XX form in CANCEL_NOTICE event in DB
        DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents.AH61XX);

        //(DD4+33) (CED) Run aaaCancellationConfirmationAsyncJob and policyStatusUpdateJob
        log.info("Policy Cancellation Started...");
        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(daysOfNotice).with(DateTimeUtils.nextWorkingDay));
        String cancellationCurrentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        JobUtils.executeJob(BatchJob.aaaCancellationConfirmationAsyncJob);
        searchForPolicy(policyNumber);

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        //(DD6-20) Select 'Reinstatement' action
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDD2));
        searchForPolicy(policyNumber);
        policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData")
                .adjust(TestData.makeKeyPath(AutoCaMetaData.ReinstatementActionTab.class.getSimpleName(), "Reinstate Date"), cancellationCurrentDate));

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        //Run aaaBillingInvoiceAsyncTaskJob to generate the Installment Bill
        JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
        BillingSummaryPage.open();
        billGenDate = getTimePoints().getBillGenerationDate(installmentDD2);
        new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDD2, billGenDate);
        new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate)
                .setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();

        //(DD6) Make Payment
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDD2));
        mainApp().open();
        SearchPage.openBilling(policyNumber);
        minDue = new Dollar(BillingSummaryPage.getMinimumDue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
        new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(installmentDD2))
                .setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();

        //(DD9-20) Run aaaBillingInvoiceAsyncTaskJob to generate the Installment Bill
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDD3));
        JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
        mainApp().open();
        SearchPage.openBilling(policyNumber);
        billGenDate = getTimePoints().getBillGenerationDate(installmentDD3);
        new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDD3, billGenDate);
        new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants
                .PaymentsAndOtherTransactionType.FEE).verifyPresent();

        //(DD9) Make Payment
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDD3));
        mainApp().open();
        SearchPage.openBilling(policyNumber);
        minDue = new Dollar(BillingSummaryPage.getMinimumDue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
        new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(installmentDD3))
                .setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();

        //Run the following jobs: Renewal_Offer_Generation_Part2, Renewal_Offer_Generation_Part1, Renewal_Offer_Generation_Part2, Renewal_Offer_Generation_Part2
        //(R-81)
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(renewalDate));
        JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
        HttpStub.executeAllBatches();
        JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

        //(R-63)
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCheckUWRules(renewalDate));
        JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
        HttpStub.executeAllBatches();
        JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

        //(R-57)
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(renewalDate));
        JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
        searchForPolicy(policyNumber);
        PolicySummaryPage.buttonRenewals.click();

        new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);

        //(R-35)
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalDate));
        JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
        searchForPolicy(policyNumber);
        PolicySummaryPage.buttonRenewals.click();

        new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

        //(R) Make payment for renewal term
        TimeSetterUtil.getInstance().nextPhase(renewalDate);
        mainApp().open();
        SearchPage.openBilling(policyNumber);
        minDue = new Dollar(BillingSummaryPage.getMinimumDue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
        new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(renewalDate))
                .setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();

        //(R+1) Run policyStatusUpdateJob
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(renewalDate));
        JobUtils.executeJob(BatchJob.policyStatusUpdateJob);
        mainApp().open();
        SearchPage.openBilling(policyNumber);
        BillingSummaryPage.showPriorTerms();

        new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);
        new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).verify(2);
    }
}
