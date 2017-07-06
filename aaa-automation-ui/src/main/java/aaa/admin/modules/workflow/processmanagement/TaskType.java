/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.workflow.processmanagement;

public enum TaskType {

    PROCESS_MANAGEMENT("Process Management", new Task());

    private String taskType;
    private ITask task;

    TaskType(String taskType, ITask task) {
        this.taskType = taskType;
        this.task = task;
    }

    public ITask get() {
        return task;
    }

    public String getName() {
        return taskType;
    }

    public String getKey() {
        return task.getClass().getSimpleName();
    }

}
