/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissiongroup;

import aaa.admin.modules.commission.commissiongroup.CommissionGroupActions.DeleteCommissionGroup;
import aaa.admin.modules.commission.commissiongroup.CommissionGroupActions.EditCommissionGroup;
import aaa.admin.modules.commission.commissiongroup.CommissionGroupActions.ExpireCommissionGroup;
import aaa.admin.modules.commission.commissiongroup.views.DefaultView;
import aaa.admin.pages.commission.CommissionPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;

public class CommissionGroup implements ICommissionGroup {
    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void initiate() {
        navigate();
        CommissionPage.buttonAddNewCommissionGroup.click();
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        log.info("Created Commission Group " + td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.COMMISSION.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.COMMISSION_COMMISSION_GROUP.get());
    }

    @Override
    public void search(TestData td) {
        CommissionPage.search(td);
    }

    @Override
    public EditCommissionGroup edit() {
        return new EditCommissionGroup();
    }

    @Override
    public ExpireCommissionGroup expire() {
        return new ExpireCommissionGroup();
    }

    @Override
    public DeleteCommissionGroup delete() {
        return new DeleteCommissionGroup();
    }
}
