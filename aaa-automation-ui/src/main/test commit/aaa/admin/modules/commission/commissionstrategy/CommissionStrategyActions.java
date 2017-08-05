/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.commission.commissionstrategy;

import aaa.admin.pages.commission.CommissionPage;
import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.CommissionConstants;
import toolkit.datax.TestData;

public final class CommissionStrategyActions {

    private CommissionStrategyActions() {}

    public abstract static class AddCommissionRule extends AbstractAction {
        @Override
        public String getName() {
            return "Add Commission Rule";
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        public AbstractAction start(int rowNumber) {
            CommissionPage.tableCommissionStrategy.getRow(rowNumber).getCell(CommissionConstants.CommissionStrategyTable.ACTION).controls.links.get(ActionConstants.EDIT).click();
            return this;
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction submit() {
            CommissionPage.buttonSaveCommissionStrategy.click();
            return this;
        }
    }

    public static class ExpireCommissionStrategy extends AbstractAction {
        @Override
        public String getName() {
            return "Expire Commission Strategy";
        }

        @Override
        public Workspace getView() {
            return null;
        }

        @Override
        public AbstractAction start() {
            CommissionPage.tableCommissionStrategy.getRow(CommissionConstants.CommissionStrategyTable.STATUS, "Active").getCell(1).controls.checkBoxes.getFirst().setValue(true);
            CommissionPage.buttonExpireCommissionStrategy.click();
            return this;
        }

        @Override
        public AbstractAction perform(TestData testData) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
        }

        public AbstractAction perform() {
            start();
            return submit();
        }

        @Override
        public AbstractAction submit() {
            Page.dialogConfirmation.confirm();
            return this;
        }
    }

    public abstract static class EditCommissionStrategy extends AbstractAction {

        @Override
        public String getName() {
            return "Edit Commission Strategy";
        }

        public AbstractAction start(int rowNumber) {
            CommissionPage.tableCommissionStrategy.getRow(rowNumber).getCell(CommissionConstants.CommissionStrategyTable.ACTION).controls.links.get(ActionConstants.EDIT).click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction submit() {
            CommissionPage.buttonSaveCommissionStrategy.click();
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

    public abstract static class CopyCommissionStrategy extends AbstractAction {

        @Override
        public String getName() {
            return "Copy Commission Strategy";
        }

        @Override
        public AbstractAction start() {
            NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.COMMISSION.get());
            NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.COMMISSION_BULK_STRATEGY.get());
            return this;
        }

        @Override
        public AbstractAction perform(TestData testData) {
            start();
            getView().fill(testData);
            return submit();
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class DeleteCommissionStrategy extends AbstractAction {
        @Override
        public String getName() {
            return "Delete Commission Strategy";
        }

        @Override
        public Workspace getView() {
            return null;
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead");
        }

        public AbstractAction start(int rowNumber) {
            CommissionPage.tableCommissionStrategy.getRow(rowNumber).getCell(CommissionConstants.CommissionStrategyTable.ACTION).controls.links.get(ActionConstants.DELETE).click();
            return this;
        }

        @Override
        public AbstractAction perform(TestData testData) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
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
    }
}
