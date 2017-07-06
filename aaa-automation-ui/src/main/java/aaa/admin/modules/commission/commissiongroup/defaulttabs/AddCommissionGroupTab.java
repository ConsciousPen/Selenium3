/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissiongroup.defaulttabs;

import aaa.admin.metadata.commission.CommissionMetaData;
import aaa.common.DefaultTab;
import aaa.common.Tab;

public class AddCommissionGroupTab extends DefaultTab {

    public AddCommissionGroupTab() {
        super(CommissionMetaData.AddCommissionGroup.class);
    }

    @Override
    public Tab submitTab() {
        buttonSave.click();
        return this;
    }
}
