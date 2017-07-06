/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.security;

public enum PrivilegesEnum {
    CLAIM_CREATE("Claim Create"),
    CLAIM_INQUIRY_CLAIM_POLICY("Claim Inquiry Claim Policy"),
    CLAIM_SECURE_CLAIM("Claim Secure Claim"),
    PLATFORM_ADMIN_PROFILE_DISABLE_USER("SecurityAdmin Profile Disable User"),
    PLATFORM_ADMIN_BRANDS_TAB_ACCESS("SecurityAdmin Brands Tab Access"),
    PAYMENT_TRANSFER("Payment Transfer");

    String id;

    PrivilegesEnum(String id) {
        this.id = id;
    }

    public String get() {
        return id;
    }
}
