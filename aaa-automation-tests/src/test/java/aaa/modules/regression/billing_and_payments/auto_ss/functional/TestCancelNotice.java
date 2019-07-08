package aaa.modules.regression.billing_and_payments.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
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

public class TestCancelNotice extends AutoSSBaseTest {

    private TestData tdBilling = testDataManager.billingAccount;
    private TestData check_payment = tdBilling.getTestData("AcceptPayment", "TestData_Check");
    private IBillingAccount billing = new BillingAccount();

    private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
    protected BillingAccount billingAccount = new BillingAccount();




    /**
     * @author Dakota Berg & Jeffrey Thwaites
     * @name Check Cancel Notice flag is absent after premium paid
     * @scenario 1. Create a customer and policy
     * 2. Shift time setter and generate the premium for the next two months - Do not pay
     * 3. Set cancellation notice
     * 4. Pay the total amount due
     * 5. Verify that the Cancel Notice Flag is no longer present
     */

    @Parameters({"state"})
    @StateList(states = Constants.States.AZ)
    @Test(groups = {Groups.FUNCTIONAL})
    public void testCancelNoticeFlagAfterPremiumPaid(@Optional("") String state) {

        List<LocalDateTime> installmentDueDates;
        LocalDateTime dueDate1;
        LocalDateTime dueDate2;
        LocalDateTime billDueDate;
        TestData td = getPolicyTD()
                .adjust(TestData.makeKeyPath("PremiumAndCoveragesTab", "Payment Plan"), "Eleven Pay - Standard");

        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusMonths(2).with(DateTimeUtils.nextWorkingDay));
        mainApp().open();
        createCustomerIndividual();
        String policyNum = createPolicy(td);
        SearchPage.openBilling(policyNum);
        installmentDueDates = BillingHelper.getInstallmentDueDates();
        dueDate1 = installmentDueDates.get(1);
        dueDate2 = installmentDueDates.get(2);

        billing.acceptPayment().perform(check_payment, new Dollar(200));
        billing.acceptPayment().perform(check_payment, new Dollar(300));


        //DD1
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dueDate1));
        JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(dueDate1));

        mainApp().open();
        SearchPage.openBilling(policyNum);


        //DD2
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dueDate2));
        JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
        billDueDate = getTimePoints().getBillDueDate(dueDate2);
        TimeSetterUtil.getInstance().nextPhase(billDueDate);


        mainApp().open();
        searchForPolicy(policyNum);
        policy.cancelNotice().start();
        policy.cancelNotice().getView().fill(getPolicyTD("CancelNotice", "TestData"));
        policy.cancelNotice().submit();


        assertThat(PolicySummaryPage.labelCancelNotice).isPresent();
        SearchPage.openBilling(policyNum);
        assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(6)).hasValue("Cancel Notice Flag");

        payTotalAmtDue(policyNum);
        assertThat(PolicySummaryPage.labelCancelNotice).isAbsent();
        SearchPage.openBilling(policyNum);
        assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(6)).hasValue("");


    }

    @Parameters({"state"})
    @StateList(states = Constants.States.AZ)
    @Test(groups = {Groups.FUNCTIONAL})
    public void testPaymentOptionOutsideNB_AC01(@Optional("") String state) {

        mainApp().open();
        createCustomerIndividual();
        createPolicy();

        BillingSummaryPage.open();
        billing.acceptPayment().start();

        assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD)).doesNotContainOption("Zelle");

    }


    @Parameters({"state"})
    @StateList(states = Constants.States.AZ)
    @Test(groups = {Groups.FUNCTIONAL})
    public void testPaymentOptionOutsideNB_AC02(@Optional("") String state) {

        mainApp().open();
        createCustomerIndividual();
        createPolicy();



    }

}
