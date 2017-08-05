/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.agencyvendor;

import aaa.admin.modules.agencyvendor.agency.Agency;
import aaa.admin.modules.agencyvendor.brand.Brand;

public enum AgencyVendorType {
    AGENCY("Agency", new Agency()),
    VENDOR("Vendor", null),
    BRAND("Brand", new Brand());

    private String agencyType;
    private IAgencyVendor agency;

    AgencyVendorType(String agencyType, IAgencyVendor agency) {
        this.agencyType = agencyType;
        this.agency = agency;
    }

    public IAgencyVendor get() {
        return agency;
    }

    public String getName() {
        return agencyType;
    }

    public String getKey() {
        return agency.getClass().getSimpleName();
    }
}
