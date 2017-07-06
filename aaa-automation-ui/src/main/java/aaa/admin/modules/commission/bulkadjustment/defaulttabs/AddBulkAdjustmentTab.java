package aaa.admin.modules.commission.bulkadjustment.defaulttabs;

import aaa.admin.metadata.commission.CommissionMetaData;
import aaa.common.DefaultTab;
import aaa.common.Tab;

public class AddBulkAdjustmentTab extends DefaultTab {

    public AddBulkAdjustmentTab() {
        super(CommissionMetaData.AddBulkAdjustment.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
