/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.enums;

public final class MyWorkConstants {

    public static final String APPROVE_FEATURE = "Approve Feature";
    public static final String CLAIM_MANAGEMENT = "Claim Management";
    public static final String PROCESS_CLAIM = "Process Claim";
    public static final String PROCESS_FEATURE = "Process Feature";

    private MyWorkConstants() {
    }

    public static final class MyWorkTasksTable {
        public static final String TASK_NAME = "Task Name";
        public static final String REFERENCE_ID = "Reference ID";
        public static final String TASK_ID = "Task ID";
        public static final String WARNING_DATE = "Warning Date";
        public static final String DUE_DATE = "Due Date";
        public static final String QUEUE = "Queue";
        public static final String NOTE_DESCRIPTION = "Note Description";
        public static final String AGENCY_LOCATION_ID = "Agency / Location ID";
    }

    public static final class MyWorkActivitiesAndUserNotesTable {
        public static final String STATUS = "Status";
        public static final String DESCRIPTION = "Description";
    }

    public enum MyWorkFilterTaskStatus {
        ACTIVE,
        COMPLETED,
        SUSPENDED
    }
}
