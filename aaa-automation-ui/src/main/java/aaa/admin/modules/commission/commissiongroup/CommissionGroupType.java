/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissiongroup;

public enum CommissionGroupType {

    COMMISSION_GROUP("Commission Group", new CommissionGroup());

    private String groupType;
    private ICommissionGroup group;

    CommissionGroupType(String groupType, ICommissionGroup group) {
        this.groupType = groupType;
        this.group = group;
    }

    public ICommissionGroup get() {
        return group;
    }

    public String getName() {
        return groupType;
    }

    public String getKey() {
        return group.getClass().getSimpleName();
    }
}
