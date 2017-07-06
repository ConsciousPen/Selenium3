/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.mywork;

import aaa.helpers.TableVerifier;
import aaa.main.pages.summary.MyWorkSummaryPage;
import toolkit.webdriver.controls.composite.table.Table;

public class MyWorkTasksVerifier extends TableVerifier {

    public MyWorkTasksVerifier setTaskID(String taskID) {
        setValue("Task ID", taskID);
        return this;
    }

    public MyWorkTasksVerifier setTaskName(String taskName) {
        setValue("Task Name", taskName);
        return this;
    }

    public MyWorkTasksVerifier setWarningDate(String warningDate) {
        setValue("Warning Date", warningDate);
        return this;
    }

    public MyWorkTasksVerifier setDueDate(String dueDate) {
        setValue("Due Date", dueDate);
        return this;
    }

    public MyWorkTasksVerifier setPriority(String priority) {
        setValue("Priority", priority);
        return this;
    }

    public MyWorkTasksVerifier setReferenceID(String referenceID) {
        setValue("Reference ID", referenceID);
        return this;
    }

    public MyWorkTasksVerifier setCustomer(String customer) {
        setValue("Customer", customer);
        return this;
    }

    public MyWorkTasksVerifier setAgencyOrLocationName(String agencyOrLocationName) {
        setValue("Agency / Location Name", agencyOrLocationName);
        return this;
    }

    public MyWorkTasksVerifier setAgencyOrLocationID(String agencyOrLocationID) {
        setValue("Agency / Location ID", agencyOrLocationID);
        return this;
    }

    public MyWorkTasksVerifier setLastPerformer(String lastPerformer) {
        setValue("Last Performer", lastPerformer);
        return this;
    }

    public MyWorkTasksVerifier setAssignee(String assignee) {
        setValue("Assignee", assignee);
        return this;
    }

    public MyWorkTasksVerifier setQueue(String queue) {
        setValue("Queue", queue);
        return this;
    }

    @Override
    protected Table getTable() {
        return MyWorkSummaryPage.tableTasks;
    }

    @Override
    protected String getTableName() {
        return "Tasks";
    }
}
