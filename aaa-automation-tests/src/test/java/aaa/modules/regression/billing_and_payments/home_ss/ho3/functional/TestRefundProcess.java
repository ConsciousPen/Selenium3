package aaa.modules.regression.billing_and_payments.home_ss.ho3.functional;

import java.io.IOException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.AdvancedAllocationsActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.billing_and_payments.auto_ss.functional.preconditions.TestRefundProcessPreConditions;
import aaa.modules.regression.billing_and_payments.helpers.RefundProcessHelper;
import aaa.modules.regression.billing_and_payments.template.PolicyBilling;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

public class TestRefundProcess extends PolicyBilling implements TestRefundProcessPreConditions {

    private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
    private static final String REMOTE_FOLDER_PATH = PropertyProvider.getProperty(CustomTestProperties.JOB_FOLDER)+"DSB_E_PASSYS_DSBCTRL_7025_D/outbound/";
    private static final String LOCAL_FOLDER_PATH = "src/test/resources/stubs/";
    private static final String PENDING_REFUND_AMOUNT = "1000";
    private static final String APPROVED_REFUND_AMOUNT = "999.99";
    private TestData tdBilling = testDataManager.billingAccount;
    private TestData tdRefund = tdBilling.getTestData("Refund", "TestData_Check");
    private BillingAccount billingAccount = new BillingAccount();
    private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
    private AdvancedAllocationsActionTab advancedAllocationsActionTab = new AdvancedAllocationsActionTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
    private BindTab bindTab = new BindTab();
    private ApplicantTab applicantTab = new ApplicantTab();
    private RefundProcessHelper refundProcessHelper = new RefundProcessHelper();

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO3;
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


    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7039", "PAS-7196"})
    public void pas7039_Debug(@Optional("VA") String state) throws SftpException, JSchException, IOException {
        String manualRefundAmount = "100";
        String automatedRefundAmount = "101";
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, "VAH3926232250");
        String policyNumber = PolicySummaryPage.getPolicyNumber();

        refundProcessHelper.refundDebug(policyNumber, "M", "CHCK", "HO", "4WUIC", "Y", "VA", manualRefundAmount, "", "N");


        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7039", "PAS-7196"})
    public void pas7039_newDataElementsDeceasedYes(@Optional("VA") String state) throws SftpException, JSchException, IOException {
        String manualRefundAmount = "100";
        String automatedRefundAmount = "101";
        mainApp().open();

        String policyNumber = preconditionPolicyCreationHo();

        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), MultiAssetList.class)
                .getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.RELATIONSHIP_TO_PRIMARY_NAMED_INSURED.getLabel(), ComboBox.class).setValue("Deceased");

        premiumsAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.submitTab();

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        billingAccount.refund().manualRefundPerform("Check", manualRefundAmount);

        CustomAssert.enableSoftMode();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));
        //RemoteHelper.clearFolder(REMOTE_FOLDER_PATH);
        JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
        refundProcessHelper.refundRecordInFileCheck(policyNumber, "M", "CHCK", "HO", "4WUIC", "Y", "VA", manualRefundAmount, "", "N");

        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        Dollar totalDue = BillingSummaryPage.getTotalDue();
        billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue.add(new Dollar(automatedRefundAmount)));
        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(8));

        //RemoteHelper.clearFolder(REMOTE_FOLDER_PATH);
        JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
        JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
        refundProcessHelper.refundRecordInFileCheck(policyNumber, "R", "CHCK", "HO", "4WUIC", "Y", "VA", automatedRefundAmount, "", "N");

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7039", "PAS-7196"})
    public void pas7039_newDataElementsDeceasedNo(@Optional("VA") String state) throws SftpException, JSchException, IOException {
        String manualRefundAmount = "100";
        String automatedRefundAmount = "101";

        String policyNumber = preconditionPolicyCreationHo();

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        billingAccount.refund().manualRefundPerform("Check", manualRefundAmount);

        CustomAssert.enableSoftMode();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));
        //RemoteHelper.clearFolder(REMOTE_FOLDER_PATH);
        JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
        refundProcessHelper.refundRecordInFileCheck(policyNumber, "M", "CHCK", "HO", "4WUIC", "N", "VA", manualRefundAmount, "", "N");

        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        Dollar totalDue = BillingSummaryPage.getTotalDue();
        billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue.add(new Dollar(automatedRefundAmount)));
        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(8));

        //RemoteHelper.clearFolder(REMOTE_FOLDER_PATH);
        JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
        JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
        refundProcessHelper.refundRecordInFileCheck(policyNumber, "R", "CHCK", "HO", "4WUIC", "N", "VA", automatedRefundAmount, "", "N");

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
    public void pas7298_pendingManualRefundsCC(@Optional("VA") String state) {

        String paymentMethod = "contains=Credit Card";

        preconditionPolicyCreationHo();

        CustomAssert.enableSoftMode();
        refundProcessHelper.pas7298_pendingManualRefunds(PENDING_REFUND_AMOUNT, APPROVED_REFUND_AMOUNT, paymentMethod);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
    public void pas7298_pendingManualRefundsACH(@Optional("MD") String state) {

        String paymentMethod = "contains=ACH";

        preconditionPolicyCreationHo();

        CustomAssert.enableSoftMode();
        refundProcessHelper.pas7298_pendingManualRefunds(PENDING_REFUND_AMOUNT, APPROVED_REFUND_AMOUNT, paymentMethod);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
    public void pas7298_pendingAutomatedRefundsCC(@Optional("VA") String state) {

        String paymentMethod = "Credit Card";

        String policyNumber = preconditionPolicyCreationHo();

        CustomAssert.enableSoftMode();
        refundProcessHelper.pas7298_pendingAutomatedRefunds(policyNumber, APPROVED_REFUND_AMOUNT, PENDING_REFUND_AMOUNT, paymentMethod, 8);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7298"})
    public void pas7298_pendingAutomatedRefundsACH(@Optional("MD") String state) {

        String paymentMethod = "ACH";

        String policyNumber = preconditionPolicyCreationHo();

        CustomAssert.enableSoftMode();
        refundProcessHelper.pas7298_pendingAutomatedRefunds(policyNumber, APPROVED_REFUND_AMOUNT, PENDING_REFUND_AMOUNT, paymentMethod, 8);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    private String preconditionPolicyCreationHo() {
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy();
        log.info("policyNumber: {}", policyNumber);
        return policyNumber;
    }

}
