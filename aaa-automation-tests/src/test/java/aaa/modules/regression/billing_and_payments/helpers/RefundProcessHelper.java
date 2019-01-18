package aaa.modules.regression.billing_and_payments.helpers;

import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.*;
import static org.assertj.core.api.Assertions.fail;
import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.config.CsaaTestProperties;
import aaa.helpers.TimePoints;
import aaa.helpers.billing.DisbursementEngineHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.AdvancedAllocationsActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.regression.billing_and_payments.template.PolicyBilling;
import aaa.toolkit.webdriver.customcontrols.AddPaymentMethodsMultiAssetList;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.waiters.Waiters;

public class RefundProcessHelper extends PolicyBilling {

	private static final String REFUND_GENERATION_FOLDER = "DSB_E_PASSYS_DSBCTRL_7025_D/outbound/";
	private static final String REFUND_GENERATION_FOLDER_PATH = PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER) + REFUND_GENERATION_FOLDER;
	private static final String REFUND_VOID_GENERATION_FOLDER = "DSB_E_PASSYS_DSBCTRL_7026_D/outbound/";
	private static final String REFUND_VOID_GENERATION_FOLDER_PATH = PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER) + REFUND_VOID_GENERATION_FOLDER;
	private static final String LOCAL_FOLDER_PATH = "src/test/resources/stubs/";
	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
	private AdvancedAllocationsActionTab advancedAllocationsActionTab = new AdvancedAllocationsActionTab();
	private BillingAccount billingAccount = new BillingAccount();
	private TestData tdBilling = testDataManager.billingAccount;
	private static final Optional<String> NOT_VALIDATE_CHECK_NUMBER = Optional.empty();
	private static final Optional<String> NOT_VALIDATE_CHECK_DATE = Optional.empty();
	private static final Optional<Boolean> NOT_VALIDATE_PAYEENAME = Optional.empty();
	private static final Optional<Boolean> NOT_VALIDATE_TRANSACTIONID = Optional.empty();

	//OSI: the class is shared between products. There should be no single PolicyType defined
/*	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}*/

	@SuppressWarnings("Unchecked")
	public void refundDebug(String policyNumber, String refundType, String refundMethod, String productType, String companyId, String deceasedNamedInsuredFlag, String policyState, String refundAmount,
			String email, String refundEligible) {
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains("Type", "Refund").getCell(TYPE).controls.links.get("Refund").click();
		String transactionID = acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).getValue();
		acceptPaymentActionTab.back();

		CustomSoftAssertions.assertSoftly(softly -> {
			//TODO doesn't work in VDMs
	/*        RemoteHelper.waitForFilesAppearance(REFUND_GENERATION_FOLDER_PATH, 10, policyNumber);
	        String neededFilePath = RemoteHelper.waitForFilesAppearance(REFUND_GENERATION_FOLDER_PATH, "csv", 10, policyNumber).get(0);
	        String fileName = neededFilePath.replace(REFUND_GENERATION_FOLDER_PATH, "");

	        RemoteHelper.downloadFile(neededFilePath, LOCAL_FOLDER_PATH + fileName);*/
			String fileName = "20180310_014138_DSB_E_PASSYS_DSBCTRL_7025_D.csv";
			List<DisbursementEngineHelper.DisbursementFile> listOfRecordsInFile = DisbursementEngineHelper.readDisbursementFile(LOCAL_FOLDER_PATH + fileName);
			DisbursementEngineHelper.DisbursementFile neededLine = null;
			for (DisbursementEngineHelper.DisbursementFile s : listOfRecordsInFile) {
				if (s.getAgreementNumber().equals(policyNumber)) {
					neededLine = s;
				}
			}
			softly.assertThat(neededLine.getRecordType()).isEqualTo("D");
			softly.assertThat(neededLine.getRequestRefereceId()).isEqualTo(transactionID);
			softly.assertThat(neededLine.getRefundType()).isEqualTo(refundType);
			// RefundMethod = 'CHCK' - check, 'EFT' - eft, 'CRDC' - credit/debit card
			if (refundMethod.contains("CHCK")) {
				softly.assertThat(neededLine.getRefundMethod()).isEqualTo(refundMethod);
			} else if (refundMethod.contains("ACH")) {
				softly.assertThat(neededLine.getRefundMethod()).isEqualTo("EFT");
			} else if (refundMethod.contains("Card")) {
				softly.assertThat(neededLine.getRefundMethod()).isEqualTo("CRDC");
			} else {
				softly.assertThat(neededLine.getRefundMethod()).isEqualTo(refundMethod);
			}
			softly.assertThat(neededLine.getIssueDate()).isEqualTo(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MMddyyyy")));
			softly.assertThat(neededLine.getAgreementNumber()).isEqualTo(policyNumber);
			softly.assertThat(neededLine.getAgreementSourceSystem()).isEqualTo("PAS");
			softly.assertThat(neededLine.getProductType()).isEqualTo(productType);
			softly.assertThat(neededLine.getCompanyId()).isEqualTo(companyId);
			softly.assertThat(neededLine.getInsuredFirstName()).isNotEmpty();
			softly.assertThat(neededLine.getInsuredLastName()).isNotEmpty();
			//TODO update once the deceased indicator is implemented
			softly.assertThat(neededLine.getDeceasedNamedInsuredFlag()).isEqualTo(deceasedNamedInsuredFlag);
			if (null == policyState) {
				softly.assertThat(neededLine.getPolicyState()).isNotEmpty();
			} else {
				softly.assertThat(neededLine.getPolicyState()).isEqualTo(policyState);
			}
			softly.assertThat(neededLine.getRefundAmount()).isEqualTo(refundAmount + ".00");
			softly.assertThat(neededLine.getPayeeName()).isEqualTo(neededLine.getInsuredFirstName() + " " + neededLine.getInsuredLastName());
			softly.assertThat(neededLine.getPayeeStreetAddress1()).isNotEmpty();
			softly.assertThat(neededLine.getPayeeCity()).isNotEmpty();
			softly.assertThat(neededLine.getPayeeState()).isNotEmpty();
			softly.assertThat(neededLine.getPayeeZip()).isNotEmpty();
			softly.assertThat(neededLine.getInsuredEmailId()).isEqualTo(email);
			softly.assertThat(neededLine.getCheckNumber()).isEqualTo("");
			softly.assertThat(neededLine.getPrinterIdentificationCode()).isEqualTo("FFD");
			softly.assertThat(neededLine.getRefundReason()).isEqualTo("Overpayment");
			softly.assertThat(neededLine.getRefundReasonDescription()).isEqualTo("");
			if (refundMethod.contains("CHCK")) {
				softly.assertThat(neededLine.getReferencePaymentTransactionNumber()).isEqualTo("");
			} else {
				softly.assertThat(neededLine.getReferencePaymentTransactionNumber()).isNotEmpty();
			}
			softly.assertThat(neededLine.geteRefundEligible()).isEqualTo(refundEligible);
		});
	}

	@SuppressWarnings("Unchecked")
	public void refundRecordInFileCheck(PolicyType policyType, String policyNumber, String refundType, String refundMethod, String companyId, String deceasedNamedInsuredFlag, String policyState,
			String refundAmount, String email, String refundEligible) {

		//This 'if' condition is just refactoring.
		String productType;
		if (policyType.isAutoPolicy()) {
			productType = "PA";
		} else if (policyType.equals(PolicyType.PUP)) {
			productType = "PU";
		} else {
			productType = "HO";
		}

		//TODO waitForFilesAppearance doesn't work in VDMs
		if (!StringUtils.isEmpty(PropertyProvider.getProperty("scrum.envs.ssh")) && !"true".equals(PropertyProvider.getProperty("scrum.envs.ssh"))) {
			mainApp().open();
			SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains("Type", "Refund").getCell(TYPE).controls.links.get("Refund").click();
			CustomSoftAssertions.assertSoftly(softly -> {
				String transactionID = acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).getValue();
				acceptPaymentActionTab.back();

				//TODO doesn't work in VDMs
				RemoteHelper.get().waitForFilesAppearance(REFUND_GENERATION_FOLDER_PATH, 10, policyNumber, transactionID);
				String neededFilePath = RemoteHelper.get().waitForFilesAppearance(REFUND_GENERATION_FOLDER_PATH, "csv", 10, policyNumber).get(0);
				String fileName = neededFilePath.replace(REFUND_GENERATION_FOLDER_PATH, "");

				RemoteHelper.get().downloadFile(neededFilePath, LOCAL_FOLDER_PATH + fileName);

				List<DisbursementEngineHelper.DisbursementFile> listOfRecordsInFile = DisbursementEngineHelper.readDisbursementFile(LOCAL_FOLDER_PATH + fileName);
				DisbursementEngineHelper.DisbursementFile neededLine = null;
				for (DisbursementEngineHelper.DisbursementFile s : listOfRecordsInFile) {
					if (s.getAgreementNumber().equals(policyNumber)) {
						neededLine = s;
					}
				}
				softly.assertThat(neededLine.getRecordType()).isEqualTo("D");
				softly.assertThat(neededLine.getRequestRefereceId()).isEqualTo(transactionID);
				softly.assertThat(neededLine.getRefundType()).isEqualTo(refundType);
				// RefundMethod = 'CHCK' - check, 'EFT' - eft, 'CRDC' - credit/debit card
				if (refundMethod.contains("CHCK") || refundMethod.contains("Check")) {
					softly.assertThat(neededLine.getRefundMethod()).isEqualTo(refundMethod);
				} else if (refundMethod.contains("ACH") || refundMethod.contains("EFT")) {
					softly.assertThat(neededLine.getRefundMethod()).isEqualTo("EFT");
				} else if (refundMethod.contains("Card")) {
					softly.assertThat(neededLine.getRefundMethod()).isEqualTo("CRDC");
				} else {
					softly.assertThat(neededLine.getRefundMethod()).isEqualTo(refundMethod);
				}
				softly.assertThat(neededLine.getIssueDate()).isEqualTo(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MMddyyyy")));
				softly.assertThat(neededLine.getAgreementNumber()).isEqualTo(policyNumber);
				softly.assertThat(neededLine.getAgreementSourceSystem()).isEqualTo("PAS");
				softly.assertThat(neededLine.getProductType()).isEqualTo(productType);
				softly.assertThat(neededLine.getCompanyId()).isEqualTo(companyId);
				softly.assertThat(neededLine.getInsuredFirstName()).isNotEmpty();
				softly.assertThat(neededLine.getInsuredLastName()).isNotEmpty();
				//TODO update once the deceased indicator is implemented
				softly.assertThat(neededLine.getDeceasedNamedInsuredFlag()).isEqualTo(deceasedNamedInsuredFlag);
				if (null == policyState) {
					softly.assertThat(neededLine.getPolicyState()).isNotEmpty();
				} else {
					softly.assertThat(neededLine.getPolicyState()).isEqualTo(policyState);
				}
				softly.assertThat(neededLine.getRefundAmount()).isEqualTo(new Dollar(refundAmount).toPlaingString());
				softly.assertThat(neededLine.getPayeeName()).isEqualTo(neededLine.getInsuredFirstName() + " " + neededLine.getInsuredLastName());
				softly.assertThat(neededLine.getPayeeStreetAddress1()).isNotEmpty();
				softly.assertThat(neededLine.getPayeeCity()).isNotEmpty();
				softly.assertThat(neededLine.getPayeeState()).isNotEmpty();
				softly.assertThat(neededLine.getPayeeZip()).isNotEmpty();
				softly.assertThat(neededLine.getInsuredEmailId()).isEqualTo(email);
				softly.assertThat(neededLine.getCheckNumber()).isEqualTo("");
				softly.assertThat(neededLine.getPrinterIdentificationCode()).isEqualTo("FFD");
				softly.assertThat(neededLine.getRefundReason()).isEqualTo("Overpayment");
				softly.assertThat(neededLine.getRefundReasonDescription()).isEqualTo("");
				if (refundMethod.contains("CHCK") || refundMethod.contains("Check")) {
					softly.assertThat(neededLine.getReferencePaymentTransactionNumber()).isEqualTo("");
				} else {
					softly.assertThat(neededLine.getReferencePaymentTransactionNumber()).isNotEmpty();
				}
				softly.assertThat(neededLine.geteRefundEligible()).isEqualTo(refundEligible);
			});
		} else {
			//to make sure Automated refund is generated also on SCRUM team envs
			mainApp().open();
			SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).controls.links.get("Refund").click();
		}
	}

	@SuppressWarnings("Unchecked")
	public void refundVoidRecordInFileCheck(String policyNumber, String transactionID, String productType, String companyId, String refundAmount)
			throws IOException {
		//TODO waitForFilesAppearance doesn't work in VDMs
		if (!StringUtils.isEmpty(PropertyProvider.getProperty("scrum.envs.ssh")) && !"true".equals(PropertyProvider.getProperty("scrum.envs.ssh"))) {
			//TODO doesn't work in VDMs
			RemoteHelper.get().waitForFilesAppearance(REFUND_VOID_GENERATION_FOLDER_PATH, 10, policyNumber, transactionID);
			String neededFilePath = RemoteHelper.get().waitForFilesAppearance(REFUND_VOID_GENERATION_FOLDER_PATH, "csv", 10, policyNumber).get(0);
			String fileName = neededFilePath.replace(REFUND_VOID_GENERATION_FOLDER_PATH, "");

			RemoteHelper.get().downloadFile(neededFilePath, LOCAL_FOLDER_PATH + fileName);

			List<DisbursementEngineHelper.DisbursementVoidFile> listOfRecordsInFile = DisbursementEngineHelper.readDisbursementVoidFile(LOCAL_FOLDER_PATH + fileName);

			DisbursementEngineHelper.DisbursementVoidFile neededLine = null;
			for (DisbursementEngineHelper.DisbursementVoidFile s : listOfRecordsInFile) {
				if (s.getAgreementNumber().equals(policyNumber)) {
					neededLine = s;
				}
			}
			assertThat(neededLine.getRecordType()).isEqualTo("D");
			assertThat(neededLine.getRequestReferenceId()).isEqualTo(transactionID + "VOID");
			assertThat(neededLine.getPcReferenceId()).isEqualTo(transactionID);
			assertThat(neededLine.getRefundType()).isEqualTo("VOID");
			assertThat(neededLine.getIssueDate()).isEqualTo(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MMddyyyy")));
			assertThat(neededLine.getRefundDate()).isEqualTo(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MMddyyyy")));
			assertThat(neededLine.getAgreementNumber()).isEqualTo(policyNumber);
			assertThat(neededLine.getAgreementSourceSystem()).isEqualTo("PAS");
			assertThat(neededLine.getProductType()).isEqualTo(productType);
			assertThat(neededLine.getCompanyId()).isEqualTo(companyId);
			assertThat(neededLine.getDummy()).isEqualTo("");
			assertThat(neededLine.getRefundAmount()).isEqualTo(new Dollar(refundAmount).toPlaingString());
			assertThat(neededLine.getRefundReason()).isEqualTo("Overpayment");
		} else {
			//to make sure Automated refund is generated also on SCRUM team envs
			mainApp().open();
			SearchPage.openBilling(policyNumber);
			BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(TYPE).controls.links.get("Refund").click();
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
		CustomSoftAssertions.assertSoftly(softly -> {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
			billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), new Dollar(pendingRefundAmount));

			billingAccount.refund().manualRefundPerform(paymentMethod, approvedRefundAmount);
			softly.assertThat("Refund").isEqualTo(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue());
			approvedRefundVoid();

			billingAccount.refund().manualRefundPerform(paymentMethod, pendingRefundAmount);
			softly.assertThat("Refund").isNotEqualTo(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue());
			pendingRefundLinksCheck(softly);
			pendingRefundVoid(softly);
		});
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
	 */
	public void pas7298_pendingAutomatedRefunds(String policyNumber, String approvedRefundAmount, String pendingRefundAmount, String paymentMethod, TimePoints getTimePoints) {
		CustomSoftAssertions.assertSoftly(softly -> {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
			Dollar totalDue1 = BillingSummaryPage.getTotalDue();
			billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue1.add(new Dollar(approvedRefundAmount)));
			LocalDateTime refundDate = getTimePoints.getRefundDate(DateTimeUtils.getCurrentDateTime());
			TimeSetterUtil.getInstance().nextPhase(refundDate);
			JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);

			mainApp().open();
			SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			//BUG PAS-12336 Automated refunds are not generated
			softly.assertThat("Refund").isEqualTo(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue());
			approvedRefundVoid();

			Dollar totalDue2 = BillingSummaryPage.getTotalDue();
			billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue2.add(new Dollar(pendingRefundAmount)));
			LocalDateTime refundDate2 = getTimePoints.getRefundDate(DateTimeUtils.getCurrentDateTime());
			TimeSetterUtil.getInstance().nextPhase(refundDate2);
			JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);

			mainApp().open();
			SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE)).doesNotHaveValue("Refund");
			pendingRefundLinksCheck(softly);
			//TODO failing because of LastPaymentMethodStub configuration and tolerance limit. Will work when we will be updating stub data on the fly.
			pendingRefundPaymentMethodCheck(paymentMethod, softly);
			pendingRefundVoid(softly);
		});
	}

	private void pendingRefundPaymentMethodCheck(String paymentMethod, ETCSCoreSoftAssertions softly) {
		BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(TYPE).controls.links.get("Refund").click();
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD)).valueContains(paymentMethod);
		acceptPaymentActionTab.back();
	}

	public void approvedRefundVoid() {
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(ACTION).controls.links.get("Void").click();
		Page.dialogConfirmation.confirm();
	}

	private void lastManualPaymentDecline() {
		BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(SUBTYPE_REASON, "Manual Payment").getCell(ACTION).controls.links.get("Decline").click();
		Page.dialogConfirmation.confirm();
	}

	private void pendingRefundLinksCheck(ETCSCoreSoftAssertions softly) {
		softly.assertThat(BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(TYPE)).hasValue("Refund");
		softly.assertThat(BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(ACTION).controls.links.get(1)).hasValue("Approve");
		softly.assertThat(BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(ACTION).controls.links.get(2)).hasValue("Reject");
		softly.assertThat(BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(ACTION).controls.links.get(3)).hasValue("Void");
		softly.assertThat(BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(ACTION).controls.links.get(4)).hasValue("Change");
	}

	private void pendingRefundVoid(ETCSCoreSoftAssertions softly) {
		BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(ACTION).controls.links.get("Void").click();
		Waiters.SLEEP(10000).go();
		Page.dialogConfirmation.confirm();
		softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE)).hasValue("Adjustment");
		softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON)).hasValue("Pending Refund Payment Voided");
		softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS)).hasValue("Applied");

		softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(TYPE)).hasValue("Refund");
		softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(STATUS)).hasValue("Voided");
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
			getResponseFromPC(paymentMethod, billingAccountNumber, policyNumber, "M", "ERR", "DSB_E_DSBCTRL_PASSYS_7037_D");
		} else {
			getResponseFromPC(paymentMethod, billingAccountNumber, policyNumber, "R", "ERR", "DSB_E_DSBCTRL_PASSYS_7037_D");
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
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_AMOUNT_ERROR_MESSAGE)).hasValue("The amount you entered exceeds the maximum amount for this payment method.");

		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).setValue(amount);
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_AMOUNT_ERROR_MESSAGE)).hasValue("");
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
			acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT).setValue(refund.get(AMOUNT));

			if (!isCheck) {
				//PAS-1937 Start
				assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD_MESSAGE_TABLE))
					.hasValue("$" + amountPaymentTypeStub.get() + " is the maximum amount available for this payment method.");
				//PAS-1937 End
			}
			acceptPaymentActionTab.submitTab();
		} else {
			performPaymentWithAllocation(refund);
			billingAccount.refund().start();
			acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue(paymentMethodMessage);
			acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT).setValue(refund.get(AMOUNT));
			BillingSummaryPage.linkAdvancedAllocation.click();
			advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.NET_PREMIUM).setValue(getAllocationAmount(refund));
			advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.OTHER).setValue(getAllocationAmount(refund));
			advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.POLICY_FEE).setValue(getAllocationAmount(refund));
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
	public void unissuedAutomatedRefundGeneration(String policyNumber, LocalDateTime refundTimePoint, Map<String, String> refund, boolean withAllocation) {
		if (!withAllocation) {
			Dollar totalDue = BillingSummaryPage.getTotalDue();
			billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue.add(new Dollar(refund.get(AMOUNT))));
		} else {
			performPaymentWithAllocation(refund);
		}
		TimeSetterUtil.getInstance().nextPhase(refundTimePoint);
		//TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(1));
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
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue("Cash");
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT).setValue(refund.get(AMOUNT));
		BillingSummaryPage.linkAdvancedAllocation.click();
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.NET_PREMIUM).setValue(getAllocationAmount(refund));
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.OTHER).setValue(getAllocationAmount(refund));
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.POLICY_FEE).setValue(getAllocationAmount(refund));
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
			assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(adjustment).getIndex()).isEqualTo(1);
			assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoided).getIndex()).isEqualTo(2);

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
				assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundApproved).getIndex()).isEqualTo(1);
				assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(adjustment).getIndex()).isEqualTo(2);
				assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refundVoided).getIndex()).isEqualTo(3);

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
		assertThat(advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.PRODUCT_SUB_TOTAL)).hasValue(refund.get(AMOUNT));
		assertThat(advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.TOTAL_AMOUNT)).hasValue(refund.get(AMOUNT));
		assertThat(advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.NET_PREMIUM)).hasValue(getAllocationAmount(refund));
		assertThat(advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.OTHER)).hasValue(getAllocationAmount(refund));
		assertThat(advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.POLICY_FEE)).hasValue(getAllocationAmount(refund));
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
		unprocessedSuccessfullyRefundVerification(billingAccountNumber, paymentMethodMessage, refundIssued, isCheck, transactionNumber);
		if (isCheck) {
			refundActions(refundIssued, status, "Void", "Clear");

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
		Waiters.SLEEP(10000).go();
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
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue(paymentMethodMessage);
		if (isCheck) {
			refundDetailsPresence(false, false, false, true);
			refundDetailsValues(billingAccountNumber, paymentMethodMessage, NOT_VALIDATE_TRANSACTIONID, NOT_VALIDATE_CHECK_NUMBER, NOT_VALIDATE_CHECK_DATE, Optional
					.of(Boolean.TRUE), null, transactionNumber);
			assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME)).isEnabled(false);
		} else {
			refundDetailsPresence(false, false, false, false);
			refundDetailsValues(billingAccountNumber, paymentMethodMessage, NOT_VALIDATE_TRANSACTIONID, NOT_VALIDATE_CHECK_NUMBER, NOT_VALIDATE_CHECK_DATE, NOT_VALIDATE_PAYEENAME, null, transactionNumber);
		}
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT)).isEnabled();
		assertThat(AddPaymentMethodsMultiAssetList.buttonAddUpdatePaymentMethod).isPresent(false);
		//PAS-1462 end
	}

	public Map<String, String> getRefundMap(String refundDate, String type, String subtypeReason, Dollar amount, String status) {
		return ImmutableMap.of(TRANSACTION_DATE, refundDate,
				TYPE, type,
				SUBTYPE_REASON, subtypeReason,
				AMOUNT, amount.toString(),
				STATUS, status);
	}

	private void refundDetailsPresence(boolean transactionIdPresent, boolean checkNumberPresent, boolean checkDatePresent, boolean payeeNamePresent) {
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD)).isPresent();
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT)).isPresent();
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID)).isPresent(transactionIdPresent);
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER)).isPresent(checkNumberPresent);
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE)).isPresent(checkDatePresent);
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME)).isPresent(payeeNamePresent);
	}

	private void refundDetailsValues(String billingAccountNumber, String paymentMethodValue, Optional<Boolean> transactionIdPresent, Optional<String> checkNumberValue,
			Optional<String> refundDateValue, Optional<Boolean> payeeNameNotEmpty, Dollar amountValue, int transactionNumber) {
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD)).hasValue(paymentMethodValue);
		String stringAmount = "";
		if (amountValue != null) {
			stringAmount = amountValue.toString();
		}
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT)).hasValue(stringAmount);
		//PAS-6615 start
		transactionIdPresent.ifPresent(p -> {
					if (transactionIdPresent.get()) {
						assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID))
								.as("TranzactionID in DB is different from TranzactionID on UI").hasValue(getRefundTransactionIDFromDB(billingAccountNumber, transactionNumber));
					}
				}
		);
		//PAS-6615 end
		checkNumberValue.ifPresent(p ->
				assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER)).hasValue(p));
		refundDateValue.ifPresent(p -> assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE)).hasValue(p));
		payeeNameNotEmpty.ifPresent(p -> assertThat(p.booleanValue()).isEqualTo(!acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME).getValue()
						.isEmpty()));
	}

	private void refundActions(Map<String, String> refund, String status, String... expectedActions) {
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(STATUS)).hasValue(status);
		int counter;
		for (int i = 0; ; i++) {
			try {
				BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(ACTION).controls.links.get(i + 1).getValue();
			} catch (Exception e) {
				counter = i;
				break;
			}
		}
		assertThat(expectedActions.length).as("Not match number of actions").isEqualTo(counter);
		for (int i = 0; i < expectedActions.length; i++) {
			assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund).getCell(ACTION).controls.links.get(i + 1)).hasValue(expectedActions[i]);
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
		assertThat(amount).isEqualTo(ledgerEntry.get("ENTRYAMT"));
		assertThat(transactionType).isEqualTo(ledgerEntry.get("TRANSACTIONTYPE"));
		assertThat(ledgerAccountNumber).isEqualTo(ledgerEntry.get("LEDGERACCOUNTNO"));
		assertThat(billingPaymentMethod).isEqualTo(ledgerEntry.get("BILLINGPAYMENTMETHOD"));
	}

	/**
	 *
	 * @param paymentMethod - can be "ACH", "Credit Card", "Debit Card".
	 * @param policyNumber - current policy number
	 * @param refundMethod - can be "M" - manual or "R" - automation
	 * @param refundStatus - can be "SUCC" - success response from PC and "ERR" - failed response from PC
	 * @param folderName - name of the folder where the file will be generate e.g. "DSB_E_DSBCTRL_PASSYS_7035_D", "DSB_E_DSBCTRL_PASSYS_7037_D"
	 */
	private void getResponseFromPC(String paymentMethod, String billingAccountNumber, String policyNumber, String refundMethod, String refundStatus, String folderName) {
		String transactionID = getRefundTransactionIDFromDB(billingAccountNumber, 0);

		if (transactionID == null) {
			fail("Transaction number isn't found on UI");
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
