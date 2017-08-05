package aaa.admin.modules.commission.bulkadjustment;

import aaa.admin.modules.commission.bulkadjustment.views.EditBulkAdjustmentView;
import aaa.admin.pages.commission.CommissionPage;
import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.common.pages.Page;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.CommissionConstants;
import toolkit.datax.TestData;

public final class BulkAdjustmentActions {

    private BulkAdjustmentActions() {}

    public static class AddBulkAdjustmentRule extends AbstractAction {
        @Override
        public String getName() {
            return "Add Bulk Adjustment Rule";
        }

        @Override
        public Workspace getView() {
            return new EditBulkAdjustmentView();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        public AbstractAction start(int rowNumber) {
            CommissionPage.tableBulkAdjustment.getRow(rowNumber).getCell(CommissionConstants.CommissionBulkAdjustmentTable.ACTION).controls.links.get(ActionConstants.EDIT).click();
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
            CommissionPage.buttonSaveBulkAdjustment.click();
            return this;
        }
    }

    public static class EditBulkAdjustment extends AbstractAction {

        @Override
        public String getName() {
            return "Edit Bulk Adjustment";
        }

        @Override
        public Workspace getView() {
            return new EditBulkAdjustmentView();
        }

        public AbstractAction start(int rowNumber) {
            CommissionPage.tableBulkAdjustment.getRow(rowNumber).getCell(CommissionConstants.CommissionBulkAdjustmentTable.ACTION).controls.links.get(ActionConstants.EDIT).click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction submit() {
            CommissionPage.buttonSaveBulkAdjustment.click();
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

    public static class DeleteBulkAdjustment extends AbstractAction {
        @Override
        public String getName() {
            return "Delete Bulk Adjustment";
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
            CommissionPage.tableBulkAdjustment.getRow(rowNumber).getCell(CommissionConstants.CommissionBulkAdjustmentTable.ACTION).controls.links.get(ActionConstants.DELETE).click();
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
