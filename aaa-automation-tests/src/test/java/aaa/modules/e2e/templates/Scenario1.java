package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.List;

import aaa.common.enums.Constants;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.http.HttpStub;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants.*;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.modules.e2e.ScenarioBaseTest;
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
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

public class Scenario1 extends ScenarioBaseTest {
	
	protected IPolicy policy;
	protected TestData tdPolicy;
	
	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	protected LocalDateTime pligaFeeLastTransactionDate;

	protected Dollar totalDue;
	protected List<LocalDateTime> installmentDueDates;
	protected Dollar installmentAmount;
	
	protected Dollar firstBillAmount;
	protected String[] endorsementReasonDataKeys;
	protected int installmentsCount = 4;
	
	protected void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();
		
		mainApp().open();
		createCustomerIndividual();

		if (getPolicyType().equals(PolicyType.PUP)) {
			policyCreationTD = new PrefillTab().adjustWithRealPolicies(policyCreationTD, getPrimaryPoliciesForPup());
		}
		policyNum = createPolicy(policyCreationTD);

		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		totalDue = BillingSummaryPage.getTotalDue();
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Quaterly payment plan", installmentsCount, installmentDueDates.size());
		installmentAmount = BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(1));
		if (getState().equals(Constants.States.NJ)) {
			new BillingPaymentsAndTransactionsVerifier().verifyPligaFee(policyEffectiveDate);
		}

		//TODO Check MVLE fee for NY Auto = $10.00
	}
	
	protected void generateFirstBill() {
		generateAndCheckBill(installmentDueDates.get(1));
		firstBillAmount = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(1), BillingBillsAndStatmentsTable.MINIMUM_DUE));
	}
	
	protected void endorsePolicy() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.endorse().performAndFill(getTestSpecificTD("TestData_Endorsement").adjust(endorsementTD));
		PolicyHelper.verifyEndorsementIsCreated();

		// Endorsement transaction displayed on billing in Payments & Other transactions section
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		LocalDateTime transactionDate = TimeSetterUtil.getInstance().getPhaseStartTime();
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(transactionDate)
			.setPolicy(policyNum).setType(PaymentsAndOtherTransactionType.PREMIUM)
			.setSubtypeReason(reason).verifyPresent();

		// AP endorsement didn't increase Bill Amount (bill generated at TC2)
		Dollar bill = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(1), BillingBillsAndStatmentsTable.MINIMUM_DUE));
		List<Dollar> installmentDues = BillingHelper.getInstallmentDues();

		// The installment schedule is recalculated starting with the Installment which doesn't yet have a bill
		bill.verify.equals(firstBillAmount);
		installmentAmount.verify.equals(installmentDues.get(1));
		installmentDues.get(2).verify.moreThan(installmentAmount);
		installmentDues.get(3).verify.moreThan(installmentAmount);

		Dollar totalDue1 = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingAccountPoliciesTable.TOTAL_DUE).getValue());
		Dollar totalDue2 = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(1), BillingBillsAndStatmentsTable.TOTAL_DUE));

		// "Total Due" field is updated to reflect AP amount 
		totalDue1.verify.moreThan(totalDue);
		totalDue2.verify.moreThan(totalDue);

		if (getState().equals(Constants.States.NJ)) {
			pligaFeeLastTransactionDate = transactionDate;
			new BillingPaymentsAndTransactionsVerifier().verifyPligaFee(pligaFeeLastTransactionDate);
		}

		//TODO Check MVLE fee for NY Auto is recalculated to added vehicle
	}

	protected void payFirstBill() {
		payAndCheckBill(installmentDueDates.get(1));
	}

	protected void generateSecondBill() {
		generateAndCheckBill(installmentDueDates.get(2), policyEffectiveDate, pligaFeeLastTransactionDate);
	}

	protected void paySecondBill() {
		payAndCheckBill(installmentDueDates.get(2));
	}

	protected void generateThirdBill() {
		generateAndCheckBill(installmentDueDates.get(3));
	}

	protected void payThirdBill() {
		payAndCheckBill(installmentDueDates.get(3));
	}

	//TC09_Renewal_R_74
	protected void earlyRenewNotGenerated() {
		LocalDateTime beforeRenewDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate).minusDays(1);
		if ((getState().equals(Constants.States.MD) || getState().equals(Constants.States.NY))
				&& beforeRenewDate.isBefore(DateTimeUtils.getCurrentDateTime())) {
			log.info(String.format("Skipping Test. State is %s and Timepoint is before the current date", getState()));
		} else {
			TimeSetterUtil.getInstance().nextPhase(beforeRenewDate);
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
			HttpStub.executeAllBatches();
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			PolicyHelper.verifyAutomatedRenewalNotGenerated(beforeRenewDate);
			PolicyHelper.verifyAutomatedRenewalNotGenerated(getTimePoints().getRenewImageGenerationDate(policyExpirationDate));
		}
	}

	protected void renewalImageGeneration() {
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewImageGenDate);
	}

	protected void renewalPreviewGeneration() {
		LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		//TODO: investigate why whether it is a bug that status is "Gathering Info" but not "Premium Calculated"
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	//TC12_Renewal_R_35
	protected void renewalOfferGeneration() {
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
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
		verifyRenewOfferGenerated(policyExpirationDate, installmentDueDates);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferGenDate)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		if (getState().equals(Constants.States.CA)) {
			verifyCaRenewalOfferPaymentAmount(policyExpirationDate,getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCount);
		}

		if (getState().equals(Constants.States.NJ)) {
			pligaFeeLastTransactionDate = renewOfferGenDate;
			new BillingPaymentsAndTransactionsVerifier().verifyPligaFee(renewOfferGenDate);
		}

		//TODO Check MVLE fee for NY Auto is generated
	}

	//Skip this step for CA
	protected void renewalPremiumNotice() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		// TODO Renew premium verification was excluded, due to unexpected installment calculations
//		if (!getState().equals(Constants.States.KY) && !getState().equals(Constants.States.WV)) {
		verifyRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), billDate, pligaFeeLastTransactionDate, installmentsCount);
//		}
		verifyRenewPremiumNotice(policyExpirationDate, getTimePoints().getBillGenerationDate(policyExpirationDate));
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate).setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();

		//TODO Check MVLE fee for NY Auto is included in bill
	}

	protected void payRenewalBill() {
		payAndCheckBill(policyExpirationDate);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	protected void updatePolicyStatus() {
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		// TODO Renew premium verification was excluded, due to unexpected installment calculations
//		if (!getState().equals(Constants.States.KY) && !getState().equals(Constants.States.WV)) {
			Dollar renewalAmount = BillingHelper.getPolicyRenewalProposalSum(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
			Dollar firstInstallment = BillingHelper.calculateFirstInstallmentAmount(renewalAmount, installmentsCount);
			Dollar lastInstallment = BillingHelper.calculateLastInstallmentAmount(renewalAmount, installmentsCount);
			BillingHelper.getInstallmentDueByDueDate(policyExpirationDate).verify.equals(firstInstallment);
			BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(1).plusYears(1)).verify.equals(lastInstallment);
			BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(2).plusYears(1)).verify.equals(lastInstallment);
			BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(3).plusYears(1)).verify.equals(lastInstallment);
//		}
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}
}
