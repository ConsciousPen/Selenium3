/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.billing.account;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.common.AbstractAction;
import aaa.common.Tab;
import aaa.common.Workspace;
import aaa.common.pages.Page;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.AddHoldActionTab;
import aaa.main.modules.billing.account.actiontabs.DeclinePaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.MovePoliciesActionTab;
import aaa.main.modules.billing.account.actiontabs.OtherTransactionsActionTab;
import aaa.main.modules.billing.account.actiontabs.RefundActionTab;
import aaa.main.modules.billing.account.actiontabs.TransferPaymentBenefitsActionTab;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.billing.account.views.AcceptPaymentView;
import aaa.main.modules.billing.account.views.AddHoldView;
import aaa.main.modules.billing.account.views.DeclinePaymentView;
import aaa.main.modules.billing.account.views.DiscardBillView;
import aaa.main.modules.billing.account.views.GenerateFutureStatementView;
import aaa.main.modules.billing.account.views.MovePoliciesView;
import aaa.main.modules.billing.account.views.OtherTransactionsView;
import aaa.main.modules.billing.account.views.RefundView;
import aaa.main.modules.billing.account.views.RegenerateBillView;
import aaa.main.modules.billing.account.views.RemoveHoldView;
import aaa.main.modules.billing.account.views.TransferPaymentBenefitsView;
import aaa.main.modules.billing.account.views.TransferPaymentView;
import aaa.main.modules.billing.account.views.UnallocatePaymentView;
import aaa.main.modules.billing.account.views.UpdateBillingAccountView;
import aaa.main.modules.billing.account.views.ViewModalPremiumView;
import aaa.main.modules.billing.account.views.WaiveFeeView;
import aaa.main.pages.summary.BillingSummaryGBPage;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.RadioButton;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Row;
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
	            new Link(By.linkText("Accept Payment")).click();
	            return this;
	        }

	        public AbstractAction perform(TestData td, String amount) {
	            td.adjust(TestData.makeKeyPath(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel()), amount);
	            return super.perform(td);
	        }

	        public void perform(TestData td, String amount, List<String> allocations, String referenceNumber) {
	            td.adjust(TestData.makeKeyPath(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel()), amount)
	                    .adjust(TestData.makeKeyPath(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.AcceptPaymentActionTab.ALLOCATIONS.getLabel()),
	                            allocations);
	            super.perform(td);

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
	            super.perform(td);
	        }

	        //Manual Allocation to Billable items
	        public void perform(TestData td, String amount, List<String> allocations, int tableAllocationsRowNumber,
	                int tableAllocationsIntoBGroupsRowNumber, Map<String, Dollar> amountsByCoverage, String referenceNumber) {
	            td.adjust(TestData.makeKeyPath(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel()), amount)
	                    .adjust(TestData.makeKeyPath(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.AcceptPaymentActionTab.ALLOCATIONS.getLabel()),
	                            allocations);
	            perform(td);

	            Table tableAllocations = new Table(By.xpath("//div[@id='paymentForm:invoicesDistributionsTable']/div/table"));
	            if (tableAllocations.isPresent()) {

	                tableAllocations.getRow(tableAllocationsRowNumber).getCell(BillingConstants.BillingAllocationsTable.INVOICE).controls.links.getFirst().click();
	                new RadioButton(By.id("advAllocationForm:automaticDestination_radio:1")).setValue(true);
	                Table tableAllocationsIntoBillingGroups = new Table(By.xpath("//div[@id='advAllocationForm:invoiceDistributionsTable_0']/div/table"));
	                tableAllocationsIntoBillingGroups.getRow(tableAllocationsIntoBGroupsRowNumber).getCell(BillingConstants.BillingAllocationsIntoGroupsTable.BILLING_GROUP).controls.links.getFirst()
	                        .click();

	                fillManualAllocationsByCoverage(amountsByCoverage);

	                submit();
	                submit();
	                submit();

	                Table tableSuspendRemainingError = new Table(By.id("paymentForm:error_message"));
	                if (tableSuspendRemainingError.isPresent()) {
	                    new RadioButton(By.id("paymentForm:suspendRemainingAmount_radio:0")).setValue(true);
	                    new TextBox(By.id("paymentForm:cashReferenceNumber")).setValue(referenceNumber);
	                    submit();
	                }
	            }
	        }

	        private void fillManualAllocationsByCoverage(Map<String, Dollar> amountsByCoverage) {
	            Table tablePaymentAllocation = new Table(By.xpath("//div[@id='advAllocationForm:invoice_items_info_table']/div/table"));
	            for (Row row : tablePaymentAllocation.getRows()) {
	                String coverageCode = row.getCell(BillingConstants.BillingPaymentAllocationTable.COVERAGE).getValue();
	                Dollar remainingDue = new Dollar(row.getCell(BillingConstants.BillingPaymentAllocationTable.REMAINING_DUE).getValue());

	                Dollar amountToAllocate = amountsByCoverage.get(coverageCode);
	                if (amountToAllocate == null || amountToAllocate.isZero()) {
	                    continue;
	                }

	                Dollar allocatedAmount = remainingDue.min(amountToAllocate);
	                row.getCell(BillingConstants.BillingPaymentAllocationTable.AMOUNT_PAID).controls.textBoxes.getFirst().setValue(allocatedAmount.toString());

	                amountsByCoverage.put(coverageCode, amountToAllocate.subtract(allocatedAmount));

	            }
	        }

	        @Override
	        public AbstractAction perform(TestData td) {
	            start();
	            getView().fill(td);
	            return this;
	        }

	        @Override
	        public AbstractAction submit() {
	            AcceptPaymentActionTab.buttonOk.click();
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
	            OtherTransactionsActionTab.buttonOk.click();
	            return this;
	        }

	        public AbstractAction perform(TestData td, String amount) {
	            td.adjust(TestData.makeKeyPath(BillingAccountMetaData.OtherTransactionsActionTab.class.getSimpleName(), BillingAccountMetaData.OtherTransactionsActionTab.AMOUNT.getLabel()), amount);
	            return super.perform(td);
	        }

	        @Override
	        public AbstractAction perform(TestData td) {
	            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, String amount) instead.");
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
	            if (BillingSummaryGBPage.tableBillsAndStatments.isPresent()) {
	                BillingSummaryGBPage.tableBillsAndStatments.getRow(rowNumber).getCell(BillingConstants.BillingGBBillsAndStatmentsTable.ACTION).controls.links.get(
	                        ActionConstants.BillingBillsAndStatementsAction.DISCARD).click();
	            } else {
	                BillingSummaryPage.tableBillsStatements.getRow(rowNumber).getCell(BillingConstants.BillingBillsAndStatmentsTable.ACTIONS).controls.links.get(
	                        ActionConstants.BillingBillsAndStatementsAction.DISCARD).click();
	            }
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
	            if (BillingSummaryGBPage.tablePaymentsOtherTransactions.isPresent()) {
	                BillingSummaryGBPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.buttons.get("Unallocate")
	                        .click();
	            } else {
	                BillingSummaryPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.buttons.get("Unallocate")
	                        .click();
	            }
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
	            if (BillingSummaryGBPage.tableBillsAndStatments.isPresent()) {
	                BillingSummaryGBPage.tableBillsAndStatments.getRow(rowNumber).getCell(BillingConstants.BillingGBBillsAndStatmentsTable.ACTION).controls.links.get(
	                        ActionConstants.BillingBillsAndStatementsAction.REGENERATE).click();
	            } else {
	                BillingSummaryPage.tableBillsStatements.getRow(rowNumber).getCell(BillingConstants.BillingBillsAndStatmentsTable.ACTIONS).controls.links.get(
	                        ActionConstants.BillingBillsAndStatementsAction.DISCARD).click();
	            }
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
	            BillingSummaryPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(
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
	                            BillingAccountMetaData.TransferPaymentActionTab.AllocationMultiSelector.POLICY_NUMBER.getLabel()),
	                    policy)
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

	    public static class TransferPaymentBenefits extends AbstractAction {
	        @Override
	        public String getName() {
	            return "Transfer Payment";
	        }

	        @Override
	        public Workspace getView() {
	            return new TransferPaymentBenefitsView();
	        }

	        //FIXME(vmarkouski) - magic number
	        @Override
	        public AbstractAction start() {
	            return start(1);
	        }

	        public AbstractAction start(int rowNumber) {
	            BillingSummaryGBPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.buttons.get("Transfer").click();
	            return this;
	        }

	        public AbstractAction perform(TestData td, int rowNumber) {
	            start(rowNumber);
	            getView().fill(td);
	            return submit();
	        }

	        public AbstractAction perform(TestData td, String policy, String transferAmount) {
	            td.adjust(
	                    TestData.makeKeyPath(BillingAccountMetaData.TransferPaymentBenefitsActionTab.class.getSimpleName(),
	                            BillingAccountMetaData.TransferPaymentBenefitsActionTab.BILLING_ACCOUNT.getLabel(),
	                            BillingAccountMetaData.TransferPaymentBenefitsActionTab.BillingAccountSingleSelector.POLICY_NUMBER.getLabel()),
	                    policy)
	                    .adjust(TestData.makeKeyPath(BillingAccountMetaData.TransferPaymentBenefitsActionTab.class.getSimpleName(),
	                            BillingAccountMetaData.TransferPaymentBenefitsActionTab.TRANSFER_AMOUNT.getLabel()), transferAmount);
	            return perform(td);
	        }

	        public void perform(int rowNumber, String billingAccountNumber, String transferAmount) {
	            start(rowNumber);
	            new TextBox(By.id("paymentForm:transferAmount")).setValue(transferAmount);
	            new Button(By.id("paymentForm:showBillingAccountSearchPopupLnk")).click();
	            new TextBox(By.id("searchBillingAccountsForm:accountNumber")).setValue(billingAccountNumber);
	            new Button(By.id("searchBillingAccountsForm:searchBtn")).click();
	            Table tableAcountSearchResults = new Table(By.xpath("//div[@id='searchBillingAccountsForm:accountSearchResults']//table"));
	            tableAcountSearchResults.getRow(1).getCell(BillingConstants.BillingAccountsSearchResultTable.BILLING_ACCOUNT).controls.buttons.getFirst().click();
	            TransferPaymentBenefitsActionTab.buttonOk.click();
	        }

	        @Override
	        public AbstractAction submit() {
	            new Button(By.xpath("//input[@id='paymentForm:okButton_footer']")).click();
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
	            if (BillingSummaryGBPage.tablePaymentsOtherTransactions.isPresent()) {
	                BillingSummaryGBPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.buttons.get(
	                        ActionConstants.BillingPaymentsAndOtherTransactionAction.DECLINE).click();
	            } else {
	                BillingSummaryPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(
	                        ActionConstants.BillingPaymentsAndOtherTransactionAction.DECLINE).click();
	            }
	            return this;
	        }

	        public AbstractAction perform(TestData td, int rowNumber) {
	            start(rowNumber);
	            getView().fill(td);
	            return submit();
	        }

	        //Decline
	        public AbstractAction perform(TestData td, int rowNumber, String referenceNumber) {
	            start(rowNumber);
	            Table suspenseForDecline = new Table(By.xpath("//div[@id = 'billingDetailedForm:suspenseItemList']//table"));
	            if (suspenseForDecline.isPresent()) {
	                suspenseForDecline.getRowContains(BillingConstants.BillingSuspenseForDeclineTable.REFERENCE_NUMBER, referenceNumber).getCell(BillingConstants.BillingSuspenseForDeclineTable.ACTION).controls.links
	                        .get("Decline").click();
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
	            if (DeclinePaymentActionTab.buttonOk.isPresent()) {
	                DeclinePaymentActionTab.buttonOk.click();
	            } else {
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
	            MovePoliciesActionTab.buttonFinish.click();
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
	            BillingSummaryPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(
	                    ActionConstants.BillingPaymentsAndOtherTransactionAction.WAIVE).click();
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

	        public AbstractAction perform(TestData td, String amount) {
	            td.adjust(TestData.makeKeyPath(BillingAccountMetaData.RefundActionTab.class.getSimpleName(), BillingAccountMetaData.RefundActionTab.AMOUNT.getLabel()), amount);
	            return super.perform(td);
	        }

	        @Override
	        public AbstractAction perform(TestData td) {
	            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, String amount) instead.");
	        }

	        @Override
	        public AbstractAction submit() {
	            RefundActionTab.buttonOk.click();
	            return this;
	        }
	    }

	    public static class Update extends AbstractAction {
	        @Override
	        public String getName() {
	            return "Update Billing Account";
	        }

	        @Override
	        public Workspace getView() {
	            return new UpdateBillingAccountView();
	        }

	        @Override
	        public AbstractAction submit() {
	            UpdateBillingAccountActionTab.buttonSave.click();
	            return this;
	        }
	    }

	    public static class AddHold extends AbstractAction {
	        @Override
	        public String getName() {
	            return "Add/Update Hold for Billing Account";
	        }

	        @Override
	        public Workspace getView() {
	            return new AddHoldView();
	        }

	        @Override
	        public AbstractAction submit() {
	            AddHoldActionTab.buttonSave.click();
	            return this;
	        }
	    }

	    public static class RemoveHold extends AbstractAction {
	        @Override
	        public String getName() {
	            return "Remove Billing Account Hold";
	        }

	        @Override
	        public Workspace getView() {
	            return new RemoveHoldView();
	        }

	        @Override
	        public AbstractAction submit() {
	            new Button(By.id("holdForm:removeButton_footer")).click();
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
}
