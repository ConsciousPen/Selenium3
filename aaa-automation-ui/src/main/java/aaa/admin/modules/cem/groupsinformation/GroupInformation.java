/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.cem.groupsinformation;

import aaa.EntityLogger;
import aaa.admin.modules.cem.groupsinformation.GroupInformationActions.DeleteGroupsInformation;
import aaa.admin.modules.cem.groupsinformation.GroupInformationActions.EditGroupsInformation;
import aaa.admin.modules.cem.groupsinformation.defaulttabs.CreateGroupInformationTab;
import aaa.admin.modules.cem.groupsinformation.views.DefaultView;
import aaa.admin.pages.cem.CemPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.enums.NavigationEnum.AdminAppMainTabs;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;

public class GroupInformation implements IGroupInformation {

    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.GROUPS_INFORMATION);
        CreateGroupInformationTab.buttonSave.click();
        log.info("Created Group " + entity);
    }

    @Override
    public void initiate() {
        navigate();
        CemPage.buttonCreateNewGroup.click();
    }

    @Override
    public void search(TestData td) {
        CemPage.search(td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(AdminAppMainTabs.CEM.get());
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.CEM_GROUPS_INFORMATION.get());
    }

    @Override
    public EditGroupsInformation editGroupsInformation() {
        return new GroupInformationActions.EditGroupsInformation();
    }

    @Override
    public DeleteGroupsInformation deleteGroupsInformation() {
        return new GroupInformationActions.DeleteGroupsInformation();
    }
}
