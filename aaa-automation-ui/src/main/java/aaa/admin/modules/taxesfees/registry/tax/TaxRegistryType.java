/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.admin.modules.taxesfees.registry.tax;

import aaa.admin.modules.taxesfees.registry.IRegistry;

public enum TaxRegistryType {
    TAX("Tax", new TaxRegistry());

    private String registryType;
    private IRegistry registry;

    TaxRegistryType(String registryType, IRegistry registry) {
        this.registryType = registryType;
        this.registry = registry;
    }

    public IRegistry get() {
        return registry;
    }

    public String getType() {
        return registryType;
    }
}
