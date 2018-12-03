package aaa.modules.regression.billing_and_payments.template;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.common.enums.Constants.UserGroups;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

/** 
 * @author oreva
 * @name Test Billing - Generate Future Statement action
 * @scenario
 * 1. Create a new customer. 
 * 2. Create a new policy with payment plan any other than Annual.
 * 3. Navigate to Billing tab. 
 * 4. Verify: 
 *  - first installment has status 'Unbilled' in Installment Schedule section;
 *  - there is no any bills generated on Bills & Statements section;
 * 5. Perform action 'Generate Future Statement'. 
 * 6. Verify that after perfomed action first installment status changed to 'Billed' in Installment Schedule section.
 * 7. Verify that a bill was generated and displays in Bills & Statements section, 
 *	and also Fee is generated and displays in Payments & Other Transactions section. 
 */

public abstract class PolicyBillingGenerateFutureStatement extends PolicyBaseTest {
	
	public void testGenerateFutureStatement(TestData td) {
		mainApp().open();
		createCustomerIndividual();  
		createPolicy(td);
		
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
		BillingSummaryPage.open();
		
		if(getUserGroup().equals(UserGroups.F35.get())||getUserGroup().equals(UserGroups.G36.get())) {
			log.info("Verifying 'Generate Future Statement' action");
			assertThat(NavigationPage.comboBoxListAction).as("Action 'Generate Future Statement' is available").doesNotContainOption("Generate Future Statement");
		}
		else {
			generateFutureStatement();
		}

	}
	
	public void generateFutureStatement() {
		IBillingAccount billing = new BillingAccount();
		String billDueDate = BillingSummaryPage.tableInstallmentSchedule.getColumn(BillingConstants.BillingInstallmentScheduleTable.INSTALLMENT_DUE_DATE).getCell(2).getValue();
		
		//verify first installment is in Unbilled status and there is no generated bills		
		assertThat(BillingSummaryPage.tableInstallmentSchedule.getRow(2).getCell(BillingConstants.BillingInstallmentScheduleTable.BILLED_STATUS).getValue())
			.isEqualTo(BillingConstants.InstallmentScheduleBilledStatus.UNBILLED);
		assertThat(BillingSummaryPage.tableBillsStatements.getRowsCount()).as("Section Bills & Statements already has some bill").isEqualTo(0);
		
		//Perform 'Generate Future Statement' action
		billing.generateFutureStatement().perform();
		
		//verify first installment is in Billed status after 'Generate Future Statement' action performed
		assertThat(BillingSummaryPage.tableInstallmentSchedule.getRow(2).getCell(BillingConstants.BillingInstallmentScheduleTable.INSTALLMENT_DUE_DATE).getValue()).isEqualTo(billDueDate);
		assertThat(BillingSummaryPage.tableInstallmentSchedule.getRow(2).getCell(BillingConstants.BillingInstallmentScheduleTable.BILLED_STATUS).getValue())
			.isEqualTo(BillingConstants.InstallmentScheduleBilledStatus.BILLED);
		
		//verify bill is displaying in Bills & Statements section
		assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE).getValue())
			.isEqualTo(billDueDate);
		assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue())
			.isEqualTo(BillingConstants.BillsAndStatementsType.BILL);
		
		//verify Fee is generated with bill and displaying in Payments & Other Transactions section
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE).getValue())
			.isEqualTo(BillingConstants.PaymentsAndOtherTransactionType.FEE);
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON).getValue())
			.isEqualTo(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE);	
	}
}
