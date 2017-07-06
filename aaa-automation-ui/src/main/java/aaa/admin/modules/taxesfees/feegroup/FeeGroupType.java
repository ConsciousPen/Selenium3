/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.taxesfees.feegroup;

public enum FeeGroupType {
    FEE_GROUP("Fee Group", null);

    private String feeGroupType;
    private IFeeGroup feeGroup;

    FeeGroupType(String feeGroupType, IFeeGroup feeGroup) {
        this.feeGroupType = feeGroupType;
        this.feeGroup = feeGroup;
    }

    public IFeeGroup get() {
        return feeGroup;
    }

    public String getName() {
        return feeGroupType;
    }

    public String getKey() {
        return feeGroup.getClass().getSimpleName();
    }
}
