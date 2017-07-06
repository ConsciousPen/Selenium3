/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.general.numberrange.defaulttabs;

import aaa.admin.metadata.general.GeneralMetaData;
import aaa.common.DefaultTab;
import aaa.common.Tab;

public class AddNumberRangeTab extends DefaultTab {

    public AddNumberRangeTab() {
        super(GeneralMetaData.AddNumberRange.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
