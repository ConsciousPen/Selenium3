/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.workflow.processmanagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aaa.utils.EntityLogger;
import aaa.admin.modules.workflow.processmanagement.views.DefaultView;
import aaa.admin.pages.workflow.ProcessManagementPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ActionConstants;
import aaa.admin.constants.AdminConstants;
import toolkit.datax.TestData;

public class Task implements ITask {

    protected static Logger log = LoggerFactory.getLogger(Task.class);

    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void initiate() {
        navigate();
        ProcessManagementPage.buttonAddManualTaskDefinition.click();
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.WORK_FLOW.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.WORK_FLOW_PROCESS_MANAGEMENT.get());
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.TASK);
        ProcessManagementPage.buttonSave.click();
        log.info("Created Task" + entity);
    }

    @Override
    public void update(TestData td) {
        ProcessManagementPage.tableSearchResults.getRow(1).getCell(AdminConstants.AdminSearchResultsTable.ACTION).controls.links.get(ActionConstants.CHANGE).click();
        getDefaultView().fill(td);
        ProcessManagementPage.buttonSave.click();
    }

    @Override
    public void search(TestData td) {
        ProcessManagementPage.search(td);
    }
}
