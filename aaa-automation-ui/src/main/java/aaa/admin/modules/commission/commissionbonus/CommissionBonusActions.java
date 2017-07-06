/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.commission.commissionbonus;

import aaa.admin.modules.commission.commissionbonus.defaulttabs.AddCommissionBonusTab;
import aaa.admin.modules.commission.commissionbonus.views.DefaultView;
import aaa.admin.pages.commission.CommissionPage;
import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.common.pages.Page;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.CommissionConstants;
import toolkit.datax.TestData;

public final class CommissionBonusActions {

    private CommissionBonusActions() {}

    public static class ExpireCommissionBonus extends AbstractAction {
        @Override
        public String getName() {
            return "Expire Commission Bonus";
        }

        @Override
        public Workspace getView() {
            return null;
        }

        public AbstractAction start(int rowNumber) {
            CommissionPage.tableCommissionBonus.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
            CommissionPage.buttonExpireCommissionBonus.click();
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
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(int rowNumber) instead.");
        }
    }

    public static class EditCommissionBonus extends AbstractAction {
        @Override
        public String getName() {
            return "Edit Commission Bonus";
        }

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

        public AbstractAction start(int rowNumber) {
            CommissionPage.tableCommissionBonus.getRow(rowNumber).getCell(CommissionConstants.CommissionBonusTable.ACTION).controls.links.get(ActionConstants.EDIT).click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction submit() {
            AddCommissionBonusTab.buttonSave.click();
            return this;
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported. Use perform(int rowNumber, TestData td) instead");
        }
    }

    public static class DeleteCommissionBonus extends AbstractAction {
        @Override
        public String getName() {
            return "Delete Commission Bonus";
        }

        @Override
        public Workspace getView() {
            return null;
        }

        public AbstractAction start(int rowNumber) {
            CommissionPage.tableCommissionBonus.getRow(rowNumber).getCell(CommissionConstants.CommissionBonusTable.ACTION).controls.links.get(ActionConstants.DELETE).click();
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
            throw new UnsupportedOperationException("start() method is not supported for this action. Use start(int rowNumber) instead");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported. Use perform(int rowNumber) instead");
        }
    }
}
