/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.cem.groupsinformation;

public enum GroupInformationType {
    GROUPS_INFORMATION("Groups Information", new GroupInformation());

    private String type;
    private IGroupInformation entity;

    GroupInformationType(String t, IGroupInformation e) {
        type = t;
        entity = e;
    }

    public IGroupInformation get() {
        return entity;
    }

    public String getName() {
        return type;
    }

    public String getKey() {
        return entity.getClass().getSimpleName();
    }
}
