/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.cem.majorlargeaccount;

import aaa.admin.modules.cem.majorlargeaccount.actiontabs.EditMajorLargeAccountActionTab;
import aaa.admin.modules.cem.majorlargeaccount.views.DeleteMajorLargeAccountView;
import aaa.admin.modules.cem.majorlargeaccount.views.EditMajorLargeAccountView;
import aaa.admin.pages.cem.CemPage;
import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.common.pages.Page;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.CEMConstants;
import toolkit.datax.TestData;

public final class MajorLargeAccountActions {
    private MajorLargeAccountActions() {}

    public static class DeleteMajorLargeAccount extends AbstractAction {
        @Override
        public String getName() {
            return "Delete Major Large Account";
        }

        @Override
        public Workspace getView() {
            return new DeleteMajorLargeAccountView();
        }

        public AbstractAction start(int rowNumber) {
            CemPage.tableMajorLargeAccount.getRow(rowNumber).getCell(CEMConstants.CEMMajorLargeAccountTable.ACTION).controls.buttons.get("Delete").click();
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

    public static class EditMajorLargeAccount extends AbstractAction {
        @Override
        public String getName() {
            return "Edit Major Large Account";
        }

        @Override
        public Workspace getView() {
            return new EditMajorLargeAccountView();
        }

        public AbstractAction start(int rowNumber) {
            CemPage.tableMajorLargeAccount.getRow(rowNumber).getCell(CEMConstants.CEMMajorLargeAccountTable.ACTION).controls.links.get(ActionConstants.EDIT).click();
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
            EditMajorLargeAccountActionTab.buttonSave.click();
            return this;
        }
    }
}
