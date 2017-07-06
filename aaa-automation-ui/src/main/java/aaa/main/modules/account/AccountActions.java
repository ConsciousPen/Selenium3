/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.account;

import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.common.pages.Page;
import aaa.main.enums.CustomerConstants;
import aaa.main.modules.account.actiontabs.AffinityGroupsTab;
import aaa.main.modules.account.views.AddAffinityGroupView;
import aaa.main.modules.account.views.AddDesignatedContactView;
import aaa.main.modules.account.views.CommunicationView;
import aaa.main.modules.account.views.EliminateAffinityGroupsView;
import aaa.main.modules.account.views.EliminateDesignatedContactView;
import aaa.main.modules.account.views.MoveCustomerToNewAccountView;
import aaa.main.modules.account.views.MoveCustomerView;
import aaa.main.modules.account.views.UpdateView;
import aaa.main.modules.customer.actiontabs.CommunicationActionTab;
import aaa.main.modules.customer.views.DefaultView;
import aaa.main.pages.summary.CustomerSummaryPage;
import toolkit.datax.TestData;

public final class AccountActions {
	private AccountActions() {}

    public static class AddCustomer extends AbstractAction {
        @Override
        public String getName() {
            return "Add Customer";
        }

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class EliminateCustomer extends AbstractAction {
        @Override
        public String getName() {
            return "Eliminate Customer";
        }

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

        public AbstractAction start(int rowNumber) {
            CustomerSummaryPage.tableCustomers.getRow(rowNumber).getCell(9).controls.links.getFirst().click();
            Page.dialogConfirmation.confirm();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class MoveCustomer extends AbstractAction {
        @Override
        public String getName() {
            return "Move Customer";
        }

        @Override
        public Workspace getView() {
            return new MoveCustomerView();
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class MoveCustomerToNewAccount extends AbstractAction {
        @Override
        public String getName() {
            return "Move Customer";
        }

        @Override
        public Workspace getView() {
            return new MoveCustomerToNewAccountView();
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class AddAffinityGroup extends AbstractAction {
        @Override
        public String getName() {
            return "Add Affinity Group";
        }

        @Override
        public Workspace getView() {
            return new AddAffinityGroupView();
        }

        @Override
        public AbstractAction start() {
            CustomerSummaryPage.buttonAddAffinityGroup.click();
            return this;
        }

        @Override
        public AbstractAction submit() {
            AffinityGroupsTab.buttonDone.click();
            return this;
        }
    }

    public static class EliminateAffinityGroup extends AbstractAction {
        @Override
        public String getName() {
            return "Eliminate Affinity Group";
        }

        @Override
        public Workspace getView() {
            return new EliminateAffinityGroupsView();
        }

        public AbstractAction start(int rowNumber) {
            CustomerSummaryPage.tableAffinityGroups.getRow(rowNumber).getCell(2).controls.links.getFirst().click();
            Page.dialogConfirmation.confirm();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {

            return this;
        }
    }

    public static class AddCommunication extends AbstractAction {
        @Override
        public String getName() {
            return "Add Communication";
        }

        @Override
        public Workspace getView() {
            return new CommunicationView();
        }

        @Override
        public AbstractAction start() {
            CustomerSummaryPage.buttonStartNewCommunication.click();
            return this;
        }

        @Override
        public AbstractAction submit() {
            CommunicationActionTab.buttonSave.click();
            return this;
        }
    }

    public static class RemoveCommunication extends AbstractAction {
        @Override
        public String getName() {
            return "Remove Communication";
        }

        @Override
        public Workspace getView() {
            return new CommunicationView();
        }

        public AbstractAction start(int rowNumber) {
            if (!CommunicationActionTab.tableCommunications.isVisible()) {
                CustomerSummaryPage.linkRelatedCommunications.click();
            }
            CommunicationActionTab.tableCommunications.getRow(rowNumber).getCell(CustomerConstants.CustomerCommunicationsTable.ACTION).controls.buttons.get("Delete").click();
            Page.dialogConfirmation.confirm();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
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
            return new UpdateView();
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class AddDesignatedContact extends AbstractAction {
        @Override
        public String getName() {
            return "Add Contact";
        }

        @Override
        public Workspace getView() {
            return new AddDesignatedContactView();
        }

        @Override
        public AbstractAction start() {
            CustomerSummaryPage.buttonAddContact.click();
            return this;
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class EliminateDesignatedContact extends AbstractAction {
        @Override
        public String getName() {
            return "Eliminate Designated Contact";
        }

        @Override
        public Workspace getView() {
            return new EliminateDesignatedContactView();
        }

        public AbstractAction start(int rowNumber) {
            CustomerSummaryPage.tableDesignatedContacts.getRow(rowNumber).getCell(4).controls.links.getFirst().click();
            Page.dialogConfirmation.confirm();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }
}
