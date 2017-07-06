/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.billing.account;

public enum BillingAccountType {
    BILLING_ACCOUNT("Billing Account", new BillingAccount());

    private String name;
    private IBillingAccount billingAccount;

    BillingAccountType(String name, IBillingAccount billingAccount) {
        this.name = name;
        this.billingAccount = billingAccount;
    }

    public IBillingAccount get() {
        return billingAccount;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return billingAccount.getClass().getSimpleName();
    }
}
