/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.billing.account;

import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.AbstractAction;
import aaa.common.Tab;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.BillingConstants.*;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.AddHoldActionTab;
import aaa.main.modules.billing.account.actiontabs.ChangePaymentPlanActionTab;
import aaa.main.modules.billing.account.actiontabs.MovePoliciesActionTab;
import aaa.main.modules.billing.account.views.*;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.table.Table;

public final class BillingAccountActions {

	public static class GenerateFutureStatement extends AbstractAction {
		@Override
		public String getName() {
			return "Generate Future Statement";
		}

		@Override
		public Workspace getView() {
			return new GenerateFutureStatementView();
		}

		@Override
		public AbstractAction submit() {
			Page.dialogConfirmation.confirm();
			return this;
		}

		public AbstractAction perform() {
			start();
			return submit();
		}
	}

	public static class AcceptPayment extends AbstractAction {
		@Override
		public String getName() {
			return "Accept Payment";
		}

		@Override
		public Workspace getView() {
			return new AcceptPaymentView();
		}

		@Override
		public AbstractAction start() {
			log.info(getName() + " action initiated.");
			new Link(By.linkText("Accept Payment")).click();
			return this;
		}

		public AbstractAction perform(TestData td, Dollar amount) {
			td.adjust(TestData.makeKeyPath(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel()), amount.toString());
			return perform(td);
		}

		public void perform(TestData td, String amount, List<String> allocations, String referenceNumber) {
			td.adjust(TestData.makeKeyPath(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel()), amount)
					.adjust(TestData.makeKeyPath(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.AcceptPaymentActionTab.ALLOCATIONS.getLabel()),
							allocations);
			perform(td);

			Table tableSuspendRemainingError = new Table(By.id("paymentForm:error_message"));
			if (tableSuspendRemainingError.isPresent()) {
				new RadioButton(By.id("paymentForm:suspendRemainingAmount_radio:0")).setValue(true);
				new TextBox(By.id("paymentForm:cashReferenceNumber")).setValue(referenceNumber);
				submit();
			}
		}

		//Accept payment from Suspense
		public void perform(TestData td, List<String> allocations) {
			td.adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.AcceptPaymentActionTab.ALLOCATIONS.getLabel()), allocations);
			perform(td);
		}

	        /* TODO Make Custom control for Payment Allocation
			private void fillManualAllocationsByCoverage(Map<String, Dollar> amountsByCoverage) {
	            Table tablePaymentAllocation = new Table(By.xpath("//div[@id='advAllocationForm:invoice_items_info_table']/div/table"));
	            for (Row row : tablePaymentAllocation.getRows()) {
	                String coverageCode = row.getCell(BillingPaymentAllocationTable.COVERAGE).getValue();
	                Dollar remainingDue = new Dollar(row.getCell(BillingPaymentAllocationTable.REMAINING_DUE).getValue());

	                Dollar amountToAllocate = amountsByCoverage.get(coverageCode);
	                if (amountToAllocate == null || amountToAllocate.isZero()) {
	                    continue;
	                }

	                Dollar allocatedAmount = remainingDue.min(amountToAllocate);
	                row.getCell(BillingPaymentAllocationTable.AMOUNT_PAID).controls.textBoxes.getFirst().setValue(allocatedAmount.toString());

	                amountsByCoverage.put(coverageCode, amountToAllocate.subtract(allocatedAmount));

	            }
	        }*/

		@Override
		public AbstractAction submit() {
			log.info(getName() + " action has been finished.");
			return this;
		}
	}

	public static class OtherTransactions extends AbstractAction {
		@Override
		public String getName() {
			return "Other Transactions";
		}

		@Override
		public Workspace getView() {
			return new OtherTransactionsView();
		}

		@Override
		public AbstractAction start() {
			new Link(By.linkText("Other Transactions")).click();
			return this;
		}

		@Override
		public AbstractAction submit() {
			return this;
		}

		public AbstractAction perform(TestData td, Dollar amount) {
			td.adjust(TestData.makeKeyPath(BillingAccountMetaData.OtherTransactionsActionTab.class.getSimpleName(), BillingAccountMetaData.OtherTransactionsActionTab.AMOUNT.getLabel()), amount.toString());
			return perform(td);
		}
	}

	public static class DiscardBill extends AbstractAction {
		@Override
		public String getName() {
			return "Discard Bill";
		}

		@Override
		public Workspace getView() {
			return new DiscardBillView();
		}

		@Override
		public AbstractAction start() {
			return start(1);
		}

		public AbstractAction start(int rowNumber) {
			BillingSummaryPage.tableBillsStatements.getRow(rowNumber).getCell(BillingBillsAndStatmentsTable.ACTIONS).controls.links.get(
					ActionConstants.BillingBillsAndStatementsAction.DISCARD).click();
			return this;
		}

		public AbstractAction perform(TestData td, int rowNumber) {
			start(rowNumber);
			getView().fill(td);
			return submit();
		}

		@Override
		public AbstractAction submit() {
			Page.dialogConfirmation.confirm();
			return this;
		}
	}

	public static class UnallocatePayment extends AbstractAction {
		@Override
		public String getName() {
			return "Unallocate";
		}

		@Override
		public Workspace getView() {
			return new UnallocatePaymentView();
		}

		@Override
		public AbstractAction start() {
			return start(1);
		}

		public AbstractAction start(int rowNumber) {
			BillingSummaryPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).controls.buttons.get("Unallocate")
					.click();
			return submit();
		}

		public AbstractAction perform(TestData td, int rowNumber) {
			start(rowNumber);
			getView().fill(td);
			return submit();
		}

		@Override
		public AbstractAction submit() {
			Page.dialogConfirmation.confirm();
			return this;
		}
	}

	public static class RegenerateBill extends AbstractAction {
		@Override
		public String getName() {
			return "Re-generate Bill";
		}

		@Override
		public Workspace getView() {
			return new RegenerateBillView();
		}

		@Override
		public AbstractAction start() {
			return start(1);
		}

		public AbstractAction start(int rowNumber) {
			BillingSummaryPage.tableBillsStatements.getRow(rowNumber).getCell(BillingBillsAndStatmentsTable.ACTIONS).controls.links.get(
					ActionConstants.BillingBillsAndStatementsAction.DISCARD).click();
			return this;
		}

		public AbstractAction perform(TestData td, int rowNumber) {
			start(rowNumber);
			getView().fill(td);
			return submit();
		}

		@Override
		public AbstractAction submit() {
			Page.dialogConfirmation.confirm();
			return this;
		}
	}

	public static class TransferPayment extends AbstractAction {
		@Override
		public String getName() {
			return "Transfer Payment";
		}

		@Override
		public Workspace getView() {
			return new TransferPaymentView();
		}

		//FIXME(vmarkouski) - magic number
		@Override
		public AbstractAction start() {
			return start(1);
		}

		public AbstractAction start(int rowNumber) {
			BillingSummaryPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(
					ActionConstants.BillingPaymentsAndOtherTransactionAction.TRANSFER).click();
			return this;
		}

		public AbstractAction perform(TestData td, int rowNumber) {
			start(rowNumber);
			getView().fill(td);
			return submit();
		}

		public AbstractAction perform(TestData td, String policy, String allocatedAmount) {
			td.adjust(
					TestData.makeKeyPath(BillingAccountMetaData.TransferPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.TransferPaymentActionTab.ALLOCATION.getLabel(),
							BillingAccountMetaData.TransferPaymentActionTab.AllocationMultiSelector.POLICY_NUMBER.getLabel()), policy)
					.adjust(TestData.makeKeyPath(BillingAccountMetaData.TransferPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.TransferPaymentActionTab.ALLOCATED_AMOUNT.getLabel()),
							allocatedAmount);
			return perform(td);
		}

		@Override
		public AbstractAction submit() {
			new Button(By.xpath("//input[contains(@id, 'transferPaymentForm:ok')]")).click();
			return this;
		}
	}

	public static class DeclinePayment extends AbstractAction {
		@Override
		public String getName() {
			return "Decline Payment";
		}

		@Override
		public Workspace getView() {
			return new DeclinePaymentView();
		}

		@Override
		public AbstractAction start() {
			return start(1);
		}

		public AbstractAction start(int rowNumber) {
			BillingSummaryPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(
					ActionConstants.BillingPaymentsAndOtherTransactionAction.DECLINE).click();
			return this;
		}

		public AbstractAction start(String amount) {
			BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingPaymentsAndOtherTransactionsTable.AMOUNT, amount).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(
					ActionConstants.BillingPaymentsAndOtherTransactionAction.DECLINE).click();
			return this;
		}

		public AbstractAction start(Map<String, String> map) {
			BillingSummaryPage.tablePaymentsOtherTransactions.getRow(map).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(
					ActionConstants.BillingPaymentsAndOtherTransactionAction.DECLINE).click();
			return this;
		}
		
		public AbstractAction perform(TestData td, int rowNumber) {
			start(rowNumber);
			getView().fill(td);
			return submit();
		}

		public AbstractAction perform(TestData td, String amount) {
			start(amount);
			getView().fill(td);
			return submit();
		}
		
		public AbstractAction perform(TestData td, Map<String, String> map) {
			start(map);
			getView().fill(td);
			return submit();
		}

		//Decline
		public AbstractAction perform(TestData td, int rowNumber, String referenceNumber) {
			start(rowNumber);
			Table suspenseForDecline = new Table(By.xpath("//div[@id = 'billingDetailedForm:suspenseItemList']//table"));
			if (suspenseForDecline.isPresent()) {
				suspenseForDecline.getRowContains(BillingSuspenseForDeclineTable.REFERENCE_NUMBER, referenceNumber)
						.getCell(BillingSuspenseForDeclineTable.ACTION).controls.links.get("Decline").click();
			}
			getView().fill(td);
			return submit();
		}

		//Click Cancel button in Decline Action PopUp
		public void perform(int rowNumber) {
			start(rowNumber);
			Table suspenseForDecline = new Table(By.xpath("//div[@id = 'billingDetailedForm:suspenseItemList']//table"));
			if (suspenseForDecline.isPresent()) {
				new Button(By.id("billingDetailedForm:multipleSuspenseDeclineCancelBtn")).click();
			}
		}

		@Override
		public AbstractAction perform(TestData td) {
			throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
		}

		@Override
		public AbstractAction submit() {
			if (Tab.buttonOk.isPresent()) {
				Tab.buttonOk.click();
			} else if (new Button(By.id("declinePaymentReasonForm:okBtnPopup_footer")).isPresent()) {
				new Button(By.id("declinePaymentReasonForm:okBtnPopup_footer")).click();
			}

			if (Page.dialogConfirmation.isPresent()) {
				Page.dialogConfirmation.confirm();
			}
			return this;
		}
	}

	public static class MovePolicies extends AbstractAction {
		@Override
		public String getName() {
			return "Move Policies";
		}

		@Override
		public Workspace getView() {
			return new MovePoliciesView();
		}

		@Override
		public AbstractAction submit() {
			Tab.buttonFinish.click();
			return this;
		}

		public AbstractAction perform(TestData td, List<String> policies) {
			return super.perform(td.adjust(TestData.makeKeyPath(MovePoliciesActionTab.class.getSimpleName(), BillingAccountMetaData.MovePoliciesActionTab.POLICIES.getLabel()), policies));
		}

		@Override
		public AbstractAction perform(TestData td) {
			throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, List<String> policies) instead.");
		}
	}

	public static class WaiveFee extends AbstractAction {
		@Override
		public String getName() {
			return "Waive Fee";
		}

		@Override
		public Workspace getView() {
			return new WaiveFeeView();
		}

		//FIXME(vmarkouski): magic number
		@Override
		public AbstractAction start() {
			return start(1);
		}

		public AbstractAction start(int rowNumber) {
			BillingSummaryPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION)
					.controls.links.get(ActionConstants.BillingPaymentsAndOtherTransactionAction.WAIVE).click();
			return this;
		}

		public AbstractAction perform(TestData td, int rowNumber) {
			start(rowNumber);
			getView().fill(td);
			return submit();
		}

		@Override
		public AbstractAction perform(TestData td) {
			throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, List<String> policies) instead.");
		}

		@Override
		public AbstractAction submit() {
			Page.dialogConfirmation.confirm();
			return this;
		}
	}

	public static class Refund extends AbstractAction {
		@Override
		public String getName() {
			return "Refund";
		}

		@Override
		public Workspace getView() {
			return new RefundView();
		}

		public AbstractAction perform(TestData td, Dollar amount) {
			td.adjust(TestData.makeKeyPath(BillingAccountMetaData.RefundActionTab.class.getSimpleName(), BillingAccountMetaData.RefundActionTab.AMOUNT.getLabel()), amount.toString());
			return super.perform(td);
		}

		@Override
		public AbstractAction perform(TestData td) {
			throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, String amount) instead.");
		}

		@Override
		public AbstractAction submit() {
			return this;
		}


		public AbstractAction manualRefundPerform(String paymentMethod, String amount) {
			BillingAccount billingAccount = new BillingAccount();
			AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
			if (!BillingSummaryPage.tableBillingGeneralInformation.isPresent()) {
				NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
			}
			billingAccount.refund().start();
			acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).setValue(paymentMethod);
			acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).setValue(amount);
			acceptPaymentActionTab.submitTab();
			return this;
		}
	}

	public static class ApproveRefund extends AbstractAction {
		@Override
		public String getName() {
			return "Approve Refund";
		}

		@Override
		public AbstractAction start() {
			return start(1);
		}

		public AbstractAction start(int rowNumber) {
			BillingSummaryPage.tablePendingTransactions.getRow(rowNumber).getCell(BillingPendingTransactionsTable.ACTION).controls.links.get(
					ActionConstants.BillingPendingTransactionAction.APPROVE).click();
			return this;
		}

		public AbstractAction start(Dollar amount) {
			BillingSummaryPage.tablePendingTransactions.getRow(BillingPendingTransactionsTable.AMOUNT, amount.toString()).getCell(BillingPendingTransactionsTable.ACTION).controls.links.get(
					ActionConstants.BillingPendingTransactionAction.APPROVE).click();
			return this;
		}

		@Override
		public Workspace getView() {
			return null;
		}

		@Override
		public AbstractAction perform(TestData td) {
			start();
			return submit();
		}

		public AbstractAction perform(int rowNumber) {
			start(rowNumber);
			return submit();
		}

		public AbstractAction perform(Dollar amount) {
			start(amount);
			return submit();
		}

		@Override
		public AbstractAction submit() {
			Page.dialogConfirmation.confirm();
			return this;
		}
	}

	public static class IssueRefund extends AbstractAction {

		@Override
		public String getName() {
			return "Issue Refund";
		}

		@Override
		public AbstractAction start() {
			return start(1);
		}

		public AbstractAction start(int rowNumber) {
			BillingSummaryPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(
					ActionConstants.BillingPaymentsAndOtherTransactionAction.ISSUE).click();
			return this;
		}

		public AbstractAction start(Dollar amount) {
			BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingPaymentsAndOtherTransactionsTable.AMOUNT, amount.toString()).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(
					ActionConstants.BillingPaymentsAndOtherTransactionAction.ISSUE).click();
			return this;
		}

		@Override
		public Workspace getView() {
			return null;
		}

		@Override
		public AbstractAction perform(TestData td) {
			start();
			return submit();
		}

		public AbstractAction perform(int rowNumber) {
			start(rowNumber);
			return submit();
		}

		public AbstractAction perform(Dollar amount) {
			start(amount);
			return submit();
		}

		@Override
		public AbstractAction submit() {
			Page.dialogConfirmation.confirm();
			return this;
		}
	}

	public static class Update extends AbstractAction {
		@Override
		public String getName() {
			return "Update";
		}

		@Override
		public Workspace getView() {
			return new UpdateBillingAccountView();
		}

		@Override
		public AbstractAction submit() {
			Tab.buttonSave.click();
			if (Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()) {
				Page.dialogConfirmation.confirm();
			}
			return this;
		}
	}

	public static class AddHold extends AbstractAction {
		@Override
		public String getName() {
			return "Hold Policies";
		}

		@Override
		public Workspace getView() {
			return new AddHoldView();
		}

		@Override
		public AbstractAction submit() {
			AddHoldActionTab.buttonAddUpdate.click();
			AddHoldActionTab.buttonCancel.click();
			return this;
		}
	}

	public static class RemoveHold extends AbstractAction {
		@Override
		public String getName() {
			return "Hold Policies";
		}

		@Override
		public Workspace getView() {
			return new RemoveHoldView();
		}

		@Override
		public AbstractAction submit() {
			AddHoldActionTab.buttonCancel.click();
			return this;
		}
	}

	public static class ViewModalPremium extends AbstractAction {
		@Override
		public String getName() {
			return "View Modal Premium";
		}

		@Override
		public Workspace getView() {
			return new ViewModalPremiumView();
		}

		@Override
		public AbstractAction submit() {
			Tab.buttonBack.click();
			return this;
		}
	}

	public static class ChangePaymentPlan extends AbstractAction {
		@Override
		public String getName() {
			return "Change Payment Plan";
		}

		@Override
		public Workspace getView() {
			return new ChangePaymentPlanView();
		}

		@Override
		public AbstractAction start() {
			return start(1);
		}

		public AbstractAction start(int rowNumber) {
			BillingSummaryPage.tableBillingAccountPolicies.getRow(rowNumber).getCell(BillingAccountPoliciesTable.PAYMENT_PLAN).
					controls.links.getFirst().click();
			return this;
		}

		public AbstractAction perform(String paymentPlan) {
			start(1);
			getView().fill(new SimpleDataProvider().adjust(new ChangePaymentPlanActionTab().getMetaKey(), new SimpleDataProvider()).adjust(TestData.makeKeyPath(new ChangePaymentPlanActionTab().getMetaKey(),
					BillingAccountMetaData.ChangePaymentPlanActionTab.PAYMENT_PLAN.getLabel()), paymentPlan));
			return submit();
		}

		@Override
		public AbstractAction submit() {
			return this;
		}
	}
}
