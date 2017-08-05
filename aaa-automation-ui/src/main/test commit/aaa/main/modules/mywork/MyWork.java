/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.mywork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aaa.common.Workspace;
import aaa.main.modules.mywork.MyWorkActions.AssignTaskTo;
import aaa.main.modules.mywork.MyWorkActions.CompleteTask;
import aaa.main.modules.mywork.MyWorkActions.CreateTask;
import aaa.main.modules.mywork.MyWorkActions.FilterTask;
import aaa.main.modules.mywork.MyWorkActions.UpdateTask;
import aaa.main.modules.mywork.views.DefaultView;

public class MyWork implements IMyWork {

    protected static Logger log = LoggerFactory.getLogger(MyWork.class);

    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public CreateTask createTask() {
        return new CreateTask();
    }

    @Override
    public AssignTaskTo assignTaskTo() {
        return new AssignTaskTo();
    }

    @Override
    public CompleteTask completeTask() {
        return new CompleteTask();
    }

    @Override
    public UpdateTask updateTask() {
        return new UpdateTask();
    }

    @Override
    public FilterTask filterTask() {
        return new FilterTask();
    }
}
