/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionbonus;

import aaa.utils.EntityLogger;
import aaa.admin.modules.commission.commissionbonus.CommissionBonusActions.DeleteCommissionBonus;
import aaa.admin.modules.commission.commissionbonus.CommissionBonusActions.EditCommissionBonus;
import aaa.admin.modules.commission.commissionbonus.CommissionBonusActions.ExpireCommissionBonus;
import aaa.admin.modules.commission.commissionbonus.views.DefaultView;
import aaa.admin.pages.commission.CommissionPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;

public class CommissionBonus implements ICommissionBonus {
    private Workspace defaultView = new DefaultView();

    @Override
    public void search(TestData td) {
        CommissionPage.search(td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.COMMISSION.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.COMMISSION_COMMISSION_BONUS.get());
    }

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        log.info("Created Commission Bonus " + EntityLogger.getEntityHeader(EntityLogger.EntityType.COMMISSION_BONUS));
    }

    @Override
    public void initiate() {
        navigate();
        CommissionPage.buttonAddCommissionBonus.click();
    }

    @Override
    public EditCommissionBonus edit() {
        return new EditCommissionBonus();
    }

    @Override
    public ExpireCommissionBonus expire() {
        return new ExpireCommissionBonus();
    }

    @Override
    public DeleteCommissionBonus delete() {
        return new DeleteCommissionBonus();
    }

}
