package aaa.modules.regression.document_fulfillment.auto_ca.choice;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

import java.time.LocalDateTime;
import java.util.List;

import static toolkit.verification.CustomAssertions.assertThat;


public class TestPolicyActions extends AutoCaChoiceBaseTest {


    @Parameters({"state"})
    @StateList(states = Constants.States.CA)
    @Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
    public void testPolicyActions(@Optional("") String state) {

        List<LocalDateTime> installmentDueDates;
        LocalDateTime billGenDate;
        LocalDateTime renewalDate;
        LocalDateTime cancellationDate;
        Dollar minDue;

        TestData td = getTestSpecificTD("TestData");
//     TestData td_plus33days = getTestSpecificTD("TestData_Plus33Days");


        mainApp().open();
        createCustomerIndividual();

        log.info("Policy Creation Started...");


        policy.initiate();
        policy.getDefaultView().fillUpTo(td, aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab.class, true);
        new ErrorTab().submitTab();
        new PurchaseTab().payRemainingBalance(BillingConstants.AcceptPaymentMethod.CASH).submitTab();


        String policyNumber = PolicySummaryPage.getPolicyNumber();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        SearchPage.openBilling(policyNumber);
        installmentDueDates = BillingHelper.getInstallmentDueDates();
        renewalDate = installmentDueDates.get(0).plusYears(1);
        billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(1));
        cancellationDate = installmentDueDates.get(1).plusMonths(1);


        // !!!   JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
        DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, DocGenEnum.Documents.AARFIXX);


        //DD3-20
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(1)));
        JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);

        mainApp().open();
        SearchPage.openBilling(policyNumber);

        new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(1), billGenDate);
        new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();


        //DD3
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(1)));

        //DD3+5
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(installmentDueDates.get(1)));

        JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
        JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        PolicySummaryPage.verifyCancelNoticeFlagPresent();


        DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents.AH34XX);


        //DD4-20
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(cancellationDate));

        mainApp().open();
        SearchPage.openBilling(policyNumber);

        minDue = new Dollar(BillingSummaryPage.getMinimumDue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
        new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillGenerationDate(installmentDueDates.get(1).plusMonths(1)))
                .setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();


        SearchPage.openPolicy(policyNumber);
        PolicySummaryPage.verifyCancelNoticeFlagNotPresent();


        //DD4
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(cancellationDate));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        log.info("Cancel Notice for Policy #" + policyNumber);


        policy.cancelNotice().start();
        policy.cancelNotice().getView().fill(getPolicyTD("CancelNotice", "TestData"));
        // int daysOfNotice = Integer.parseInt(policy.cancelNotice().getView().getTab(CancelNoticeActionTab.class).getAssetList().getAsset(AutoCaMetaData.CancelNoticeActionTab.DAYS_OF_NOTICE.getLabel(), TextBox.class).getValue());
        policy.cancelNotice().submit();

        assertThat(PolicySummaryPage.labelCancelNotice).isPresent();

        JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
        DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents.AH61XX);


        //DD4+33 (CED)


        //IMPORTANT!!! IT'S NOT RIGHT - Expected correction

        log.info("Policy Cancellation Started...");

        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(cancellationDate.plusDays(33)));
        JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);

        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
        log.info("Assert is successful");


        //DD6-20

        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(2)));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.reinstate().start();
        policy.reinstate().getView().fill(getPolicyTD("Reinstatement", "TestData"));
        policy.reinstate().submit();

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
        SearchPage.openBilling(policyNumber);

        billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(2));
        new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(2), billGenDate);
        new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();

        //DD6

        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(2)));
        mainApp().open();
        SearchPage.openBilling(policyNumber);


        //DD9-20

        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(3)));
        JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
        mainApp().open();
        SearchPage.openBilling(policyNumber);

        billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(3));
        new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(3), billGenDate);
        new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();


        //DD9

        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(3)));
        mainApp().open();
        SearchPage.openBilling(policyNumber);

        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
        new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(installmentDueDates.get(3)))
                .setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();


        //R-81
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(renewalDate));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);


        //R-63
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCheckUWRules(installmentDueDates.get(3)));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);


        //R-57
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(renewalDate));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

        mainApp().open();
        SearchPage.openPolicy(policyNumber);


        //R-35
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalDate));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);


        //R
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalDate));


        //R+1
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(renewalDate));
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);
        JobUtils.executeJob(Jobs.policyLapsedRenewalProcessAsyncJob);

        mainApp().open();
        SearchPage.openBilling(policyNumber);
    }
}
