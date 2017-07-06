/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionstrategy.commissionstrategy_gb.views;

import aaa.admin.modules.commission.commissionstrategy.commissionstrategy_gb.defaulttabs.AddGBCommissionRuleTab;
import aaa.admin.modules.commission.commissionstrategy.commissionstrategy_gb.defaulttabs.AddGBCommissionStrategyTab;
import aaa.common.Workspace;

public class DefaultView extends Workspace {

    public DefaultView() {
        registerTab(AddGBCommissionStrategyTab.class);
        registerTab(AddGBCommissionRuleTab.class);
    }
}
