/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.mywork;

import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.main.modules.mywork.actiontabs.AssignTaskToActionTab;
import aaa.main.modules.mywork.actiontabs.CompleteTaskActionTab;
import aaa.main.modules.mywork.actiontabs.CreateTaskActionTab;
import aaa.main.modules.mywork.actiontabs.UpdateTaskActionTab;
import aaa.main.modules.mywork.views.AssignTaskToView;
import aaa.main.modules.mywork.views.CompleteTaskView;
import aaa.main.modules.mywork.views.CreateTaskView;
import aaa.main.modules.mywork.views.FilterTaskView;
import aaa.main.modules.mywork.views.UpdateTaskView;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.TaskDetailsSummaryPage;
import toolkit.datax.TestData;

/**
 * Set of abstract classes describing all actions available for the product entities of each type.
 * Modify this class if the set of actions for a particular product entity has to be changed.
 * @category Generated
 */
public final class MyWorkActions {
    private MyWorkActions() {}

    public static class CreateTask extends AbstractAction {
        @Override
        public String getName() {
            return "Create Task";
        }

        @Override
        public Workspace getView() {
            return new CreateTaskView();
        }

        @Override
        public AbstractAction start() {
        	CreateTaskActionTab.buttonCreateTask.click();
            return this;
        }

        @Override
        public AbstractAction submit() {
        	CreateTaskActionTab.buttonCreate.click();
            return this;
        }
    }

    public static class AssignTaskTo extends AbstractAction {
        @Override
        public String getName() {
            return "Assign Task To";
        }

        @Override
        public Workspace getView() {
            return new AssignTaskToView();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        public AbstractAction start(int rowNumber) {
            MyWorkSummaryPage.selectTask(rowNumber);
            MyWorkSummaryPage.buttonAssign.click();
            return this;
        }

        public AbstractAction startFromView(int rowNumber) {
        	MyWorkSummaryPage.openTaskDetails(rowNumber);
            TaskDetailsSummaryPage.buttonAssign.click();
            return this;
        }
        
        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(int rowNumber, TestData td) instead.");
        }

        public AbstractAction perform(int rowNumber, TestData td) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        public AbstractAction performFromView(int rowNumber, TestData td) {
        	startFromView(rowNumber);
            getView().fill(td);
            return submitFromView();
        }
        
        @Override
        public AbstractAction submit() {
        	AssignTaskToActionTab.buttonAssign.click();
            return this;
        }
        
        public AbstractAction submitFromView() {
        	AssignTaskToActionTab.buttonAssign.click();
        	TaskDetailsSummaryPage.buttonCancel.click();
            return this;
        }
    }

    public static class CompleteTask extends AbstractAction {
        @Override
        public String getName() {
            return "Complete Task";
        }

        @Override
        public Workspace getView() {
            return new CompleteTaskView();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        public AbstractAction start(int rowNumber) {
        	MyWorkSummaryPage.selectTask(rowNumber);
        	MyWorkSummaryPage.buttonComplete.click();
            return this;
        }

        @Override
        public AbstractAction submit() {
        	CompleteTaskActionTab.buttonComplete.click();
            return this;
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(int rowNumber, TestData td) instead.");
        }

        public AbstractAction perform(int rowNumber, TestData td) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }
    }

    public static class UpdateTask extends AbstractAction {
        @Override
        public String getName() {
            return "Update Task";
        }

        @Override
        public Workspace getView() {
            return new UpdateTaskView();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        public AbstractAction start(int rowNumber) {
            MyWorkSummaryPage.openTaskDetails(rowNumber);
            TaskDetailsSummaryPage.buttonUpdate.click();
            return this;
        }

        @Override
        public AbstractAction submit() {
        	UpdateTaskActionTab.buttonUpdate.click();
        	TaskDetailsSummaryPage.buttonCancel.click();
            return this;
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(int rowNumber, TestData td) instead.");
        }

        public AbstractAction perform(int rowNumber, TestData td) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }
    }

    public static class FilterTask extends AbstractAction {
        @Override
        public String getName() {
            return "Filter Task";
        }

        @Override
        public Workspace getView() {
            return new FilterTaskView();
        }

        @Override
        public AbstractAction start() {
            MyWorkSummaryPage.showFilter();
            return this;
        }

        @Override
        public AbstractAction submit() {
            MyWorkSummaryPage.submitFilter();
            return this;
        }
    }
}
