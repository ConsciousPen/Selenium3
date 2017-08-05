/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.security.role;

import aaa.admin.modules.security.ChannelType;

public enum RoleType {

    CORPORATE("Corporate", new Role(ChannelType.CORPORATE)),
    AGENCY("Agency", new Role(ChannelType.AGENCY)),
    VENDOR("Vendor", new Role(ChannelType.VENDOR));

    private String roleType;
    private IRole role;

    RoleType(String roleType, IRole role) {
        this.roleType = roleType;
        this.role = role;
    }

    public IRole get() {
        return role;
    }

    public String getName() {
        return roleType;
    }

    public String getKey() {
        return role.getClass().getSimpleName();
    }
}
