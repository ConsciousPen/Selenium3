package aaa.admin.modules.commission.bulkadjustment.views;

import aaa.admin.modules.commission.bulkadjustment.defaulttabs.AddBulkAdjustmentRuleTab;
import aaa.admin.modules.commission.bulkadjustment.defaulttabs.AddBulkAdjustmentTab;
import aaa.common.Workspace;

public class EditBulkAdjustmentView extends Workspace {

    public EditBulkAdjustmentView() {
        registerTab(AddBulkAdjustmentTab.class);
        registerTab(AddBulkAdjustmentRuleTab.class);
    }
}
