/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.product.productstrategy;

public enum ProductStrategyType {

    STRATEGY("Strategy", new ProductStrategy());

    private String strategyType;
    private IProductStrategy strategy;

    ProductStrategyType(String strategyType, IProductStrategy strategy) {
        this.strategyType = strategyType;
        this.strategy = strategy;
    }

    public IProductStrategy get() {
        return strategy;
    }

    public String getName() {
        return strategyType;
    }

    public String getKey() {
        return strategy.getClass().getSimpleName();
    }
}
