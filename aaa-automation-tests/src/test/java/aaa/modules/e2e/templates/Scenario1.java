package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.enums.NavigationEnum;
import aaa.common.enums.SearchEnum.SearchBy;
import aaa.common.enums.SearchEnum.SearchFor;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants.BillingAccountPoliciesTable;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
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
	
	protected String billingAccNum;
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
//		policyNumber = "UTH3954211946";
//		SearchPage.search(SearchFor.POLICY, SearchBy.POLICY_QUOTE, "UTH3954211946");
		
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyPremium = PolicySummaryPage.getTotalPremiumSummary();
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccNum = BillingSummaryPage.labelBillingAccountNumber.getValue();
		totalDue = BillingSummaryPage.getTotalDue();
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		installmentAmount = BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(0));
	}
	
	public void TC02_Generate_First_Bill() {
		generateAndCheckBill(installmentDueDates.get(0));
		firstBillAmount = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(0), BillingBillsAndStatmentsTable.MINIMUM_DUE));
	}
	
	public void TC03_Endorse_Policy() {
		mainApp().open();
		SearchPage.search(SearchFor.POLICY, SearchBy.POLICY_QUOTE, policyNumber);
		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.endorse().performAndFill(getStateTestData(tdPolicy, "TestScenario1", "TestData_Endorsement").adjust(endorsementTD));
//		TODO: PolicyTabHelper.verifyEndorsementIsCreated();

		// Endorsement transaction displayed on billing in Payments & Other transactions section
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		HashMap<String, String> values = new HashMap<String, String>();
		values.put(BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, TimeSetterUtil.getInstance().getPhaseStartTime().format(DateTimeUtils.MM_DD_YYYY));
		values.put(BillingPaymentsAndOtherTransactionsTable.POLICY, policyNumber);
		values.put(BillingPaymentsAndOtherTransactionsTable.TYPE, "Premium");
		values.put(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, reason);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(values).verify.present();

		// AP endorsement didn't increase Bill Amount (bill generated at TC2)
		Dollar bill = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(0), BillingBillsAndStatmentsTable.MINIMUM_DUE));
		List<Dollar> installmentDues = BillingHelper.getInstallmentDues();

		// The installment schedule is recalculated starting with the
		// Installment which doesn't yet have a bill
		bill.verify.equals(firstBillAmount);
		installmentAmount.verify.equals(installmentDues.get(0));
		installmentDues.get(1).verify.moreThan(installmentAmount);
		installmentDues.get(2).verify.moreThan(installmentAmount);

		Dollar totalDue1 = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingAccountPoliciesTable.TOTAL_DUE).getValue());
		Dollar totalDue2 = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(0), BillingBillsAndStatmentsTable.TOTAL_DUE));

		// "Total Due" field is updated to reflect AP amount 
		totalDue1.verify.moreThan(totalDue);
		totalDue2.verify.moreThan(totalDue);
	}

	
	
	
	private void generateAndCheckBill(LocalDateTime installmentDate) {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(installmentDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.search(SearchFor.BILLING, SearchBy.BILLING_ACCOUNT, billingAccNum);
		BillingHelper.verifyBillGenerated(installmentDate, getTimePoints().getBillGenerationDate(installmentDate));
		BillingHelper.verifyFeeTransactionGenerated(billDate);
	}

}
