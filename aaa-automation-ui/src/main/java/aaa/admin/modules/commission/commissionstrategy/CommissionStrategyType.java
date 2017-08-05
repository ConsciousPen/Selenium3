/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionstrategy;

import aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc.PCCommissionStrategy;
import aaa.admin.modules.commission.commissionstrategy.commissionstrategy_gb.GBCommissionStrategy;

public enum CommissionStrategyType {

    PC_COMMISSION_STRATEGY_PREC_HO("P&C Commission Strategy (PREC-HO)", new PCCommissionStrategy()),
    PC_COMMISSION_STRATEGY_CLAIM_HO("P&C Commission Strategy (CLAIM_HO)", new PCCommissionStrategy()),
    PC_COMMISSION_STRATEGY_PREC_AU("P&C Commission Strategy (PREC-AUTO)", new PCCommissionStrategy()),
    GB_COMMISSION_STRATEGY_GB("GB Commission Strategy (GB)", new GBCommissionStrategy());

    private String strategyType;
    private ICommissionStrategy strategy;

    CommissionStrategyType(String strategyType, ICommissionStrategy strategy) {
        this.strategyType = strategyType;
        this.strategy = strategy;
    }

    public ICommissionStrategy get() {
        return strategy;
    }

    public String getName() {
        return strategyType;
    }

    public String getKey() {
        return strategy.getClass().getSimpleName();
    }
}
