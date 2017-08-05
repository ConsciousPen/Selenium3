/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.mywork;

import aaa.common.Workspace;
import aaa.main.modules.mywork.MyWorkActions.AssignTaskTo;
import aaa.main.modules.mywork.MyWorkActions.CompleteTask;
import aaa.main.modules.mywork.MyWorkActions.CreateTask;
import aaa.main.modules.mywork.MyWorkActions.FilterTask;
import aaa.main.modules.mywork.MyWorkActions.UpdateTask;

public interface IMyWork {
    Workspace getDefaultView();

    CreateTask createTask();

    AssignTaskTo assignTaskTo();

    CompleteTask completeTask();

    UpdateTask updateTask();

    FilterTask filterTask();
}
