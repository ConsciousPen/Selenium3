/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.cem.groupsinformation;

import aaa.admin.modules.cem.groupsinformation.actiontabs.EditGroupInformationActionTab;
import aaa.admin.modules.cem.groupsinformation.views.DeleteGroupInformationView;
import aaa.admin.modules.cem.groupsinformation.views.EditGroupInformationView;
import aaa.admin.pages.cem.CemPage;
import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.common.pages.Page;
import aaa.main.enums.CEMConstants;
import toolkit.datax.TestData;

public final class GroupInformationActions {
    private GroupInformationActions() {}

    public static class DeleteGroupsInformation extends AbstractAction {
        @Override
        public String getName() {
            return "Delete Groups Information";
        }

        @Override
        public Workspace getView() {
            return new DeleteGroupInformationView();
        }

        public AbstractAction start(int rowNumber) {
            CemPage.tableGroupsInformation.getRow(rowNumber).getCell(CEMConstants.CEMGroupsInformationTable.ACTIONS).controls.buttons.get("Delete").click();
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
            Page.dialogConfirmation.confirm();
            return this;
        }
    }

    public static class EditGroupsInformation extends AbstractAction {
        @Override
        public String getName() {
            return "Edit Groups Information";
        }

        @Override
        public Workspace getView() {
            return new EditGroupInformationView();
        }

        public AbstractAction start(int rowNumber) {
            CemPage.tableGroupsInformation.getRow(rowNumber).getCell(CEMConstants.CEMGroupsInformationTable.ACTIONS).controls.buttons.get("Edit").click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(int rowNumber, TestData td) instead.");
        }

        @Override
        public AbstractAction submit() {
            EditGroupInformationActionTab.buttonSave.click();
            return this;
        }
    }
}
