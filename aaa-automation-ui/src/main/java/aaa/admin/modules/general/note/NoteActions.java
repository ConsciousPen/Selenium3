/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.general.note;

import aaa.admin.pages.general.NotePage;
import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.common.pages.Page;
import aaa.main.enums.ActionConstants;
import toolkit.datax.TestData;

public final class NoteActions {

    private NoteActions() {}

    public static class DeleteNoteCategory extends AbstractAction {
        @Override
        public String getName() {
            return "Delete Note Category";
        }

        @Override
        public Workspace getView() {
            return null;
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        public AbstractAction start(int rowNumber) {
            NotePage.tableSearchResults.getRow(rowNumber).getCell(NotePage.tableSearchResults.getColumnsCount()).controls.links.get(ActionConstants.DELETE).click();
            return this;
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without parameters is not supported for this action. Use perform(int rowNumber) instead.");
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            submit();
            return this;
        }

        @Override
        public AbstractAction submit() {
            Page.dialogConfirmation.confirm();
            return this;
        }
    }

    public static class DisableNoteCategory extends AbstractAction {
        @Override
        public String getName() {
            return "Disable Note Category";
        }

        @Override
        public Workspace getView() {
            return null;
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        public AbstractAction start(int rowNumber) {
            NotePage.tableSearchResults.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
            NotePage.buttonDisableCategory.click();
            return this;
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without parameters is not supported for this action. Use perform(int rowNumber) instead.");
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return this;
        }
    }
}
