/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.account;

public enum AccountType {
    INDIVIDUAL("Individual", new Account()),
    NON_INDIVIDUAL("Nonindividual", new Account());

    private String accountType;
    private IAccount product;

    AccountType(String t, IAccount p) {
        accountType = t;
        product = p;
    }

    public IAccount get() {
        return product;
    }

    public String getName() {
        return accountType;
    }

    public String getKey() {
        return product.getClass().getSimpleName();
    }
}
