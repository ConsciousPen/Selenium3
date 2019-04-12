/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy;

import static aaa.main.pages.summary.PolicySummaryPage.tableDifferences;
import org.openqa.selenium.By;
import aaa.common.AbstractAction;
import aaa.common.Tab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.modules.policy.auto_ss.actiontabs.UpdateRulesOverrideActionTab;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Set of abstract classes describing all actions available for the product entities of each type.
 * Modify this class if the set of actions for a particular product entity has to be changed.
 *
 * @category Generated
 */
public final class PolicyActions {
	public static Button buttonPurchase = new Button(By.xpath("//input[@value = 'Purchase' and not(@class = 'hidden') and not(contains(@style,'none'))]"));
	public static Button buttonRollOnChanges = new Button(By.xpath("//input[@value = 'Roll On Changes' and not(@class = 'hidden') and not(contains(@style,'none'))]"));
	public static Button buttonOk = new Button(By.xpath("//*[@id='headerForm']//input[contains(@value, 'OK')]"));

	private PolicyActions() {
	}

	public abstract static class Endorse extends AbstractAction {
		@Override
		public String getName() {
			return "Endorsement";
		}

		/**
		 * Fill Endorsement action tab, confirm endorsement and stay in Data Gathering mode</br>
		 * Use method like policy.getDefaultView().fillUpTo(td, tabClass) after this.
		 *
		 * @param td - test data for filling Endorsement action tab
		 */
		@Override
		public AbstractAction perform(TestData td) {
			return super.perform(td);
		}

		@Override
		public AbstractAction submit() {
			buttonOk.click();
			if (Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()) {
				Page.dialogConfirmation.confirm();
			}
			return this;
		}

		/**
		 * Fill Endorsement action tab, confirm endorsement and press Save and Exit without filling policy
		 *
		 * @param td - test data for filling Endorsement action tab
		 */
		public AbstractAction performAndExit(TestData td) {
			start();
			getView().fill(td);
			submit();
			Tab.buttonSaveAndExit.click();
			return this;
		}

		/**
		 * Fill Endorsement action tab, confirm endorsement, fill policy endorsement and purchase
		 *
		 * @param td - test data for filling Endorsement action tab and policy endorsement
		 */
		public abstract AbstractAction performAndFill(TestData td);
	}

	public abstract static class PriorTermEndorsement extends AbstractAction {
		@Override
		public String getName() {
			return "Prior Term Endorsement";
		}

		/**
		 * Fill Endorsement action tab, confirm endorsement and stay in Data Gathering mode</br>
		 * Use method like policy.getDefaultView().fillUpTo(td, tabClass) after this.
		 *
		 * @param td - test data for filling Endorsement action tab
		 */
		@Override
		public AbstractAction perform(TestData td) {
			return super.perform(td);
		}

		@Override
		public AbstractAction submit() {
			buttonOk.click();
			if (Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()) {
				Page.dialogConfirmation.confirm();
			}
			return this;
		}

		/**
		 * Fill Endorsement action tab, confirm endorsement and press Save and Exit without filling policy
		 *
		 * @param td - test data for filling Endorsement action tab
		 */
		public AbstractAction performAndExit(TestData td) {
			start();
			getView().fill(td);
			submit();
			Tab.buttonSaveAndExit.click();
			return this;
		}

		/**
		 * Fill Endorsement action tab, confirm endorsement, fill policy endorsement and purchase
		 *
		 * @param td - test data for filling Endorsement action tab and policy endorsement
		 */
		public abstract AbstractAction performAndFill(TestData td);

	}

	public abstract static class Renew extends AbstractAction {
		@Override
		public String getName() {
			return "Renew";
		}

		/**
		 * Perform Renew action withot changing any values, confirm and stay in Data Gathering mode</br>
		 * Use method like policy.getDefaultView().fillUpTo(td, tabClass) after this.
		 */
		public AbstractAction perform() {
			return super.perform(new SimpleDataProvider());
		}

		/**
		 * Fill Renew action tab, confirm and stay in Data Gathering mode</br>
		 * Use method like policy.getDefaultView().fillUpTo(td, tabClass) after this.
		 *
		 * @param td - test data for filling Renew action tab
		 */
		@Override
		public AbstractAction perform(TestData td) {
			return super.perform(td);
		}

		@Override
		public AbstractAction submit() {
			buttonOk.click();
			// Renew with lapse
			if (Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()) {
				Page.dialogConfirmation.confirm();
			}
			return this;
		}

		/**
		 * Perform Renew action without changing any values, then click Save and Exit without filling policy
		 */
		public AbstractAction performAndExit() {
			return performAndExit(new SimpleDataProvider());
		}

		/**
		 * Fill Renew action tab, confirm and press Save and Exit without filling policy
		 *
		 * @param td - test data for filling Renew action tab
		 */
		public AbstractAction performAndExit(TestData td) {
			start();
			getView().fill(td);
			submit();
			Tab.buttonSaveAndExit.click();
			return this;
		}

		/**
		 * Fill Renew action tab, confirm, fill policy renewal and purchase
		 *
		 * @param td - test data for filling Renew action tab and policy renewal data
		 */
		public abstract AbstractAction performAndFill(TestData td);
	}

	public abstract static class Bind extends AbstractAction {
		@Override
		public String getName() {
			return "Bind";
		}
	}

	public abstract static class Cancel extends AbstractAction {
		@Override
		public String getName() {
			return "Cancellation";
		}

		@Override
		public AbstractAction perform(TestData td) {
			return super.perform(td);
		}

	}

	public abstract static class CancelNotice extends AbstractAction {
		@Override
		public String getName() {
			return "Cancel Notice";
		}

		@Override
		public AbstractAction submit() {
			buttonOk.click();
			return this;
		}
	}

	public abstract static class ChangeBrokerRequest extends AbstractAction {
		@Override
		public String getName() {
			return "Change Broker";
		}

		@Override
		public AbstractAction submit() {
			buttonOk.click();
			return this;
		}
	}

	public abstract static class ChangeReinstatementLapse extends AbstractAction {
		@Override
		public String getName() {
			return "Change Reinstatement Lapse Period";
		}
	}

	public abstract static class CopyQuote extends AbstractAction {
		@Override
		public String getName() {
			return "Copy from Quote";
		}
	}

	public abstract static class DataGather extends AbstractAction {
		@Override
		public String getName() {
			return "Data Gathering";
		}

		@Override
		public AbstractAction submit() {
			Tab.buttonSaveAndExit.click();
			return this;
		}
	}

	public abstract static class DeclineByCompanyQuote extends AbstractAction {
		@Override
		public String getName() {
			return "Company Decline";
		}
	}

	public abstract static class DeclineByCustomerQuote extends AbstractAction {
		@Override
		public String getName() {
			return "Customer Decline";
		}
	}

	public abstract static class DeleteCancelNotice extends AbstractAction {
		@Override
		public String getName() {
			return "Remove Cancel Notice";
		}
	}

	public abstract static class DoNotRenew extends AbstractAction {
		@Override
		public String getName() {
			return "Do Not Renew";
		}
	}

	public abstract static class ManualRenew extends AbstractAction {
		@Override
		public String getName() {
			return "Manual Renew";
		}
	}

	public abstract static class PolicyCopy extends AbstractAction {
		@Override
		public String getName() {
			return "Copy from Policy";
		}
	}

	public abstract static class PolicyDocGen extends AbstractAction {
		@Override
		public String getName() {
			return "On-Demand Documents";
		}
	}

	public abstract static class PolicyInquiry extends AbstractAction {
		@Override
		public String getName() {
			return "Inquiry";
		}
	}

	public abstract static class PolicySpin extends AbstractAction {
		@Override
		public String getName() {
			return "Spin";
		}

		@Override
		public AbstractAction perform(TestData td) {
			start();
			getView().fill(td);
			for (String v : td.getValue("SpinActionTab", "Drivers").split(",")) {
				new CheckBox(By.id("policyDataGatherForm:cb_splitToNew_Drivers_" + (Integer.parseInt(v) - 1))).setValue(true);
				new CheckBox(By.id("policyDataGatherForm:cb_primaryInsured_Drivers_" + (Integer.parseInt(v) - 1))).setValue(true);
			}
			for (String v : td.getValue("SpinActionTab", "Vehicles").split(",")) {
				new CheckBox(By.id("policyDataGatherForm:cb_splitToNew_Vehicles_" + (Integer.parseInt(v) - 1))).setValue(true);
			}
			return submit();
		}
	}

	public abstract static class PolicySplit extends AbstractAction {
		@Override
		public String getName() {
			return "Split";
		}

		@Override
		public AbstractAction perform(TestData td) {
			start();
			getView().fill(td);
			for (String v : td.getValue("SplitActionTab", "Insureds").split(",")) {
				new CheckBox(By.id("policyDataGatherForm:cb_splitToNew_Insureds_" + (Integer.parseInt(v) - 1))).setValue(true);
				new CheckBox(By.id("policyDataGatherForm:cb_primaryInsured_Insureds_" + (Integer.parseInt(v) - 1))).setValue(true);
			}
			for (String v : td.getValue("SplitActionTab", "Vehicles").split(",")) {
				new CheckBox(By.id("policyDataGatherForm:cb_splitToNew_Vehicles_" + (Integer.parseInt(v) - 1))).setValue(true);
			}
			return submit();
		}
	}

	public abstract static class Rewrite extends AbstractAction {
		@Override
		public String getName() {
			return "Rewrite Policy";
		}
		//@Override
		// public AbstractAction submit() {
		//     super.submit();
		//Tab.buttonSaveAndExit.click();
		//     return this;
		// }
	}

	public abstract static class Propose extends AbstractAction {
		@Override
		public String getName() {
			return "Propose";
		}

		@Override
		public AbstractAction submit() {
			buttonOk.click();
			return this;
		}
	}

	public abstract static class QuoteDocGen extends AbstractAction {
		@Override
		public String getName() {
			return "On-Demand Documents";
		}
	}

	public abstract static class QuoteInquiry extends AbstractAction {
		@Override
		public String getName() {
			return "Inquiry";
		}
	}

	public abstract static class Reinstate extends AbstractAction {
		@Override
		public String getName() {
			return "Reinstatement";
		}
	}

	public abstract static class RemoveDoNotRenew extends AbstractAction {
		@Override
		public String getName() {
			return "Remove Do Not Renew";
		}

		@Override
		public AbstractAction submit() {
			buttonOk.click();
			return this;
		}
	}

	public abstract static class RemoveManualRenew extends AbstractAction {
		@Override
		public String getName() {
			return "Remove Manual Renew";
		}

		@Override
		public AbstractAction submit() {
			buttonOk.click();
			return this;
		}
	}

	public abstract static class RollBackEndorsement extends AbstractAction {
		@Override
		public String getName() {
			return "Roll Back Endorsement";
		}

		@Override
		public AbstractAction start() {
			log.info(getName() + " action initiated.");
			NavigationPage.setActionAndGo(getName());
			if (Page.dialogConfirmation.isPresent()) {
				Page.dialogConfirmation.confirm();
			}
			return this;
		}
	}

	public abstract static class RollOn extends AbstractAction {
		@Override
		public String getName() {
			return "Roll On Changes";
		}

		@Override
		public AbstractAction submit() {
			buttonRollOnChanges.click();
			return this;
		}

		/*       public AbstractAction perform(boolean isAutomatic, boolean setOldValues) {
				   start();

				   Table tableOosEndorsements = new Table(By.id("affectedEndoresmentForm:historyTable"));
				   int rowsCount = tableOosEndorsements.getRowsCount();
				   int columnsCount = tableOosEndorsements.getColumnsCount();

				   for (int i = 1; i <= rowsCount; i++) {
					   tableOosEndorsements.getRow(i).getCell(columnsCount).controls.links.get(
							   isAutomatic ? 1 : 2).click();
				   }

				   if (!isAutomatic) {
					   Table tableDifferences = new Table(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']/table"));
					   rowsCount = tableDifferences.getRowsCount();
					   columnsCount = tableDifferences.getColumnsCount();

					   //expand rows
					   for (int i = 0; i < rowsCount; i++) {
						   new Link(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']//tr[@id='comparisonTreeForm:comparisonTree_node_" + i
								   + "']/td[1]/span[contains(@class, 'ui-treetable-toggler')]")).click();
					   }

					   //apply values
					   Link linkSetValue;
					   rowsCount = tableDifferences.getRowsCount();
					   for (int i = 1; i <= rowsCount; i++) {
						   linkSetValue = tableDifferences.getRow(i).getCell(columnsCount).controls.links.get(
								   setOldValues ? 2 : 1);

						   if (linkSetValue.isPresent() && linkSetValue.isVisible()) {
							   linkSetValue.click();
						   }
					   }
				   }
				   return submit();
			   }
		   }
		   */
		public AbstractAction perform(boolean isAutomatic, boolean setCurrentValues) {
			openConflictPage(isAutomatic);

			int rowsCount;
			int columnsCount;

			if (tableDifferences.isPresent()) {
				columnsCount = tableDifferences.getColumnsCount();

				//expand rows
				boolean expandRows;
				do {
					Link linkTriangle = new Link(By.xpath("//tr[contains(@aria-expanded, 'false')]//span[contains(@class,'ui-treetable-toggler ui-icon ui-c ui-icon-triangle-1-e') and not(contains(@style, 'hidden'))]"));
					if (linkTriangle.isPresent() && linkTriangle.isVisible()) {
						linkTriangle.click();
						expandRows = true;
						continue;
					}
					expandRows = false;
				}
				while (expandRows);

				//apply values
				Link linkSetValue;
				rowsCount = tableDifferences.getRowsCount();
				for (int i = 1; i <= rowsCount; i++) {
					linkSetValue = tableDifferences.getRow(i).getCell(columnsCount).controls.links.get(
							setCurrentValues ? 1 : 2);

					if (linkSetValue.isPresent() && linkSetValue.isVisible()) {
						linkSetValue.click();
					}
				}
				submit();
			}
			return this; //submit();
		}

		public void openConflictPage(boolean isAutomatic) {
			start();

			Table tableOosEndorsements = new Table(By.id("affectedEndoresmentForm:historyTable"));
			int rowsCount = tableOosEndorsements.getRowsCount();
			int columnsCount = tableOosEndorsements.getColumnsCount();

			for (int i = 1; i <= rowsCount; i++) {
				if (tableOosEndorsements.getRow(i).getCell(columnsCount).getValue().contains("Automatic") &&
						tableOosEndorsements.getRow(i).getCell(columnsCount).controls.links.get(isAutomatic ? 1 : 2).isPresent()) {
					tableOosEndorsements.getRow(i).getCell(columnsCount).controls.links.get(isAutomatic ? 1 : 2).click();
				} else if (tableOosEndorsements.getRow(i).getCell(columnsCount).getValue().contains("Roll On") &&
						tableOosEndorsements.getRow(i).getCell(columnsCount).controls.links.getFirst().isPresent()) {
					tableOosEndorsements.getRow(i).getCell(columnsCount).controls.links.getFirst().click();
				}
				if (Page.dialogConfirmation.isPresent()) {
					Page.dialogConfirmation.confirm();
				}
			}
		}

		/**
		 * Perform RollOn action without fill Conflict page. 
		 * Fill opened Differences tab 
		 */
		public AbstractAction perform(boolean setCurrentValues) {
			int rowsCount;
			int columnsCount;

			if (tableDifferences.isPresent()) {
				rowsCount = tableDifferences.getRowsCount();
				columnsCount = tableDifferences.getColumnsCount();

				//expand rows
				
				for (int i = 0; i < rowsCount; i++) {
					Link linkTriangle = new Link(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']//tr[@id='comparisonTreeForm:comparisonTree_node_" + i
							+ "']/td[1]/span[contains(@class, 'ui-treetable-toggler ui-icon ui-c ui-icon-triangle-1-e')]"));
					if (linkTriangle.isPresent() && linkTriangle.isVisible()) {
						linkTriangle.click();
					}
				}

				//apply values
				Link linkSetValue;
				rowsCount = tableDifferences.getRowsCount();
				for (int i = 1; i <= rowsCount; i++) {
					linkSetValue = tableDifferences.getRow(i).getCell(columnsCount).controls.links.get(
							setCurrentValues ? 1 : 2);

					if (linkSetValue.isPresent() && linkSetValue.isVisible()) {
						linkSetValue.click();
					}
				}
				submit();
			}
			return this;
		}
	}

	public abstract static class SuspendQuote extends AbstractAction {
		@Override
		public String getName() {
			return "Suspend Quote";
		}
	}

	public abstract static class DeletePendedTransaction extends AbstractAction {
		@Override
		public String getName() {
			return "Delete Pended Transaction";
		}

		@Override
		public AbstractAction submit() {
			buttonOk.click();
			return this;
		}
	}

	public abstract static class DeletePendingRenwals extends AbstractAction {
		@Override
		public String getName() {
			return "Delete Pending Renewals";
		}

		@Override
		public AbstractAction submit() {
			buttonOk.click();
			return this;
		}
	}

	public abstract static class NonPremiumBearingEndorsement extends AbstractAction {
		@Override
		public String getName() {
			return "Update Insured/Interest Info";
		}

		@Override
		public AbstractAction submit() {
			buttonOk.click();
			return this;
		}
	}

	public abstract static class RescindCancellation extends AbstractAction {
		@Override
		public String getName() {
			return "Rescind Cancellation";
		}

		@Override
		public AbstractAction perform(TestData td) {
			throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
		}

		public AbstractAction perform() {
			start();
			return submit();
		}
	}

	public abstract static class UpdateRulesOverride extends AbstractAction {
		@Override
		public String getName() {
			return "Update Rules Override";
		}

		@Override
		public AbstractAction submit() {
			UpdateRulesOverrideActionTab.btnUpdateOverride.click();
			return this;
		}
	}

	public abstract static class ManualRenewalWithOrWithoutLapse extends AbstractAction {
		@Override
		public String getName() {
			return "Manual Renewal with or without Lapse";
		}
	}

	//TODO Remove next actions if not used in AAA:
	public abstract static class RemoveSuspendQuote extends AbstractAction {
		@Override
		public AbstractAction submit() {
			Page.dialogConfirmation.confirm();
			Tab.buttonSaveAndExit.click();
			return this;
		}
	}

	public abstract static class ChangeRenewalQuoteLapse extends AbstractAction {
	}

	public abstract static class PendedEndorsementChange extends AbstractAction {
	}

	public abstract static class PolicyChangeRenewalLapse extends AbstractAction {
	}

	public abstract static class InitiateHOQuote extends AbstractAction {

		@Override
		public String getName() {
			return "Initiate HO Quote";
		}

		/**
		 * Perform Initiate HO Quote action without changing any values, confirm and stay in Data Gathering mode</br>
		 * Use method like policy.getDefaultView().fillUpTo(td, tabClass) after this.
		 */
		public AbstractAction perform() {
			return perform(new SimpleDataProvider());
		}
	}

}
