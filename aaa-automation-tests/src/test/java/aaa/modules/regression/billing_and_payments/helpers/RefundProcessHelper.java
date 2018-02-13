package aaa.modules.regression.billing_and_payments.helpers;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.*;
import static aaa.modules.regression.billing_and_payments.auto_ss.functional.preconditions.TestRefundProcessPreConditions.REFUND_DOCUMENT_GENERATION_CONFIGURATION_CHECK_SQL;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.DisbursementEngineHelper;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.db.DbAwaitHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.AdvancedAllocationsActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.billing_and_payments.template.PolicyBilling;
import aaa.toolkit.webdriver.customcontrols.AddPaymentMethodsMultiAssetList;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.waiters.Waiters;

public class RefundProcessHelper extends PolicyBilling {

	private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
	private static final String REFUND_GENERATION_FOLDER = "DSB_E_PASSYS_DSBCTRL_7025_D/outbound/";
	private static final String REFUND_GENERATION_FOLDER_PATH = PropertyProvider.getProperty(CustomTestProperties.JOB_FOLDER) + REFUND_GENERATION_FOLDER;
	private static final String LOCAL_FOLDER_PATH = "src/test/resources/stubs/";
	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
	private AdvancedAllocationsActionTab advancedAllocationsActionTab = new AdvancedAllocationsActionTab();
	private BillingAccount billingAccount = new BillingAccount();
	private TestData tdBilling = testDataManager.billingAccount;
	private static final Optional<String> NOT_VALIDATE_CHECK_NUMBER = Optional.empty();
	private static final Optional<String> NOT_VALIDATE_CHECK_DATE = Optional.empty();
	private static final Optional<Boolean> NOT_VALIDATE_PAYEENAME = Optional.empty();
	private static final Optional<Boolean> NOT_VALIDATE_TRANSACTIONID = Optional.empty();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

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
		//TODO failing because of LastPaymentMethodStub configuration and tolerance limit. Will work when we will be updating stub data on the fly.
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

	/**
	 * *@author Viktoria Lutsenko
	 * *@name Process refund successfully
	 * *@scenario
	 * 1. Put mock file with SUCC result to DSB_E_DSBCTRL_PASSYS_7035_D folder
	 * 2. Run AAA_REFUND_DISBURSEMENT_RECEIVE_INFO_JOB.
	 * *@details
	 */
	public void processedRefundGeneration(boolean isManual, String paymentMethod, String billingAccountNumber, String policyNumber) {
		if (isManual) {
			getResponseFromPC(paymentMethod, billingAccountNumber, policyNumber, "M", "SUCC", "DSB_E_DSBCTRL_PASSYS_7035_D");
		} else {
			getResponseFromPC(paymentMethod, billingAccountNumber, policyNumber, "R", "SUCC", "DSB_E_DSBCTRL_PASSYS_7035_D");
		}
	}

	/**
	 * *@author Viktoria Lutsenko
	 * *@name Issue automatically manual/automated refunds
	 * *@scenario
	 * 1. Move time to +1 day.
	 * 2. Run AAA_REFUND_GENERATION_ASYNC_JOB
	 * *@details
	 */
	public void issuedAutomatedRefundGeneration(String policyNumber) {
		//TODO workaround for Time-setter parallel execution
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(2));
		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	}

	/**
	 * *@author Viktoria Lutsenko
	 * *@name Void refunds automatically
	 * *@scenario
	 * 1. Put mock file with ERR result to DSB_E_DSBCTRL_PASSYS_7036_D folder
	 * 2. Run AAA_REFUNDS_DISBURSMENT_REJECTIONS_ASYNC_JOB
	 * *@details
	 */
	public void voidedAutomatedRefundGeneration(boolean isManual, String paymentMethod, String billingAccountNumber, String policyNumber) {
		if (isManual) {
			getResponseFromPC(paymentMethod, billingAccountNumber, policyNumber, "M", "ERR", "DSB_E_DSBCTRL_PASSYS_7036_D");
		} else {
			getResponseFromPC(paymentMethod, billingAccountNumber, policyNumber, "R", "ERR", "DSB_E_DSBCTRL_PASSYS_7036_D");
		}
	}

	/**
	 * *@author Megha Gubbala
	 * *@name Enter Too much and get message
	 * *@scenario
	 * 1. Create a debit card, credit card and ACH refund more than last payment method amount.
	 * 2. Verify message saying The amount you entered exceeds the maximum amount for this payment method.
	 * 3. Create a debit card, credit card and ACH refund same as last payment method amount.
	 * 4. Verify not getting message saying The amount you entered exceeds the maximum amount for this payment method.
	 * *@details
	 */
	public void manualRefundAmountMessageVerify(String amount, String paymentMethodMessage) {

		billingAccount.refund().start();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).setValue(paymentMethodMessage);
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).setValue(new Dollar(amount).add(0.01).toString());
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_AMOUNT_ERROR_MESSAGE.getLabel(), StaticElement.class).verify.value("The amount you entered exceeds the maximum amount for this payment method.");

		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).setValue(amount);
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_AMOUNT_ERROR_MESSAGE.getLabel(), StaticElement.class).verify.value("");
		acceptPaymentActionTab.submitTab();
	}

	/**
	 * *@author Viktoria Lutsenko
	 * *@name Creation of manual refund
	 * *@scenario
	 * 1. Without allocations:
	 * - initiate manual refund
	 * - verify default values for refund fields
	 * - verify static text about available refund amount.
	 * 2. With allocations:
	 * - perform payment with allocations
	 * - create manual refund with allocations
	 * *@details
	 */
	public void unissuedManualRefundGeneration(Optional<String> amountPaymentTypeStub, String billingAccountNumber, String paymentMethodMessage, Map<String, String> refund, boolean isCheck,
			int transactionNumber, boolean withAllocation) {

		if (!withAllocation) {
			billingAccount.refund().start();
			manualRefundDefaultValues(billingAccountNumber, paymentMethodMessage, isCheck, transactionNumber);
			acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).setValue(refund.get(AMOUNT));

			if (!isCheck) {
				//PAS-1937 Start
				acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD_MESSAGE_TABLE.getLabel(), StaticElement.class).getValue();
				acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD_MESSAGE_TABLE.getLabel(), StaticElement.class).verify
						.value("$" + amountPaymentTypeStub.get() + " is the maximum amount available for this payment method.");
				//PAS-1937 End
			}
			acceptPaymentActionTab.submitTab();
		} else {
			performPaymentWithAllocation(refund);
			billingAccount.refund().start();
			acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).setValue(paymentMethodMessage);
			acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).setValue(refund.get(AMOUNT));
			BillingSummaryPage.linkAdvancedAllocation.click();
			advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.NET_PREMIUM.getLabel(), TextBox.class).setValue(getAllocationAmount(refund));
			advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.OTHER.getLabel(), TextBox.class).setValue(getAllocationAmount(refund));
			advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.POLICY_FEE.getLabel(), TextBox.class).setValue(getAllocationAmount(refund));
			advancedAllocationsActionTab.submitTab();
		}

	}

	private String getAllocationAmount(Map<String, String> refund) {
		return new Dollar(refund.get(AMOUNT)).divide(3).toString();
	}

	/**
	 * *@author Viktoria Lutsenko
	 * *@name Creation of automated refund
	 * *@scenario
	 * 1. Without allocations:
	 * - Perform overpayment with check without allocations.
	 * - Run AAA_REFUND_GENERATION_ASYNC_JOB
	 * 2. With allocations:
	 * - Perform overpayment with check with allocations.
	 * - Run AAA_REFUND_GENERATION_ASYNC_JOB
	 * *@details
	 */
	public void unissuedAutomatedRefundGeneration(String policyNumber, Map<String, String> refund, boolean withAllocation) {
		if (!withAllocation) {
			Dollar totalDue = BillingSummaryPage.getTotalDue();
			billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue.add(new Dollar(refund.get(AMOUNT))));
		} else {
			performPaymentWithAllocation(refund);
		}
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(1));
		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	}

	/**
	 * *@author Viktoria Lutsenko
	 * *@name Payment with allocations
	 * *@scenario
	 * 1. Initiate payment.
	 * 2. Go to Advanced allocations.
	 * 3. Add allocations to payment.
	 * *@details
	 */
	private void performPaymentWithAllocation(Map<String, String> refund) {
		billingAccount.acceptPayment().start();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).setValue("Cash");
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).setValue(refund.get(AMOUNT));
		BillingSummaryPage.linkAdvancedAllocation.click();
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.NET_PREMIUM.getLabel(), TextBox.class).setValue(getAllocationAmount(refund));
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.OTHER.getLabel(), TextBox.class).setValue(getAllocationAmount(refund));
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.POLICY_FEE.getLabel(), TextBox.class).setValue(getAllocationAmount(refund));
		advancedAllocationsActionTab.submitTab();
	}

	/**
	 * *@author Viktoria Lutsenko
	 * *@name Void manually automated/manual refunds
	 * *@scenario
	 * 1. Click on Void link in Actions of refund.
	 * *@details
	 */
	public void voidedManualRefundGeneration(Map<String, String> refund) {
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(ACTION).controls.links.get("Void").click();
		Page.dialogConfirmation.confirm();
	}

	/**
	 * *@author Viktoria Lutsenko
	 * *@name Unissued refund verification
	 * *@scenario
	 * 1. Unissued refund has Approved status.
	 * 2. Verify details of unissued refund.
	 * 3. Available action for unissued refund - Void.
	 * *@details
	 */
	public void unissuedRefundVerification(String billingAccountNumber, String paymentMethodMessage, Map<String, String> refund, boolean isCheck, int transactionNumber) {
		String status = "Approved";
		unprocessedSuccessfullyRefundVerification(billingAccountNumber, paymentMethodMessage, refund, isCheck, transactionNumber);
		refundActions(refund, status, "Void");
	}

	/**
	 * *@author Viktoria Lutsenko
	 * *@name Voided refund verification
	 * *@scenario
	 * 1. Check manual/automated refunds can't be voided automatically.
	 * 2. If manual refund was voided, and available amount more or equal to refund amount  - new automated check refund is generated.
	 * 3. If manual refund was voided, and available amount less than refund amount  - new automated check refund isn't generated.
	 * 4. If manual refund was voided, and available amount less than refund amount  - new automated check refund isn't generated.
	 * 5. If manual refund was voided, and available amount the same as refund amount  - new automated check refund is generated.
	 * *@details
	 */
	public void voidedRefundVerification(Boolean voidedManual, String billingAccountNumber, String paymentMethodMessage, Map<String, String> refund, boolean isCheck,
			int transactionNumber, boolean withAllocation) {
		if (voidedManual) {
			String statusRefund = "Voided";
			String statusAdjustment = "Applied";
			String adjustmentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
			Map<String, String> adjustment = getRefundMap(adjustmentDate, "Adjustment", "Refund Payment Voided", new Dollar(refund.get(AMOUNT)).negate(), statusAdjustment);
			Map<String, String> refundVoided = new HashMap<>(refund);
			refundVoided.put(STATUS, statusRefund);
			CustomAssert.assertEquals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(adjustment).getIndex(), 1);
			CustomAssert.assertEquals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoided).getIndex(), 2);

			unprocessedSuccessfullyRefundVerification(billingAccountNumber, paymentMethodMessage, refundVoided, isCheck, transactionNumber);
			refundActions(refundVoided, statusRefund);
			refundActions(adjustment, statusAdjustment);
		} else {
			if ("Check".equals(paymentMethodMessage)) {
				issuedRefundVerification(billingAccountNumber, paymentMethodMessage, refund, isCheck, transactionNumber);
			} else {
				String statusRefundVoided = "Voided";
				String statusRefundApproved = "Approved";
				String statusAdjustment = "Applied";
				String adjustmentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
				Map<String, String> adjustment = getRefundMap(adjustmentDate, "Adjustment", "Refund Payment Voided", new Dollar(refund.get(AMOUNT)).negate(), statusAdjustment);
				Map<String, String> refundVoided = new HashMap<>(refund);
				refundVoided.put(STATUS, statusRefundVoided);
				Map<String, String> refundApproved = new HashMap<>(refund);
				refundApproved.put(STATUS, statusRefundApproved);
				refundApproved.put(SUBTYPE_REASON, "Automated Refund");
				CustomAssert.assertEquals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundApproved).getIndex(), 1);
				CustomAssert.assertEquals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(adjustment).getIndex(), 2);
				CustomAssert.assertEquals(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoided).getIndex(), 3);

				unprocessedSuccessfullyRefundVerification(billingAccountNumber, paymentMethodMessage, refundVoided, isCheck, transactionNumber);
				unprocessedSuccessfullyRefundVerification(billingAccountNumber, "Check", refundApproved, true, 0);
				refundActions(refundVoided, statusRefundVoided);
				refundActions(adjustment, statusAdjustment);
				refundActions(refundApproved, statusRefundApproved, "Void");
				if (withAllocation) {
					checkRefundAllocationAmount(refundApproved);
				}
			}
		}
	}

	/**
	 * *@author Viktoria Lutsenko
	 * *@name Check refund allocations in generated automated check refund after void transaction
	 * *@scenario
	 * 1. Open AdvancedAllocation and verify that allocation are the same as in voided refund
	 * *@details
	 */
	private void checkRefundAllocationAmount(Map<String, String> refund) {
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(TYPE).controls.links.get(1).click();
		BillingSummaryPage.linkAdvancedAllocation.click();
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.PRODUCT_SUB_TOTAL.getLabel(), TextBox.class).verify.value(refund.get(AMOUNT));
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.TOTAL_AMOUNT.getLabel(), TextBox.class).verify.value(refund.get(AMOUNT));
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.NET_PREMIUM.getLabel(), TextBox.class).verify
				.value(getAllocationAmount(refund));
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.OTHER.getLabel(), TextBox.class).verify.value(getAllocationAmount(refund));
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.POLICY_FEE.getLabel(), TextBox.class).verify
				.value(getAllocationAmount(refund));
		advancedAllocationsActionTab.back();
		acceptPaymentActionTab.back();
	}

	/**
	 * *@author Viktoria Lutsenko
	 * *@name Issued refund verification
	 * *@scenario
	 * 1. Status of refund - 'Issue'
	 * 2. Check refund details.
	 * 3. Available actions for issued refund - Void/Clear for check and none for others.
	 * *@details
	 */
	public void issuedRefundVerification(String billingAccountNumber, String paymentMethodMessage, Map<String, String> refund, boolean isCheck, int transactionNumber) {
		String status = "Issued";
		Map<String, String> refundIssued = new HashMap<>(refund);
		refundIssued.put(STATUS, status);
		String policyNumber = BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(1).getValue();
		unprocessedSuccessfullyRefundVerification(billingAccountNumber, paymentMethodMessage, refundIssued, isCheck, transactionNumber);
		if (isCheck) {
			refundActions(refundIssued, status, "Void", "Clear");
			checkRefundDocumentInDb(getState(), policyNumber);
		} else {
			refundActions(refundIssued, status);
		}
	}

	/**
	 * *@author Viktoria Lutsenko
	 * *@name Processed refund verification
	 * *@scenario
	 * 1. Status of refund = 'Issued'.
	 * 2. Verify refund details.
	 * 3. Available actions for refund - Void/Clear for check and none for others.
	 * *@details
	 */
	public void processedRefundVerification(String billingAccountNumber, String paymentMethodMessage, Map<String, String> refund, boolean isCheck, int transactionNumber) {
		String status = "Issued";
		Map<String, String> refundIssued = new HashMap<>(refund);
		refundIssued.put(STATUS, status);
		processedSuccessfullyRefundVerification(billingAccountNumber, paymentMethodMessage, refundIssued, isCheck, transactionNumber);
		if (isCheck) {
			refundActions(refundIssued, status, "Void", "Clear");
		} else {
			refundActions(refundIssued, status);
		}
	}

	private void unprocessedSuccessfullyRefundVerification(String billingAccountNumber, String paymentMethodMessage, Map<String, String> refund, boolean isCheck, int transactionNumber) {
		Dollar amount = new Dollar(refund.get(AMOUNT));
		Waiters.SLEEP(6000).go();
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(TYPE).controls.links.get(1).click();
		if (isCheck) {
			refundDetailsPresence(true, true, true, true);
			refundDetailsValues(billingAccountNumber, paymentMethodMessage, Optional.of(Boolean.TRUE), Optional.of("Processing"), Optional.ofNullable(refund.get(TRANSACTION_DATE)), Optional
					.of(Boolean.TRUE), amount, transactionNumber);
		} else {
			refundDetailsPresence(true, false, false, false);
			refundDetailsValues(billingAccountNumber, paymentMethodMessage, Optional
					.of(Boolean.TRUE), NOT_VALIDATE_CHECK_NUMBER, NOT_VALIDATE_CHECK_DATE, NOT_VALIDATE_PAYEENAME, amount, transactionNumber);
		}
		acceptPaymentActionTab.back();
	}

	private void processedSuccessfullyRefundVerification(String billingAccountNumber, String paymentMethodMessage, Map<String, String> refund, boolean isCheck, int transactionNumber) {
		Dollar amount = new Dollar(refund.get(AMOUNT));
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(TYPE).controls.links.get(1).click();
		if (isCheck) {
			refundDetailsPresence(true, true, true, true);
			refundDetailsValues(billingAccountNumber, paymentMethodMessage, Optional.of(Boolean.TRUE), Optional.of("123456789"), Optional.ofNullable(refund.get(TRANSACTION_DATE)), Optional
					.of(Boolean.TRUE), amount, transactionNumber);
		} else {
			refundDetailsPresence(true, false, false, false);
			refundDetailsValues(billingAccountNumber, paymentMethodMessage, Optional
					.of(Boolean.TRUE), NOT_VALIDATE_CHECK_NUMBER, NOT_VALIDATE_CHECK_DATE, NOT_VALIDATE_PAYEENAME, amount, transactionNumber);
		}
		acceptPaymentActionTab.back();
	}

	private void manualRefundDefaultValues(String billingAccountNumber, String paymentMethodMessage, boolean isCheck, int transactionNumber) {
		//PAS-1462 start
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).setValue(paymentMethodMessage);
		if (isCheck) {
			refundDetailsPresence(false, false, false, true);
			refundDetailsValues(billingAccountNumber, paymentMethodMessage, NOT_VALIDATE_TRANSACTIONID, NOT_VALIDATE_CHECK_NUMBER, NOT_VALIDATE_CHECK_DATE, Optional
					.of(Boolean.TRUE), null, transactionNumber);
			acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME.getLabel(), TextBox.class).verify.enabled(false);
		} else {
			refundDetailsPresence(false, false, false, false);
			refundDetailsValues(billingAccountNumber, paymentMethodMessage, NOT_VALIDATE_TRANSACTIONID, NOT_VALIDATE_CHECK_NUMBER, NOT_VALIDATE_CHECK_DATE, NOT_VALIDATE_PAYEENAME, null, transactionNumber);
		}
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.enabled();
		AddPaymentMethodsMultiAssetList.buttonAddUpdatePaymentMethod.verify.present(false);
		//PAS-1462 end
	}

	public String policyCreation() {
		mainApp().open();
		createCustomerIndividual();
		getPolicyType().get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("policyNumber: {}", policyNumber);
		return policyNumber;
	}

	public Map<String, String> getRefundMap(String refundDate, String type, String subtypeReason, Dollar amount, String status) {
		return ImmutableMap.of(TRANSACTION_DATE, refundDate,
				TYPE, type,
				SUBTYPE_REASON, subtypeReason,
				AMOUNT, amount.toString(),
				STATUS, status);
	}

	private void refundDetailsPresence(boolean transactionIdPresent, boolean checkNumberPresent, boolean checkDatePresent, boolean payeeNamePresent) {
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).isPresent();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).isPresent();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).verify.present(transactionIdPresent);
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).verify.present(checkNumberPresent);
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE.getLabel(), TextBox.class).verify.present(checkDatePresent);
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME.getLabel(), TextBox.class).verify.present(payeeNamePresent);
	}

	private void refundDetailsValues(String billingAccountNumber, String paymentMethodValue, Optional<Boolean> transactionIdPresent, Optional<String> checkNumberValue,
			Optional<String> refundDateValue, Optional<Boolean> payeeNameNotEmpty, Dollar amountValue, int transactionNumber) {
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).verify.value(paymentMethodValue);
		String stringAmount = "";
		if (amountValue != null) {
			stringAmount = amountValue.toString();
		}
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.value(stringAmount);
		//PAS-6615 start
		transactionIdPresent.ifPresent(p -> {
					if (transactionIdPresent.get()) {
						CustomAssert.assertEquals("TranzactionID in DB is different from TranzactionID on UI", getRefundTransactionIDFromDB(billingAccountNumber, transactionNumber),
								acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).getValue());
					}
				}
		);
		//PAS-6615 end
		checkNumberValue.ifPresent(p ->
				acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).verify.value(p));
		refundDateValue.ifPresent(p -> acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE.getLabel(), TextBox.class).verify.value(p));
		payeeNameNotEmpty.ifPresent(p -> CustomAssert
				.assertEquals(p.booleanValue(), !acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME.getLabel(), TextBox.class).getValue()
						.isEmpty()));
	}

	private void refundActions(Map<String, String> refund, String status, String... expectedActions) {
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(STATUS).verify.value(status);
		int counter;
		for (int i = 0; ; i++) {
			try {
				BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(ACTION).controls.links.get(i + 1).getValue();
			} catch (Exception e) {
				counter = i;
				break;
			}
		}
		CustomAssert.assertEquals("Not match number of actions", expectedActions.length, counter);
		for (int i = 0; i < expectedActions.length; i++) {
			BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(ACTION).controls.links.get(i + 1).verify.value(expectedActions[i]);
		}
	}

	private String getRefundTransactionIDFromDB(String billingAccountNumber, int i) {
		return DBService.get().getRows("select TRANSACTIONNUMBER from BILLINGTRANSACTION "
				+ "where account_id = (select id from BILLINGACCOUNT where ACCOUNTNUMBER = '" + billingAccountNumber + "') "
				+ "order by CREATIONDATE desc").get(i).get("TRANSACTIONNUMBER");
	}

	private Map<String, String> getLedgerEntryFromDB(String transactionID, String billingAccountNumber, String entryType) {
		return DBService.get().getRows("select le.LEDGERACCOUNTNO, le.TRANSACTIONTYPE,le.BILLINGPAYMENTMETHOD,le.TRANSACTIONID,le.ENTRYAMT , le.ENTRYTYPE\n"
				+ " from LEDGERENTRY le join LEDGERTRANSACTION lt on lt.ID = le.LEDGERTRANSACTION_ID  \n"
				+ " where BILLINGACCOUNTNUMBer = '" + billingAccountNumber + "'  and TRANSACTIONID ='" + transactionID + "'  and entrytype = '" + entryType + "'").get(0);
	}

	public void getSubLedgerInformation(String billingAccountNumber, String amount, String transactionType, String billingPaymentMethod, boolean isVoided, boolean isRegenerated) {
		if (!isVoided) {
			String transactionID = getRefundTransactionIDFromDB(billingAccountNumber, 0);
			Map<String, String> ledgerEntryCredit = getLedgerEntryFromDB(transactionID, billingAccountNumber, "CREDIT");
			Map<String, String> ledgerEntryDebit = getLedgerEntryFromDB(transactionID, billingAccountNumber, "DEBIT");
			subLedgerVerification(amount, transactionType, "1060", billingPaymentMethod, ledgerEntryCredit);
			subLedgerVerification(amount, transactionType, "1044", billingPaymentMethod, ledgerEntryDebit);
		} else {
			if (isRegenerated) {
				String transactionID = getRefundTransactionIDFromDB(billingAccountNumber, 1);
				Map<String, String> ledgerEntryCredit = getLedgerEntryFromDB(transactionID, billingAccountNumber, "CREDIT");
				Map<String, String> ledgerEntryDebit = getLedgerEntryFromDB(transactionID, billingAccountNumber, "DEBIT");
				subLedgerVerification(amount, transactionType, "1044", billingPaymentMethod, ledgerEntryCredit);
				subLedgerVerification(amount, transactionType, "1060", billingPaymentMethod, ledgerEntryDebit);
			} else {
				String transactionID = getRefundTransactionIDFromDB(billingAccountNumber, 0);
				Map<String, String> ledgerEntryCredit = getLedgerEntryFromDB(transactionID, billingAccountNumber, "CREDIT");
				Map<String, String> ledgerEntryDebit = getLedgerEntryFromDB(transactionID, billingAccountNumber, "DEBIT");
				subLedgerVerification(amount, transactionType, "1044", billingPaymentMethod, ledgerEntryCredit);
				subLedgerVerification(amount, transactionType, "1060", billingPaymentMethod, ledgerEntryDebit);
			}
		}
	}

	private void subLedgerVerification(String amount, String transactionType, String ledgerAccountNumber, String billingPaymentMethod, Map<String, String> ledgerEntry) {
		CustomAssert.assertEquals(amount, ledgerEntry.get("ENTRYAMT"));
		CustomAssert.assertEquals(transactionType, ledgerEntry.get("TRANSACTIONTYPE"));
		CustomAssert.assertEquals(ledgerAccountNumber, ledgerEntry.get("LEDGERACCOUNTNO"));
		CustomAssert.assertEquals(billingPaymentMethod, ledgerEntry.get("BILLINGPAYMENTMETHOD"));
	}

	private static void checkRefundDocumentInDb(String state, String policyNumber) {
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
			CustomAssert.assertEquals(Integer.parseInt(DBService.get().getValue(query2).get()), 1);
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
	private void getResponseFromPC(String paymentMethod, String billingAccountNumber, String policyNumber, String refundMethod, String refundStatus, String folderName) {
		String transactionID = getRefundTransactionIDFromDB(billingAccountNumber, 0);

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
						.setRefundAmount("30.00")
						.setAccountLast4("1542")
						.setAccountType("CHKG");
				break;
			case "Credit card":
				builder = builder.setTransactionNumber(transactionID)
						.setPaymentType("CRDC")
						.setRefundAmount("10.00")
						.setAccountLast4("4113")
						.setAccountType("VISA")
						.setCardSubType("Credit");
				break;
			case "Debit card":
				builder = builder.setTransactionNumber(transactionID)
						.setPaymentType("CRDC")
						.setRefundAmount("21.99")
						.setAccountLast4("4444")
						.setAccountType("MASTR")
						.setCardSubType("Debit");
				break;
			case "Check":
				builder = builder.setTransactionNumber(transactionID)
						.setPaymentType("CHCK")
						.setRefundAmount("10.01")
						.setCheckNumber("123456789");
				break;
			default:
				log.info("never reached");
		}

		File disbursementEngineFile = DisbursementEngineHelper.createFile(builder, folderName);
		DisbursementEngineHelper.copyFileToServer(disbursementEngineFile, folderName);
		if ("ERR".equals(refundStatus)) {
			//TODO workaround for Time-setter parallel execution
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));
			JobUtils.executeJob(Jobs.aaaRefundsDisbursementRejectionsAsyncJob);
		} else if ("SUCC".equals(refundStatus)) {
			//TODO workaround for Time-setter parallel execution
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(2));
			JobUtils.executeJob(Jobs.aaaRefundDisbursementRecieveInfoJob);
		}
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	}

}
