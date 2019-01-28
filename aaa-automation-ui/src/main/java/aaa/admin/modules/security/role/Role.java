/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.security.role;

import com.exigen.ipb.eisa.base.app.CSAAApplicationFactory;
import aaa.admin.metadata.security.RoleMetaData;
import aaa.admin.modules.security.ChannelType;
import aaa.admin.modules.security.role.defaulttabs.GeneralRoleTab;
import aaa.admin.modules.security.role.views.DefaultView;
import aaa.admin.pages.security.RolePage;
import aaa.common.Tab;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ActionConstants;
import aaa.utils.EntityLogger;
import toolkit.datax.TestData;

public class Role implements IRole {
    private Workspace defaultView = new DefaultView();
    private ChannelType channelType;

    public Role(ChannelType channelType) {
        this.channelType = channelType;
    }

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void initiate() {
        navigateToFlow();
        RolePage.assetListSearchForm.getAsset(RoleMetaData.SearchByField.BUSINESS_DOMAIN).setValue(channelType.getName());
        RolePage.buttonAddNewRole.click();
    }

    @Override
    public void search(TestData td) {
        RolePage.search(td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.SECURITY.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.SECURITY_ROLE.get());
    }

    @Override
    public void navigateToFlow() {
        NavigationPage.toFlow(CSAAApplicationFactory.get().adminApp(), NavigationEnum.AdminAppLeftMenu.SECURITY_ROLE.getFlow());
    }

    @Override
    public void create(TestData td) {
        channelType = channelType == null ? ChannelType.valueOf(td.getValue("Channel")) : channelType;
        initiate();
        getDefaultView().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.ROLE);
		Tab.buttonSave.click();
        log.info("Created Role " + entity);
    }

    @Override
    public void update(TestData td) {
        RolePage.tableRolesSearchResult.getRow(1).getCell(3).controls.links.get(ActionConstants.CHANGE).click();
        getDefaultView().fill(td);
        GeneralRoleTab.buttonUpdate.click();
    }
}
