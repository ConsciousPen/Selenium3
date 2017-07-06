/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.product.productfactory.policy.defaulttabs;

import aaa.admin.metadata.product.ProductMetaData;
import aaa.common.Tab;

public class RuleSetsTab extends PFDefaultTab {

    public RuleSetsTab() {
        super(ProductMetaData.RuleSetsTab.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
