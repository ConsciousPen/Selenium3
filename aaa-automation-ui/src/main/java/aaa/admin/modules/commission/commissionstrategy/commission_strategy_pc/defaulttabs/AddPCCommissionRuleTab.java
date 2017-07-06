/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionstrategy.commission_strategy_pc.defaulttabs;

import aaa.admin.metadata.commission.CommissionMetaData;
import aaa.admin.pages.commission.CommissionPage;
import aaa.common.DefaultTab;
import aaa.common.Tab;

public class AddPCCommissionRuleTab extends DefaultTab {

    public AddPCCommissionRuleTab() {
        super(CommissionMetaData.AddPCCommissionRule.class);
    }

    @Override
    public Tab submitTab() {
        CommissionPage.buttonAddNewCommissionRule.click();
        return this;
    }
}
