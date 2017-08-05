/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc;

import aaa.admin.modules.commission.commissionstrategy.CommissionStrategy;
import aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc.PCCommissionStrategyActions.AddCommissionRule;
import aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc.PCCommissionStrategyActions.CopyCommissionStrategy;
import aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc.PCCommissionStrategyActions.EditCommissionStrategy;
import aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc.views.DefaultView;
import aaa.admin.pages.commission.CommissionPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;

public class PCCommissionStrategy extends CommissionStrategy {
    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void initiate() {
        navigate();
        CommissionPage.buttonAddNewCommissionStrategy.click();
    }

    @Override
    public AddCommissionRule addCommissionRule() {
        return new PCCommissionStrategyActions.AddCommissionRule();
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.COMMISSION.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.PC_COMMISSION_COMMISSION_STRATEGY.get());
    }

    @Override
    public EditCommissionStrategy edit() {
        return new EditCommissionStrategy();
    }

    @Override
    public CopyCommissionStrategy copy() {
        return new CopyCommissionStrategy();
    }
}
