/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.security.profile;

import aaa.admin.modules.security.ChannelType;

public enum ProfileType {
    AGENCY("Agency", new Profile(ChannelType.AGENCY)),
    VENDOR("Vendor", new Profile(ChannelType.VENDOR)),
    CORPORATE("Corporate", new Profile(ChannelType.CORPORATE));

    private String profileType;
    private IProfile profile;

    ProfileType(String profileType, IProfile profile) {
        this.profileType = profileType;
        this.profile = profile;
    }

    public IProfile get() {
        return profile;
    }

    public String getName() {
        return profileType;
    }

    public String getKey() {
        return profile.getClass().getSimpleName();
    }
}
