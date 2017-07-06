/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.product.productfactory;

import aaa.admin.modules.product.productfactory.policy.ProductFactoryPolicy;

public enum ProductFactoryType {

    POLICY("Policy", new ProductFactoryPolicy());

    private String productFactoryType;
    private IProductFactory productFactory;

    ProductFactoryType(String productFactoryType, IProductFactory productFactory) {
        this.productFactoryType = productFactoryType;
        this.productFactory = productFactory;
    }

    public IProductFactory get() {
        return productFactory;
    }

    public String getName() {
        return productFactoryType;
    }

    public String getKey() {
        return productFactory.getClass().getSimpleName();
    }
}
