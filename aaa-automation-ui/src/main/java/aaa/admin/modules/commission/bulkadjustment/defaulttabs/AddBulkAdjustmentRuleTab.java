package aaa.admin.modules.commission.bulkadjustment.defaulttabs;

import aaa.admin.metadata.commission.CommissionMetaData;
import aaa.admin.pages.commission.CommissionPage;
import aaa.common.DefaultTab;
import aaa.common.Tab;

public class AddBulkAdjustmentRuleTab extends DefaultTab {

    public AddBulkAdjustmentRuleTab() {
        super(CommissionMetaData.AddBulkAdjustmentRule.class);
    }

    @Override
    public Tab submitTab() {
        CommissionPage.buttonAddBulkAdjustmentRule.click();
        return this;
    }
}
