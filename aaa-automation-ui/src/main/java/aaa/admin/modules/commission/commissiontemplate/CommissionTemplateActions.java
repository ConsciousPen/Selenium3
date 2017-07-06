/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.commission.commissiontemplate;

import aaa.admin.modules.commission.commissiontemplate.views.DefaultView;
import aaa.admin.pages.commission.CommissionTemplatePage;
import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.common.pages.Page;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.CommissionConstants;
import toolkit.datax.TestData;

public final class CommissionTemplateActions {

    private CommissionTemplateActions() {}

    public static class EditCommissionTemplate extends AbstractAction {
        @Override
        public String getName() {
            return "Edit Commission Template";
        }

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

        public AbstractAction start(int rowNumber) {
            CommissionTemplatePage.tableCommissionTemplate.getRow(rowNumber).getCell(CommissionConstants.CommissionTemplateTable.ACTIONS).controls.links.get(ActionConstants.EDIT).click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction submit() {
            CommissionTemplatePage.buttonOK.click();
            return this;
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber)");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported. Use perform(int rowNumber, TestData td)");
        }
    }

    public static class DeleteCommissionTemplate extends AbstractAction {
        @Override
        public String getName() {
            return "Delete Commission Template";
        }

        @Override
        public Workspace getView() {
            return null;
        }

        public AbstractAction start(int rowNumber) {
            CommissionTemplatePage.tableCommissionTemplate.getRow(rowNumber).getCell(CommissionConstants.CommissionTemplateTable.ACTIONS).controls.links.get(ActionConstants.DELETE).click();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction submit() {
            Page.dialogConfirmation.confirm();
            return this;
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method is not supported for this action. Use start(int rowNumber)");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported. Use perform(int rowNumber)");
        }
    }
}
