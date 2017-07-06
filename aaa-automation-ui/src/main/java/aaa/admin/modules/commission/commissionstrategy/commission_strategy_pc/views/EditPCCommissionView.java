/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc.views;

import aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc.defaulttabs.AddPCCommissionRuleTab;
import aaa.common.Workspace;

public class EditPCCommissionView extends Workspace {

    public EditPCCommissionView() {
        super();
        registerTab(AddPCCommissionRuleTab.class);
    }
}
