/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionstrategy;

import aaa.admin.modules.IAdmin;
import aaa.admin.modules.commission.commissionstrategy.CommissionStrategyActions.CopyCommissionStrategy;
import aaa.admin.modules.commission.commissionstrategy.CommissionStrategyActions.DeleteCommissionStrategy;
import aaa.admin.modules.commission.commissionstrategy.CommissionStrategyActions.EditCommissionStrategy;
import aaa.admin.modules.commission.commissionstrategy.CommissionStrategyActions.ExpireCommissionStrategy;
import aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc.PCCommissionStrategyActions.AddCommissionRule;
import aaa.common.Workspace;
import toolkit.datax.TestData;

public interface ICommissionStrategy extends IAdmin {

    Workspace getDefaultView();

    void create(TestData td);

    void initiate();

    EditCommissionStrategy edit();

    AddCommissionRule addCommissionRule();

    ExpireCommissionStrategy expire();

    CopyCommissionStrategy copy();

    DeleteCommissionStrategy delete();
}
