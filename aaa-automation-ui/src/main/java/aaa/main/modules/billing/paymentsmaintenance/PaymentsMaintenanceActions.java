/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.billing.paymentsmaintenance;

import java.util.List;

import org.openqa.selenium.By;

import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.RadioButton;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Table;
import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.common.pages.Page;
import aaa.main.metadata.PaymentsMaintenanceMetaData;
import aaa.main.modules.billing.paymentsmaintenance.actiontabs.AddBulkPaymentActionTab;
import aaa.main.modules.billing.paymentsmaintenance.actiontabs.AddPaymentBatchActionTab;
import aaa.main.modules.billing.paymentsmaintenance.actiontabs.AddSuspenseActionTab;
import aaa.main.modules.billing.paymentsmaintenance.actiontabs.ClearSuspenseActionTab;
import aaa.main.modules.billing.paymentsmaintenance.views.AddBulkPaymentView;
import aaa.main.modules.billing.paymentsmaintenance.views.AddPaymentBatchView;
import aaa.main.modules.billing.paymentsmaintenance.views.AddSuspenseView;
import aaa.main.modules.billing.paymentsmaintenance.views.ClearSuspenseView;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.billing.PaymentsAndBillingMaintenancePage;

public final class PaymentsMaintenanceActions {

	public static class AddBulkPayment extends AbstractAction {
		@Override
		public String getName() {
			return "Add Bulk Payment";
		}

		@Override
		public Workspace getView() {
			return new AddBulkPaymentView();
		}

		@Override
		public AbstractAction start() {
			BillingSummaryPage.buttonPaymentsBillingMaintenance.click();
			PaymentsAndBillingMaintenancePage.buttonAddBulkPayment.click();
			return this;
		}

		public AbstractAction perform(TestData td, String bulkAmount, String policy, String allocatedAmounts) {
			td.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.BULK_AMOUNT.getLabel()),
				bulkAmount)
				.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.ALLOCATION.getLabel()),
					policy)
				.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.class.getSimpleName(),
					PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.ALLOCATED_AMOUNT.getLabel()), allocatedAmounts);
			return super.perform(td);
		}

		public AbstractAction perform(TestData td, String bulkAmount, String allocatedAmounts) {
			td.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.BULK_AMOUNT.getLabel()),
				bulkAmount)
				.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.class.getSimpleName(),
					PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.ALLOCATED_AMOUNT.getLabel()), allocatedAmounts);
			return super.perform(td);
		}

		@Override
		public AbstractAction perform(TestData td) {
			throw new UnsupportedOperationException(
				"perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, String bulkAmount, String allocatedAmounts) instead.");
		}

		@Override
		public AbstractAction submit() {
			AddBulkPaymentActionTab.buttonOk.click();
			return this;
		}
	}

	public static class AddSuspense extends AbstractAction {
		@Override
		public String getName() {
			return "Add Suspense";
		}

		@Override
		public Workspace getView() {
			return new AddSuspenseView();
		}

		@Override
		public AbstractAction start() {
			BillingSummaryPage.buttonPaymentsBillingMaintenance.click();
			PaymentsAndBillingMaintenancePage.buttonAddSuspense.click();
			return this;
		}

		public AbstractAction perform(TestData td, String amount, String reference) {
			td.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddSuspenseActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.AddSuspenseActionTab.SUSPENSE_AMOUNT.getLabel()), amount)
				.adjust(TestData
					.makeKeyPath(PaymentsMaintenanceMetaData.AddSuspenseActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.AddSuspenseActionTab.SUSPENSE_REFERENCE.getLabel()),
					reference);
			return super.perform(td);
		}

		// Add Suspense to #BA
		public void perform(TestData td, String amount, String reference, String billingAccount, List<String> associatedAmount) {
			td.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddSuspenseActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.AddSuspenseActionTab.SUSPENSE_AMOUNT.getLabel()), amount)
				.adjust(TestData
					.makeKeyPath(PaymentsMaintenanceMetaData.AddSuspenseActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.AddSuspenseActionTab.SUSPENSE_REFERENCE.getLabel()),
					reference);
			perform(td);

			Button buttonSelectBusinessName = new Button(By.id("suspenseForm:selectBusinessCusomerBtn"));
			if (buttonSelectBusinessName.isPresent()) {
				buttonSelectBusinessName.click();
				// Enter Billing Account name in Search PopUp
				new TextBox(By.id("customerSearch:billingAccountNo")).setValue(billingAccount);
				// Search Customer
				new Button(By.id("customerSearch:searchBtn")).click();
				// Select Customer in Search Results
				new Link(By.id("customerSearch:payorSearchResults:0:selectBusinessCustomerLink")).click();
				// Search BA
				new Button(By.id("suspenseForm:showBillingAccountSearchPopupLnk")).click();
				// Enter Billing Account name in Search PopUp
				new TextBox(By.id("billingAccountSearch:accountNumber")).setValue(billingAccount);
				new Button(By.id("billingAccountSearch:searchBtn")).click();
				// Select BA
				new CheckBox(By.id("billingAccountSearch:accountSearchResults:0:selectedCbk")).setValue(true);
				new Button(By.id("billingAccountSearch:addBtn")).click();

				int i = 0;
				for (String value : associatedAmount) {
					new TextBox(By.id(String.format("suspenseForm:accountAssociationResults:%d:amountInput", i))).setValue(value);
					i++;
				}
			}
			submit();
		}

		@Override
		public AbstractAction perform(TestData td) {
			start();
			getView().fill(td);
			return this;
		}

		@Override
		public AbstractAction submit() {
			AddSuspenseActionTab.buttonOk.click();
			return this;
		}
	}

	public static class AddPaymentBatch extends AbstractAction {
		@Override
		public String getName() {
			return "Add Payment Batch";
		}

		@Override
		public Workspace getView() {
			return new AddPaymentBatchView();
		}

		@Override
		public AbstractAction start() {
			BillingSummaryPage.buttonPaymentsBillingMaintenance.click();
			PaymentsAndBillingMaintenancePage.buttonAddPaymentBatch.click();
			return this;
		}

		// Batch Payment to Suspend Payment
		public void perform(TestData td, String numberOfPayments, String totalAmount, String batchReference, List<String> amount,
			List<String> checkNumber, List<String> billingAccount) {
			td.adjust(
				TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddPaymentBatchActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.AddPaymentBatchActionTab.NUMBER_OF_PAYMENTS.getLabel()),
				numberOfPayments)
				.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddPaymentBatchActionTab.class.getSimpleName(),
					PaymentsMaintenanceMetaData.AddPaymentBatchActionTab.TOTAL_AMOUNT.getLabel()), totalAmount)
				.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddPaymentBatchActionTab.class.getSimpleName(),
					PaymentsMaintenanceMetaData.AddPaymentBatchActionTab.BATCH_REFERENCE.getLabel()), batchReference);
			perform(td);

			Table tablePaymentList = new Table(By.xpath("//div[@id='paymentBatchForm:batchPaymentsList']/div/table"));
			if (tablePaymentList.isPresent()) {
				int i = 0;
				for (String value : amount) {
					new TextBox(By.id(String.format("paymentBatchForm:batchPaymentsList:%d:amountInput", i))).setValue(value);
					i++;
				}
				int j = 0;
				for (String value : checkNumber) {
					new TextBox(By.id(String.format("paymentBatchForm:batchPaymentsList:%d:chequeNumber", j))).setValue(value);
					i++;
				}
				int k = 0;
				for (String value : billingAccount) {
					new Button(By.id(String.format("paymentBatchForm:batchPaymentsList:%d:selectPayorLink", k))).click();
					new TextBox(By.id("paymentBatchForm:billingAccountNo")).setValue(value);
					new Button(By.id("paymentBatchForm:searchBtn")).click();
					new Button(By.id("paymentBatchForm:payorSearchResults:0:selectBusinessCustomerLink")).click();
					i++;
				}
				int l = 0;
				for (String value : billingAccount) {
					new Button(By.id(String.format("paymentBatchForm:batchPaymentsList:%d:allocateLink", l))).click();
					new RadioButton(By.id("batchPaymentItemForm:allocation_Action_radio:1")).setValue(true);
					new ComboBox(By.id("batchPaymentItemForm:billingAccountSelection")).setValue(value);
					AddPaymentBatchActionTab.buttonOk.click();
					i++;
				}

				submit();
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
			AddPaymentBatchActionTab.buttonStartAllocations.click();
			Page.dialogConfirmation.confirm();
			return this;
		}
	}

	public static class ClearSuspense extends AbstractAction {
		@Override
		public String getName() {
			return "Clear Suspense";
		}

		@Override
		public Workspace getView() {
			return new ClearSuspenseView();
		}

		@Override
		public AbstractAction start() {
			BillingSummaryPage.buttonPaymentsBillingMaintenance.click();
			PaymentsAndBillingMaintenancePage.buttonClearSuspense.click();
			return this;
		}

		@Override
		public AbstractAction submit() {
			ClearSuspenseActionTab.buttonOk.click();
			return this;
		}

		public AbstractAction perform(TestData td, String policy) {
			td.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.ClearSuspenseActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.ClearSuspenseActionTab.ALLOCATION.getLabel(),
				PaymentsMaintenanceMetaData.ClearSuspenseActionTab.AllocationMultiSelector.POLICY_NUMBER.getLabel()), policy);
			start();
			getView().fill(td);
			return submit();
		}

		public AbstractAction perform(TestData td, String policy, String suspenseReference, String paymentAmount) {
			td.adjust(
				TestData.makeKeyPath(PaymentsMaintenanceMetaData.SearchSuspenseActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.SearchSuspenseActionTab.SUSPENSE_REFERENCE.getLabel()),
				suspenseReference)
				.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.ClearSuspenseActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.ClearSuspenseActionTab.ALLOCATION.getLabel(),
					PaymentsMaintenanceMetaData.ClearSuspenseActionTab.AllocationMultiSelector.POLICY_NUMBER.getLabel()), policy)
				.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.ClearSuspenseActionTab.class.getSimpleName(), "Payment Amount"), paymentAmount);
			start();
			getView().fill(td);
			return submit();
		}

		@Override
		public AbstractAction perform(TestData td) {
			throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
		}
	}
}
