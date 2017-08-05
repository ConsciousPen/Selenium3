/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer;

public enum CustomerType {
    INDIVIDUAL("Individual", new Customer()),
    NON_INDIVIDUAL("Nonindividual", new Customer());

    private String customerType;
    private ICustomer product;

    CustomerType(String t, ICustomer p) {
        customerType = t;
        product = p;
    }

    public ICustomer get() {
        return product;
    }

    public String getName() {
        return customerType;
    }

    public String getKey() {
        return product.getClass().getSimpleName();
    }
}
