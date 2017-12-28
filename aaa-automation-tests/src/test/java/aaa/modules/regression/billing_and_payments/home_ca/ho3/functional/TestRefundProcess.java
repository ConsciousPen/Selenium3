package aaa.modules.regression.billing_and_payments.home_ca.ho3.functional;

import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
import aaa.helpers.billing.DisbursementEngineHelper;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.AdvancedAllocationsActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.regression.billing_and_payments.auto_ss.functional.preconditions.TestRefundProcessPreConditions;
import aaa.modules.regression.billing_and_payments.template.PolicyBilling;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

public class TestRefundProcess extends PolicyBilling implements TestRefundProcessPreConditions {

	private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
	private static final String REMOTE_FOLDER_PATH = "/home/mp2/pas/sit/DSB_E_PASSYS_DSBCTRL_7025_D/outbound/";
	private static final String LOCAL_FOLDER_PATH = "src/test/resources/stubs/";
	private TestData tdBilling = testDataManager.billingAccount;
	private TestData tdRefund = tdBilling.getTestData("Refund", "TestData_Check");
	private BillingAccount billingAccount = new BillingAccount();
	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
	private AdvancedAllocationsActionTab advancedAllocationsActionTab = new AdvancedAllocationsActionTab();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private BindTab bindTab = new BindTab();
	private ApplicantTab applicantTab = new ApplicantTab();

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
	public void pas7039_newDataElements(@Optional("VA") String state) throws SftpException, JSchException, IOException {
		String manualRefundAmount = "100";
		String automatedRefundAmount = "101";
		mainApp().open();
		//SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, "VAH3926232128");
		createCustomerIndividual();
		String policyNumber = createPolicy();
		log.info("policyNumber: {}", policyNumber);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), MultiAssetList.class).getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.RELATIONSHIP_TO_PRIMARY_NAMED_INSURED.getLabel(), ComboBox.class).setValue("Deceased");

		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
		bindTab.submitTab();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.refund().manualRefundPerform("Check",manualRefundAmount);

		//TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));

		CustomAssert.enableSoftMode();
		RemoteHelper.clearFolder(REMOTE_FOLDER_PATH);
		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);

		//String policyNumber = "VAH3933661310";
		refundRecordInFileCheck(policyNumber, "M", "CHCK", manualRefundAmount);


		Dollar totalDue = BillingSummaryPage.getTotalDue();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue.add(new Dollar(automatedRefundAmount)));
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(1));

		RemoteHelper.clearFolder(REMOTE_FOLDER_PATH);
		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);

		refundRecordInFileCheck(policyNumber, "R", "CHCK", automatedRefundAmount);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void refundRecordInFileCheck(String policyNumber, String refundType, String refundMethod, String refundAmount) throws IOException {
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains("Type", "Refund").getCell(TYPE).controls.links.get("Refund").click();
		//TODO missing config in PAS18.1
		//String transactionID = acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).getValue();
		acceptPaymentActionTab.back();

		//TODO doesn't work in VDMs
		RemoteHelper.waitForFilesAppearance(REMOTE_FOLDER_PATH, 10, policyNumber);
		String neededFilePath = RemoteHelper.waitForFilesAppearance(REMOTE_FOLDER_PATH, "csv", 10, policyNumber).get(0);
		String fileName = neededFilePath.replace(REMOTE_FOLDER_PATH, "");

		RemoteHelper.downloadFile(neededFilePath, LOCAL_FOLDER_PATH + fileName);

		//String fileName = "20171222_180434_DSB_E_PASSYS_DSBCTRL_7025_D.csv";
		List<DisbursementEngineHelper.DisbursementFile> listOfRecordsInFile = DisbursementEngineHelper.readDisbursementFile(LOCAL_FOLDER_PATH + fileName);
		DisbursementEngineHelper.DisbursementFile neededLine = null;
		for (DisbursementEngineHelper.DisbursementFile s : listOfRecordsInFile) {
			if (s.getAgreementNumber().equals(policyNumber)) {
				neededLine = s;
			}
		}
		CustomAssert.assertTrue(neededLine.getRecordType().equals("D"));
		//TODO missing config in PAS18.1
		//neededLine.getRequestRefereceId().equals(transactionID);
		CustomAssert.assertTrue(neededLine.getRefundType().equals(refundType));
		CustomAssert.assertTrue(neededLine.getRefundMethod().equals(refundMethod));
		CustomAssert.assertTrue(neededLine.getIssueDate().equals(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MMddyyyy"))));
		CustomAssert.assertTrue(neededLine.getAgreementNumber().equals(policyNumber));
		CustomAssert.assertTrue(neededLine.getAgreementSourceSystem().equals("PAS"));
		CustomAssert.assertTrue(neededLine.getProductType().equals("HO"));
		CustomAssert.assertTrue(neededLine.getCompanyId().equals("4WUIC"));
		CustomAssert.assertFalse(neededLine.getInsuredFirstName().isEmpty());
		CustomAssert.assertFalse(neededLine.getInsuredLastName().isEmpty());
		//TODO update once the deceased indicator is implemented
		//CustomAssert.assertTrue(neededLine.deceasedNamedInsuredFlag.equals("Y"));
		CustomAssert.assertTrue(neededLine.getPolicyState().equals("VA"));
		CustomAssert.assertTrue(neededLine.getRefundAmount().equals(refundAmount+".00"));
		CustomAssert.assertTrue(neededLine.getPayeeName().equals(neededLine.getInsuredFirstName() + " " + neededLine.getInsuredLastName()));
		CustomAssert.assertFalse(neededLine.getPayeeStreetAddress1().isEmpty());
		CustomAssert.assertFalse(neededLine.getPayeeCity().isEmpty());
		CustomAssert.assertFalse(neededLine.getPayeeState().isEmpty());
		CustomAssert.assertFalse(neededLine.getPayeeZip().isEmpty());
		//TODO check CustomAssert.assertTrue(neededLine.getInsuredEmailId().contains("@"));
		CustomAssert.assertTrue(neededLine.getCheckNumber().equals(""));
		CustomAssert.assertTrue(neededLine.getPrinterIdentificationCode().equals("FFD"));
		CustomAssert.assertTrue(neededLine.getRefundReason().equals("Overpayment"));
		CustomAssert.assertTrue(neededLine.getRefundReasonDescription().equals(""));
		CustomAssert.assertTrue(neededLine.getReferencePaymentTransactionNumber().equals(""));
		//TODO missing config in PAS18.1
		//CustomAssert.assertTrue(neededLine.geteRefundEligible().equals("Y"));
	}

}
