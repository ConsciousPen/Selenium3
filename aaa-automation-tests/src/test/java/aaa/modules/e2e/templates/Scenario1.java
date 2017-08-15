package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.List;

import aaa.common.enums.Constants;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.http.HttpStub;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants.*;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants.*;
import aaa.main.modules.policy.IPolicy;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class Scenario1 extends BaseTest {
	
	protected IPolicy policy;
	protected TestData tdPolicy;
	
	protected String policyNumber;
	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	protected Dollar policyPremium;
	
	protected Dollar totalDue;
	protected List<LocalDateTime> installmentDueDates;
	protected Dollar installmentAmount;
	
	protected Dollar firstBillAmount;
	protected String[] endorsementReasonDataKeys;
	
	public void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();
		
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(policyCreationTD);
//		policyNumber = "UTH3950536086";
//		SearchPage.search(SearchFor.POLICY, SearchBy.POLICY_QUOTE, policyNumber);
		
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyPremium = PolicySummaryPage.getTotalPremiumSummary();
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		totalDue = BillingSummaryPage.getTotalDue();
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		installmentAmount = BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(3));
	}
	
	public void TC02_Generate_First_Bill() {
		generateAndCheckBill(installmentDueDates.get(3));
		firstBillAmount = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(3), BillingBillsAndStatmentsTable.MINIMUM_DUE));
	}
	
	public void TC03_Endorse_Policy() {
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.endorse().performAndFill(getTestSpecificTD("TestData_Endorsement").adjust(endorsementTD));
		PolicyHelper.verifyEndorsementIsCreated();

		// Endorsement transaction displayed on billing in Payments & Other transactions section
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(TimeSetterUtil.getInstance().getPhaseStartTime())
			.setPolicy(policyNumber).setType(PaymentsAndOtherTransactionType.PREMIUM)
			.setSubtypeReason(reason).verifyPresent();

		// AP endorsement didn't increase Bill Amount (bill generated at TC2)
		Dollar bill = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(3), BillingBillsAndStatmentsTable.MINIMUM_DUE));
		List<Dollar> installmentDues = BillingHelper.getInstallmentDues();

		// The installment schedule is recalculated starting with the Installment which doesn't yet have a bill
		bill.verify.equals(firstBillAmount);
		installmentAmount.verify.equals(installmentDues.get(1));
		installmentDues.get(2).verify.moreThan(installmentAmount);
		installmentDues.get(3).verify.moreThan(installmentAmount);

		Dollar totalDue1 = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingAccountPoliciesTable.TOTAL_DUE).getValue());
		Dollar totalDue2 = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(3), BillingBillsAndStatmentsTable.TOTAL_DUE));

		// "Total Due" field is updated to reflect AP amount 
		totalDue1.verify.moreThan(totalDue);
		totalDue2.verify.moreThan(totalDue);
	}

	public void TC04_Pay_First_Bill() {
		payAndCheckBill(installmentDueDates.get(3));
	}

	public void TC05_Generate_Second_Bill() {
		generateAndCheckBill(installmentDueDates.get(3));
	}

	public void TC06_Pay_Second_Bill() {
		payAndCheckBill(installmentDueDates.get(3));
	}

	public void TC07_Generate_Third_Bill() {
		generateAndCheckBill(installmentDueDates.get(3));
	}

	public void TC08_Pay_Third_Bill() {
		payAndCheckBill(installmentDueDates.get(3));
	}

	public void TC09_Renewal_R_74() {
		LocalDateTime renewDate74 = getTimePoints().getRenewImageGenerationDate(policyExpirationDate).minusDays(1);
		//TODO
		if ((getState().equals(Constants.States.MD) || getState().equals(Constants.States.NY))
				&& renewDate74.isBefore(DateTimeUtils.getCurrentDateTime())) {
			log.info(String.format("Skipping Test. State is %s and Timepoint is before the current date", getState()));
		} else {
			TimeSetterUtil.getInstance().nextPhase(renewDate74);
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
			HttpStub.executeAllBatches();
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
			mainApp().open();
			SearchPage.openPolicy(policyNumber);
			PolicyHelper.verifyAutomatedRenewalNotGenerated(renewDate74);
			PolicyHelper.verifyAutomatedRenewalNotGenerated(getTimePoints().getRenewImageGenerationDate(policyExpirationDate));
		}
	}

	public void TC10_Renewal_R_73() {
		LocalDateTime renewDate73 = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDate73);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewDate73);
	}

	public void TC11_Renewal_R_45() {
		LocalDateTime renewDate45 = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDate45);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	public void TC12_Renewal_R_35() {
		LocalDateTime renewDate35 = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDate35);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
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

	public void TC13_Renewal_Premium_Notice() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		// TODO Renew premium verification was excluded, due to unexpected installment calculations
//		if (!getState().equals(Constants.States.KY) && !getState().equals(Constants.States.WV)) {
			BillingHelper.verifyRenewalOfferPaymentAmount(policyExpirationDate,
					getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), billDate, 4);
//		}
		BillingHelper.verifyRenewPremiumNotice(policyExpirationDate, getTimePoints().getBillGenerationDate(policyExpirationDate));
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate)
				.setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	public void TC14_Pay_Renewal_Bill_R() {
		payAndCheckBill(policyExpirationDate);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	public void TC15_Update_Policy_Status() {
		LocalDateTime renewDatePlus1 = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDatePlus1);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		// TODO Renew premium verification was excluded, due to unexpected installment calculations
//		if (!getState().equals(Constants.States.KY) && !getState().equals(Constants.States.WV)) {
			Dollar renewalAmount = BillingHelper.getPolicyRenewalProposalSum(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
			Dollar firstInstallment = BillingHelper.calculateFirstInstallmentAmount(renewalAmount, 4);
			Dollar lastInstallment = BillingHelper.calculateLastInstallmentAmount(renewalAmount, 4);
			BillingHelper.getInstallmentDueByDueDate(policyExpirationDate).verify.equals(firstInstallment);
			BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(3).plusYears(1)).verify.equals(lastInstallment);
			BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(3).plusYears(1)).verify.equals(lastInstallment);
			BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(3).plusYears(1)).verify.equals(lastInstallment);
//		}
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}
	
	
	private void generateAndCheckBill(LocalDateTime installmentDate) {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(installmentDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDate, billDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate)
				.setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	private void payAndCheckBill(LocalDateTime installmentDueDate) {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.recurringPaymentsJob);
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDate, BillingBillsAndStatmentsTable.MINIMUM_DUE));
		new BillingPaymentsAndTransactionsVerifier().verifyAutoPaymentGenerated(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}
}
