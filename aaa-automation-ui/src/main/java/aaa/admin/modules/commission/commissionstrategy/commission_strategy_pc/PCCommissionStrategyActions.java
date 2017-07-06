/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc;

import aaa.admin.modules.commission.commissionstrategy.CommissionStrategyActions;
import aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc.views.CopyPCCommissionStrategyView;
import aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc.views.DefaultView;
import aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc.views.EditPCCommissionView;
import aaa.common.Workspace;

public final class PCCommissionStrategyActions {
    private PCCommissionStrategyActions() {}

    public static class AddCommissionRule extends CommissionStrategyActions.AddCommissionRule {

        @Override
        public Workspace getView() {
            return new EditPCCommissionView();
        }
    }

    public static class EditCommissionStrategy extends CommissionStrategyActions.EditCommissionStrategy {

        @Override
        public Workspace getView() {
            return new DefaultView();
        }
    }

    public static class CopyCommissionStrategy extends CommissionStrategyActions.CopyCommissionStrategy {

        @Override
        public Workspace getView() {
            return new CopyPCCommissionStrategyView();
        }
    }
}
