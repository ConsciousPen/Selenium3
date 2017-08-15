package aaa.modules.e2e.templates;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants.*;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

public class Scenario2 extends BaseTest {

	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;

	protected String policyNum;
	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;

	protected List<LocalDateTime> installmentDueDates;

	public void createTestPolicy(TestData policyCreationTD) {
		mainApp().open();
		createCustomerIndividual();
		policyNum = createPolicy(policyCreationTD);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
	}

	public void TC02_Generate_First_Bill() {
		generateAndCheckBill(installmentDueDates.get(1));
	}

	public void TC03_Pay_First_Bill() {
		payAndCheckBill(installmentDueDates.get(1));
	}

	public void TC04_Billing_on_hold() {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		mainApp().open();
		SearchPage.openBilling(policyNum);
		// Set on hold status
		billingAccount.addHold().perform(tdBilling.getTestData("AddHold", "TestData"));
		new BillingAccountPoliciesVerifier().setBillingStatus(BillingStatus.ON_HOLD).verifyRowWithEffectiveDate(policyEffectiveDate);
	}

	public void TC05_Bill_Not_Generated() {
		LocalDateTime secondBillGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(2));
		TimeSetterUtil.getInstance().nextPhase(secondBillGenDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		// Verify Bill is not generated
		new BillingBillsAndStatementsVerifier().setDueDate(secondBillGenDate).setType(BillsAndStatementsType.BILL).verifyPresent(false);
		billingAccount.addHold().perform(tdBilling.getTestData("RemoveHold", "TestData"));
		new BillingAccountPoliciesVerifier().setBillingStatus(BillingStatus.ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
	}

	public void TC06_Generate_Second_Bill() {
		LocalDateTime secondBillGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(2));
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(2), secondBillGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(secondBillGenDate)
				.setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	public void TC07_Pay_Second_Bill() {
		payAndCheckBill(installmentDueDates.get(2));
	}

	public void TC08_Generate_Third_Bill() {
		generateAndCheckBill(installmentDueDates.get(3));
	}

	public void TC09_Pay_Third_Bill() {
		payAndCheckBill(installmentDueDates.get(3));
	}

	public void TC10_Generate_Fourth_Bill() {
		generateAndCheckBill(installmentDueDates.get(4));
	}

	public void TC11_Pay_Fourth_Bill() {
		payAndCheckBill(installmentDueDates.get(4));
	}

	public void TC12_Generate_Fifth_Bill() {
		generateAndCheckBill(installmentDueDates.get(5));
	}

	public void TC13_Pay_Fifth_Bill() {
		payAndCheckBill(installmentDueDates.get(5));
	}

	public void TC14_Generate_Sixth_Bill() {
		generateAndCheckBill(installmentDueDates.get(6));
	}

	public void TC15_Pay_Sixth_Bill() {
		payAndCheckBill(installmentDueDates.get(6));
	}

	public void TC16_Generate_Seventh_Bill() {
		generateAndCheckBill(installmentDueDates.get(7));
	}

	public void TC17_Pay_Seventh_Bill() {
		payAndCheckBill(installmentDueDates.get(7));
	}

	public void TC18_Generate_Eighth_Bill() {
		generateAndCheckBill(installmentDueDates.get(8));
	}

	public void TC19_Pay_Eighth_Bill() {
		payAndCheckBill(installmentDueDates.get(8));
	}

	public void TC20_Generate_Ninth_Bill() {
		generateAndCheckBill(installmentDueDates.get(9));
	}

	public void TC21_Pay_Ninth_Bill() {
		payAndCheckBill(installmentDueDates.get(9));
	}

	public void TC22_Generate_Tenth_Bill() {
		if (!getState().equals("KY"))
			generateAndCheckBill(installmentDueDates.get(10));
		else {
			LocalDateTime billDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(10));
			if (DateTimeUtils.getCurrentDateTime().isAfter(billDate)) {
				billDate = DateTimeUtils.getCurrentDateTime();
			}
			TimeSetterUtil.getInstance().nextPhase(billDate);
			JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
			mainApp().open();
			SearchPage.openBilling(policyNum);
			new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(10), getTimePoints().getBillGenerationDate(installmentDueDates.get(10)));
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillGenerationDate(installmentDueDates.get(10)))
					.setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
		}
	}

	public void TC23_Renewal_R_73() {
		LocalDateTime renewDate73 = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDate73);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewDate73);
	}

	public void TC24_Pay_Tenth_Bill() {
		payAndCheckBill(installmentDueDates.get(10));
	}

	public void TC25_Renewal_R_45() {
		LocalDateTime renewDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
		if (getState().equals("MD") && DateTimeUtils.getCurrentDateTime().isAfter(renewDate)) {
			renewDate = DateTimeUtils.getCurrentDateTime();
		}
		TimeSetterUtil.getInstance().nextPhase(renewDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	public void TC26_Renewal_R_35() {
		LocalDateTime renewDate35 = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDate35);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		BillingHelper.verifyRenewOfferGenerated(policyExpirationDate, installmentDueDates);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewDate35)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();
	}

	public void TC27_Renewal_Premium_Notice() {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		// TODO Renew premium verification was excluded, due to unexpected installment calculations
//		if (!getState().equals(States.KY) && !getState().equals(States.WV)) {
			BillingHelper.verifyRenewalOfferPaymentAmount(policyExpirationDate,
					getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), billGenDate, 11);
//		}
		BillingHelper.verifyRenewPremiumNotice(policyExpirationDate, billGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate)
				.setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	public void TC28_Verify_DocGen_Forms() {
		//TODO DocGen utils
//		DocGenHelper.verifyDocumentsGeneratedByJob(DateTimeUtils.getCurrentDateTime(), policyNum, Arrays.asList(OnDemandDocuments.AH35XX, OnDemandDocuments.AHRBXX));
	}

	public void TC29_Remove_AutoPay() {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_RemoveAutopay"));
	}

	public void TC30_Renewal_R() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.recurringPaymentsJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDueDate).setType(PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RECURRING_PAYMENT).verifyPresent(false);
	}

	public void TC31_Update_Policy_Status() {
		LocalDateTime renewDatePlus1 = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDatePlus1);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	public void TC32_Make_Manual_Payment_In_Full_Renewal_Offer_Amount() {
		LocalDateTime renewDatePlus15 = policyExpirationDate.plusDays(15);
		TimeSetterUtil.getInstance().nextPhase(renewDatePlus15);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		Dollar sum = BillingHelper.getPolicyRenewalProposalSum(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData"), sum);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	private void generateAndCheckBill(LocalDateTime installmentDate) {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDate, billGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate)
				.setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	private void payAndCheckBill(LocalDateTime installmentDueDate) {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.recurringPaymentsJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDate, BillingBillsAndStatmentsTable.MINIMUM_DUE));
		new BillingPaymentsAndTransactionsVerifier().verifyAutoPaymentGenerated(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}
}