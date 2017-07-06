/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.admin.modules.taxesfees.strategy.fee;

import aaa.admin.modules.taxesfees.strategy.IStrategy;

public enum FeeStrategyType {
    FEE("Fee", new FeeStrategy());

    private String strategyType;
    private IStrategy strategy;

    FeeStrategyType(String strategyType, IStrategy strategy) {
        this.strategyType = strategyType;
        this.strategy = strategy;
    }

    public IStrategy get() {
        return strategy;
    }

    public String getType() {
        return strategyType;
    }
}
