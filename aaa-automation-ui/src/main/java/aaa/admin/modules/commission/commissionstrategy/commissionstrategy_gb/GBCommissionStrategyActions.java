/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package aaa.admin.modules.commission.commissionstrategy.commissionstrategy_gb;

import aaa.admin.modules.commission.commissionstrategy.CommissionStrategyActions;
import aaa.admin.modules.commission.commissionstrategy.commissionstrategy_gb.views.DefaultView;
import aaa.common.Workspace;

public final class GBCommissionStrategyActions {
    private GBCommissionStrategyActions() {
    }

    public static class EditCommissionStrategy extends CommissionStrategyActions.EditCommissionStrategy {

        @Override
        public Workspace getView() {
            return new DefaultView();
        }
    }
}
