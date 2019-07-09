package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.main.enums.BillingConstants.BillingGeneralInformationTable.ID;
import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT;
import static aaa.main.enums.PolicyConstants.PolicyGeneralInformationTable.EVALUE_STATUS;
import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.AAARecurringPaymentResponseHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.billing.PaymentCentralHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.controls.waiters.Waiters;

public class TestEValueNsfAHDRXX extends AutoSSBaseTest {

	private static final String GET_PAYMENT_REFERENCE_ID_BY_BILLING_ACCOUNT = "select referenceid from(\n"
			+ "select p.* --, bt.*,  ba.*,\n"
			+ "from Payment p\n"
			+ "join BillingTransaction bt on bt.paymentNumber = p.paymentNumber\n"
			+ "join BillingAccount ba on ba.id = bt.account_id\n"
			+ "join paymentdetails pd on pd.id = p.paymentdetails_id\n"
			+ "and ba.accountnumber = '%s'\n"
			+ "order by p.CREATIONDATE desc\n"
			+ ")where rownum = 1";

	private static final String GET_PAYMENT_NUMBER_BY_BILLING_ACCOUNT = "select paymentNumber from(\n"
			+ "        select p.* --, bt.*,  ba.*,\n"
			+ "        from Payment p\n"
			+ "        join BillingTransaction bt on bt.paymentNumber = p.paymentNumber\n"
			+ "        join BillingAccount ba on ba.id = bt.account_id\n"
			+ "        and ba.accountnumber = '%s'\n"
			+ "        order by p.CREATIONDATE desc\n"
			+ "        )where rownum = 1";

	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();
	private PaymentCentralHelper paymentCentralHelper = new PaymentCentralHelper();
	private AAARecurringPaymentResponseHelper aaaRecurringPaymentResponseHelper = new AAARecurringPaymentResponseHelper();
	private TestEValueMembershipProcess testEValueMembershipProcess = new TestEValueMembershipProcess();

	@Test(description = "Precondition", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public void precondJobAdding() {
		JobUtils.createJob(BatchJob.aaaPaymentCentralRejectFeedAsyncJob);
		JobUtils.createJob(BatchJob.recurringPaymentsProcessingJob);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test AHDRXX is produced when Autopay is removed automatically
	 * @scenario
	 * 0. Create an eValue with Autopay and Monthly payment plan
	 * 1. generate a bill
	 * 2. generate a recurring payment
	 * 3. generate successful response for aaaRecurringPaymentsResponseProcessAsyncJob and run the job
	 * 4. generate decline payment and run aaaPaymentCentralRejectFeedAsyncJob
	 * 5. check:
	 * a) payment is declined
	 * b) autopay is removed
	 * c) eValue discount is removed
	 * 6. run NB+30 jobs
	 * 7. Check AHDRXX is generated and contains eValue discount, PaymentPlan, Autopay related tags
	see PaymentCentralDeclineReasonList.csv for decline codes
	aaaPaymentCentralRejectFeedAsyncJob needs to use payment decline code not from below list for Autopay to be removed:
	"RF1","RF2","RF3","RF4","RF5","RF6","RV1","RV2","RV3","RV4","RV5","RV6","ADJ1","ADJ2"
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-7454", "PAS-314", "PAS-244"})
	public void pas7454_eValueRemovedAutopayNsfDeclineRecurringPaymentResponse(@Optional("VA") String state) {
		precondJobAdding();
		pas7454_eValueRemovedAutopayNsfDecline("SUCC");
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test AHDRXX is produced when Autopay is removed automatically
	 * @scenario
	 * 0. Create an eValue with Autopay and Monthly payment plan
	 * 1. generate a bill
	 * 2. generate a recurring payment
	 * 3. generate error response for aaaRecurringPaymentsResponseProcessAsyncJob and run the job
	 * 4. check:
	 * a) payment is declined
	 * b) autopay is removed
	 * c) eValue discount is removed
	 * 5. run NB+30 jobs
	 * 6. Check AHDRXX is generated and contains eValue discount, PaymentPlan, Autopay related tags
	see PaymentCentralDeclineReasonList.csv for decline codes
	aaaPaymentCentralRejectFeedAsyncJob needs to use payment decline code not from below list for Autopay to be removed:
	"RF1","RF2","RF3","RF4","RF5","RF6","RV1","RV2","RV3","RV4","RV5","RV6","ADJ1","ADJ2"
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-7454", "PAS-314", "PAS-244"})
	public void pas7454_eValueRemovedAutopayNsfDeclinePaymentCentralReject(@Optional("VA") String state) {
		precondJobAdding();
		pas7454_eValueRemovedAutopayNsfDecline("ERR");
	}

	private void pas7454_eValueRemovedAutopayNsfDecline(String recurringPaymentResponseStatus) {
		String paymentPlan = "contains=Standard"; //"Monthly"
		String paymentPlanMetaKey = TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel());
		String applyEValueDiscountMetaKey = TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT.getLabel());
		TestData policyTdAdjusted = getPolicyTD().adjust(paymentPlanMetaKey, paymentPlan).adjust(applyEValueDiscountMetaKey, "Yes");

		mainApp().open();
		createCustomerIndividual();
		getPolicyType().get().createQuote(policyTdAdjusted);
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue("ACH");
		assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(EVALUE_STATUS)).hasValue("Pending");
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		BillingSummaryPage.open();
		LocalDateTime dd1 = BillingSummaryPage.getInstallmentDueDate(2);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dd1));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(dd1));
		JobUtils.executeJob(BatchJob.aaaRecurringPaymentsProcessingJob);

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		String billingAccount = BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(ID).getValue();

		String paymentAmount = BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT).getValue();
		String paymentAmountPlain = new Dollar(paymentAmount).multiply(-1).toPlaingString();

		if ("SUCC".equals(recurringPaymentResponseStatus)) {
			//Generate file for successful Recurring Payment Response and run job
			generateFileForRecurringPaymentResponseJob(policyNumber, billingAccount, paymentAmountPlain, "SUCC");

			//Generate file for PaymentCentralRejectFeed and run job
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));
			String paymentReferenceId = DBService.get().getValue(String.format(GET_PAYMENT_REFERENCE_ID_BY_BILLING_ACCOUNT, billingAccount)).get();
			File paymentCentralFile = paymentCentralHelper.createFile(policyNumber, paymentAmountPlain, paymentReferenceId);
			PaymentCentralHelper.copyFileToServer(paymentCentralFile);
			JobUtils.executeJob(BatchJob.aaaPaymentCentralRejectFeedAsyncJob, true);
		} else if ("ERR".equals(recurringPaymentResponseStatus)) {
			//Generate file for unsuccessful Recurring Payment Response and run job
			generateFileForRecurringPaymentResponseJob(policyNumber, billingAccount, paymentAmountPlain, "ERR");
		} else {
			throw new IstfException("Bad Recurring Payment Response status");
		}
		mainApp().reopen();
		SearchPage.openBilling(policyNumber);
		verifyPaymentDeclinedTransactionPresent(paymentAmountPlain);
		verifyPaymentTransactionBecameDeclined(paymentAmount);
		BillingSummaryPage.linkUpdateBillingAccount.click();
		assertThat(updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY)).hasValue(false);
		assertThat(updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.AUTOPAY_SELECTION)).hasValue("");
		UpdateBillingAccountActionTab.buttonCancel.click();

		SearchPage.openPolicy(policyNumber);
		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(EVALUE_STATUS)).hasValue("");

			//PAS-244 start
			PolicySummaryPage.transactionHistoryRecordCountCheck(policyNumber, 2, "eValue Removed - NSF", softly);
			//PAS-244 end

			if (TimeSetterUtil.getInstance().getCurrentTime().isBefore(policyEffectiveDate.plusDays(30))) {
				TestEValueMembershipProcess.jobsNBplus15plus30runNoChecks(policyEffectiveDate.plusDays(30));
			}

			testEValueMembershipProcess.checkDocumentContentAHDRXX(policyNumber, true, false, true, false, false, softly);

			String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AHDRXX", "ENDORSEMENT_ISSUE");
			softly.assertThat(DocGenHelper.getDocumentDataElemByName("PayPlnYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField())
					.isEqualTo("Y");
			softly.assertThat(DocGenHelper.getDocumentDataElemByName("PlcyPayFullAmtYN", DocGenEnum.Documents.AHDRXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField())
					.isEqualTo("Y");
		});
	}

	private void generateFileForRecurringPaymentResponseJob(String policyNumber, String billingAccount, String paymentAmountPlain, String err) {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));
		String paymentNumber = DBService.get().getValue(String.format(GET_PAYMENT_NUMBER_BY_BILLING_ACCOUNT, billingAccount)).get();
		File recurringPaymentResponseFile = aaaRecurringPaymentResponseHelper.createFile(policyNumber, paymentAmountPlain, paymentNumber, err);
		AAARecurringPaymentResponseHelper.copyFileToServer(recurringPaymentResponseFile);
		JobUtils.executeJob(BatchJob.aaaRecurringPaymentsResponseProcessAsyncJob, true);
		Waiters.SLEEP(5000).go();
	}

	private void verifyPaymentDeclinedTransactionPresent(String amount) {
		new BillingPaymentsAndTransactionsVerifier().setType("Adjustment").setSubtypeReason("Payment Declined").setAmount(new Dollar(amount)).setStatus("Applied").verifyPresent();
	}

	private void verifyPaymentTransactionBecameDeclined(String amount) {
		new BillingPaymentsAndTransactionsVerifier().setType("Payment").setSubtypeReason("Recurring Payment").setAmount(new Dollar(amount)).setStatus("Declined").verifyPresent();
	}
}
