/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.commission.commissionreferral;

import aaa.admin.modules.commission.commissionreferral.defaulttabs.AddCommissionReferralTab;
import aaa.admin.modules.commission.commissionreferral.views.DefaultView;
import aaa.admin.pages.commission.CommissionPage;
import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.common.pages.Page;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.CommissionConstants;
import toolkit.datax.TestData;

public final class CommissionReferralActions {

    private CommissionReferralActions() {}

    public static class ExpireCommissionReferral extends AbstractAction {
        @Override
        public String getName() {
            return "Expire Commission Referral";
        }

        @Override
        public Workspace getView() {
            return null;
        }

        public AbstractAction start(int rowNumber) {
            CommissionPage.tableCommissionReferral.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
            CommissionPage.buttonExpireCommissionReferral.click();
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

    public static class EditCommissionReferral extends AbstractAction {
        @Override
        public String getName() {
            return "Edit Commission Referral";
        }

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

        public AbstractAction start(int rowNumber) {
            CommissionPage.tableCommissionReferral.getRow(rowNumber).getCell(CommissionConstants.CommissionReferralTable.ACTION).controls.links.get(ActionConstants.EDIT).click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction submit() {
            AddCommissionReferralTab.buttonSave.click();
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

    public static class DeleteCommissionReferral extends AbstractAction {
        @Override
        public String getName() {
            return "Delete Commission Referral";
        }

        @Override
        public Workspace getView() {
            return null;
        }

        public AbstractAction start(int rowNumber) {
            CommissionPage.tableCommissionReferral.getRow(rowNumber).getCell(CommissionConstants.CommissionReferralTable.ACTION).controls.links.get(ActionConstants.DELETE).click();
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
