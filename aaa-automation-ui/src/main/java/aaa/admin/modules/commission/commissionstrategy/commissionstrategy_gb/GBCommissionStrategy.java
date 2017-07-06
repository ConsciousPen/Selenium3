/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionstrategy.commissionstrategy_gb;

import aaa.admin.modules.commission.commissionstrategy.CommissionStrategy;
import aaa.admin.modules.commission.commissionstrategy.CommissionStrategyActions.CopyCommissionStrategy;
import aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc.PCCommissionStrategyActions.AddCommissionRule;
import aaa.admin.modules.commission.commissionstrategy.commissionstrategy_gb.GBCommissionStrategyActions.EditCommissionStrategy;
import aaa.admin.modules.commission.commissionstrategy.commissionstrategy_gb.views.DefaultView;
import aaa.admin.pages.commission.CommissionPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;

public class GBCommissionStrategy extends CommissionStrategy {

    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public AddCommissionRule addCommissionRule() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.COMMISSION.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GB_COMMISSION_COMMISSION_STRATEGY.get());
    }

    @Override
    public void initiate() {
        navigate();
        CommissionPage.buttonAddNewCommissionStrategy.click();
    }

    @Override
    public EditCommissionStrategy edit() {
        return new EditCommissionStrategy();
    }

    @Override
    public CopyCommissionStrategy copy() {
        // TODO Auto-generated method stub
        return null;
    }
}
